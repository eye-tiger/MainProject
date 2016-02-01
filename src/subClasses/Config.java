package subClasses;

import com.cloudant.client.api.Database;

public class Config {
	
	private Database config;
	private JSONhandler data;
	
	public Config(Database admin){
		
	}
	
	public String startTime(){
		return "";
	}
	
	public String endTime(){
		return "";
	}
	
	public int periodLength(){
		return 0;
	}
	
	public int num_periods(){
		return 0;
	}
	
	public int getCurrentPeriod(String time){
		return 0;
	}
}
