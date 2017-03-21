package Migration;

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
	
	public VM(int code[]){
		stackSize=1000;
		ip=0;
		sp=0;
		
		this.code=code;
		global=new int[1000];
		stack=new int[1000];
	}
	
	
}
