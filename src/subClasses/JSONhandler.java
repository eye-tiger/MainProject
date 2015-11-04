package subClasses;

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

	private JsonObject instance;
	
	public JSONhandler( JsonObject data ){
		this.instance = data;
	}
	
	public void addData( String field, String newdata ){
		this.instance.addProperty(field, newdata);
	}
	
	public void removeData( String field, String data ){
		
	}
	
	public String toString( String datafield ){
		
		if( this.instance.has(datafield) ){
			return this.instance.get(datafield).toString();
		}
		return "nothing";
	}
	
	
	
	public static void main( String[] args){
		JSONhandler test = new JSONhandler( new JsonObject() );
		test.addData("hello", "testing");
		System.out.println( test.toString("hello") );
	}
	
}

