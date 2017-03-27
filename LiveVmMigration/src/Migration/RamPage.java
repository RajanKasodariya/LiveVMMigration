package Migration;

import java.io.Serializable;

public class RamPage implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int PAGE_INDEX;  // represents index of memory page
	private int PAGE_VALUE;  // represents value of memory page
	
	public RamPage(int index, int value){
		this.PAGE_INDEX = index;
		this.PAGE_VALUE = value;
	}
	
	public int getPAGE_INDEX() {
		return PAGE_INDEX;
	}
	public void setPAGE_INDEX(int pAGE_INDEX) {
		PAGE_INDEX = pAGE_INDEX;
	}
	public int getPAGE_VALUE() {
		return PAGE_VALUE;
	}
	public void setPAGE_VALUE(int pAGE_VALUE) {
		PAGE_VALUE = pAGE_VALUE;
	}
}
