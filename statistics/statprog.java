/*
statprog.java v2.1
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

1.2
correcting error in picard tool syntax

2.0
removed picard
created basic read mapping code (untested) - a whole lot of it

2.1
finally got the read-mapping to work on my small tests
(the research paper lies)


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
		wind100				-		Windows where they are fewer than 50% N
		contigs				-		Number of contigs in assembly.
		bigContigs			-		Number of contigs of large size
		Nnum				-		Number of Ns found
		contigArr			-		Array of contig sizes
		gcWind				-		CG% counter for windows (not the OS)
		NX					-		Array of NX values
		endOfFile			-		Indicates end of input has been reached
		WindowSize			-		Constant defining size of windows
		LargeSize			-		Constant defining minimum size of large contigs
		NSize				-		Constant defining for what size the expected number of Ns will be
		DebugMode			-		Constant determining if program will run in debug mode
		AllowedError		-		Maximum number of mismatchings allowed per each read mapping
	 */
	public static BigInteger len, cg, wind100, contigs, bigContigs, largestContig, Nnum;
	public static ArrayList<BigInteger> contigArr, gcWind;
	public static BigInteger[] NX;
	public static ArrayList<StringBuilder> genome;
	public static boolean endOfFile;
	
	public static long WindowSize = 100;
	public static long LargeSize = 1000;
	public static long NSize = 100000;
	
	public static int AllowedError = 5;
	
	public static final boolean DebugMode = false;
	
	public static Random r;
	
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		init();
		String input_file = "";
		String out_files = "stat_out"; // default
		
		// processing command-line arguments
		// allows for customization of statistics (mostly for testing purposes)
		for (int i = 0; i < args.length; ++i)
			switch(args[i].charAt(0)) {
			case 'I': input_file = args[i].substring(2); break;
			case 'W': WindowSize = Long.parseLong(args[i].substring(2)); break;
			case 'L': LargeSize = Long.parseLong(args[i].substring(2)); break;
			case 'N': NSize = Long.parseLong(args[i].substring(2)); break;
			case 'O': out_files = args[i].substring(2); break;
			case 'E': AllowedError = Integer.parseInt(args[i].substring(2)); break;
			}
		
		
		FirstScanner in = new FirstScanner(new File(input_file));
		
		// grab stats from a single sweep of the assembly
		read1(in);
		calculateNX();
		
		/*
		alright this next section is the read-mapping section
		and it's a doozy, i'll tell you that - i'll put some heavy comments in
		because i don't expect this to work on the first go and it may not work exactly as we want to in the end
		
		basic runthrough: i'm not doing the trees or any of that for the read-mapping, i'll leave that to someone else
		instead i saw the paper on the burrows-wheeler transform (BWT) in conjunction with suffix arrays (SA) and decided to use that
		since i have worked with SAs before
		
		the read mapping is that we generate the suffix array for the genome (in reality multiple suffix arrays for each contig)
		and then we'll create the BWT string from this, and then precalc a bunch of information
		using that information, we can quickly match all locations where the string matches in the genome with a given error (think edit distance
		if you remember dynamic programming).
		
		we select matchings for the reads (for now randomly because it is even more complex work to determine best matchings) and then
		we run through and calculate alignment statistics
		
		this is definitely the bottleneck on runtime for this program
		calculating a suffix array takes O(n*(logn)^2) time, which is supposedly good enough for genome work - however i'm doing it on a lot of
		contigs, not a single genome.  there's a lot of linear sweeps on the genome through here, but it's always a constant order of times.
		in addition, i'm storing all these suffix arrays for each contig
		in further addition, while i'm only temporarily storing the precalc arrays, i am running a recursive algorithm to find the matching
		intervals - this may have some concerns with stack and heap space in memory.  i could possibly improve this but it would be kinda difficult
		with how complicated this part already is
		so there's both a little runtime and memory concerns, which isn't a good scenario.  i don't know how much memory this program will be
		allocated when run, it might need to be given more than usual.
		
		there are definitely parallelizable elements to this (after all, each read can be mapped separately from one another based on how i'm doing it)
		but once again, this is already pretty complicated without also trying to parallelize it.  for now i'll keep it serial unless it does fail
		to run in a reasonable time.
		
		testing this is going to be quite a bundle of fun
		if i can't prove the mapping works properly in time for us to be finished, i'll just remove the mapping and work with the basics
		 */
		
		// assume there exists the read file with the same name as the assembly (aside from file type)
		ArrayList<String> reads = new ArrayList<String>();
		FastScanner newIn = new FastScanner(new File(input_file.replace(".fasta", ".fastq")));
		
		// collect all the reads (account for FASTQ format)
		read2(newIn, reads);
		
		// Create an array of arrays of triplets, storing, for each read, the SA interval and contig it was found in
		ArrayList<Trip>[] locs = new ArrayList[reads.size()];
		for (int i = 0; i < reads.size(); ++i)
			locs[i] = new ArrayList<Trip>();
		
		// store the SAs for each contig once found, they will be needed again later
		ArrayList<Suf[]> suffixArrays = new ArrayList<Suf[]>();
		ArrayList<Suf[]> rsuffixArrays = new ArrayList<Suf[]>();
		
		// iterate through each contig
		for (int i = 0; i < genome.size(); ++i) {
			
			// generate a suffix array on the contig, and also on the reversed contig (yes, we need that)
			Suf[] suffixArray = buildSuffixArray(genome.get(i));
			Suf[] rSuffixArray = buildSuffixArray(reverse(genome.get(i)));
			
			// create the BWT string for the SA, and the BWT string for the SA of the reversed contig
			StringBuilder BWT = new StringBuilder("");
			StringBuilder rBWT = new StringBuilder("");
			
			// go through and build the strings based on the SAs
			for (int j = 0; j < genome.get(i).length(); ++j) {
				BWT = BWT.append((suffixArray[j].ind + genome.get(i).length() - 1) % genome.get(i).length());
				rBWT = rBWT.append((rSuffixArray[j].ind + genome.get(i).length() - 1) % genome.get(i).length());
			}
			
			// precalc the C, O, and O' arrays for this contig
			// C[c]			-		Array containing the number of letters smaller than the given letter c in the contig
			// O[c][i]		-		Array containing the number of matches to the letter c in the prefix of length i in the BWT string
			// O'			-		Same as O, but on the rBWT string.  Called OP in code.
			
			// calculate C
			int[] C = new int[6]; // i am assuming only 6 relevant characters:  C, G, N, A, T, $
			for (int j = 0; j < genome.get(i).length(); ++j)
				for (int k = charCode(genome.get(i).charAt(j)) + 1; k < 6; ++k)
					++C[k];
			
			// calculate O and O'
			int[][] O = new int[6][genome.get(i).length()];
			int[][] OP = new int[6][genome.get(i).length()];
			
			for (int j = 0; j < BWT.length(); ++j) {
				if (j != 0)
					for (int k = 0; k < 6; ++k) {
						O[k][j] = O[k][j - 1];
						OP[k][j] = OP[k][j - 1];
					}
				O[charCode(BWT.charAt(j))][j]++;
				OP[charCode(rBWT.charAt(j))][j]++;
			}
			
			// iterate through each read and see if the range at which it matches in this contig (if it matches at all)
			for (int j = 0; j < reads.size(); ++j) {
				
				// okay whatever i explained previously, if i did fully explain, was a lie - i'll go back and edit the comments later
				// but we're instead generating matchings for the reversed reference, and also we're generating a list of intervals for each contig
				// that defines the range of indices where we match on the reversed suffix array
				ArrayList<Trip> ap = unify(presearch(reads.get(j), C, O, OP, AllowedError), i);
				for (int k = 0; k < ap.size(); ++k)
					locs[j].add(ap.get(k));
			}
			
			// store the SA we found, we will need it later
			suffixArrays.add(suffixArray);
			rsuffixArrays.add(rSuffixArray);
		}
		
		// The read mappings are a triplet:  the contig number, the location in the contig, and the length of the read
		// null indicates that a read was not mapped (this can happen)
		Map[] map = new Map[reads.size()];
		Arrays.fill(map, null);
		
		// find a mapping for each read from among its potential mappings
		for (int i = 0; i < locs.length; ++i) {
			
			// first find out the number of possible locations the read could be mapped to
			int sum = 0;
			for (int j = 0; j < locs[i].size(); ++j)
				if (locs[i].get(j) != null) {
					Trip temp = locs[i].get(j);
					sum += temp.b - temp.a + 1;
				}
			
			// randomly pick one of these locations and map the read to it
			int ind = r.nextInt(sum);
			for (int j = 0; j < locs[i].size(); ++j)
				if (locs[i].get(j) != null) {
					Trip temp = locs[i].get(j);
					if (ind < temp.b - temp.a + 1) {
						map[i] = new Map(temp.c, temp.a + ind, reads.get(i).length());
						break;
					}
					ind -= temp.b - temp.a + 1;
				}
		}
		
		// we're going to treat the alignment statistics evaluation as a sweep on intervals
		// since this requires all "events" to be sorted, we'll use a priority queue to pull out events in order
		PriorityQueue<Event> q = new PriorityQueue<Event>();
		
		// first add each contig start and end
		for (int i = 0; i < genome.size(); ++i) {
			q.add(new Event(i, 0, 0));
			q.add(new Event(i, genome.get(i).length(), 3));
		}
		
		// next add each mapping start and end
		for (int i = 0; i < map.length; ++i) if (map[i] != null) {
			q.add(new Event(map[i].cont, genome.get(map[i].cont).length() - 1 - rsuffixArrays.get(map[i].cont)[map[i].loc].ind - map[i].len, 1));
			q.add(new Event(map[i].cont, genome.get(map[i].cont).length() - 1 - rsuffixArrays.get(map[i].cont)[map[i].loc].ind, 2));
		}
		
		// now here comes the fun part, we need to pull out the events and keep track of a lot of info to calculate the stats
		long align = 0, dupalign = 0, numGaps = 0, longestAlign = 0, fullUnalign = 0, partUnalign = 0;
		int prev = 0, prev2 = 0, curContig = -1, stack = 0, prevEnd = 0;
		boolean inContig = false;
		
		// first count the number of reads that are not aligned at all, as well as the longest part of the assembly that is aligned
		for (int i = 0; i < map.length; ++i) {
			if (map[i] == null || map[i].loc + map[i].len < 0 || map[i].loc >= genome.get(map[i].cont).length())
				++fullUnalign;
			else
				longestAlign = Math.max(longestAlign, Math.min(genome.get(map[i].cont).length(),
						rsuffixArrays.get(map[i].cont)[map[i].loc].ind + map[i].len) - rsuffixArrays.get(map[i].cont)[map[i].loc].ind);
		}
		
		// now go through all "events" (start/ends of reads and contigs)
		while (!q.isEmpty()) {
			Event e = q.poll();
			
			// we have moved on to a new contig, update everything - some of this is redundant but just for peace of mind and safety
			if (e.cont != curContig) {
				prev = 0;
				prev2 = 0;
				stack = 0;
				prevEnd = 0;
				curContig = e.cont;
				inContig = false;
			}
			
			// check the event type we are on
			switch (e.type) {
			case 0: // this is the beginning of a contig
				partUnalign += stack;
				inContig = true; // we are now in a contig
				break;
				
			case 1: // this is the beginning of a read
				// this is the first read for this part of the contig, so update the values before incrementing the stack
				if (stack == 0) {
					prev = e.loc;
					if (prevEnd != e.loc)
						++numGaps;
				}
				
				// this is the second read of this part of the contig, so update the first encountered location of duplicate coverage
				if (stack == 1)
					prev2 = e.loc;
				
				// push the "stack" - this is like a parsing problem, but it's primitive enough that we don't need a real stack, just a counter
				++stack;
				break;
				
			case 2: // this is end of a read
				// we are ending coverage, so update some stuff
				if (stack == 1) {
					if (inContig)
						align += e.loc - prev;
					else
						align = genome.get(curContig).length() - prev;
					prevEnd = e.loc;
				}
				
				// we are ending duplicate coverage, so update
				else if (stack == 2) {
					if (inContig)
						dupalign += e.loc - prev2;
					else
						dupalign = genome.get(curContig).length() - prev2;
				}
				
				// if we ended outside of a contig, then this read is partially unaligned
				// (this is technically false due to the edit distance allowed error, but that is small enough error to ignore, hopefully)
				if (!inContig)
					++partUnalign;
				
				// pop off the "stack"
				--stack;
				break;
				
			case 3: // this is the end of a contig
				inContig = false;
				break;
			}
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
			out.printf("Aligned genome:  %.9f\n", ((double) align) / (Double.parseDouble(len.toString())));
			out.printf("Duplication rate:  %.9f\n", ((double) dupalign) / (Double.parseDouble(len.toString())));
			out.printf("Number of gaps: %d\n", numGaps);
			out.printf("Largest alignment length:  %d\n", longestAlign);
			out.printf("Number of fully unaligned reads:  %d\n", fullUnalign);
			out.printf("Number of partially unaligned reads:  %d\n", partUnalign);
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
		genome = new ArrayList<StringBuilder>();
		r = new Random();
	}
	
	// splitting the reading into its own function
	// for better readability, more of this should be put into separate functions
	// it's kinda spaghetti-i right now and debugging this will be difficult when i finally get to testing
	// this SHOULD account for FASTA file format
	public static void read1(FirstScanner in) {
		
		String str = "";
		int idx = 0;
		boolean inContig = false;
		
		// N counts the number of N nucleotides found in a given window of 100
		// QUAST does this, not sure if that's relevant for this project
		long N = 0;
		
		// ind marks how far in the window of 100 we have gone
		long ind = 0;
		
		genome.add(new StringBuilder(""));
		
		// prev will mark the number of cumulative GCs found in previous windows of 100
		BigInteger prev = BigInteger.ZERO;
		
		// size marks the size of the current contig
		BigInteger size = BigInteger.ZERO;
					
		do {
			if (idx == str.length()) {
				idx = 0;
				while (in.badLine)
					str = in.next();
//				if (DebugMode && str.charAt(0) == '$')
//					endOfFile = true;
			}
			
			if (!endOfFile && str.charAt(0) != '>' && str.charAt(0) != ';') {
			
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
				
				genome.get(genome.size() - 1).append(str.charAt(idx));
			}
			
			// this executes when we are finished with the current contig
			else if (inContig) {
				genome.get(genome.size() - 1).append("$");
				genome.add(new StringBuilder(""));
				inContig = false;
				updateContigs(size);
				
				size = BigInteger.ZERO;
			}
			
			// this executes if we encounter a line we shouldn't be reading (starts with ; or >)
			else {
				in.badLine = true;
			}
			++idx;
		} while (!endOfFile);
	}
	
	// get the reads - hopefully this accounts for FASTQ format correctly
	public static void read2(FastScanner in, ArrayList<String> a) {
		endOfFile = false;
		while (!endOfFile) {
			String temp = in.next();
			if (in.x == 2)
				a.add(temp);
			else {
				int t = in.x;
				while (t == in.x) in.next();
			}
		}
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
	
	public static Suf[] buildSuffixArray(StringBuilder str) {
		Suf[] a = new Suf[str.length()];
		for (int i = 0; i < a.length; ++i)
			a[i] = new Suf(i, str.charAt(i) - '$', (i + 1 < a.length) ? (str.charAt(i + 1) - '$') : -1);
		Arrays.sort(a);
		int[] ind = new int[a.length];
		Arrays.fill(ind, 0);
		for (int k = 4; k < 2 * a.length; k = k * 2) {
			int rank = 0;
			int prevRank = a[0].rank[0];
			a[0].rank[0] = rank;
			ind[a[0].ind] = 0;
			for (int i = 1; i < a.length; ++i) {
				if (a[i].rank[0] == prevRank && a[i].rank[1] == a[i - 1].rank[1]) {
					prevRank = a[i].rank[0];
					a[i].rank[0] = rank;
				}
				else {
					prevRank = a[i].rank[0];
					a[i].rank[0] = ++rank;
				}
				ind[a[i].ind] = i;
			}
			for (int i = 0; i < a.length; ++i) {
				int nextInd = a[i].ind + k / 2;
				a[i].rank[1] = (nextInd < a.length) ? a[ind[nextInd]].rank[0] : -1;
			}
			Arrays.sort(a);
		}
		return a;
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
	
	public static int charCode(char c) {
		switch (c) {
		case '$': return 0;
		case 'A': return 1;
		case 'C': return 2;
		case 'G': return 3;
		case 'N': return 4;
		default: return 5;
		}
	}
	
	public static class Map {
		int cont, loc, len;
		Map(int a, int b, int c) {
			cont = a;
			loc = b;
			len = c;
		}
	}
	
	public static class Trip {
		int a, b, c;
		Trip(int x, int y, int z) {
			a = x;
			b = y;
			c = z;
		}
	}
	
	public static ArrayList<Trip> union(ArrayList<Trip> a, ArrayList<Trip> b) {
		if (a == null && b == null)
			return null;
		if (a == null)
			return b;
		if (b == null)
			return a;
		for (int i = 0; i < b.size(); ++i)
			a.addAll(b);
		return a;
	}
	
	public static ArrayList<Trip> presearch(String s, int[] C, int[][] O, int[][] OP, int e) {
		int[] D = calcD(s, C, OP);
		return search(D, C, OP, s, s.length() - 1, e, 1, O[0].length - 1);
	}
	
	public static int[] calcD(String s, int[] C, int[][] O) {
		int z = 0, k = 1, l = O[0].length - 1;
		int[] ret = new int[s.length()];
		for (int i = 0; i < s.length(); ++i) {
			k = C[charCode(s.charAt(i))] + O[charCode(s.charAt(i))][k - 1] + 1;
			l = C[charCode(s.charAt(i))] + O[charCode(s.charAt(i))][l];
			if (k > l) {
				k = 1;
				l = O[0].length - 1;
				z++;
			}
			ret[i] = z;
		}
		return ret;
	}
	
	public static ArrayList<Trip> search(int[] D, int[] C, int[][] O, String s, int i, int z, int k, int l) {
		if (i < 0) {
			if (k > l)
				return null;
			ArrayList<Trip> ret = new ArrayList<Trip>();
			ret.add(new Trip(k, l, z));
			return ret;
		}
		if (z < D[i])
			return null;
		ArrayList<Trip> temp = search(D, C, O, s, i - 1, z - 1, k, l);
		for (char c : new char[]{'A', 'C', 'G', 'T'}) {
			int tk = C[charCode(c)] + O[charCode(c)][k - 1] + 1;
			int tl = C[charCode(c)] + O[charCode(c)][l];
			if (tk <= tl) {
				temp = union(temp, search(D, C, O, s, i, z - 1, tk, tl));
				if (c == s.charAt(i))
					temp = union(temp, search(D, C, O, s, i - 1, z, tk, tl));
				else
					temp = union(temp, search(D, C, O, s, i - 1, z - 1, tk, tl));
			}
		}
		return temp;
	}
	
	public static StringBuilder reverse(StringBuilder s) {
		StringBuilder ret = new StringBuilder("");
		for (int i = s.length() - 1; i >= 0; --i)
			if (s.charAt(i) != '$')
				ret = ret.append(s.charAt(i));
		ret = ret.append("$");
		return ret;
	}
	
	// this is an improvement to the java Scanner class
	// Scanner is quite slow on large inputs, and genomes can get large
	// FastScanner has similar functionality but uses BufferedReader, which is faster
	public static class FastScanner {
		BufferedReader br;
		StringTokenizer st;
		int x;
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
					x = (x + 1) % 4;
				} catch (Exception e) {
					endOfFile = true;
					return "";
				}
			}
			return st.nextToken();
		}
	}
	
	public static class FirstScanner {
		BufferedReader br;
		StringTokenizer st;
		boolean badLine;
		FirstScanner(File f) throws FileNotFoundException {
			br = new BufferedReader(new FileReader(f));
			st = new StringTokenizer("");
		}
		FirstScanner(InputStream in) {
			br = new BufferedReader(new InputStreamReader(in));
			st = new StringTokenizer("");
		}
		String next() {
			while (!st.hasMoreTokens()) {
				try {
					st = new StringTokenizer(br.readLine());
					badLine = false;
				} catch (Exception e) {
					endOfFile = true;
					return "";
				}
			}
			return st.nextToken();
		}
	}
	public static class Suf implements Comparable<Suf> {
		int ind;
		int[] rank;
		Suf(int a, int b, int c) {
			ind = a;
			rank = new int[2];
			rank[0] = b;
			rank[1] = c;
		}
		public int compareTo(Suf s) {
			return (rank[0] == s.rank[0]) ? (rank[1] - s.rank[1]) : (rank[0] - s.rank[0]);
		}
	}
	/*
	Event types:
	0:  Beginning of contig
	1:  Beginning of read mapping
	2:  End of read mapping
	3:  End of contig
	 */
	public static class Event implements Comparable<Event> {
		int cont, loc, type;
		Event(int a, int b, int c) {
			cont = a;
			loc = b;
			type = c;
		}
		public int compareTo(Event e) {
			if (cont == e.cont) {
				if (loc == e.loc)
					return type - e.type;
				return loc - e.loc;
			}
			return cont - e.cont;
		}
	}
	
	public static ArrayList<Trip> unify(ArrayList<Trip> a, int cont) {
		ArrayList<Trip> ret = new ArrayList<Trip>();
		PriorityQueue<Event> q = new PriorityQueue<Event>();
		for (int i = 0; i < a.size(); ++i) {
			q.add(new Event(cont, a.get(i).a, 1));
			q.add(new Event(cont, a.get(i).b, 2));
		}
		Trip curTrip = null;
		int depth = 0;
		while (!q.isEmpty()) {
			Event cur = q.poll();
			if (cur.type == 1) {
				if (curTrip == null)
					curTrip = new Trip(cur.loc, cur.loc, cur.cont);
				else if (depth == 0 && curTrip.b + 1 == cur.loc)
					curTrip.b = cur.loc;
				else if (depth == 0) {
					ret.add(curTrip);
					curTrip = new Trip(cur.loc, cur.loc, cur.cont);
				}
				else
					curTrip.b = cur.loc;
				++depth;
			}
			else {
				curTrip.b = cur.loc;
				--depth;
			}
		}
		ret.add(curTrip);
		return ret;
	}
}
