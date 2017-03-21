package Migration;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Destination {
	
	public static void main(String args[]) throws ClassNotFoundException{
	    int A[];
	    A = new int[2];
	    
		// creating VM which is not doing anything
		VM vm = new VM(A,10);  
		
		vm.receiveVM();
		
		try {
			Socket client = new Socket(Config.sourceIP,Config.sourcePORT);
			
			ObjectInputStream ip; // input stream
			ip=new ObjectInputStream(client.getInputStream());
			
			while(true){
				RamPage page=(RamPage) ip.readObject();
				
				// end receiving if Source has sent all pages
				if(page.getPAGE_INDEX()==-1) break;
				
				vm.setRamPage(page.getPAGE_INDEX(), page.getPAGE_VALUE());
			}
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
