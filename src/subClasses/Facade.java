package subClasses;

import java.util.ArrayList;
import java.util.Map;

import com.cloudant.client.api.CloudantClient;

//main class to use
public class Facade {

	private CloudantClient client;
	private DBpull student;
	private String id;
	
	public Facade(String id){
		String account = System.getenv("account");
		String pass = System.getenv("password");
    	this.client = new CloudantClient(account, account, pass);
    	this.student = new DBpull(this.client, id);
	}
	
	public String getFirstName(){
		Map<String, String> info = this.student.getStudentInfo();		
		return info.get("user_first_name");
	}
	
	public String getLastName(){
		Map<String, String> info = this.student.getStudentInfo();			
		return info.get("user_last_name");
	}
	
	public ArrayList<String> getTimetable(){
		return this.student.getStudentTimetable();
	}
	
	private void updateStudentInstance(){
		
	}
	
	public static void main(String args[]){
		Facade test = new Facade("12345");
		
		System.out.println(test.getFirstName());
		System.out.println(test.getLastName());
		System.out.println(test.getTimetable().get(3));
	}
	
}
