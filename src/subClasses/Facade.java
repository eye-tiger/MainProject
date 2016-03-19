package subClasses;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;

//main class to use
public class Facade {

	private CloudantClient client;
	private Database db_static;		
	private Database db_dynamic;	
	private Database db_config;
	private DBpull student;
	/**
	 * @param id - Student id
	 * 
	 * Basically opens up the student's account on the database
	 */
	public Facade(String id){
		String account = System.getenv("account");
		String pass = System.getenv("password");
    	this.client = new CloudantClient(account, account, pass);
    	
    	this.db_static = client.database("static_user_info", false);
    	this.db_dynamic = client.database("dynamic_user_info", false);
    	this.db_config = client.database("configurations", false);
    	
    	this.student = new DBpull(id, this.db_static, this.db_dynamic, this.db_config);
	}
	
	/**
	 * @return - Student's timetable name
	 */
	public ArrayList<String> getTimetable(){
		return this.student.getStudentTimetable();
	}
	

	public void updateStudentInstance( int hour, int min, String location) {
		DBpush update = new DBpush(this.db_dynamic, this.db_static, this.student.getDynamic_info(), this.student.getStatic_info());
	
		ArrayList<String> timetable = this.student.getStudentTimetable();
		
		
		int minute = 4;
		
		if( minute%20 > 5 && minute%20 <= 10 ){
			//attend.add("present");
			update.updateStatus("present");
		}
		else {
			//attend.add("late");
			update.updateStatus("late");
			update.updateTotalLates();
		}
		update.updateLocation(location);
		//update.updateCurrentClass(course);

		update.commitChanges();
	}
	
	private int period(){
	
		
		return 1;
	}
	
	public static void main( String args[]){
		Facade test = new Facade("BruceWayne");
		//test.updateStudentInstance("pse321", "bio");
	}
}
