package subClasses;

import subClasses.JSONhandler;

import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.google.gson.JsonObject;

import java.util.*;


public class DBpull {
	
	private Database db_static;
	private Database db_dynamic;
	private Database db_class;
	
	private String dbStatic  = "static_user_info";
	private String dbDynamic = "dynamic_user_info";
	private String dbClass   = "class_info";	
	
	public DBpull( CloudantClient client){
		this.db_static  = client.database(dbStatic , false);
		this.db_dynamic = client.database(dbDynamic, false);
		this.db_class   = client.database(dbClass  , false);
	}

	public ArrayList<String> getStudentTimetable( String id ){
		JSONhandler response = new JSONhandler(  this.db_static.find(JsonObject.class, id) );
		return response.toArray("user_timetable");
	}
	
	public Map<String, String> getClassInfo( String course){
		HashMap<String, String> classInfo = new HashMap<String, String>();
		JSONhandler response = new JSONhandler(  this.db_class.find(JsonObject.class, course) );
		classInfo.put("start", response.toString("class_time_start") );
		classInfo.put("end", response.toString("class_time_end") );
		classInfo.put("location", response.toString("class_location") );
		return classInfo;
	}
	
	public Map<String, String> getStudentInfo( String id ){
		HashMap<String, String> studentInfo = new HashMap<String, String>();
		JSONhandler response = new JSONhandler(  this.db_static.find(JsonObject.class, id) );
		studentInfo.put("firstName", response.toString("user_first_name") );
		studentInfo.put("lastName", response.toString("user_last_name") );
		studentInfo.put("lates", response.toString("user_number_lates") );
		studentInfo.put("attendances", response.toString("number_of_attendances") );
		return studentInfo;
	}
	
	public Map<String, String> getCurrentStudentInfo( String id ){
		HashMap<String, String> studentInfo = new HashMap<String, String>();
		JSONhandler response = new JSONhandler(  this.db_dynamic.find(JsonObject.class, id) );
		studentInfo.put("user_status", response.toString("user_status") );
		studentInfo.put("user_current_class", response.toString("user_current_class") );
		studentInfo.put("curr_location", response.toString("user_location") );
		studentInfo.put("daily_attendance", response.toString("user_daily_attendance") );
		return studentInfo;
	}
	
	public static void main( String [] args){	
		//Set environment variables
		String account = System.getenv("account");
		String pass = System.getenv("password");
    	CloudantClient client = new CloudantClient(account, account, pass);
    	DBpull test = new DBpull( client );
    	String get = test.getStudentInfo("12345").get("firstName");    	
    	System.out.println(get);
	}
	
	
}
