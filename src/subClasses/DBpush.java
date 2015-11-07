package subClasses;

import java.util.ArrayList;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import subClasses.JSONhandler;

/**
 * @author jason
 *
 *This class provides all the necessary write operations to the Database. One instance can only update one student's information
 *This class must be used in conjunction with the DBpull class. Specifically, it uses the data returned from the getDynamic_info
 *and the getStatic_info of the DBpull to update a given student's data
 */
public class DBpush {
	
	private Database db_static;		//This database connection connects to the student database that contains name, timetable, etc
	private Database db_dynamic;	//This db connection connects to the student db with current location, current class etc	
	//private Database db_class;		//This db connection connects to the db containing all the class start, end times and locations
	
	//Defines the names of the 3 databases to connect to 
	private String dbStatic  = "static_user_info";	
	private String dbDynamic = "dynamic_user_info";
	//private String dbClass   = "class_info";	
	
	private JSONhandler static_info;		//Stores all the static info of the student which will be updated to send back to the db
	private JSONhandler dynamic_info;    //Stores the all of the dynamic info of the student which will be updated to send back to the db
		
	/**
	 * @param client - The cloudant account to connect to 
	 * @param dynamic - the dynamic data pulled from the DB by the DBpull class. It is obtained using the getDynamic_info method
	 * 					of the DBpull class
	 * @param static - The static data pulled from the DB. Similiar method to obtain it from the DBpull class using a getter method
	 * 
	 * The constructor initializes all db connections with the data provided by the DBpull class
	 */
	public DBpush( CloudantClient client, JSONhandler dynamic, JSONhandler stat ){
		//connects to the 3 databases to be used
		this.db_static  = client.database(dbStatic , false);
		this.db_dynamic = client.database(dbDynamic, false);
		//this.db_class   = client.database(dbClass  , false);
		
		this.static_info = new JSONhandler(stat);		//sets the static data
		this.dynamic_info = new JSONhandler(dynamic);	//sets the dynamic data
	}
	
	/**
	 * @param update - An arraylist containing a student's attendance record of being late, absent or present for a given class.
	 * 				   The indexes of the array indicates the attendance for a specific class	
	 */
	public void updateDailyAttendance(ArrayList<String> update){
		this.dynamic_info.addData("user_daily_attendance", update);    //Updates the daily attendance for the student
	}
	
	/**
	 * @param course - The class the student currently has
	 * 
	 * Updates the current student's current class 
	 */
	public void updateCurrentClass(String course){
		this.dynamic_info.addData("user_current_class", course);
	}
	
	/**
	 * @param update - The current classroom location of the student
	 * 
	 * updates the students last registered location
	 */
	public void updateLocation(String update){
		this.dynamic_info.addData("user_location", update);	//updates the dynamic page
	}
	
	/**
	 * @param status - the status of the current student. For example sick, vacation etc
	 */
	public void updateStatus(String status){
		this.dynamic_info.addData("user_status", status);
	}
	
	/**
	 * @param lates - the total number of times a student has been late 
	 */
	public void updateTotalLates(int lates){
		this.static_info.addData("user_number_lates", "" + lates + "");
	}
	
	/**
	 * @param absents - the total number of times a student has been absent
	 */
	public void updateTotalAbsences(int absents){
		this.static_info.addData("number_of_attendances", ""+absents+"");
	}
	
	/**
	 * THIS METHOD IS CALLED AFTER ALL UPDATES HAVE BEEN APPLIED TO A STUDENT. ONCE THIS METHOD IS CALLED, THERE CAN BE NO
	 * FURTHER UPDATES APPLIED USING THIS INSTANCE OF DBPUSH OR THE INSTANCE OF DBPULL FROM WHICH THE DATA WAS OBTAINED. BOTH
	 * INSTANCES BECOME ESSENTIALLY USELESS AND MUST NOT BE USED AGAIN.
	 * 
	 * This method SHOULD only be called once for any given instance of DBpush
	 */
	public void commitChanges(){
		this.db_dynamic.update(this.dynamic_info.instance);
		this.db_static.update(this.static_info.instance);
	}
	
	//tester for both dbpull and dbpush class
	//
	public static void main( String args[] ){
		String account = System.getenv("account");
		String pass = System.getenv("password");
    	CloudantClient client = new CloudantClient(account, account, pass);
    	
    	DBpull test = new DBpull( client, "12345" );
    	
    	//gets and prints end time for chem101
    	String get = test.getClassInfo("chem101").get("class_time_end");
    	System.out.println(get);

    	
    	//updates student's attendance
    	ArrayList<String> update = new ArrayList<String>();     	
    	update.add("present");
    	update.add("present");
    	update.add("absent");
    	
    	//updates the databases
    	DBpush te = new DBpush( client, test.getDynamic_info(), test.getStatic_info() );
    	te.updateDailyAttendance(update );
    	te.updateCurrentClass("chem101");
    	te.updateLocation("room3430");
    	te.updateStatus("vacation");
    	te.updateTotalLates(5);
    	te.updateTotalAbsences(20);
    	
    	te.commitChanges();		//commits changes
    	
	}
	
	
}
