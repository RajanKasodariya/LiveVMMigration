package Migration;

import java.io.Serializable;

public class VMInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int stackSize;
	private int ramSize;
	private int ip;
	private int sp;
	
	public VMInfo(int stackSize,int ramSize){
		this.stackSize=stackSize;
		this.ramSize=ramSize;
	}

	public int getStackSize() {
		return stackSize;
	}

	public void setStackSize(int stackSize) {
		this.stackSize = stackSize;
	}

	public int getRamSize() {
		return ramSize;
	}

	public void setRamSize(int ramSize) {
		this.ramSize = ramSize;
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

	public String toString(){
		return "IP="+ip+" SP= "+sp+"\n" ;
		
	}
	
}
