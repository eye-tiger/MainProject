package subClasses;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;

//main class to use
public class Facade {

	private CloudantClient client;
	private Database db_static;		
	private Database db_dynamic;	
	private Database db_config;
	private DBpull student;
	private String status;		//stores the student status i.e whether they are late, present or absent
	private boolean check;
	
	/**
	 * @param id - Student id
	 * Basically opens up the student's account on the database
	 */
	public Facade(String id){
		String account = System.getenv("account");
		String pass = System.getenv("password");
		
    	this.client = new CloudantClient(account, account, pass);    	
    	this.db_static = client.database("static_user_info", false);
    	this.db_dynamic = client.database("dynamic_user_info", false);
    	this.db_config = client.database("config_info", false);
    	
    	if( this.db_static.contains(id) ){
        	this.student = new DBpull(id, this.db_static, this.db_dynamic, this.db_config);
        	check = true;
    	}
    	else{
    		check = false;
    	}
	}

	public boolean check(){
		return check;
	}
	
	/**
	 * @param hour - the hour the tag was scanned
	 * @param min  - the minute the tag was scanned
	 * @param location - the location where the tag was scanned
	 * @param indicator - whether the person is leaving or entering
	 */
	public void updateStudentInstance( int hour, int min, String location, String indicator) {
		DBpush update = new DBpush(this.db_dynamic, this.db_static, this.student.getDynamic_info(), this.student.getStatic_info());
	
		ArrayList<String> timetableLoc = this.student.getClassLocation();	//gets all the students class locations
		ArrayList<String> timetableCla = this.student.getClasses();			//gets all the students periods
		ArrayList<String> userStatus = this.student.getUserStatus();
		
		update.updateLocation(location);		//updates their location to the db
		int period = this.getPeriod(hour, min);	//gets period info based on time their tag was scanned
		
		if( period == 0){ //lunch time
			update.updateCurrentClass("Lunch");	//update current class of student with what they have
			this.update(userStatus, hour, min, indicator, "-");
			update.updateStatus(userStatus);
		}
		else if( period == -1){ //not school hours
		
		}
		else{
			update.updateCurrentClass(timetableCla.get(period-1));	//update current class of student with what they have
			
			//checks if the student belongs to that class
			if( timetableLoc.get(period-1).equals(location) ){
				//if person is entering
				this.update(userStatus, hour, min, indicator, this.status);
				update.updateStatus(userStatus);
			}
			else{
				this.update(userStatus, hour, min, indicator, "-");
				update.updateStatus(userStatus);
			}
		}
		update.commitChanges();
	}
	
	private ArrayList<String> update(ArrayList<String> userStatus, int hour, int min, String indicator, String status){
		//if person is entering
		if( indicator.equals("entry")){		
			userStatus.set(0, status);
			String minu = (min < 10) ? "0" + min : min + "";
			userStatus.set(1, hour + ":" + minu);
		}
		else{
			//if person is leaving
			userStatus.set(0, status);
			String minu = (min < 10) ? "0" + min : min + "";
			userStatus.set(2, hour + ":" + minu);
		}
		
		return userStatus;
	}
	
	/**
	 * @param hour - the hour the tag was scanned
	 * @param minute - the minute the tag was scanned
	 * @return - returns what period it currently is
	 */
	private int getPeriod(int hour, int minute){
		
		String time = hour + ":" + minute;				//converts scanned tag time to proper format
		DateFormat sdf = new SimpleDateFormat("hh:mm");	//the format convert the string time to 
		int period = 0;									//return value for the period
		
		Date date = new Date();	//the time the tag was scanned
		Date per1 = new Date(); //the period 1 start time
		Date per2 = new Date(); //the period 2 start time
		Date per3 = new Date(); //the period 3 start time
		Date per4 = new Date(); //the period 4 start time
		Date lunc = new Date(); //the lunch start time
		Date sEnd = new Date(); //the school end time
		 
		try {
			//converts all db time values to actual time objects for easy comparison
			date = sdf.parse(time);
			per1 = sdf.parse( this.student.getConfig_info().toString("first_period_start") );
			per2 = sdf.parse( this.student.getConfig_info().toString("second_period_start") );
			per3 = sdf.parse( this.student.getConfig_info().toString("third_period_start") );
			per4 = sdf.parse( this.student.getConfig_info().toString("fourth_period_start") );
			lunc = sdf.parse( this.student.getConfig_info().toString("lunch_start") );
			sEnd = sdf.parse( this.student.getConfig_info().toString("school_end") );
		} catch (ParseException e) {}
		
		
		if( (date.compareTo(per1) == 1 && date.compareTo(per2) == -1 )   || date.compareTo(per1) == 0 ){	//first period
			period = 1;	//between the period 1 and 2
		}
		else if( date.compareTo(per2) == 1 && date.compareTo(lunc) == -1 || date.compareTo(per2) == 0){ //second period
			period = 2; //between the period 2 and lunch
		}
		else if( date.compareTo(lunc) == 1 && date.compareTo(per3) == -1 || date.compareTo(lunc) == 0){ //lunch period
			//between the lunch and period 3
		}
		else if( date.compareTo(per3) == 1 && date.compareTo(per4) == -1 || date.compareTo(per3) == 0){ //third period
			period = 3; //between the period 3 and 4
		}
		else if(date.compareTo(per4) == 1 && date.compareTo(sEnd) == -1  || date.compareTo(per4) == 0){ //fourth period
			period = 4; //between the period 4 and school end
		}
		else{
			period = -1;
		}
		
		//person is on time if scanned when period starts
		if(date.compareTo(per1) == 0 || date.compareTo(per2) == 0 || date.compareTo(per3) == 0 || date.compareTo(per4) == 0 ){
			this.status = "PRESENT";
		}else{
			this.status = "LATE";
		}
		return period;
	}
	
	public static void main( String args[]){
		Facade test = new Facade("80:ea:ca:00:42:27");
		test.updateStudentInstance(1, 9, "SC301", "exit");
	}
}
