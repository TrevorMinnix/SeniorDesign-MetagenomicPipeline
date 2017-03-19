package metagenomePipeline;

public class MetagenomeJob extends pipeline.Job{
	//metadata
	String jobID;
	public Boolean pairedEnd, idba, megahit, metaspades;
	
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
	
	public MetagenomeJob(String jobID, Boolean pairedEnd, Boolean idba, Boolean megahit, 
			Boolean metaspades, String trimParam, String idbaParam, String megahitParam, String metaspadesParam, String trimmed, 
			String idbaAssembly, String idbaReadMap, String idbaStats, String idbaVisual,
			String megahitAssembly, String megahitReadMap, String megahitStats, String megahitVisual,
			String metaspadesAssembly, String metaspadesReadMap, String metaspadesStats, String metaspadesVisual) {
		super(jobID);
		this.jobID = jobID;
		this.pairedEnd = pairedEnd;
		this.idba = idba;
		this.megahit = megahit;
		this.metaspades = metaspades;
		this.trimParam = trimParam;
		this.idbaParam = idbaParam;
		this.megahitParam = megahitParam;
		this.metaspadesParam = metaspadesParam;
		this.idbaAssembly = idbaAssembly;
		this.idbaReadMap = idbaReadMap;
		this.idbaStats = idbaStats;
		this.idbaVisual = idbaVisual;
		this.megahitAssembly = megahitAssembly;
		this.megahitReadMap = megahitReadMap;
		this.megahitStats = megahitStats;
		this.megahitVisual = megahitVisual;
		this.metaspadesAssembly = metaspadesAssembly;
		this.metaspadesReadMap = metaspadesReadMap;
		this.metaspadesStats = metaspadesStats;
		this.metaspadesVisual = metaspadesVisual;
	}
}