package Migration;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.print.attribute.standard.PrinterLocation;

public class VM {
	/* Variables */
	private int stackSize;		// stack size
	private int ip; 			// instruction pointer
	private int sp;				// stack pointer
	
	/* memory management variables */
	private int code[];
	private int global[];
	private int stack[];
	
	int nxt;  					// points to the current line of code to execute
	
	RAM rm;						// RAM 
	
	boolean dirty[];			// shows id page is dirtied or not ?
	
	public VM(int code[],int stackSize){
		this.stackSize=stackSize;
		ip=-1;
		sp=-1;
		
		this.code=code;
		global=new int[stackSize];
		stack=new int[stackSize];
	}
	
	/*
	 * Displays content of stack
	 * */
	public void stackDisplay(){
		for(int i=0;i<stackSize;i++){
			if(i%11==0) System.out.println("");
			System.out.println(stack[i]+" ");
		}
	}
	
	/*
	 * Sends complete VM to destination
	 * 
	 * Sender behaves as a Server
	 * */
	
	public void sendVM(){
		try {
			ServerSocket sc=new ServerSocket();
			
			// Accept client request
			Socket client=sc.accept();
			
			ObjectOutputStream op=new ObjectOutputStream(client.getOutputStream());
			op.writeObject(this);
			
			op.close();
			client.close();
			sc.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * Receives complete VM at destination
	 * */
	
	public void receiveVM() throws ClassNotFoundException{
		try {
			Socket client = new Socket(Config.sourceIP,Config.sourcePORT);
			ObjectInputStream in;
			in = new ObjectInputStream(client.getInputStream());
			
			VM vm = (VM) in.readObject();
			
			this.reset(vm);
			in.close();
			client.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void reset(VM vm) {
		this.stackSize=vm.getStackSize();
		ip=vm.getIP();
		sp=vm.getSP();
		
	}

	public int getSP() {
		return sp;
	}

	public int getIP() {
		return ip;
	}

	public int getStackSize() {
		return stackSize;
	}
}
