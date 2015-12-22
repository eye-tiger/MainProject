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
	
	private JSONhandler static_info;		//Stores all the static info of the student which will be updated to send back to the db
	private JSONhandler dynamic_info;    //Stores the all of the dynamic info of the student which will be updated to send back to the db
		
	/**
	 * @param client - The cloudant account to connect to 
	 * @param dynamic - the dynamic data pulled from the DB by the DBpull class. It is obtained using the getDynamic_info method
	 * 					of the DBpull class
	 * @param static - The static data pulled from the DB. Similiar method to obtain it from the DBpull class using a getter method
	 * The constructor initializes all db connections with the data provided by the DBpull class
	 */
	public DBpush( Database dynm, Database sta, JSONhandler dynamic, JSONhandler stat ){
		//connects to the 3 databases to be used
		this.db_static  = sta;
		this.db_dynamic = dynm;
		
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
	 * Updates the current student's current class 
	 */
	public void updateCurrentClass(String course){
		this.dynamic_info.addData("user_current_class", course);
	}
	
	/**
	 * @param update - The current classroom location of the student
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
	public void updateTotalLates(){
		int late = Integer.parseInt(this.static_info.toString("user_number_of_lates"));
		late++;
		this.static_info.addData("user_number_of_lates", "" + late + "");
	}
	
	/**
	 * @param absents - the total number of times a student has been absent
	 */
	public void updateTotalAbsences(){
		int absents = Integer.parseInt(this.static_info.toString("user_number_of_absences"));
		absents++;
		this.static_info.addData("user_number_of_absences", ""+absents+"");
	}
	
	/**
	 * THIS METHOD IS CALLED AFTER ALL UPDATES HAVE BEEN APPLIED TO A STUDENT. ONCE THIS METHOD IS CALLED, THERE CAN BE NO
	 * FURTHER UPDATES APPLIED USING THIS INSTANCE OF DBPUSH OR THE INSTANCE OF DBPULL FROM WHICH THE DATA WAS OBTAINED. BOTH
	 * INSTANCES BECOME ESSENTIALLY USELESS AND MUST NOT BE USED AGAIN.
	 * 
	 * This method SHOULD only be called once for any given instance of DBpush
	 */
	public void commitChanges(){
		
		//Creates two threads to update both the dynamic and static db at the same time
		MultiPush stat = new MultiPush("static");
		MultiPush dyna = new MultiPush("dynamic");
		
		//starts the two threads and hence update process
		stat.start();
		dyna.start();
		
		try{
			//waits for the two threads to end
			stat.join();
			dyna.join();
		}
		catch(Exception e){	System.out.println("Problem!!!!!!!!!!!!!!!!");	}
	}
	
	//this class is responsible for the threads
	private class MultiPush extends Thread{
		private String dbtype;	//stores the type of db to update
		
		MultiPush(String dbtype){
			this.dbtype = dbtype;
		}
		
		public void run(){
			if( this.dbtype == "static"){
				db_static.update(static_info.instance);
			}
			else{
				db_dynamic.update(dynamic_info.instance);
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
    	
    	System.out.println(student.getStudentTimetable().get(0) );
    	
    	DBpush stu = new DBpush(dynamic, stat, student.getDynamic_info(), student.getStatic_info() );
    	stu.updateLocation("hello");
    	stu.updateTotalAbsences();
    	stu.updateStatus("present");
    	stu.commitChanges();
	}	
	
}
