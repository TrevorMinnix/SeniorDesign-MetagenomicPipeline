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
	private String command;
	private String assemblerTable;
	
	private enum Assembler{
		IDBA, MEGAHIT, SPADES;
	}
	
	public AssemblerStage(){
		super();
	}
	
	public AssemblerStage(MetagenomeStage[] nextStage, DatabaseConnection db, String assembler) throws Exception{
		super(nextStage, db);
		
		//set assembler
		setAssembler(assembler);
		
		//get assembler locations and defaults from text file
		getProperties();
	}

	private void assemble(){	
		switch(assembler){
		case IDBA:
			//ensure paired end
			if(!currentJob.pairedEnd){
				//TODO: assembly failure
				break;
			}
			
			//combine input reads into single FASTA file
			combineReads();
			
			//assembly command
			idbaCommand();
			break;
		case MEGAHIT:
			//single end command
			megahitCommand();
			break;
		case SPADES:
			//ensure paired end
			if(!currentJob.pairedEnd){
				//TODO: assembly failure
				break;
			}
			
			//assembly command
			spadesCommand();
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
		//TODO: add back in after testing
		//db.updateAssembly(currentJob.jobID, assemblerTable, true);
	}
	
	private static String replaceIdbaConvert(String original, String forward, String reverse, String combined){
		original = original.replaceAll("pe_1.fq", forward);
		original = original.replaceAll("pe_2.fq", reverse);
		original = original.replaceAll("pe_combined.fa", combined);
		
		return original;
	}
	
	private static String replaceIdbaPE(String original, String combined, String assembled){
		original = original.replaceAll("pe_combined.fa", combined);
		original = original.replaceAll("idba_assembled", assembled);

		return original;
	}
	
	private static String replaceMegahitSE(String original, String reads, String assembled){
		original = original.replaceAll("se.fq", reads);
		original = original.replaceAll("megahit_se_assembled", assembled);

		return original;
	}
	
	private static String replaceMegahitPE(String original, String forward, String reverse, String assembled){
		original = original.replaceAll("pe_1.fq", forward);
		original = original.replaceAll("pe_2.fq", reverse);
		original = original.replaceAll("megahit_pe_assembled", assembled);

		return original;
	}
	
	private static String replaceSpadesPE(String original, String forward, String reverse, String assembled){
		original = original.replaceAll("pe_1.fq", forward);
		original = original.replaceAll("pe_2.fq", reverse);
		original = original.replaceAll("spades_assembled", assembled);

		return original;
	}
	
	private void getProperties(){
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
	
	private void combineReads(){
		String command = idbaConvertPath + " " + idbaConvert;
		command = replaceIdbaConvert(command, currentJob.trimmedForwardPaired, currentJob.trimmedReversePaired, currentJob.trimmedCombined);
		try {
			RunTool.runProgramAndWait(command);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	private void idbaCommand(){
		command = idbaPath + " " + idbaPEDefault;
		command = replaceIdbaPE(command, currentJob.trimmedCombined, currentJob.idbaAssembly);
	}
	
	private void megahitCommand(){
		if(!currentJob.pairedEnd){
			command = megahitPath + " " + megahitSEDefault;
			command = replaceMegahitSE(command, currentJob.trimmedSE, currentJob.megahitAssembly);
		}else{	//paired end command
			command = megahitPath + " " + megahitPEDefault;
			command = replaceMegahitPE(command, currentJob.trimmedForwardPaired, currentJob.trimmedReversePaired, currentJob.megahitAssembly);
		}
	}
	
	private void spadesCommand(){
		command = spadesPath + " " + spadesPEDefault;
		command = replaceSpadesPE(command, currentJob.trimmedForwardPaired, currentJob.trimmedReversePaired, currentJob.metaspadesAssembly);
	}
	
	private void setAssembler(String assembler) throws Exception{
		switch(assembler) {
		case "idba":
		case "IDBA":
			this.assembler = Assembler.IDBA;
			assemblerTable = "idba";
			break;
		case "megahit":
		case "MEGAHIT":
		case "Megahit":
			this.assembler = Assembler.MEGAHIT;
			assemblerTable = "megahit";
			break;
		case "spades":
		case "SPAdes":
		case "SPADES":
			this.assembler = Assembler.SPADES;
			assemblerTable = "metaspades";
			break;
		default:
			throw new Exception("Invalid assembler.");
		}
	}
}
