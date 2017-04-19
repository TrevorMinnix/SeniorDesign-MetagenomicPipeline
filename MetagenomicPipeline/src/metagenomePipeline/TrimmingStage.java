package metagenomePipeline;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TrimmingStage extends MetagenomeStage{
	//private static final String CONFIG = "/home/student/SeniorDesign-MetagenomicPipeline/assembler_config.txt";
	//TODO
	private static final String CONFIG = "D:/GoogleDrive/UCF/SeniorDesign/SeniorDesign-MetagenomicPipeline/assembler_config.txt";
	
	private String trimPrefix;
	private String trimPath;
	private String trimSEDefault;
	private String trimPEDefault;
	private String command;
	
	public TrimmingStage(){
		super();
	}
	
	public TrimmingStage(MetagenomeStage[] nextStage, DatabaseConnection db) throws Exception{
		super(nextStage, db);
		
		//get assembler locations and defaults from text file
		getProperties();
		
		//TODO
		trimPath = "D:/GoogleDrive/UCF/SeniorDesign/SeniorDesign-MetagenomicPipeline/Trimmomatic-0.36/trimmomatic-0.36.jar";
	}

	private void trim(){
		//build command by replacing files names in default string
		buildCommand();
		
		try {
			System.out.println(RunTool.runProgramAndWait(command));
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	protected void process(){
		trim();
		//db.updateTrimming(currentJob.jobID, true);
	}
	
	private static String replaceSEFilePaths(String original, String input, String output){
		original = original.replaceAll("input.fq.gz", input);
		original = original.replaceAll("output.fq.gz", output);
		
		return original;
	}
	
	private static String replacePEFilePaths(String original, String inputForward, String inputReverse, String outputForwardPaired, 
			String outputForwardUnpaired, String outputReversePaired, String outputReverseUnpaired){
		original = original.replaceAll("input_forward.fq.gz", inputForward);
		original = original.replaceAll("input_reverse.fq.gz", inputReverse);
		original = original.replaceAll("output_forward_paired.fq.gz", outputForwardPaired);
		original = original.replaceAll("output_forward_unpaired.fq.gz", outputForwardUnpaired);
		original = original.replaceAll("output_reverse_paired.fq.gz", outputReversePaired);
		original = original.replaceAll("output_reverse_unpaired.fq.gz", outputReverseUnpaired);
		
		return original;
	}
	
	private void getProperties(){
		Properties config = new Properties();
		InputStream input;
		try{
			input = new FileInputStream(CONFIG);
			config.load(input);
			
			//get properties
			trimPrefix = config.getProperty("trimPrefix");
			trimPath = config.getProperty("trimPath");
			trimSEDefault = config.getProperty("trimSEDefault");
			trimPEDefault = config.getProperty("trimPEDefault");
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	private void buildCommand(){
		if(currentJob.pairedEnd){
			command = trimPrefix + " " + trimPath + " " + trimPEDefault;
			command = replacePEFilePaths(command, currentJob.inputForward, currentJob.inputReverse, 
					currentJob.trimmedForwardPaired, currentJob.trimmedForwardUnpaired, currentJob.trimmedReversePaired, currentJob.trimmedReverseUnpaired);
		}else{
			command = trimPrefix + " " + trimPath + " " + trimSEDefault;
			command = replaceSEFilePaths(command, currentJob.inputForward, currentJob.trimmedSE);
		}
	}
}
