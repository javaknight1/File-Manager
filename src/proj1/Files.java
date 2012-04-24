/**
 * This class keeps track of all the files and includes methods that 
 * will add, delete, truncate, extend, or print any file accordingly.
 *
 * @version 2/28/2012
 * @author Rob Avery <pw97976@umbc.edu>
 * CMSC 341 - Spring 2012 - Project 1
 * Section 02
 */

package proj1;

public class Files {

	// System information
	// ------------------
	private static int totalBlocks; // Total number of all Disk Blocks in the
									// system ex. 20000
	private static int usedBlocks; // Total number of Disk Blocks being
									// allocated ex. 0 //at start up
	private static int freeBlocks; // Total number of free Disk Blocks ex. 20000
	private static int memperBlock; // The amount of allocated memory per Disk
									// Block ex. 1024
	private boolean invalid = false;
	// File information
	private String filename; // The filename
	private int actMem; // The actual size of the file
	private int alloMem; // The allocated memory for the file
	private int nrBlocks; // The number of blocks used by this file
	private MyLinkedList<DiskBlocks> sector = new MyLinkedList<DiskBlocks>(); // The sectors of memory when data is stored
	private DiskBlocks db = new DiskBlocks();
	java.util.Iterator<DiskBlocks> itr;

	
	/**
	 * Constructor used for files with invalid amounts
	 * @param memory amount of memory for file
	 */
	public Files(int memory){
		if ((freeBlocks*memperBlock) < memory)
			invalid = true;
	}
	
	
	/**
	 * Constructor used for the system
	 * @param totalBlock the total amount of blocks in the system
	 * @param perBlock amount of memory per block
	 */
	public Files(int totalBlock, int perBlock) {
		totalBlocks = freeBlocks = totalBlock;
		memperBlock = perBlock;
		usedBlocks = 0;
		db = new DiskBlocks(totalBlock);
	}

	/**
	 * Constructor used for a file
	 * @param filename the actual name of the file
	 * @param memory how much memory it uses
	 */
	public Files(String filename, int memory) {
		if ((freeBlocks*memperBlock) < memory) {
			invalid = true;
		}else{	
			this.filename = filename;
			actMem = memory;
			int remain = CalcnrBlocks() - nrBlocks;
		
			invalid = false;
			if (actMem >= alloMem) {
				nrBlocks = CalcnrBlocks();
				CalcAllo();
				AddupdateSystem(0);
			}
		
			db.compensateIncrease(remain);
			while (remain > 0) {			
				db = new DiskBlocks();
				int startpoint = db.firstFreeSpace(0);
				int endpoint = db.nextFreeSpace(startpoint, remain);
				db.setBegin(startpoint);
				db.setEnd(endpoint);
				db.fillFull(startpoint, endpoint);
				sector.add(db);
				remain = remain - (endpoint - startpoint) - 1;
			}
		}
	}

	/**
	 * After a file has been created or extended, this will compensate the system memory
	 * @param oldBlocks blocks before the change was made
	 */
	private void AddupdateSystem(int oldBlocks) {
		usedBlocks += (nrBlocks - oldBlocks);
		freeBlocks -= (nrBlocks - oldBlocks);
	}

	/**
	 * After a file has be removed, this will compensate the system memory
	 * @param oldBlocks
	 */
	private void RemoveupdateSystem(int oldBlocks) {
		usedBlocks -= (oldBlocks - nrBlocks);
		freeBlocks += (oldBlocks - nrBlocks);
	}

	/**
	 * Calculates the new amount of blocks for the file
	 * @return the new amount of allocated blocks
	 */
	private int CalcnrBlocks() {
		int tempBlocks;
		if (actMem % memperBlock != 0)
			tempBlocks = (int) Math.floor((actMem / memperBlock) + 1);
		else
			tempBlocks = actMem / memperBlock;
		return tempBlocks;
	}

	/**
	 * Calculates the number of allocated memory
	 */
	private void CalcAllo() {
		alloMem = nrBlocks * memperBlock;
	}

	/**
	 * Returns the name of the file
	 * @return name of the file
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * Deletes a file
	 */
	public void delete() {
		RemoveupdateSystem(2 * nrBlocks);
		itr = sector.iterator();
		while(itr.hasNext()){
			db = itr.next();
			db.clearDB();
		}
	}

