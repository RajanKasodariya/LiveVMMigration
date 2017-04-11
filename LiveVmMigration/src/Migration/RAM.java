package Migration;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class RAM implements Serializable{
	
	private static final long serialVersionUID = 2633050814469107501L;
	private final int SIZE;
	private int RAM[]; 
	private boolean FLAG[];
	//private boolean dirty[];   // check if page is dirty or not ?
	public Set <Integer> dirtyPage;
	
	/*
	 * S : Support 
	 * E : Error
	 * */
	
	int S;
	double E;
	int windowSize;
	int currentWindowSize;
	
	// Stores frequency of Trending pages
	Map<Integer,Integer> trendingPages;
	
	// stores freq of pages in each window
	Map<Integer,Integer> pageFreq;
	
	public RAM(int size){
		this.SIZE=size;
		RAM=new int[size];
		FLAG=new boolean[size];
		//dirty=new boolean[size];
		trendingPages=new HashMap<Integer,Integer>();
		pageFreq=new HashMap<Integer,Integer>();
		dirtyPage=new HashSet<Integer>();
		
		//initilization 
		for(int i=0;i<size;i++){
			dirtyPage.add(i);
		}
		Arrays.fill(RAM, 0);
		//Arrays.fill(dirty, true);
		Arrays.fill(FLAG, false);
		S=3;
		E=0.01;
		windowSize=40; /* (1/E) */
		currentWindowSize=0;
	}
	
	/*
	 * checks if Page is dirty or not 
	 * */
	public boolean isPageDirty(int index){
//		return dirty[index];
		return dirtyPage.contains(index);
	}
	
	/*
	 * Sets the value of the Page
	 * */
	public void setPageDirty(int index,boolean value){
		//dirty[index]=value;
		
		
		if(value==true){
			dirtyPage.add(index);
			int freq=0;
			// increment freq of this page in pageFreq
			Integer x = pageFreq.get(index);
			if(pageFreq.containsKey(index)){
				freq=(int) pageFreq.get(index);
			}
			else{
				freq = 0;
			}
			if(freq<=0) freq=0;
			pageFreq.put(index, freq + 1);
			incrementWindowSize();
		}
		else
		{
			dirtyPage.remove(index);
		}
	}
	
	public void displayFreqPage()
	{
		for(Map.Entry m:pageFreq.entrySet()) {
			System.out.print(m.getKey()+"   "+m.getValue()+"--------");
		}
		System.out.print("\n");
		for(Map.Entry m:trendingPages.entrySet()){
			System.out.print(m.getKey()+"   "+m.getValue()+"--------");
		}
		System.out.print("\n");
	}
	
	/* Increments window size by 1 */
	private void incrementWindowSize() {
		currentWindowSize++;
		
		
		// decrement of all pageFreq by 1
		if(currentWindowSize==windowSize){
			// add pageFreq to trendingPages
			for(Map.Entry m:pageFreq.entrySet()) {
				int index=(int) m.getKey();
				int freq=0;
				if(trendingPages.containsKey(index)) {
					freq=trendingPages.get(index);
				}
				trendingPages.put((Integer) m.getKey(),freq+(int)m.getValue());
			}
			
			for(Map.Entry m:trendingPages.entrySet()){
				if(m!=null){
					int freq=(int) m.getValue();
					m.setValue(freq-1);
				}
			}
			currentWindowSize=0;
			//sudharo
			pageFreq=new HashMap<Integer,Integer>();
		}
	}

	/*
	 * Returns content at RAM[index]
	 * */
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
	
	public int getDirtyPageSize(){
		return dirtyPage.size();
	}
	
	public int getSupport(){
		return S;
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
	
	public boolean isTrending(int index){
		int freq=0;
		if(trendingPages.get(index)==null) freq = 0;
		else{
			freq=trendingPages.get(index);
		}
		return freq>=getSupport();
	}
}
