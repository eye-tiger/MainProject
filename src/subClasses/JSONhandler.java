package subClasses;

import java.util.ArrayList;
import java.util.Arrays;
import com.google.gson.*;


/**
 * @author jason kuffour
 *
 * This class handles all data obtained and supplied to the DB to 
 * ensure that it's in the proper format. It is used by the DBpull
 * and DBpush class
 * 
 * Still in development
 */
public class JSONhandler {

	public JsonObject instance;
	
	/**
	 * This constructor creates an instance of the JSONhandler class using an instance of
	 * a JsonObject
	 * 
	 * @param data - A JsonObject containing information
	 */
	public JSONhandler( JsonObject data ){
		this.instance = data;
	}
	
	public JSONhandler( JSONhandler data ){
		this.instance = data.instance;
	}
	
	/**
	 * @param field - the name of the attribute of the data to be added in the json data
	 * @param data  - the data to be written to the json data
	 * 
	 * This constructor creates a new instance of this class and adds the given property to the 
	 * newly created json
	 */
	public JSONhandler( ArrayList<String> data, String field  ){
		this.instance = new JsonObject();
		String fin = toString(data);
		this.instance.addProperty(field, fin);
	}

	/**
	 * @param field   - the name of the attribute field to be added/updated
	 * @param newdata - the new value of the data to be added to the json data
	 * 
	 * This method adds/updates data in the json data with the given parameters
	 */
	public void addData( String field, String newdata ){
		this.instance.addProperty(field, newdata);
	}
	
	public void addData( String field, ArrayList<String> newdata  ){	
		this.instance.addProperty(field, toString(newdata) );
	}
	
	public String toString( String datafield ){
		if( this.instance.has(datafield) ){
			return this.instance.get(datafield).getAsString().toString();
		}
		return "nothing";
	}
	
	/**
	 * @param data
	 * @return
	 */
	private String toString( ArrayList<String> data ){
		String fin = "";
		for( String i: data){
			fin = fin + "," + i;
		}
		fin = fin.substring(1, fin.length() );
		return fin;
	}
	
	
	public ArrayList<String> toArray(String member){
		String list[] = this.instance.get(member).toString().split(",");
		ArrayList<String> array = new ArrayList<String>( Arrays.asList(list));
		return array;
	}
	
	
	
	
	
	public static void main( String args []){
		ArrayList<String> check = new ArrayList<String>();
		check.add("eng");
		check.add("bio");
		check.add("math");
		JsonObject t = new JsonObject();
		JSONhandler test = new JSONhandler(check, "hello");
		test.addData("hello2", "testing");
		
		System.out.println( test.toString("hello") );
	}
	
}

