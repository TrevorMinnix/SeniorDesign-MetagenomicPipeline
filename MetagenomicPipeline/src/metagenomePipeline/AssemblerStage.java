package metagenomePipeline;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AssemblerStage extends MetagenomeStage{
	private Assembler assembler;
	private static final String CONFIG = "/home/student/SeniorDesign-MetagenomicPipeline/assembler_config.txt";
	
	private String idbaPath;
	private String idbaConvertPath;
	private String idbaPEDefault;
	private String idbaConvert;
	private String megahitPath;
	private String megahitSEDefault;
	private String megahitPEDefault;
	private String spadesPath;
	private String spadesPEDefault;
	
	private enum Assembler{
		IDBA, MEGAHIT, SPADES;
	}
	
	public AssemblerStage(){
		super();
	}
	
	public AssemblerStage(MetagenomeStage[] nextStage, DatabaseConnection db, String assembler) throws Exception{
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
			idbaPath = config.getProperty("idbaPath");
			idbaConvertPath = config.getProperty("idbaConvertPath");
			idbaPEDefault = config.getProperty("idbaPEDefault");
			idbaConvert = config.getProperty("idbaConvert");
			megahitPath = config.getProperty("megahitPath");
			megahitSEDefault = config.getProperty("megahitSEDefault");
			megahitPEDefault = config.getProperty("megahitPEDefault");
			spadesPath = config.getProperty("spadesPath");
			spadesPEDefault = config.getProperty("spadesPEDefault");
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	private void assemble(){
		String command  = "";
		
		switch(assembler){
		case IDBA:
			//ensure paired end
			if(!currentJob.pairedEnd){
				//TODO: assembly failure
				break;
			}
			
			//combine input reads into single FASTA file
			command = "." + idbaConvertPath + " " + idbaConvert;
			replaceIdbaConvert(command, currentJob.trimmedForwardPaired, currentJob.trimmedReversePaired, currentJob.trimmedCombined);
			try {
				RunTool.runProgramAndWait(command);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			//assembly command
			command = "." + idbaPath + " " + idbaPEDefault;
			replaceIdbaPE(command, currentJob.trimmedCombined);
			break;
		case MEGAHIT:
			//single end command
			if(currentJob.pairedEnd){
				command = "." + megahitPath + " " + megahitSEDefault;
				replaceMegahitSE(command, currentJob.trimmedSE);
			}else{	//paired end command
				command = "." + megahitPath + " " + megahitPEDefault;
				replaceMegahitPE(command, currentJob.trimmedForwardPaired, currentJob.trimmedReversePaired);
			}
			break;
		case SPADES:
			//ensure paired end
			if(!currentJob.pairedEnd){
				//TODO: assembly failure
				break;
			}
			
			//assembly command
			command = "." + spadesPath + " " + spadesPEDefault;
			replaceSpadesPE(command, currentJob.trimmedForwardPaired, currentJob.trimmedReversePaired);
			break;
		default:
		}
		
		try {
			RunTool.runProgramAndWait(command);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	protected void process(){
		assemble();
	}
	
	private static void replaceIdbaConvert(String original, String forward, String reverse, String combined){
		original = original.replace("pe_1.fq", forward);
		original = original.replace("pe_2.fq", reverse);
		original = original.replace("pe_combined.fa", combined);
	}
	
	private static void replaceIdbaPE(String original, String combined){
		original = original.replace("pe_combined.fa", combined);
	}
	
	private static void replaceMegahitSE(String original, String reads){
		original = original.replace("se.fq", reads);
	}
	
	private static void replaceMegahitPE(String original, String forward, String reverse){
		original = original.replace("pe_1.fq", forward);
		original = original.replace("pe_2.fq", reverse);
	}
	
	private static void replaceSpadesPE(String original, String forward, String reverse){
		original = original.replace("pe_1.fq", forward);
		original = original.replace("pe_2.fq", reverse);
	}
}