	/**
	 * Extends a file memory
	 * @param amt the amount being extended
	 */
	public void extend(int amt) {
		actMem += amt;
		int remain = CalcnrBlocks() - nrBlocks;
		if (freeBlocks < remain) {
			invalid = true;
			actMem -= amt;
		} else {
			if (actMem >= alloMem) {
				int tempBlock = nrBlocks;
				nrBlocks = CalcnrBlocks();
				CalcAllo();
				AddupdateSystem(tempBlock);
				db.compensateIncrease(remain);
				while (remain > 0) {
					db = new DiskBlocks();
					int startpoint = db.firstFreeSpace(0);
					int endpoint = db.nextFreeSpace(startpoint, remain);
					db.fillFull(startpoint, endpoint);
					itr = sector.iterator();
					int joinBlocks = 0;
					//
					int idx = 0;
					while(itr.hasNext()){
						db = itr.next();
						if(startpoint-1 == db.getEnd()){
							sector.get(idx).setEnd(endpoint);
							joinBlocks++;
							break;
						}
						idx++;
					}
					//
					itr = sector.iterator();
					idx = 0;
					while(itr.hasNext()){
						db = itr.next();
						if(endpoint+1 == db.getBegin()){
							sector.get(idx).setBegin(startpoint);
							joinBlocks++;
							break;
						}
						idx++;
					}
					if(joinBlocks == 2){
						int newEndpoint = sector.get(idx).getEnd();
						int newStartpoint = sector.get(idx-1).getBegin();
						db = new DiskBlocks(newStartpoint, newEndpoint);
						sector.set(idx-1, db);
						sector.remove(idx);
					}
					if(joinBlocks == 0){
						db = new DiskBlocks(startpoint, endpoint);
						idx = 0;
						if(sector.size() == 1)
							idx = sector.size();
						while(idx < sector.size()-1){							
							if(sector.get(idx).getEnd() < startpoint && sector.get(idx+1).getBegin() > endpoint){
								idx++;
								break;
							}
							idx++;
						}
						sector.add(idx, db);
					}
					remain = remain - (endpoint - startpoint) - 1;
				}
			}
		}
	}

	/**
	 * Truncates the file memory
	 * @param amt the amount being truncated
	 */
	public void truncate(int amt) {
		if (amt > actMem) {
			invalid = true;
		} else {
			actMem -= amt;
			if (nrBlocks > CalcnrBlocks()) {
				int oldBlock = nrBlocks;
				int remain = nrBlocks - CalcnrBlocks();
				nrBlocks = CalcnrBlocks();
				CalcAllo();
				RemoveupdateSystem(oldBlock);
				while(remain > 0){
					db = new DiskBlocks();
					int endval = sector.get(sector.size()-1).getEnd();
					int beginval = sector.get(sector.size()-1).getBegin();
					sector.get(sector.size()-1).setEnd(endval-1);
					if(endval == beginval)
						sector.remove(sector.size()-1);
					db.emptyBlock(endval);
					remain--;
				}
			}
		}
	}
	/**
	 * Returns if the amount was valid or invalid
	 * @return invalid - either true or false
	 */
	public boolean invalidAmt(){
		return invalid;
	}
	
	/**
	 * A sorta-kinda toString method for the system memory
	 * @return a string to output the system memory
	 */
	public String SystemInfo() {
		StringBuilder sb = new StringBuilder("FILE MANAGER STATUS");

		sb.append("\n-------------------");
		sb.append("\nDisk Block Size:  " + memperBlock);
		sb.append("\nNumber of Blocks: " + totalBlocks);
		sb.append("\nAllocated Blocks: " + usedBlocks);
		sb.append("\nFree Blocks:      " + freeBlocks);

		return new String(sb);

	}
	
	/**
	 * Prints all the free blocks in the system memory
	 */
	public void freeList(){
		int first =  db.firstFreeSpace(0);
		int end = db.nextFreeSpace(first, db.fullSize());
		int remain = db.fullSize();
		
		if(first == db.fullSize())
			System.out.println(" No Free Blocks");
		else{
			do{
				System.out.print(" [ " + first + ", " + end + " ]");
				first = db.firstFreeSpace(end+1);
				if(first >= db.fullSize())
					break;				
				end = db.nextFreeSpace(first, remain);
			}while(first != (db.fullSize()-1));
		}
	}

	/**
	 * toString method for the file
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder("File : " + filename);

		sb.append("\n        Actual Size:      " + actMem);
		sb.append("\n        Allocated Blocks: " + alloMem);
		sb.append("\n        Number of Blocks: " + nrBlocks);
		sb.append("\n        Disk Blocks:      ");

		
		itr = sector.iterator();
		while(itr.hasNext()){ 
			db = itr.next(); 
			sb.append("[ " + db.getBegin() + ", " + db.getEnd() + " ] ");
		}
		 

		return new String(sb);
	}

}
