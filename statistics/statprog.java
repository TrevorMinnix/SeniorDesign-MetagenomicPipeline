/*
statprog.java v0.1
senior design group 8

VERSION NOTES:
0.1
adding beginning work to github
sorry i've been out of contact, and sorry i took a while to upload this
first i'm adding the basic data, using metaQUAST as a guideline for which statistics constitute that
i am not going to pull req this until i have tested it and standardized input for our pipeline

0.2
finished up, standardized input to work with fasta and tested basic stats
everything works as desired so far
code still needs to be cleaned up and unspaghetti-fied

program description:
takes the data from a genome assembler and provides statistics on it for a particular data set
further details will be provided throughout the program

DEBUG MODE INSTRUCTIONS
program will not accept from a file, but from system.in
program input will end when the string "$" is entered
please follow fasta format when debugging save for the terminating string
output will be provided via system.out

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
	public static boolean endOfFile;
	
	public static final long WindowSize = 100;
	public static final long LargeSize = 1000;
	public static final long NSize = 100000;
	
	public static final boolean DebugMode = true;
	
	
	public static void main(String[] args) throws FileNotFoundException {
		
		endOfFile = false;
		
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
		
		Nnum = BigInteger.ZERO;
		
		// contigArr - List of contig sizes.  This is not a statistic, but values used to obtain a later one
		contigArr = new ArrayList<BigInteger>();
		
		// gcWind - GC% counter for windows.  counts the number of GC base pairs in windows of size 100.
		gcWind = new ArrayList<BigInteger>();
		
		// NX - NX array.  calculates all NX values for 0 < X <= 100
		NX = new BigInteger[101];
		
		FastScanner in;
		if (DebugMode)
			in = new FastScanner(System.in);
		else
			in = new FastScanner(new File("assembly.fasta"));
		read(in);
		
		// sort contigs by size to calculate NX array
		Collections.sort(contigArr);
		BigInteger sum = contigArr.get(contigArr.size() - 1);
		int ind = contigArr.size() - 2;
		
		for (int i = 1; i <= 100; ++i) {
			while (((double) sum.longValue()) / ((double) len.longValue()) < ((double) i) / 100.0)
				sum = sum.add(contigArr.get(ind--));
			NX[i] = contigArr.get(ind + 1);
		}
		
		if (DebugMode)
			sysprint();
		
	}
	
	public static class FastScanner {
		BufferedReader br;
		StringTokenizer st;
		FastScanner(File f) throws FileNotFoundException {
			br = new BufferedReader(new FileReader(f));
			st = new StringTokenizer("");
		}
		FastScanner(InputStream in) {
			br = new BufferedReader(new InputStreamReader(in));
			st = new StringTokenizer("");
		}
		String next() {
			while (!st.hasMoreTokens()) {
				try {
					st = new StringTokenizer(br.readLine());
				} catch (Exception e) {
					endOfFile = true;
					return "";
				}
			}
			return st.nextToken();
		}
	}
	
	// splitting the reading into its own function
	// for better readability, more of this should be put into separate functions
	// it's kinda spaghetti-i right now and debugging this will be difficult when i finally get to testing
	public static void read(FastScanner in) {
		
		String str = "";
		int idx = 0;
		boolean inContig = false;
		
		// N counts the number of N nucleotides found in a given window of 100
		// QUAST does this, not sure if that's relevant for this project
		int N = 0;
		
		// ind marks how far in the window of 100 we have gone
		int ind = 0;
		
		// prev will mark the number of cumulative GCs found in previous windows of 100
		BigInteger prev = BigInteger.ZERO;
		
		// size marks the size of the current contig
		BigInteger size = BigInteger.ZERO;
					
		do {
			if (idx == str.length()) {
				idx = 0;
				str = in.next();
				if (DebugMode && str.charAt(0) == '$')
					endOfFile = true;
			}
			
			if (!endOfFile && str.charAt(0) != '>') {
			
				inContig = true;
				
				// contigs end when next one is found or file neds
				++ind;
				size = size.add(BigInteger.ONE);
				
				if (str.charAt(idx) != 'N')
					++N;
				else
					Nnum = Nnum.add(BigInteger.ONE);
				
				len = len.add(BigInteger.ONE);
				
				if (str.charAt(idx) == 'C' || str.charAt(idx) == 'G')
					cg = cg.add(BigInteger.ONE);
				
				if (ind % WindowSize == 0) {
					ind = 0;
					
					if (N >= (WindowSize + 1) / 2)
						wind100 = wind100.add(BigInteger.ONE);
					
					N = 0;
					gcWind.add(cg.subtract(prev));
					prev = cg;
				}
			}
			else if (inContig) {
				inContig = false;
				contigs = contigs.add(BigInteger.ONE);
				contigArr.add(size);
				
				if (size.compareTo(BigInteger.valueOf(LargeSize)) >= 0)
					bigContigs = bigContigs.add(BigInteger.ONE);
				
				size = BigInteger.ZERO;
			}
			++idx;
		} while (!endOfFile);
	}
	
	public static void sysprint() {
		System.out.printf("Assembly Length: %s\n", len.toString());
		System.out.printf("Number of contigs: %s\n", contigs.toString());
		System.out.printf("Number of clean windows of size %d:  %s\n", WindowSize, wind100.toString());
		System.out.printf("Number of large contigs:  %s\n", bigContigs.toString());
		System.out.printf("List of contig sizes:\n");
		for (int i = 0; i < contigArr.size(); ++i)
			System.out.printf("%s ", contigArr.get(i).toString());
		System.out.printf("\nGC comp:  %.9f\n", ((double) cg.longValue()) / ((double) len.longValue()));
		System.out.printf("CG content per windows of size %d:\n", WindowSize);
		for (int i = 0; i < gcWind.size(); ++i)
			System.out.printf("%.9f ", ((double) gcWind.get(i).longValue()) / 100.0);
		System.out.printf("\nNX array:\n");
		for (int i = 1; i < NX.length; ++i)
			System.out.printf("%s ", NX[i].toString());
		System.out.printf("\nNumber of N per %dbp:  %.9f\n", NSize, ((double) Nnum.longValue()) / ((double) len.longValue()) * NSize);
	}
}
