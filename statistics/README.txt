Run the java file statprog with the following arguments in any order (arguments with a * are required):

I=input_file_name.fasta*
Where input_file_name is the name of the assembly being provided

O=output_file_names
Give a desired name for the output files (there are multiple).  Default is stat_out

R=reference_file_name.fastq
Provide the reference data for a genome.  If not provided, reference statistics will not be provided.

W=window_size
Where window_size is an integer specifying the desired length of windows to calculate GC% in, by base pairs.  Default is 100.

L=large_contig_size
Where contig_size is an integer specifying the smallest length of a contig that will be considered a "large" one, by base pairs.  Default is 1000.

N=n_size
Where n_size is the desired size of the genome where the average number of N characters in the assembly will be calculated, in base pairs.  Default is 100000.
