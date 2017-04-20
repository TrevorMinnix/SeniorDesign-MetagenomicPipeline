package metagenomePipeline;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class StatsStage extends MetagenomeStage{
	private Assembler assembler;
	private static final String CONFIG = "/home/student/SeniorDesign-MetagenomicPipeline/assembler_config.txt";
	
	private String statsPrefix;
	private String statsPath;
	private String statsDefault;
	private String command;
	
	private enum Assembler{
		IDBA, MEGAHIT, SPADES;
	}
	
	public StatsStage(){
		super();
	}
	
	public StatsStage(MetagenomeStage[] nextStage, DatabaseConnection db, String assembler) throws Exception{
		super(nextStage, db);
		
		//set assembler
		setAssembler(assembler);
		
		//get default command from text file
		getProperties();
		if(command != null){
			System.out.println("Stats:");
			try {
				RunTool.runProgramAndWait(command);
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void stats(){
		//build command by replacing files names in default string
		buildCommand();
		
		
	}
	
	@Override
	protected void process(){
		stats();
		db.updateStatistics(currentJob.jobID, assemblerString(assembler), true);
	}
	
	
	
	private void getProperties(){
		Properties config = new Properties();
		InputStream input;
		try{
			input = new FileInputStream(CONFIG);
			config.load(input);
			
			//get properties
			statsPrefix = config.getProperty("statsPrefix");
			statsPath = config.getProperty("statsPath");
			statsDefault = config.getProperty("statsDefault");
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	private void buildCommand(){
		command = statsPrefix + " " + statsPath + " " + statsDefault;
		
		//for each assembler
		String input, output, reads;
		reads = currentJob.pairedEnd ? currentJob.trimmedForwardPaired : currentJob.trimmedSE;
		switch(assembler){
		case IDBA:
			if(currentJob.idba){
				input = currentJob.idbaAssembly + "contig.fa";
				output = currentJob.idbaStats;
				command = replaceFilePath(command, input, output, reads);
			} else {
				command = null;
			}
			break;
		case MEGAHIT:
			if(currentJob.megahit){
				input = currentJob.megahitAssembly + "final.contigs.fa";
				output = currentJob.megahitStats;
				command = replaceFilePath(command, input, output, reads);
			} else {
				command = null;
			}
			break;
		case SPADES:
			if(currentJob.metaspades){
				input = currentJob.metaspadesAssembly + "contigs.fasta";
				output = currentJob.metaspadesStats;
				command = replaceFilePath(command, input, output, reads);
			} else {
				command = null;
			}
			break;
		}
	}
	
	private static String replaceFilePath(String command, String input, String output, String reads){
		command = command.replaceAll("INPUT", input);
		command = command.replaceAll("OUTPUT", output);
		command = command.replaceAll("READS", reads);
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
