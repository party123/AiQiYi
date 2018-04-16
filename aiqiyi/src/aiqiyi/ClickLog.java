package aiqiyi;

public class ClickLog {
	private String ip;
	private String time;
	private int categaryId;
	private String refer;
	private int statusCode;
	public ClickLog(String ip, String time, int categaryId, String refer,int statusCode){
		this.ip = ip;
		this.time = time;
		this.categaryId = categaryId;
		this.refer = refer;
		this.statusCode = statusCode;
	}
	
	public int getcategaryId(){
		return this.categaryId;
	}
	
	public String gettime(){
		return this.time;
	}
}
