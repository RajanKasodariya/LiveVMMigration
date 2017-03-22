package Migration;

import java.io.IOException;
import java.net.ServerSocket;

import Migration.VM;

public class Destination {
	
	public static void main(String args[]) throws ClassNotFoundException, IOException, InterruptedException{

		// creating VM which is not doing anything
		VM vm = new VM(Program.code2,1000);  
		
		ServerSocket sc=new ServerSocket(Config.destinationPORT);
		vm.receiveVM(sc);
		vm.receiveRAMPages(sc);
		vm.receiveState(sc);
		
		vm.cpu();
		sc.close();
	}
}
