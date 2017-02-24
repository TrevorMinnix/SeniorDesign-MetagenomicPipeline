/*
statprog.java v1.0
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

0.3
updated code so it is more readable, less spaghetti code

1.0
finished reference statistics, needs testing to make sure i'm calling picard right and parsing the input
needs some commentation but i'll do that later

1.1
add some basic output, right now just a text file


program description:
takes the data from a genome assembler and provides statistics on it for a particular data set
further details will be provided throughout the program

DEBUG MODE INSTRUCTIONS
program will not accept from a file, but from system.in
program input will end when the string "$" is entered
please follow fasta format when debugging save for the terminating string
output will be provided via system.out

*/

import java.util.*; // ArrayList, StringTokenizer
import java.math.*; // BigInteger
import java.io.*;   // BufferedReader, FileReader, File, FileNotFoundException
public class statprog {
	
	// i am using BigIntegers to be safe, because i'm not quite sure how big some genomes might get
	// a long probably works, but whatever
	
	/*
	variable explanations
		cg					-		CG% counter.  counts the total number of GC pairs in the assembly
		len					-		Length of assembly
		wind100				-	Windows where they are fewer than 50% N
		contigs				-	Number of contigs in assembly.
		bigContigs			-	Number of contigs of large size
		Nnum				-	Number of Ns found
		contigArr			-	Array of contig sizes
		gcWind				-	CG% counter for windows (not the OS)
		NX					-	Array of NX values
		calculateReference	- 
		endOfFile			-	Indicates end of input has been reached
		WindowSize			-	Constant defining size of windows
		LargeSize			-	Constant defining minimum size of large contigs
		NSize				-	Constant defining for what size the expected number of Ns will be
		DebugMode			-	Constant determining if program will run in debug mode
	 */
	public static BigInteger len, cg, wind100, contigs, bigContigs, largestContig, Nnum;
	public static ArrayList<BigInteger> contigArr, gcWind;
	public static BigInteger[] NX;
	public static boolean calculateReference;
	public static boolean endOfFile;
	
	public static long WindowSize = 100;
	public static long LargeSize = 1000;
	public static long NSize = 100000;
	
	public static final boolean DebugMode = false;
	
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		init();
		String input_file = "";
		String ref_file = "";
		String out_files = "stat_out";
		for (int i = 0; i < args.length; ++i)
			switch(args[i].charAt(0)) {
			case 'I': input_file = args[i].substring(2); break;
			case 'R':
				calculateReference = true;
				ref_file = args[i].substring(2);
				break;
			case 'W': WindowSize = Long.parseLong(args[i].substring(2)); break;
			case 'L': LargeSize = Long.parseLong(args[i].substring(2)); break;
			case 'N': NSize = Long.parseLong(args[i].substring(2)); break;
			case 'O': out_files = args[i].substring(2); break;
			}
		
		
		FastScanner in;
		if (DebugMode)
			in = new FastScanner(System.in);
		else
			in = new FastScanner(new File(input_file));
		read(in);
		
		calculateNX();
		
		
		if (calculateReference) {
			ProcessBuilder pb = new ProcessBuilder("java", "-Xmx2g", "-jar", "picard.jar", "FastqToSam", "F1="+ref_file, "O=pout.bam");
			Process p = pb.start();
			p.waitFor();
			pb = new ProcessBuilder("java", "-Xmx2g", "-jar", "picard.jar", "CollectMultipleMetrics", "I=pout.bam", "O="+out_files, "R=reference_sequence.fasta");
			p = pb.start();
			p.waitFor();
		}
		
		if (DebugMode)
			sysprint();
		else {
			PrintWriter out = new PrintWriter(new File("norm_"+out_files+".txt"));
			out.printf("Assembly Length: %s\n", len.toString());
			out.printf("Number of contigs: %s\n", contigs.toString());
			out.printf("Number of clean windows of size %d:  %s\n", WindowSize, wind100.toString());
			out.printf("Number of large contigs:  %s\n", bigContigs.toString());
			out.printf("List of contig sizes: %d\n", contigArr.size());
			for (int i = 0; i < contigArr.size(); ++i)
				out.printf("%s ", contigArr.get(i).toString());
			out.printf("\nGC comp:  %.9f\n", ((double) cg.longValue()) / ((double) len.longValue()));
			out.printf("CG content per windows of size %d: %d\n", WindowSize, gcWind.size());
			for (int i = 0; i < gcWind.size(); ++i)
				out.printf("%.9f ", ((double) gcWind.get(i).longValue()) / 100.0);
			out.printf("\nNX array:\n");
			for (int i = 1; i < NX.length; ++i)
				out.printf("%s ", NX[i].toString());
			out.printf("\nNumber of N per %dbp:  %.9f\n", NSize, ((double) Nnum.longValue()) / ((double) len.longValue()) * NSize);
			out.close();
		}
		
	}
	
	public static void init() {
		endOfFile = false;
		cg = BigInteger.ZERO;
		len = BigInteger.ZERO;
		wind100 = BigInteger.ZERO;
		contigs = BigInteger.ZERO;
		bigContigs = BigInteger.ZERO;
		Nnum = BigInteger.ZERO;
		contigArr = new ArrayList<BigInteger>();
		gcWind = new ArrayList<BigInteger>();
		NX = new BigInteger[101];
		WindowSize = 100;
		LargeSize = 1000;
		NSize = 100000;
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
		long N = 0;
		
		// ind marks how far in the window of 100 we have gone
		long ind = 0;
		
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
				
				if (isCG(str.charAt(idx)))
					cg = cg.add(BigInteger.ONE);
				
				// end of window has been reached
				if (ind % WindowSize == 0) {
					updateWindows(N, prev);
					
					prev = cg;
					ind = 0;
					N = 0;
				}
			}
			
			// this executes when we are finished with the current contig
			else if (inContig) {
				inContig = false;
				updateContigs(size);
				
				size = BigInteger.ZERO;
			}
			++idx;
		} while (!endOfFile);
	}
	
	public static void updateWindows(long N, BigInteger prev) {
		if (N >= (WindowSize + 1) / 2)
			wind100 = wind100.add(BigInteger.ONE);
		
		gcWind.add(cg.subtract(prev));
	}
	
	public static void updateContigs(BigInteger size) {
		contigs = contigs.add(BigInteger.ONE);
		contigArr.add(size);
		
		if (size.compareTo(BigInteger.valueOf(LargeSize)) >= 0)
			bigContigs = bigContigs.add(BigInteger.ONE);
	}
	
	public static boolean isCG(char c) {
		return c == 'C' || c == 'G';
	}
	
	public static void calculateNX() {
		Collections.sort(contigArr);
		
		BigInteger sum = contigArr.get(contigArr.size() - 1);
		int ind = contigArr.size() - 2;
		
		for (int i = 1; i <= 100; ++i) {
			
			while (((double) sum.longValue()) / ((double) len.longValue()) < ((double) i) / 100.0)
				sum = sum.add(contigArr.get(ind--));
			
			NX[i] = contigArr.get(ind + 1);
		}
	}
	
	// this might look ugly, but it's for debug purposes only
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
	
	// this is an improvement to the java Scanner class
	// Scanner is quite slow on large inputs, and genomes can get large
	// FastScanner has similar functionality but uses BufferedReader, which is faster
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
}
