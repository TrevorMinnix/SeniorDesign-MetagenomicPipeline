package metagenomePipeline;

public class MetagenomeJob extends pipeline.Job{
	//metadata
	String jobID;
	public Boolean pairedEnd, idba, megahit, metaspades;
	boolean idbaReady = false, megahitReady = false, metaspadesReady = false;
	
	//file paths
	String inputForward, inputReverse, trimmedSE, trimmedForwardPaired, trimmedForwardUnpaired, trimmedReversePaired, trimmedReverseUnpaired, trimmedCombined;
	String idbaAssembly, idbaReadMap, idbaStats, idbaVisual;
	String megahitAssembly, megahitReadMap, megahitStats, megahitVisual;
	String metaspadesAssembly, metaspadesReadMap, metaspadesStats, metaspadesVisual;
	
	public MetagenomeJob(String jobID) {
		super(jobID);
		this.jobID = jobID;
	}
	
	public MetagenomeJob(String jobID, Boolean pairedEnd, Boolean idba, Boolean megahit, 
			Boolean metaspades, String inputForward, String inputReverse, String trimmedSE, String trimmedForwardPaired, String trimmedForwardUnpaired, String trimmedReversePaired, String trimmedReverseUnpaired, String trimmedCombined,
			String idbaAssembly, String idbaReadMap, String idbaStats, String idbaVisual,
			String megahitAssembly, String megahitReadMap, String megahitStats, String megahitVisual,
			String metaspadesAssembly, String metaspadesReadMap, String metaspadesStats, String metaspadesVisual) {
		super(jobID);
		this.jobID = jobID;
		this.pairedEnd = pairedEnd;
		this.idba = idba;
		this.megahit = megahit;
		this.metaspades = metaspades;
		this.inputForward = inputForward;
		this.inputReverse = inputReverse;
		this.trimmedSE = trimmedSE;
		this.trimmedForwardPaired = trimmedForwardPaired;
		this.trimmedForwardUnpaired = trimmedForwardUnpaired;
		this.trimmedReversePaired = trimmedReversePaired;
		this.trimmedReverseUnpaired = trimmedReverseUnpaired;
		this.trimmedCombined = trimmedCombined;
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
	
	public String basePath(){
		return inputForward.replaceAll("inputForward.fq", "");
	}
}
