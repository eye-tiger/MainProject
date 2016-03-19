package subClasses;


import java.util.ArrayList;
import com.google.gson.*;


/**
 * @author jason kuffour
 *
 * This class handles all data obtained and supplied to the DB to 
 * ensure that it's in the proper format. It is used by the DBpull
 * and DBpush class
 * 
 * 
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
	
	/**
	 * @param field  - The field name to be given to the arraylist data
	 * @param newdata - Arraylist to be added to the json data
	 * 
	 * Adds the strings in the arraylist to the json data with the given field name
	 */
	public void addData( String field, ArrayList<String> newdata  ){	
		this.instance.addProperty(field, toString(newdata) );
	}
	
	/**
	 * @param datafield - the json attribute to find and convert to a string
	 * @return - String representation of the value with the given json attribute field
	 */
	public String toString( String datafield ){
		if( this.instance.has(datafield) ){
			return this.instance.get(datafield).getAsString().toString();
		}
		return "nothing";
	}
	
	/**
	 * @param datafield - the json attribute that has it's value stored as a list delimited by commas
	 * @return - an arraylist containing the elements in  a json field 
	 * 
	 * 
	 */
	public ArrayList<String> toArray(String datafield){
		
		ArrayList<String> abcd = new ArrayList<String>();
		
		if( this.instance.has(datafield) ){
			String a = this.instance.get(datafield).getAsString().toString();
			String c[] = a.split("/");
			
			for( String f: c){	abcd.add(f); }
		}
		
		return abcd;
	}
	
	/**
	 * @param field - the json field that contains the array
	 * @return - An arraylist of strings containing all the field values in the array
	 */
	public String[] extractToArray( String field){
		
		if( this.instance.has(field)){
			String[] t = this.instance.get(field).getAsString().toString().split("/");
			return t;
		}
		String[] f = {"hello", "boy"};
		return f;
	}
	
	/**
	 * @param array - the json field that contains the array
	 * @param field - the json field that is in each array element to be extracted
	 * @return - An arraylist of strings containing all the field values in the array
	 */
	public ArrayList<String> extractFromArray( String array, String field){
		if( this.instance.getAsJsonArray(array).isJsonArray()){
			
			JsonArray temp = this.instance.getAsJsonArray(array);
			ArrayList<String> re = new ArrayList<String>();
			
			for(int i = 0; i < temp.size(); i++){
				re.add(temp.get(i).getAsJsonObject().get(field).getAsString());
			}
			return re;
		}
		return new ArrayList<String>();
	}
	
	
	/**
	 * @param data - An arraylist
	 * @return - A string that contains the items in the arraylist as a comma delimited list
	 */
	private String toString( ArrayList<String> data ){
		String fin = "";
		for( String i: data){
			fin = fin + "/" + i;
		}
		fin = fin.substring(1, fin.length() );
		return fin;
	}
	
}

