package metagenomePipeline;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import pipeline.Stage;

public class TrimmingStage extends MetagenomeStage{
	private Assembler assembler;
	private static final String CONFIG = "/home/student/SeniorDesign-MetagenomicPipeline/assembler_config.txt";
	
	private String idbaPath;
	private String idbaDefault;
	private String megahitPath;
	private String megahitDefault;
	private String spadesPath;
	private String spadesDefault;
	
	private enum Assembler{
		IDBA, MEGAHIT, SPADES;
	}
	
	public TrimmingStage(){
		super();
	}
	
	public TrimmingStage(MetagenomeStage[] nextStage, DatabaseConnection db, String assembler) throws Exception{
		super(nextStage, db);
		
		//set assembler
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
		
		//get assembler locations and defaults from text file
		Properties config = new Properties();
		InputStream input;
		try{
			input = new FileInputStream(CONFIG);
			config.load(input);
			
			//get properties
			idbaPath = config.getProperty(idbaPath);
			idbaDefault = config.getProperty(idbaDefault);
			megahitPath = config.getProperty(megahitPath);
			megahitDefault = config.getProperty(megahitDefault);
			spadesPath = config.getProperty(spadesPath);
			spadesDefault = config.getProperty(spadesDefault);
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	private void trim(){
//		switch(assembler){
//		case IDBA:
//		case MEGAHIT:
//		case SPADES:
//		}
//		RunTool.runProgramAndWait(args);
	}
	
	@Override
	protected void process(){
		trim();
	}
}
