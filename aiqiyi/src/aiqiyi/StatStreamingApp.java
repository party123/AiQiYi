package aiqiyi;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.*;
import scala.Tuple2;
import org.apache.spark.streaming.Seconds;
import org.apache.spark.streaming.StreamingContext;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import scala.collection.mutable.ListBuffer;

/**
 * 使用SparkStreaming处理kafaka数据
 * jar包要为kafka-clients，不能是kafka。。。调了一天的bug
 * @author 6
 *
 */
public class StatStreamingApp {
	public static void main(String[] args) throws InterruptedException{
	//创建sparkconf和sparkstreaming
	SparkConf sparkconf = new SparkConf().setMaster("local").setAppName("aiqiyi");
	JavaStreamingContext sc = new JavaStreamingContext(sparkconf,new Duration(2000)); 
	
	//kafka相关参数
	Map<String,Object> kafkaParams = new HashMap<>();
	kafkaParams.put("bootstrap.servers", "localhost:9092");
	kafkaParams.put("key.deserializer", StringDeserializer.class);
	kafkaParams.put("value.deserializer", StringDeserializer.class);
	kafkaParams.put("group.id", "test");
	kafkaParams.put("auto.offset.reset", "latest");
	kafkaParams.put("enable.auto.commit", false);
    
    //Topic分区，创建一个从主题到接收器线程数的映射表
	Collection<String> topics = Arrays.asList("flumeTopic");
   
    //通过KafkaUtils订阅主题，获得kafka数据，kafka相关参数由kafkaParams指定
	JavaInputDStream<ConsumerRecord<String,String>> lines = KafkaUtils.createDirectStream(
			sc,
			LocationStrategies.PreferConsistent(),
            ConsumerStrategies.Subscribe(topics, kafkaParams)
        );
	//只取value部分
	JavaDStream log = lines.map(line->line.value());
    
    //清理数据
	/*
	JavaDStream ll = lines.map(line ->(Function<ConsumerRecord<String, String>, R>{
		String[] infos = line.split("\t");
    	String url = infos[2].split(" ")[1];
    	int categaryId = 0;
    	if(url.startsWith("www")){
    		categaryId = Integer.valueOf(url.split("/")[1]);
	}));
	*/
	
    JavaDStream<ClickLog> cleanLog = log.map(new Function<String, ClickLog>(){
    	public ClickLog call(String line) throws NumberFormatException, ParseException{
    		String[] infos = line.split("\t");
    		String url = infos[2].split(" ")[1];
    		int categaryId = 0;
    		if(url.startsWith("www")){
    			categaryId = Integer.valueOf(url.split("/")[1]);
    		}
    	return new ClickLog(infos[0],new DataUtil().parseToMin(infos[1]),categaryId,infos[3],Integer.parseInt(infos[4]));
    	}

    }).filter(line->((ClickLog) line).getcategaryId()!=0);
    
    cleanLog.print();
    //计算每个类别每天的点击量(day_categaryId,1)
    JavaPairDStream num = cleanLog.mapToPair(new PairFunction<ClickLog,String,Integer>(){
    	public Tuple2<String, Integer> call(ClickLog s) throws Exception {
            Tuple2<String, Integer> tuple2 = null;
            tuple2 = new Tuple2<>(s.gettime().substring(0,8),1);
            return tuple2;
        }
    }).reduceByKey(new Function2<Integer, Integer, Integer>(){
    	public Integer call(Integer v1, Integer v2) throws Exception {
            return v1 + v2;
        }
    });
    //将保存保存到hbase
    //foreachRDD对于每个RDD操作（JavaPairRDD），foreach对于每个元素操作（Tuple2）
    num.foreachRDD(new VoidFunction<JavaPairRDD<String,Integer>>(){
    	
		@Override
		public void call(JavaPairRDD<String,Integer> rdd) throws Exception {
			// TODO Auto-generated method stub
					rdd.foreach(new VoidFunction<Tuple2<String,Integer>>(){
						@Override
						public void call(Tuple2<String,Integer> pair) throws Exception {
							// TODO Auto-generated method stub
							CategaryClickCount temp = new CategaryClickCount(pair._1(),pair._2());
						    new CategaryClickCountDAO().save(temp);
				}	
			});	
		}
    });
    
    		
    //每个栏目下面从渠道过来的流量20171122_www.baidu.com_1 100 20171122_2（渠道）_1（类别） 100
    //categary_search_count   create "categary_search_count","info"
    //124.30.187.10	2017-11-20 00:39:26	"GET www/6 HTTP/1.0"
    // 	https:/www.sogou.com/web?qu=我的体育老师	302
    
    //启动StreamingContext作业
    sc.start();
    //等待作业完成
    sc.awaitTermination();
  
}
}
