package metagenomePipeline;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class CombinedVisualStage extends MetagenomeStage{
	private static final String CONFIG = "/home/student/SeniorDesign-MetagenomicPipeline/assembler_config.txt";
	
	private String visualDefault;
	private String visualPath;
	private String command;
	
	public CombinedVisualStage(){
		super();
	}
	
	public CombinedVisualStage(MetagenomeStage[] nextStage, DatabaseConnection db) throws Exception{
		super(nextStage, db);
		
		//default command from text file
		getProperties();
	}

	private boolean visual(){
		//check if job is ready for all relevant assemblers
		if((currentJob.megahit && !currentJob.megahitReady) || (currentJob.idba && !currentJob.idbaReady) || 
				(currentJob.metaspades && !currentJob.metaspadesReady)){
			return false;
		}
		
		//build command by replacing files names in default string
		buildCommand();
		
		if(command != null){
			System.out.println("Visual:");
			try {
				RunTool.runProgramAndWait(command);
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return true;
	}
	
	@Override
	protected void process(){
		if(visual()){
			//update database for each relevant visualization
			if(currentJob.megahit){
				db.updateVisualization(currentJob.jobID, "megahit", true);
			}
			if(currentJob.idba){
				db.updateVisualization(currentJob.jobID, "idba", true);
			}
			if(currentJob.metaspades){
				db.updateVisualization(currentJob.jobID, "metaspades", true);
			}
		}
		
		//if currentJob cannot be completed, add job to the back of the queue
		this.addJob(currentJob);
	}
	
	private void getProperties(){
		Properties config = new Properties();
		InputStream input;
		try{
			input = new FileInputStream(CONFIG);
			config.load(input);
			
			//get properties
			visualPath = config.getProperty("combinedVisualPath");
			visualDefault = config.getProperty("combinedVisualDefault");
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	private void buildCommand(){
		command = visualPath + " " + visualDefault;
		
		replacePath();
	}
	
	private void replacePath(){
		//megahit
		if(currentJob.megahit){
			command = command.replaceAll("INPUT1", currentJob.megahitVisual);
			command = command.replaceAll("OUTPUT1", currentJob.megahitAssembly);
		}else{
			command = command.replaceAll("INPUT1", "");
			command = command.replaceAll("OUTPUT1", "");
		}
		//idba
		if(currentJob.idba){
			command = command.replaceAll("INPUT1", currentJob.idbaVisual);
			command = command.replaceAll("OUTPUT1", currentJob.idbaAssembly);
		}else{
			command = command.replaceAll("INPUT1", "");
			command = command.replaceAll("OUTPUT1", "");
		}
		//spades
		if(currentJob.metaspades){
			command = command.replaceAll("INPUT1", currentJob.metaspadesVisual);
			command = command.replaceAll("OUTPUT1", currentJob.metaspadesAssembly);
		}else{
			command = command.replaceAll("INPUT1", "");
			command = command.replaceAll("OUTPUT1", "");
		}
		//combined
		command = command.replaceAll("OUTPUTC", currentJob.basePath());
	}
}
