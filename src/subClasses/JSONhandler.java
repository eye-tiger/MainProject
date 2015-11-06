package subClasses;

import java.util.Iterator;
import java.util.Map;

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
	
	/**
	 * This constructor creates an instance of the JSONhandler class using an instance of
	 * a JsonObject
	 * 
	 * @param data - A JsonObject containing information
	 */
	public JSONhandler( JsonObject data ){
		this.instance = data;
	}
	
	/**
	 * @param field - the name of the attribute of the data to be added in the json data
	 * @param data  - the data to be written to the json data
	 * 
	 * This constructor creates a new instance of this class and adds the given property to the 
	 * newly created json
	 */
	public JSONhandler( String field, String data  ){
		this.instance = new JsonObject();
		this.instance.addProperty(field, data);
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
	
	public void addData( JSONhandler data ){	
	}
	
	public String toString( String datafield ){
		
		if( this.instance.has(datafield) ){
			return this.instance.get(datafield).toString();
		}
		return "nothing";
	}
	
	
}

