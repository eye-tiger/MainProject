package subClasses;


public class Main {

	public static void main( String args[]){
		
		
		//args[0] - student id;
		//args[1] - class room id;
		//args[2] - course id
		Facade test = new Facade(args[0]);
		test.updateStudentInstance(args[1], args[2]);
		
	}
	
}
