package subClasses;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import com.cloudant.client.api.CloudantClient;

//main class to use
public class Facade {

	private CloudantClient client;
	private DBpull student;
	/**
	 * @param id - Student id
	 * 
	 * Basically opens up the student's account on the database
	 */
	public Facade(String id){
		String account = "eyeofthetiger";//System.getenv("account");
		String pass = "eng40000";//System.getenv("password");
    	this.client = new CloudantClient(account, account, pass);
    	this.student = new DBpull(this.client, id);
	}
	
	/**
	 * @return - Student's first name
	 * 
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
		DBpush update = new DBpush(this.client, this.student.getDynamic_info(), this.student.getStatic_info());
	
		ArrayList<String> attend = this.student.getDailyAttendance();
		
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
		update.updateDailyAttendance(attend);
		update.updateCurrentClass(course);

		update.commitChanges();
		
	}
	
	public static void main( String args[]){
		Facade test = new Facade("JasonKuffour");
		
		test.updateStudentInstance("pse321", "bio");
	}
}
