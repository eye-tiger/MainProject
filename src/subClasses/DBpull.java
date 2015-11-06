package subClasses;

import subClasses.JSONhandler;

import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.google.gson.JsonObject;
import java.util.*;
import java.text.*;


public class DBpull {
	
	private CloudantClient client;
	private Database db;
	
	public DBpull( CloudantClient client, String db){
		this.client = client; 
		this.db = client.database(db, false);
	}

	public String getStudentInfo( String id ){
		
		JSONhandler test = new JSONhandler(  this.db.find(JsonObject.class, id) );
		return test.toString("schedule");
	}
	
	public static void main( String [] args){
		
    	CloudantClient client = new CloudantClient("eyeofthetiger", "eyeofthetiger", "eng40000");

    	DBpull test = new DBpull( client, "practice");
    	String get = test.getStudentInfo("212352755");    	
    	System.out.println(get);
	}
}
