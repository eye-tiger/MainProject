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
 */
public class DBpull {
	
	private Database db_static;		//This database connection connects to the student database that contains name, timetable, etc
	private Database db_dynamic;	//This db connection connects to the student db with current location, current class etc	
	private Database db_class;		//This db connection connects to the db containing all the class start, end times and locations
	
	
	private JSONhandler static_info;		//Stores all the static info of the student which will be updated to send back to the db
	private JSONhandler dynamic_info;    //Stores the all of the dynamic info of the student which will be updated to send back to the db
	
	private String studentId;
	
	/**
	 * @param stat - database instance for the static_user_info
	 * @param dyna - database instance for the dynamic_user_info
	 * @param cla - database instance for the class_info
	 * @param studentId - unique student id
	 * The constructor initializes all db connections and gets all db info for a student
	 */
	protected DBpull( String studentId, Database stat, Database dyna, Database cla ){
		//connects to the 3 databases to be used
		this.db_static  = stat;
		this.db_dynamic = dyna;
		this.db_class   = cla;
		this.studentId = studentId;
	
		//creates two threads to get the student's dynamic and static info
		MultiPull stati = new MultiPull("static");
		MultiPull dynam = new MultiPull("dynamic");
		
		//starts the threads
		stati.start();
		dynam.start();
		
		try{
			//Waits for the two threads to finish getting the data before continuing
			stati.join();
			dynam.join();
		}
		catch( Exception e){ e.printStackTrace(); }
	}

	/**
	 * @return An arraylist containing the student's timetable in order of 
	 *         occurence      
	 * Searches for the timetable of the student with the id id
	 */
	protected ArrayList<String> getStudentTimetable(){
		return this.static_info.toArray("user_timetable");
	}
	
	/**
	 * @param course - The course id
	 * @return A Hashmap containing the course's start time, end time and classroom location
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
	 * @return a HashMap containing the student's first name, last name, number of times they have been late
	 *         and absent
	 * Searches the student DB to get the student's information and returns it in a hashmap
	 */
	protected Map<String, String> getStudentInfo(){
		HashMap<String, String> studentInfo = new HashMap<String, String>();
		
		//Obtains the necessary data and puts it into the map
		studentInfo.put("user_first_name", this.static_info.toString("user_first_name") );
		studentInfo.put("user_last_name", this.static_info.toString("user_last_name") );
		studentInfo.put("user_number_lates", this.static_info.toString("user_number_of_lates") );
		studentInfo.put("user_number_of_absences", this.static_info.toString("user_number_of_absences") );
		return studentInfo;
	}
	
	/**
	 * @returns a HashMap containing the student's status, current location, current class
	 * 			and their attendance record for the day
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
	
	//Getter method for setting static info
	protected JSONhandler getStatic_info() {
		return static_info;
	}
	
	//Setter method for setting static info
	protected void setStatic_info(JSONhandler info){
		this.static_info = info;
	}

	//Getter method for setting dynamic info
	protected JSONhandler getDynamic_info() {
		return dynamic_info;
	}
	
	//Setter method for setting dynamic info
	protected void setDynamic_info(JSONhandler info){
		this.dynamic_info = info;
	}
	
   //this class is responsible for the threads
   private class MultiPull extends Thread{
		private String dbtype; //stores the db from which to obtain the info
		
		MultiPull(String dbtype){
			this.dbtype = dbtype;
		}
		
		public void run( ){
					
			JSONhandler sync;
			
			if( this.dbtype == "static"){
				sync = new JSONhandler(  db_static.find(JsonObject.class, studentId) ); //gets student info from static db
				static_info = sync;
			}
			else{
				sync = new JSONhandler(  db_dynamic.find(JsonObject.class, studentId) ); //gets student info from dynamic db
				dynamic_info = sync;
			}
			return;
		}
   }
   
   
	public static void main(String args[]){
		
		String account = System.getenv("account");
		String pass = System.getenv("password");
    	CloudantClient client = new CloudantClient(account, account, pass);
    	Database stat = client.database("static_user_info", false);
    	Database dynamic = client.database("dynamic_user_info", false);
    	Database classs = client.database("class_info", false);
    	
    	DBpull student = new DBpull("BruceWayne", stat, dynamic, classs);
    	
    	System.out.println(student.getDynamicStudentInfo().get("user_status") );
	}
}
