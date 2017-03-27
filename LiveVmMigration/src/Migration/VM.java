package Migration;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Random;

import static Migration.Instructions.*;

import javax.print.attribute.standard.PrinterLocation;

public class VM implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/* Variables */
	private int stackSize;		// stack size
	private int ip; 			// instruction pointer
	private int sp;				// stack pointer
	private boolean ableToMigrate=true; //migration eligibility    
	
	/* memory management variables */
	private int code[];
	//private int global[];
	private int stack[];
	
	int nxt;  					// points to the current line of code to execute
	
	RAM rm;						// RAM 
		
	public VM(int code[],int stackSize){
		this.stackSize=stackSize;
		ip=0;
		sp=-1;
		
		rm=new RAM(stackSize);
		rm.fillRAM();     // fill RAM with random values
		
		this.code=code;
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
	 * Sender behaves as a client
	 * */
	
	public void sendVM(){
		try {			
			// Accept client request
			Socket client=new Socket(Config.destinationIP,Config.destinationPORT);
			
			ObjectOutputStream op=new ObjectOutputStream(client.getOutputStream());
			op.writeObject(new VMInfo(this.getStackSize(),this.getStackSize()));
			
			op.close();
			client.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * Receives complete VM at destination
	 * */
	
	public void receiveVM(ServerSocket sc) throws ClassNotFoundException{
		try {
			//ServerSocket sc=new ServerSocket(Config.destinationPORT);
			Socket client = sc.accept();
			ObjectInputStream in;
			in = new ObjectInputStream(client.getInputStream());
			
			VMInfo vminfo = (VMInfo) in.readObject();
			
			this.reset(vminfo);
			
			in.close();
			client.close();
			//sc.close();
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void reset(VMInfo vminfo) {
		this.stackSize=vminfo.getStackSize();
		this.ip=vminfo.getIp();
		this.sp=vminfo.getSp();	
		
		// deleting RAM and Stack
		rm=null;
		stack=null;
		
		// create new RAM and Stack
		rm=new RAM(vminfo.getRamSize());
		stack=new int[this.stackSize];
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

	public void setRamPage(int page_INDEX, int page_VALUE) {
		rm.setRAM(page_INDEX, page_VALUE);
	}
	
	
	
	/*
	 * cpu
	 */
	public void cpu() throws InterruptedException{
		if(ip>=code.length) return;
		int opcode=code[ip];
		
		int a,b;
		
		while(opcode!=HALT && ip<code.length){
			ip++;
			ableToMigrate=false;
			switch(opcode){
			case IADD:
				System.out.println("Executing ADD: ");
				a=stack[sp--];
				b=stack[sp--];
				stack[++sp]=a+b;
				break;
			case ISUB:
				System.out.println("Executing SUB: ");
				a=stack[sp--];
				b=stack[sp--];
				stack[++sp]=a-b;
				break;
			case IMUL:
				System.out.println("Executing MUL: ");
				a=stack[sp--];
				b=stack[sp--];
				stack[++sp]=a*b;
				break;
			case READ:
				a=code[ip++];
				b=rm.getRAM(a);
				stack[++sp]=b;
				System.out.println("Executing READ from i: "+a+" = "+b);
				break;
			case WRITE:
				a=code[ip++];
				System.out.println("Executing Write to i: "+a);
				rm.setRAM(a, stack[sp]);
				rm.setPageDirty(a, true);
				Thread.sleep(1000);
				break;
			case LT:
				b=stack[sp--];
				a=stack[sp--];
				stack[++sp]=(a<b)?1:0;
				break;
			case EQ:
				b=stack[sp--];
				a=stack[sp--];
				stack[++sp]=(a==b)?1:0;
				break;
			case BR:
				System.out.printf("Executing BR %d\n",code[ip]);
				ip=code[ip];
				break;
			case BRT:
				System.out.printf("Executing BRT %d\n",code[ip]);
				if(stack[sp--]==1) ip=code[ip];
				break;
			case BRF:
				System.out.printf("Executing BRF %d\n",code[ip]);
				if(stack[sp--]==0) ip=code[ip];
				break;
			case CONST:
				System.out.println("Executing CONST "+code[ip]);
				stack[++sp]=code[ip++];
				break;
			case PRINT:
				System.out.println("Exectuting PRINT");
				System.out.println(stack[sp]);
				break;
			case POP:
				System.out.println("Execuiting POP");
				sp--;
				break;
			default :
				throw new Error("invalid opcode: "+opcode+" at ip="+(ip-1));
			
			}
			ableToMigrate=true;
			if(ip==code.length) break;
			opcode=code[ip];
			Thread.sleep(300);
			System.out.println("IP :: "+ip);
		}
	}

	boolean stopMigration(int migratedPages,int noOfIterations){
		return migratedPages<=2 || noOfIterations==3;
	}
	
	public void migrate() {
		int migratedPages=0;
		int noOfIterations=0;
		
		Socket client;
		try {
			client = new Socket(Config.destinationIP,Config.destinationPORT);
			
			ObjectOutputStream op;
			op=new ObjectOutputStream(client.getOutputStream());
			
			System.out.println("Migration stared");
			
			
			do {
				migratedPages=0;
				for(int i=0;i<rm.getSize();i++){
					/* Send page if it is dirty */
					if(rm.isPageDirty(i) && rm.isTrending(i)==false) {
						// send page
						rm.setPageDirty(i, false);
						op.writeObject(new RamPage(i, rm.getRAM(i)));
						migratedPages++;
						System.out.println("Page sent "+i);
						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				System.out.println("In loop...");
				noOfIterations++;
			}while(!stopMigration(migratedPages,noOfIterations));
			
			op.writeObject(new RamPage(-1, -1));
			System.out.println("Migration over");
			
			op.close();
			client.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	
	
	public void migrateStates() {
		
		System.out.println("CPU States Migrating...");
		Socket client;
		try {
			client = new Socket(Config.destinationIP,Config.destinationPORT);
			
			ObjectOutputStream op;
			op=new ObjectOutputStream(client.getOutputStream());
						
			/* Send RAM last time */
			
			/* Send whole stack after ram migration */
			System.out.println("Before stack Migration");
			op.writeObject(stack);
			System.out.println("After stack Migration");
			
			/* send state info */
			VMInfo vminfo = new VMInfo(this.getStackSize(),this.getStackSize());
			vminfo.setIp(this.getIP());
			vminfo.setSp(this.getSP());
			while(ableToMigrate==false){}
			op.writeObject(vminfo);
			
			System.out.println(">>> VM INFO : "+ vminfo);
			
			System.out.println("Migration over");
			
			op.close();
			client.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("CPU States Migrated");
	}

	/* receives RAM Pages at destination side */
	public void receiveRAMPages(ServerSocket sc) throws ClassNotFoundException {
		
		//ServerSocket sc;
		try {
			//sc = new ServerSocket(Config.destinationPORT);
			//sc = new ServerSocket(4449);
			Socket client = sc.accept();
			
			ObjectInputStream ip; // input stream
			ip=new ObjectInputStream(client.getInputStream());
			
			while(true){
				RamPage page=(RamPage) ip.readObject();
				
				// end receiving if Source has sent all pages
				if(page.getPAGE_INDEX()==-1) break;
				
				setRamPage(page.getPAGE_INDEX(), page.getPAGE_VALUE());
				
				System.out.println("Received Page : ["+ page.getPAGE_INDEX() + "][" + page.getPAGE_VALUE()+"]" );
			}
			
			//sc.close();
			ip.close();
			client.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	/* Receive state from source */
	public void receiveState(ServerSocket sc) throws ClassNotFoundException {
		
		//ServerSocket sc;
		try {
			//sc = new ServerSocket(Config.destinationPORT);
			//sc = new ServerSocket(4449);
			Socket client = sc.accept();
			
			ObjectInputStream ip; // input stream
			ip=new ObjectInputStream(client.getInputStream());
			
			
			System.out.println("Before stack migration");
			/* read stack from source*/
			stack = (int []) ip.readObject();
			System.out.println("After stack migration");
			
			
			/* read state info */
			VMInfo vminfo = (VMInfo) ip.readObject();
			this.setIp(vminfo.getIp());
			this.setSp(vminfo.getSp());
			
			//sc.close();
			ip.close();
			client.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	public int getIp() {
		return ip;
	}

	public void setIp(int ip) {
		this.ip = ip;
	}

	public int getSp() {
		return sp;
	}

	public void setSp(int sp) {
		this.sp = sp;
	}
	

}
