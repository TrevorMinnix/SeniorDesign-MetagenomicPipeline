package metagenomePipeline;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class VisualStage extends MetagenomeStage{
	private static final String CONFIG = "/home/student/SeniorDesign-MetagenomicPipeline/assembler_config.txt";
	
	private String visualDefault;
	private String visualPath;
	private String command;
	
	public VisualStage(){
		super();
	}
	
	public VisualStage(MetagenomeStage[] nextStage, DatabaseConnection db) throws Exception{
		super(nextStage, db);
		
		//get assembler locations and defaults from text file
		getProperties();
	}

	private void visual(){
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
		visual();
		//TODO: add back in after testing
		//db.updateVisual(currentJob.jobID, true);
	}
	
	private void getProperties(){
		Properties config = new Properties();
		InputStream input;
		try{
			input = new FileInputStream(CONFIG);
			config.load(input);
			
			//get properties
			visualPath = config.getProperty("visualPath");
			visualDefault = config.getProperty("trimSEDefault");
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
