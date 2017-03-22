package Migration;

import Migration.VM;

public class Destination {
	
	public static void main(String args[]) throws ClassNotFoundException{
	    int A[];
	    A = new int[2];
	    
		// creating VM which is not doing anything
		VM vm = new VM(A,10);  
		
		vm.receiveVM();
		
		vm.receiveRAMPages();
		
	}
}
