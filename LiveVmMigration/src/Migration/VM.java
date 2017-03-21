package Migration;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

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
		ip=0;
		sp=0;
		
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
			Socket client=new Socket();
			
			ObjectOutputStream op=new ObjectOutputStream(client.getOutputStream());
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * Receives complete VM at destination
	 * */
}
