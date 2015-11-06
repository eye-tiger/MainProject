package subClasses;

import java.util.ArrayList;
import java.util.Arrays;

import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.Response;
import com.google.gson.JsonObject;

import subClasses.JSONhandler;

public class DBpush {
	
	private Database db_static;		//This database connection connects to the student database that contains name, timetable, etc
	private Database db_dynamic;	//This db connection connects to the student db with current location, current class etc	
	private Database db_class;		//This db connection connects to the db containing all the class start, end times and locations
	
	//Defines the names of the 3 databases to connect to 
	private String dbStatic  = "static_user_info";	
	private String dbDynamic = "dynamic_user_info";
	private String dbClass   = "class_info";	
	
	private String studentId;
	
	/**
	 * @param client - The cloudant account to connect to 
	 * 
	 * The constructor initializes all db connections
	 */
	public DBpush( CloudantClient client, String studentid ){
		//connects to the 3 databases to be used
		this.db_static  = client.database(dbStatic , false);
		this.db_dynamic = client.database(dbDynamic, false);
		this.db_class   = client.database(dbClass  , false);
		
		this.studentId = studentid;
	}
	
	public void updateDailyAttendance(ArrayList<String> update, String field, JSONhandler wholePage){
		//Searches the db for the info about the course and returns the response
		JSONhandler response = new JSONhandler(wholePage);
		
		response.addData(field, update);
		
		response.addData("_id", this.studentId);
		db_static.update(response.instance);
	}
	
	public void updateLocation(ArrayList<String> update){
		
	}
	
	public void updateStatus(){
		
	}
	
	public void updateTotalLates(){
		
	}
	
	public void updateTotalAbsences(){
		
	}
	
	
	
	
	public static void main( String args[] ){
		String account = System.getenv("account");
		String pass = System.getenv("password");
    	CloudantClient client = new CloudantClient(account, account, pass);
    	DBpull test = new DBpull( client, "12345" );
    	String get = test.getClassInfo("chem101").get("class_time_end");   
    	
    	ArrayList<String> update = new ArrayList<String>(); 
    	test.getStudentTimetable();
    	
    	update.add("PE");
    	update.add("bio");
    	update.add("math");
    	update.add("testing");
    	DBpush te = new DBpush( client, "12345" );
    	te.updateDailyAttendance(update, "user_timetable", test.getStatic_info());
    	System.out.println(get);
	}
	
	
}
