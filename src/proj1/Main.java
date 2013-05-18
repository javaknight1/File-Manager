/**
 * This is the main class that gets the command line arguments and
 * displays the correct results of what's in the file.
 *
 * @version 2/28/2012
 * @author Rob Avery <pw97976@umbc.edu>
 * CMSC 341 - Spring 2012 - Project 1
 * Section 02
 */

package proj1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.io.BufferedReader;
import java.io.FileReader;

public class Main {

	/**
	 * @param args
	 *            : 0 - Total memory space 1 - Memory per block 2 - File
	 *            containing commands
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		int totalMem = Integer.parseInt(args[0]);
		int memBlock = Integer.parseInt(args[1]);
		String filename = args[2];
		String line;

		// Check to see if the file exists, if not, the program will exit
		if (!(new File(filename)).exists()) {
			System.out.println("File does not exist.");
			System.exit(0);
		}

		FileReader reader = new FileReader(filename);
		BufferedReader br = new BufferedReader(reader);

		// Checks to see if the file has anything inside, if not, then it will
		// exit
		if (br.readLine() == null) {
			System.out.println("File is empty.");
			System.exit(1);
		}

		Files f = new Files(totalMem, memBlock);
		MyLinkedList<Files> file = new MyLinkedList<Files>();
		java.util.Iterator<Files> itr;

		// Loops until it hits the end of the file
		while ((line = br.readLine()) != null) {
			StringTokenizer sToken = new StringTokenizer(line);
			
			String tempcmd = null;
			String tempfile;
			int tempamt;
			
			if( sToken.hasMoreTokens() ){
				tempcmd = sToken.nextToken();
			}else{
				tempcmd = "comment";
			}
			
			boolean error = false;
			int idx = 0;
			itr = file.iterator();

			// CREATE command
			if (tempcmd.equals("CREATE")) {
				System.out.println("-----------------------------------------------------------------");
				tempfile = sToken.nextToken();
				tempamt = Integer.parseInt(sToken.nextToken());
				System.out.println(tempcmd + " " + tempfile + " " + tempamt);

				// If the user a negative amount
				if (tempamt < 0) {
					System.out.println("ERROR: " + tempamt
							+ " is an invalid amount.");
					break;
				}

				// Goes through all the files to see if it is already
				// created
				while (itr.hasNext()) {
					f = itr.next();
					if (f.getFilename().equals(tempfile)) {
						error = true;
						break;
					}
				}
				f = new Files(tempamt);

				if (error)
					System.out.println("ERROR: " + tempfile
							+ " - Duplicate file.");
				else if (f.invalidAmt())
					System.out.println("ERROR: Insufficient disk space");
				else {
					System.out.println(tempfile + " created successfully");

					f = new Files(tempfile, tempamt);
					itr = file.iterator();
					idx = 0;

					// Gets the idx of where it should be alphabetically
					while (itr.hasNext()) {
						String currentfile = itr.next().getFilename();
						int alphaResult = tempfile.compareTo(currentfile);
						if (alphaResult < 0)
							break;
						idx++;
					}
					file.add(idx, f);
				}
			} else if (tempcmd.equals("DELETE")) {
				System.out.println("-----------------------------------------------------------------");
				// DELETE command
				tempfile = sToken.nextToken();
				System.out.println(tempcmd + " " + tempfile);
				error = true;

				// Finds if the file already exists, if it does, it removes
				// it
				while (itr.hasNext()) {
					f = itr.next();
					if (f.getFilename().equals(tempfile)) {
						f.delete();
						itr.remove();
						error = false;
						break;
					}
				}

				if (error)
					System.out.println("ERROR: " + tempfile
							+ " - No such file.");
				else
					System.out.println(tempfile + " deleted successfully");
				
			} else if (tempcmd.equals("EXTEND")) {
				System.out.println("-----------------------------------------------------------------");
				// EXTEND command
				tempfile = sToken.nextToken();
				tempamt = Integer.parseInt(sToken.nextToken());
				System.out.println(tempcmd + " " + tempfile + " " + tempamt);

				// If the user enters a negative amount
				if (tempamt < 0) {
					System.out.println("ERROR: " + tempamt
							+ " is an invalid amount.");
					break;
				}
				error = true;

				// Finds the file and extends it
				while (itr.hasNext()) {
					f = itr.next();
					if (f.getFilename().equals(tempfile)) {
						error = false;
						f.extend(tempamt);
						break;
					}
					idx++;
				}

				if (error)
					System.out.println("ERROR: " + tempfile
							+ " - No such file.");
				else if (f.invalidAmt())
					System.out.println("ERROR: Insufficient disk space");
				else {
					System.out.println(tempfile + " extended successfully");
					file.set(idx, f);
				}
			} else if (tempcmd.equals("TRUNCATE")) {
				System.out.println("-----------------------------------------------------------------");
				// TRUNCATE command
				tempfile = sToken.nextToken();
				tempamt = Integer.parseInt(sToken.nextToken());
				System.out.println(tempcmd + " " + tempfile + " " + tempamt);

				// If the user enter a negative number
				if (tempamt < 0) {
					System.out.println("ERROR: " + tempamt
							+ " is an invalid amount.");
					break;
				}
				error = true;

				// Finds the file, and truncates it
				while (itr.hasNext()) {
					f = itr.next();
					if (f.getFilename().equals(tempfile)) {
						error = false;
						f.truncate(tempamt);
						break;
					}
					idx++;
				}
				if (error)
					System.out.println("ERROR: " + tempfile
							+ " - No such file.");
				else if (f.invalidAmt())
					System.out.println("ERROR: File Underflow");
				else {
					System.out.println(tempfile + " truncated successfully");
					file.set(idx, f);
				}
			} else if (tempcmd.equals("PRINT")) {
				System.out.println("-----------------------------------------------------------------");
				// PRINT command
				System.out.println(tempcmd);
				System.out.println(f.SystemInfo() + "\n");
				System.out.println("FREE LIST\n---------");
				f.freeList();
				System.out.println("\n");
				if (file.size() == 0) {
					System.out.println("FILES\n-----");
					System.out.println("No Files\n");
				} else {
					System.out.println("FILES\n-----");

					while (itr.hasNext()) {
						f = itr.next();
						System.out.println(f + "\n");
					}
				}
			} else if (tempcmd.equals("comment") || tempcmd.charAt(0) == '#') {
				//COMMENTS:
				//	Do nothing.
			} else {
				System.out.println("-----------------------------------------------------------------");
				//Invalid command
				System.out.println("ERROR: " + tempcmd
						+ " is an unrecognizable command.\n" + tempcmd
						+ " not processed.\n");
			}
		}// while
	}
}
