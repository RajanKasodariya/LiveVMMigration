package Migration;

import java.io.IOException;
import java.net.ServerSocket;

import Migration.VM;

public class Destination {
	
	public static void main(String args[]) throws ClassNotFoundException, IOException{
	    int A[];
	    A = new int[2];
	    
		// creating VM which is not doing anything
		VM vm = new VM(A,10);  
		
		ServerSocket sc=new ServerSocket(Config.destinationPORT);
		vm.receiveVM(sc);
		vm.receiveRAMPages(sc);
		vm.receiveState(sc);
		sc.close();
	}
}
