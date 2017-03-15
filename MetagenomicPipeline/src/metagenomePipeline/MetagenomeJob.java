package metagenomePipeline;

import java.nio.file.Path;

public class MetagenomeJob extends pipeline.Job{
	//metadata
	String jobID;
	Boolean pairedEnd, idba, megahit, metaspades;
	
	//tool parameters
	String trimParam, idbaParam, megahitParam, metaspadesParam;
	
	//file paths
	String trimmed;
	String idbaAssembly, idbaReadMap, idbaStats, idbaVisual;
	String megahitAssembly, megahitReadMap, megahitStats, megahitVisual;
	String metaspadesAssembly, metaspadesReadMap, metaspadesStats, metaspadesVisual;
	
	public MetagenomeJob(String jobID) {
		super(jobID);
		this.jobID = jobID;
	}
	
	
}