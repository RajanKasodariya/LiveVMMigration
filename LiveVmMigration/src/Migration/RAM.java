package Migration;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;

public class RAM implements Serializable{
	
	private static final long serialVersionUID = 2633050814469107501L;
	private final int SIZE;
	private int RAM[]; 
	private boolean FLAG[];
	
	public RAM(int size){
		this.SIZE=size;
		RAM=new int[size];
		FLAG=new boolean[size];
		Arrays.fill(RAM, 0);
		Arrays.fill(FLAG, false);
	}

	public int getRAM(int index) {
		return RAM[index];
	}

	public void setRAM(int index,int value) {
		RAM[index]=value;
	}

	public boolean getFlag(int index) {
		return FLAG[index];
	}

	public void setFlag(int index,boolean value) {
		FLAG[index] = value;
	}

	public int getSize() {
		return SIZE;
	}
	
	public void displayRAM(){
		int i=0;
		
		while(i<this.SIZE) {
			for(int j=0;i<this.SIZE && j<15;j++){
				System.out.print(this.RAM[i++] + " ");
			}
			System.out.println("");
		}
	}

	public void fillRAM() {
		Random r=new Random();
		
		for(int i=0;i<getSize();i++) {
			setRAM(i, r.nextInt(1000));
		}
		
	}
}
