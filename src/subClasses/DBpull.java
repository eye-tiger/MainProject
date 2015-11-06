package subClasses;

import subClasses.JSONhandler;

import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.google.gson.JsonObject;

import java.util.*;


/**
 * @author jay
 *
 */
public class DBpull {
	
	private Database db_static;		//This database connection connects to the student database that contains name, timetable, etc
	private Database db_dynamic;	//This db connection connects to the student db with current location, current class etc	
	private Database db_class;		//This db connection connects to the db containing all the class start, end times and locations
	
	//Defines the names of the 3 databases to connect to 
	private String dbStatic  = "static_user_info";	
	private String dbDynamic = "dynamic_user_info";
	private String dbClass   = "class_info";	
	
	/**
	 * @param client - The cloudant account to connect to 
	 * 
	 * The constructor initializes all db connections
	 */
	public DBpull( CloudantClient client ){
		//connects to the 3 databases to be used
		this.db_static  = client.database(dbStatic , false);
		this.db_dynamic = client.database(dbDynamic, false);
		this.db_class   = client.database(dbClass  , false);
	}

	/**
	 * @param id - The student id of the student being looked at
	 * @return An arraylist containing the student's timetable in order of 
	 *         occurence
	 *         
	 * Searches for the timetable of the student with the id id
	 */
	public ArrayList<String> getStudentTimetable( String id ){
		JSONhandler response = new JSONhandler(  this.db_static.find(JsonObject.class, id) );
		return response.toArray("user_timetable");
	}
	
	/**
	 * @param course - The course id
	 * @return A Hashmap containing the course's start time, end time and classroom location
	 * 
	 * Returns the class information such as start time, end time and location of the given course id
	 */
	public Map<String, String> getClassInfo( String course){
		HashMap<String, String> classInfo = new HashMap<String, String>();
		
		//Searches the db for the info about the course and returns the response
		JSONhandler response = new JSONhandler(  this.db_class.find(JsonObject.class, course) );
		
		//Obtains the necessary data and puts it into the map
		classInfo.put("class_time_start", response.toString("class_time_start") );
		classInfo.put("class_time_end", response.toString("class_time_end") );
		classInfo.put("class_location", response.toString("class_location") );
		return classInfo;
	}
	
	/**
	 * @param id - The student id of the student being looked at
	 * @return a HashMap containing the student's first name, last name, number of times they have been late
	 *         and absent
	 *         
	 * Searches the student DB to get the student's information and returns it in a hashmap
	 */
	public Map<String, String> getStudentInfo( String id ){
		HashMap<String, String> studentInfo = new HashMap<String, String>();
		
		//Searches the db for the info about the course and returns the response
		JSONhandler response = new JSONhandler(  this.db_static.find(JsonObject.class, id) );
		
		//Obtains the necessary data and puts it into the map
		studentInfo.put("user_first_name", response.toString("user_first_name") );
		studentInfo.put("user_last_name", response.toString("user_last_name") );
		studentInfo.put("user_number_lates", response.toString("user_number_lates") );
		studentInfo.put("number_of_attendances", response.toString("number_of_attendances") );
		return studentInfo;
	}
	
	/**
	 * @param id - The student id of the student being looked at
	 * @returns a HashMap containing the student's status, current location, current class
	 * 			and their attendance record for the day
	 * 
	 * Gets the student's current data and returns it as a Hashmap
	 */
	public Map<String, String> getCurrentStudentInfo( String id ){
		HashMap<String, String> studentInfo = new HashMap<String, String>();
		JSONhandler response = new JSONhandler(  this.db_dynamic.find(JsonObject.class, id) );
		
		//Obtains the necessary data and puts it into the map
		studentInfo.put("user_status", response.toString("user_status") );
		studentInfo.put("user_current_class", response.toString("user_current_class") );
		studentInfo.put("curr_location", response.toString("user_location") );
		studentInfo.put("daily_attendance", response.toString("user_daily_attendance") );
		return studentInfo;
	}
	
	//Tester
	public static void main( String [] args){	
		//Set environment variables
		String account = System.getenv("account");
		String pass = System.getenv("password");
    	CloudantClient client = new CloudantClient(account, account, pass);
    	DBpull test = new DBpull( client );
    	String get = test.getClassInfo("chem101").get("class_time_end");    	
    	System.out.println(get);
	}
	
	
}
