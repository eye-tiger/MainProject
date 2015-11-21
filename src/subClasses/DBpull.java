package subClasses;

import subClasses.JSONhandler;

import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.google.gson.JsonObject;

import java.util.*;


/**
 * @author jason
 *
 *This class provides all the necessary operations to read from the DB given using a student's id 
 *and course. Always create different instances of this class for different students. One instance can only
 *be used to access one student information only
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
	
	private JSONhandler static_info;		//Stores all the static info of the student which will be updated to send back to the db
	private JSONhandler dynamic_info;    //Stores the all of the dynamic info of the student which will be updated to send back to the db
	
	private String studentId;	//Stores the student id
	
	
	/**
	 * @param client - The cloudant account to connect to 
	 * 
	 * The constructor initializes all db connections
	 */
	protected DBpull( CloudantClient client, String studentId ){
		this.studentId = studentId;
		
		//connects to the 3 databases to be used
		this.db_static  = client.database(dbStatic , false);
		this.db_dynamic = client.database(dbDynamic, false);
		this.db_class   = client.database(dbClass  , false);
		
		JSONhandler responseS = new JSONhandler(  this.db_static.find(JsonObject.class, this.studentId) );	
		this.static_info = responseS;
		
		JSONhandler responseD = new JSONhandler(  this.db_dynamic.find(JsonObject.class, this.studentId) );
		this.dynamic_info = responseD;
	}

	/**
	 * 
	 * @return An arraylist containing the student's timetable in order of 
	 *         occurence
	 *         
	 * Searches for the timetable of the student with the id id
	 */
	protected ArrayList<String> getStudentTimetable(){
		return this.static_info.toArray("user_timetable");
	}
	
	/**
	 * @param course - The course id
	 * @return A Hashmap containing the course's start time, end time and classroom location
	 * 
	 * Returns the class information such as start time, end time and location of the given course id
	 */
	protected Map<String, String> getClassInfo( String course){
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
	 * 
	 * @return a HashMap containing the student's first name, last name, number of times they have been late
	 *         and absent
	 *         
	 * Searches the student DB to get the student's information and returns it in a hashmap
	 */
	protected Map<String, String> getStudentInfo(){
		HashMap<String, String> studentInfo = new HashMap<String, String>();
		
		//Obtains the necessary data and puts it into the map
		studentInfo.put("user_first_name", this.static_info.toString("user_first_name") );
		studentInfo.put("user_last_name", this.static_info.toString("user_last_name") );
		studentInfo.put("user_number_lates", this.static_info.toString("user_number_lates") );
		studentInfo.put("user_number_of_absences", this.static_info.toString("user_number_of_absences") );
		return studentInfo;
	}
	
	/**
	 *
	 * @returns a HashMap containing the student's status, current location, current class
	 * 			and their attendance record for the day
	 * 
	 * Gets the student's current data and returns it as a Hashmap
	 */
	protected Map<String, String> getDynamicStudentInfo(){
		HashMap<String, String> studentInfo = new HashMap<String, String>();
		
		//Obtains the necessary data and puts it into the map
		studentInfo.put("user_status", this.dynamic_info .toString("user_status") );
		studentInfo.put("user_current_class", this.dynamic_info .toString("user_current_class") );
		studentInfo.put("user_location", this.dynamic_info .toString("user_location") );
		studentInfo.put("user_daily_attendance", this.dynamic_info .toString("user_daily_attendance") );
		return studentInfo;
	}
	
	protected ArrayList<String> getDailyAttendance(){
		return this.dynamic_info.toArray("user_daily_attendance");
	}
	
	//Getters and Setters methods
	protected JSONhandler getStatic_info() {
		return static_info;
	}

	protected JSONhandler getDynamic_info() {
		return dynamic_info;
	}

}
