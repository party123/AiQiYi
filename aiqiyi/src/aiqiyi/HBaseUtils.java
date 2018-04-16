package aiqiyi;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

public class HBaseUtils {

    HBaseAdmin admin = null;
    Configuration configration = null;
    //单例模式，创建私有构造方法
    private HBaseUtils(){
    	//配置信息
        configration = new Configuration();
        configration.set("hbase.zookeeper.quorum", "s201:2181");
        configration.set("hbase.rootdir", "hdfs://s201/hbase");
        try {
            admin = new HBaseAdmin(configration);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //线程安全创建单例模式实例
    private static HBaseUtils instance = null;

    public static synchronized HBaseUtils getInstance() {
        if (null == instance) {
            instance = new HBaseUtils();
        }
        return instance;
    }

    /**
     * 根据表名获取htable实例
     * @param tableName
     * @return
     */
    public  HTable getHtable(String tableName){
        HTable table = null;
        try {
            table = new HTable(configration,tableName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return table;

    }

    /**
     * 添加数据到hbase里面
     * @param tableName 表名
     * @param rowKey 对应key的值
     * @param cf    hbase列簇
     * @param colum hbase对应的列
     * @param value hbase对应的值
     */
    
    public void put(String tableName, String rowKey, String cf, String colum, String value){
        HTable table = getHtable(tableName);
        Put put = new Put(Bytes.toBytes(rowKey));
        put.add(Bytes.toBytes(cf),Bytes.toBytes(colum),Bytes.toBytes(value));
        try {
            table.put(put);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String tableName = "category_clickcount";
        String rowkey="20171122_1";
        String cf = "info";
        String colum = "cagegory_click_count";
        String value = "100";

        HBaseUtils.getInstance().put(tableName,rowkey,cf,colum,value);
    }


}
