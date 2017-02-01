package metagenomePipeline;

import java.nio.file.Path;

public class MetagenomeJob extends pipeline.Job{
	private Path reads;
	private Path trimmed;
	private Path assembled;
	String trimParam;
	String idbaParam;
	String metahitParam;
	String spadesParam;
	
	public MetagenomeJob(String jobID, Path reads, Path trimmed, 
			Path assembled) {
		super(jobID);
		this.reads = reads;
		this.trimmed = trimmed;
		this.assembled = assembled;
	}
	
	public void setTrimParam(String param){
		trimParam = param;
	}
	
	public void setIdbaParam(String param){
		idbaParam = param;
	}
	
	public void setMetahitParam(String param){
		metahitParam = param;
	}
	
	public void setSpadesParam(String param){
		spadesParam = param;
	}
}
