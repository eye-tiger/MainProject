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
	private Database db_class;
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
    	this.db_class = client.database("class_info", false);
    	
    	this.student = new DBpull(id, this.db_static, this.db_dynamic, this.db_class);
	}
	
	/**
	 * @return - Student's first name
	 */
	public String getFirstName(){
		Map<String, String> info = this.student.getStudentInfo();		
		return info.get("user_first_name");
	}
	
	/**
	 * @return - Student's last name
	 * 
	 */
	public String getLastName(){
		Map<String, String> info = this.student.getStudentInfo();			
		return info.get("user_last_name");
	}
	
	/**
	 * @return - Student's timetable name
	 */
	public ArrayList<String> getTimetable(){
		return this.student.getStudentTimetable();
	}
	

	public void updateStudentInstance( String classID, String course) {
		DBpush update = new DBpush(this.db_dynamic, this.db_static, this.student.getDynamic_info(), this.student.getStatic_info());
	
		ArrayList<String> timetable = this.student.getStudentTimetable();
		
		
		Calendar time = Calendar.getInstance();
		//int hour = time.get(Calendar.HOUR_OF_DAY);
		int minute = time.get(Calendar.MINUTE);
		
		if( minute%20 > 5 && minute%20 <= 10 ){
			//attend.add("present");
			update.updateStatus("present");
		}
		else {
			//attend.add("late");
			update.updateStatus("late");
			update.updateTotalLates();
		}
		update.updateLocation(classID);
		update.updateCurrentClass(course);

		update.commitChanges();
	}
	
	private int period(){
	
		Calendar time = Calendar.getInstance();
		int hour = time.get(Calendar.HOUR_OF_DAY);
		int minute = time.get(Calendar.MINUTE);
		
		return 1;
	}
	
	public static void main( String args[]){
		Facade test = new Facade("BruceWayne");
		test.updateStudentInstance("pse321", "bio");
	}
}
