package aiqiyi;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;

import scala.Unit;
import scala.collection.mutable.ListBuffer;

public class CategaryClickCountDAO {
	public String tableName = "category_clickcount";
	public String cf = "info";
	public String qualifer = "click_count";
	
	/**
	 * 保存数据
	 * @throws IOException 
	 */
	
	public void save(CategaryClickCount els) throws IOException{
		//获得表名
		HTable table = HBaseUtils.getInstance().getHtable(tableName);
		
			table.incrementColumnValue((els.getcategaryID()).getBytes(), cf.getBytes(), qualifer.getBytes(), els.getclickCout());
		
	
}

	public long count(String day_categary) throws IOException{
		HTable table = HBaseUtils.getInstance().getHtable(tableName);
		Get get = new Get(day_categary.getBytes());
		 Object value = table.get(get).getValue(cf.getBytes(), qualifer.getBytes());
		 if(value == null){
			 return 0;
		 }
		 else{
			 return Long.valueOf(value.toString());
		 }
	}
	
	
}
