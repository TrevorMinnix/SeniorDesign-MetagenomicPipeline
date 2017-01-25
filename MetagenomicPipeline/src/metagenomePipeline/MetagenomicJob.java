package metagenomePipeline;

import java.nio.file.Path;

public class MetagenomicJob extends pipeline.Job{
	Path reads;
	Path trimmed;
	Path assembled;
	
	public MetagenomicJob(String jobID, Path reads, Path trimmed, 
			Path assembled) {
		super(jobID);
		this.reads = reads;
		this.trimmed = trimmed;
		this.assembled = assembled;
	}
	
}
