package aiqiyi;
import java.text.ParseException;
import java.util.Date;
import org.apache.commons.lang3.time.FastDateFormat;
/**
 * 时间格式转换
 * @author 6
 * 2018-04-11 10:23:45 => 20180411
 */
public class DataUtil {
	private FastDateFormat YYYYMMDDHHMMSS_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
	 private  FastDateFormat TAG_FORMAT = FastDateFormat.getInstance("yyyyMMdd");
	 
	 /**
	  * 把时间转化成时间戳
	 * @throws ParseException 
	  */
	 public long getTime(String time) throws ParseException{
		 return YYYYMMDDHHMMSS_FORMAT.parse(time).getTime();
	 }
	 
	 public String parseToMin(String time) throws ParseException{
		 return TAG_FORMAT.format(new Date(getTime(time)));
	 }
	 
	 /*Test
	 public static void main(String args[]) throws ParseException{
		 System.out.println(parseToMin("2018-04-11 10:23:45"));
	 }
	 */
}
