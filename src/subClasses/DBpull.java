package subClasses;

import subClasses.JSONhandler;

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
    private Database db_config;	
	
	private JSONhandler static_info;		//Stores all the static info of the student which will be updated to send back to the db
	private JSONhandler dynamic_info;    //Stores the all of the dynamic info of the student which will be updated to send back to the db
	private JSONhandler config_info;
	
	private String studentId;
	
	/**
	 * @param stat - database instance for the static_user_info
	 * @param dyna - database instance for the dynamic_user_info
	 * @param con - database instance for the config_info
	 * @param studentId - unique student id
	 * The constructor initializes all db connections and gets all db info for a student
	 */
	protected DBpull( String studentId, Database stat, Database dyna, Database con ){
		//connects to the 3 databases to be used
		this.db_static  = stat;
		this.db_dynamic = dyna;
		this.db_config   = con;
		this.studentId = studentId;
	
		//creates two threads to get the student's dynamic and static info
		MultiPull stati = new MultiPull("static");
		MultiPull dynam = new MultiPull("dynamic");
		MultiPull confi = new MultiPull("config");
		
		//starts the threads
		stati.start();
		dynam.start();
		confi.start();
		
		try{
			//Waits for the two threads to finish getting the data before continuing
			stati.join();
			dynam.join();
			confi.join();
		}
		catch( Exception e){ e.printStackTrace(); }
	}
	
	/**
	 * @return a HashMap containing the student's first name, last name, number of times they have been late
	 *         and absent
	 * Searches the student DB to get the student's information and returns it in a hashmap
	 */
	protected Map<String, String> getStudentInfo(){
		HashMap<String, String> studentInfo = new HashMap<String, String>();
		
		//Obtains the necessary data and puts it into the map
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
		return studentInfo;
	}
	
	/**
	 * @return - An arraylist of the list of classes that the student is currently taking. All four 
	 * 		     in the order of their time
	 */
	protected ArrayList<String> getClasses(){
		ArrayList<String> all = static_info.toArray("user_timetable");	//gets the students timetable
		ArrayList<String> result = new ArrayList<String>();				//stores the result
		int size = all.size();											//gets the size of the timetable
		
		//adds all classes to the array
		//classes are even
		for(int i = 0; i < size; i=i+2){
			result.add( all.get(i) );
		}
		return result;
	}
	
	/**
	 * @return - An arraylist of the list of class locations of all the classes that the student is currently taking. All four 
	 * 		     in the order of their time
	 */
	protected ArrayList<String> getClassLocation(){
		ArrayList<String> all = static_info.toArray("user_timetable");	//gets the students timetable
		ArrayList<String> result = new ArrayList<String>();				//stores the result
		int size = all.size();											//gets the size of the timetable
		
		//adds all class locations to the array
		//locations are odd
		for(int i = 1; i < size; i=i+2){
			result.add( all.get(i) );
		}
		return result;
	}
	
	//Getter method for getting static info
	protected JSONhandler getStatic_info() {
		return static_info;
	}

	//Getter method for getting dynamic info
	protected JSONhandler getDynamic_info() {
		return dynamic_info;
	}
	
	//Getter method for getting the config info
	protected JSONhandler getConfig_info() {
		return config_info;
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
			else if(this.dbtype =="dynamic"){
				sync = new JSONhandler(  db_dynamic.find(JsonObject.class, studentId) ); //gets student info from dynamic db
				dynamic_info = sync;
			}
			else{
				sync = new JSONhandler( db_config.find(JsonObject.class, "configurations") ); //gets the config info from the config db
				config_info = sync;
			}
			return;
		}
   }
}
