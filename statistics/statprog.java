/*
statprog.java v0.1
senior design group 8
WARNING:  THIS PROGRAM HAS NOT YET BEEN TESTED
WARNING:  THIS PROGRAM HAS NOT STANDARDIZED ITS INPUT YET
WARNING:  THIS PROGRAM DOES NOT PROVIDE OUTPUT YET
(although it does a bunch of the calculations)

*******************************************
******* DO NOT RUN THIS PROGRAM YET *******
*******************************************

VERSION NOTES:
0.1
adding beginning work to github
sorry i've been out of contact, and sorry i took a while to upload this
first i'm adding the basic data, using metaQUAST as a guideline for which statistics constitute that
i am not going to pull req this until i have tested it and standardized input for our pipeline

program description:
takes the data from a genome assembler and provides statistics on it for a particular data set
further details will be provided throughout the program

 */

import java.util.*; // ArrayList
import java.math.*; // BigInteger
import java.io.*;   // BufferedReader, FileReader, File, FileNotFoundException
public class statprog {
	
	// i am using BigIntegers to be safe, because i'm not quite sure how big some genomes might get
	// a long probably works, but whatever
	public static BigInteger len, cg, wind100, contigs, bigContigs, largestContig, Nnum;
	public static ArrayList<BigInteger> contigArr, gcWind;
	public static BigInteger[] NX;
	
	
	public static void main(String[] args) throws FileNotFoundException {
		
		// cg - CG% counter.  counts the total number of GC pairs in the assembly
		cg = BigInteger.ZERO;
		
		// len - Length of assembly.
		len = BigInteger.ZERO;
		
		// wind100 - Windows of size 100bp where the exact pairings are known for half or more of the window
		wind100 = BigInteger.ZERO;
		
		// contigs - Number of contigs in assembly.
		// for now, program assumes assembly is in multiFASTA format.  this can be altered if necessary
		contigs = BigInteger.ZERO;
		
		// bigContigs - Number of contigs of size > 1000
		bigContigs = BigInteger.ZERO;
		
		// contigArr - List of contig sizes.  This is not a statistic, but values used to obtain a later one
		contigArr = new ArrayList<BigInteger>();
		
		// gcWind - GC% counter for windows.  counts the number of GC base pairs in windows of size 100.
		gcWind = new ArrayList<BigInteger>();
		
		// NX - NX array.  calculates all NX values for 0 < X <= 100
		NX = new BigInteger[101];
		
		// INPUT IS NOT STANDARDIZED - check with the rest of project to find what the file is named and in what format
		BufferedReader assemblerOutput = new BufferedReader(new FileReader(new File("output.fasta")));
		
		read(assemblerOutput);
		
		// sort contigs by size to calculate NX array
		Collections.sort(contigArr);
		BigInteger sum = contigArr.get(contigArr.size() - 1);
		int ind = contigArr.size() - 2;
		
		for (int i = 1; i <= 100; ++i) {
			while (sum.compareTo(new BigInteger(Integer.toString(i)).multiply(len)) == 1)
				sum = sum.add(contigArr.get(ind--));
			NX[i] = sum;
		}
		
	}
	
	// splitting the reading into its own function
	// for better readability, more of this should be put into separate functions
	// it's kinda spaghetti-i right now and debugging this will be difficult when i finally get to testing
	public static void read(BufferedReader in) {
		
		char c;
		
		// N counts the number of N nucleotides found in a given window of 100
		// QUAST does this, not sure if that's relevant for this project
		int N = 0;
		
		// ind marks how far in the window of 100 we have gone
		int ind = 0;
		
		// prev will mark the number of cumulative GCs found in previous windows of 100
		BigInteger prev = BigInteger.ZERO;
		
		// size marks the size of the current contig
		BigInteger size = BigInteger.ZERO;
		
		
		contigs = contigs.add(BigInteger.ONE);
		
		// try-catch because of input exception
		try {
			do {
				c = (char) in.read();
				
				// contigs end with a newline
				// NOTE:  CURRENT VERSION DOES NOT PARSE FASTA FILES CORRECTLY YET
				if (c != '\n') {
					
					++ind;
					size = size.add(BigInteger.ONE);
					
					if (c != 'N')
						++N;
					else
						Nnum = Nnum.add(BigInteger.ONE);
					
					len = len.add(BigInteger.ONE);
					
					if (c == 'C' || c == 'G')
						cg = cg.add(BigInteger.ONE);
					
					if (ind % 100 == 0) {
						ind = 0;
						
						if (N >= 50)
							wind100 = wind100.add(BigInteger.ONE);
						
						N = 0;
						gcWind.add(cg.subtract(prev));
						prev = cg;
					}
				}
				else {
					contigs = contigs.add(BigInteger.ONE);
					contigArr.add(size);
					
					if (size.compareTo(new BigInteger("1000")) >= 0)
						bigContigs = bigContigs.add(BigInteger.ONE);
					
					size = BigInteger.ZERO;
				}
			} while (c != '\n');
			
		} catch (Exception e) {
			// assuming this exception will be reached when file ends and another read attempt is made
			// i don't usually do exception handling, i prefer simply coding to avoid the exception
			// but this seemed like the best way for a BufferedReader
			
			//System.out.printf("finished reading\n");
			return;
		}
	}
}
