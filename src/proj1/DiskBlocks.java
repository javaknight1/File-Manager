/**
 * This class will keep track of the DiskBlocks of a File 
 * and will tell you where it begins and ends. This will also 
 * keep track of which DBs are filled.
 *
 * @version 2/28/2012
 * @author Rob Avery <pw97976@umbc.edu>
 * CMSC 341 - Spring 2012 - Project 1
 * Section 02
 */

package proj1;

public class DiskBlocks {

	private int begin;	//Tells where the disk space sector starts
	private int end;	//Tells where the disk space sector ends
	
	private static MyLinkedList<Boolean> full = new MyLinkedList<Boolean>();		//Static boolean linked list that tells whether a space is empty or not
	private java.util.Iterator<Boolean> itr;				//An iterator used through the class
	
	/**
	 * Blank contructor
	 */
	public DiskBlocks(){}
	
	/**
	 * Constructor that makes all the DiskBlocks empty
	 * @param fullLength how many actual DiskBlocks
	 */
	public DiskBlocks(int fullLength){
		for(int i=0; i<fullLength; i++)
			full.add(false);
	}
	
	/**
	 * Constructor for individual DiskBlock
	 * @param startp the startpoint
	 * @param endp the endpoint
	 */
	public DiskBlocks(int startp, int endp){
		setBegin(startp);
		setEnd(endp);
	}
	
	/**
	 * Returns the size of the entire system
	 * @return the system size
	 */
	public int fullSize(){
		return full.size();
	}
	
	/**
	 * Returns the number of Disk Blocks that are filled
	 * @return the count of filled disk blocks
	 */
	private int filledBlocks(){
		itr = full.iterator();
		int count = 0;
		while(itr.hasNext()){
			boolean temp = itr.next();
			if(temp)
				count++;
		}
		return count;
	}
	
	/**
	 * Returns the number of Disk Blocks that are empty
	 * @return the count of empty disk blocks
	 */
	private int emptyBlocks(){
		itr = full.iterator();
		int count = 0;
		while(itr.hasNext()){
			boolean temp = itr.next();
			if(!temp)
				count++;
		}
		return count;
	}
	
	/**
	 * Increases the linked list according to how is needed
	 * @param memoryNeeded how many memory blocks needed to increase
	 */
	public void compensateIncrease(int memoryNeeded){
		if(memoryNeeded > emptyBlocks()){
			int newMem = memoryNeeded - emptyBlocks();
			for(int i=0; i<newMem; i++)
				full.add(false);
		}
	}
	
	/**
	 * Finds the first free space starting from the point "start" parameter
	 * Finds the starting point of a disk block
	 * @param start the point in the linked list to start searching from
	 * @return the position of the first free space
	 */
	public int firstFreeSpace(int start){
		itr = full.iterator();
		int idx = 0;
		for(int i=0; i<start; i++)
			itr.next();
		
		while(itr.hasNext()){
			boolean temp = itr.next();
			if(!temp)
				return idx+start;
			idx++;
		}
		return idx+start;
	}
	
	/**
	 * Finds the next filled space starting from the "start" point
	 * Finds the end point of a disk block
	 * To find the end point of a 
	 * @param start where to start
	 * @param remain if there is only a certain number of disk blocks needed
	 * @return the position of the next filled space needed
	 */
	public int nextFreeSpace(int start, int remain){
		itr = full.iterator();
		int idx = 0;
		for(int i=0; i<start; i++)
			itr.next();
		
		while(itr.hasNext() && remain != idx){
			boolean temp = itr.next();
			if(temp)
				return idx+start-1;	
			idx++;
		}
		return idx+start-1;		
	}
	
	/**
	 * Makes all the blocks empty
	 */
	public void clearDB(){
		for(int i=begin; i<= end; i++){
			emptyBlock(i);
		}
	}
	
	/**
	 * Set the beginning
	 * @param set the beginning
	 */
	public void setBegin(int start){
		begin = start;
	}
	
	/**
	 * Returns the beginning point
	 * @return beginning point
	 */
	public int getBegin(){
		return begin;
	}
	
	/**
	 * Set the end
	 * @param set the end
	 */
	public void setEnd(int end){
		this.end = end;
	}
	
	/**
	 * Returns the end point
	 * @return end point
	 */
	public int getEnd(){
		return end;
	}

	/**
	 * Fills all the positions from "startpoint" to "endpoint"
	 * @param startpoint start position
	 * @param endpoint end position
	 */
	public void fillFull(int startpoint, int endpoint) {
		for(int i=startpoint; i<=endpoint; i++)
			full.set(i, true);		
	}
	
	/**
	 * Empties a specific block
	 * @param idx which block to empty
	 */
	public void emptyBlock(int idx){
		full.set(idx, false);
	}
	
}
