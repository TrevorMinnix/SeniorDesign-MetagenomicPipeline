Current statistics included in this program:

- Assembly length
- Number of contigs
- Number of "clean" windows of a given size (default 100bp).  A "clean" window is defined as one with <50% 'N' base - pairs.
- Number of "large" contigs.  A "large" contig is one above a given size (default 1000bp).
- List of contig sizes
- GC% composition
- GC% composition of windows of a given size (default 100bp).
- NX values (1-100)
- Expected number of 'N' pairs per a given number of nucleotides (default 10000bp).

If the untested BWT read-mapping works, I also have implemented these:

- % coverage of contigs by reads
- Number of gaps between mapped reads
- Number of unaligned reads (partial and full)
- % duplicate coverage of contigs by reads
- Longest mapping of a single read

The program right now assumes that there exists a FASTA and FASTQ file both with the same name (aside from filetype) available to it.  The FASTA file is assumed to hold contigs, not scaffolds or chromosomes.  It takes the following arguments by command line (ones with * are mandatory):

I=input_file_name (*)
Determines what the name of the input file is (in FASTA format, such as filename.fasta).

W=window_size
Determines what the size of "windows" examined should be.  Default is 100bp.

L=large_size
Determines at what size a contig will be considered "large."  Default is 1000bp.

N=n_size
Determines the relative size where the expected number of N pairs will be.  Default is 10000bp.
(NOTE:  Because the program is working with contigs, not scaffolds, this may be pointless.
I may either alter the program to also calculate via scaffolds, or simply remove this statistic)

O=out_file
Determines what the output file name should be.  Default is stat_out.

E=allowed_error
Determines at what edit distance a substring of a contig will match to a read.  Default is 5.
