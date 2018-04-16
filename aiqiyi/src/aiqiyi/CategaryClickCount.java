package aiqiyi;

public class CategaryClickCount {
	private String categaryID;
	private int clickCout;
	public CategaryClickCount(String categaryID, int clickCout){
		this.categaryID = categaryID;
		this.clickCout = clickCout;
	}
	
	public String getcategaryID(){
		return this.categaryID;
	}
	
	public int getclickCout(){
		return this.clickCout;
	}
	
	public void setcategaryID(String categaryID){
		this.categaryID = categaryID;
	}
	
	public void setclickCout(int clickCout){
		this.clickCout = clickCout;
	}
}
