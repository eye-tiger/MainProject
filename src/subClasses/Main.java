package subClasses;


public class Main {

	public static void main( String args[]){
		//args[0] - mac Address;
		//args[1] - scanned hour the beacon was detected
		//args[2] - scanned minute the beacon was detected
		//args[3] - beacon location
		//args[4] - entry/exit indicator
		Facade test = new Facade(args[0]);
		
		if( test.check() ){
			test.updateStudentInstance(Integer.parseInt(args[1]), Integer.parseInt(args[2]), args[3], args[4]);
		}
	}
	
}
