package metagenomePipeline;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class VisualStage extends MetagenomeStage{
	private Assembler assembler;
	private static final String CONFIG = "/home/student/SeniorDesign-MetagenomicPipeline/assembler_config.txt";
	
	private String visualDefault;
	private String visualPath;
	private String command;
	
	private enum Assembler{
		IDBA, MEGAHIT, SPADES;
	}
	
	public VisualStage(){
		super();
	}
	
	public VisualStage(MetagenomeStage[] nextStage, DatabaseConnection db, String assembler) throws Exception{
		super(nextStage, db);
		
		//default command from text file
		getProperties();
		
		//set assembler
		setAssembler(assembler);
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
		db.updateVisualization(currentJob.jobID, assemblerString(assembler), true);
	}
	
	private void getProperties(){
		Properties config = new Properties();
		InputStream input;
		try{
			input = new FileInputStream(CONFIG);
			config.load(input);
			
			//get properties
			visualPath = config.getProperty("visualPath");
			visualDefault = config.getProperty("visualDefault");
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	private void buildCommand(){
		command = visualPath + " " + visualDefault;
		
		String input, output;
		switch(assembler){
		case IDBA:
			input = currentJob.idbaStats;
			output = currentJob.idbaVisual;
			command = replacePath(command, input, output);
			break;
		case MEGAHIT:
			input = currentJob.megahitStats;
			output = currentJob.megahitVisual;
			command = replacePath(command, input, output);
			break;
		case SPADES:
			input = currentJob.metaspadesStats;
			output = currentJob.metaspadesVisual;
			command = replacePath(command, input, output);
		}
	}
	
	private static String replacePath(String command, String input, String output){
		command = command.replaceAll("INPUT", input);
		command = command.replaceAll("OUTPUT", output);
		return command;
	}
	
	private void setAssembler(String assembler) throws Exception{
		switch(assembler) {
		case "idba":
		case "IDBA":
			this.assembler = Assembler.IDBA;
			break;
		case "megahit":
		case "MEGAHIT":
		case "Megahit":
			this.assembler = Assembler.MEGAHIT;
			break;
		case "spades":
		case "SPAdes":
		case "SPADES":
			this.assembler = Assembler.SPADES;
			break;
		default:
			throw new Exception("Invalid assembler.");
		}
	}
	
	public static String assemblerString(Assembler assembler){
		switch(assembler){
		case IDBA:
			return "idba";
		case MEGAHIT:
			return "megahit";
		case SPADES:
			return "metaspades";
		default:
			return null;	
		}
	}
}
