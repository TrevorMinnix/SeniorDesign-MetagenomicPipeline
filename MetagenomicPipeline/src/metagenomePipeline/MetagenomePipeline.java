package metagenomePipeline;
import pipeline.*;

public class MetagenomePipeline {
	public static void main(String[] args) throws Exception{
		DatabaseConnection db;
		Pipeline<MetagenomeJob> pipeline;
		MetagenomeStage[] stages;
		JobGetter jobGetter;
		
		//pipeline stages
		TrimmingStage trimming;
		AssemblerStage assemblerIdba, assemblerMegahit, assemblerSpades;
		StatsStage stats;
		VisualStage visual;
		//TODO: other stages
		
//		//connect to database
//		db = new DatabaseConnection();
		db = null;
		
		//connect stages and put in array
		assemblerIdba = new AssemblerStage(new MetagenomeStage[]{}, db, "IDBA");
		assemblerMegahit = new AssemblerStage(new MetagenomeStage[]{}, db, "MEGAHIT");
		assemblerSpades = new AssemblerStage(new MetagenomeStage[]{}, db, "SPADES");
		trimming = new TrimmingStage(new MetagenomeStage[]{assemblerIdba, assemblerMegahit, assemblerSpades}, db);
		stats = new StatsStage(new MetagenomeStage[]{}, db, "MEGAHIT");
		visual = new VisualStage(new MetagenomeStage[]{}, db, "MEGAHIT");
		
		stages = new MetagenomeStage[]{trimming, assemblerIdba, assemblerMegahit, assemblerSpades};
		
//		//put stages in pipeline
//		pipeline = new Pipeline<MetagenomeJob>(stages, trimming);
//		
//		//start job getter thread
//		jobGetter = new JobGetter(pipeline, db);
//		jobGetter.start();
//		
//		//run pipeline
//		pipeline.runPipeline();
		
		//test job
		MetagenomeJob job = new MetagenomeJob("testID");
		job.pairedEnd = true;
		job.inputForward = "/home/student/SeniorDesign-MetagenomicPipeline/TestData/simulatedReads1.fq";
		job.inputReverse = "/home/student/SeniorDesign-MetagenomicPipeline/TestData/simulatedReads2.fq";
		job.trimmedSE =  "/home/student/Testing/trimmedSE.fq";
		job.trimmedForwardPaired = "/home/student/Testing/trimmedFP.fq";
		job.trimmedForwardUnpaired = "/home/student/Testing/trimmedFU.fq";
		job.trimmedReversePaired = "/home/student/Testing/trimmedRP.fq";
		job.trimmedReverseUnpaired = "/home/student/Testing/trimmedRU.fq";
		job.trimmedCombined = "/home/student/Testing/trimmedC.fq";
		job.megahitAssembly = "/home/student/Testing/megahitAssembly/";
		job.megahitStats = "/home/student/SeniorDesign-MetagenomicPipeline/TestData/statistics.txt";
		job.megahitVisual = "/home/student/Testing/megahit/";
		job.idbaAssembly = "/home/student/Testing/idbaAssembly/";
		job.idbaStats = "/home/student/Testing/idbaStats.txt";
		job.idbaVisual = "/home/student/Testing/idba/";
		job.metaspadesAssembly = "/home/student/Testing/spadesAssembly/";
		job.metaspadesStats = "/home/student/Testing/metaspadesStats.txt";
		job.metaspadesVisual = "/home/student/Testing/spades/";

//		trimming.addJob(job);
//		trimming.nextJob();
//		trimming.process();
		
//		assemblerMegahit.addJob(job);
//		assemblerMegahit.nextJob();
//		assemblerMegahit.process();
		
//		assemblerSpades.addJob(job);
//		assemblerSpades.nextJob();
//		assemblerSpades.process();
		
//		assemblerIdba.addJob(job);
//		assemblerIdba.nextJob();
//		assemblerIdba.process();
		
//		stats.addJob(job);
//		stats.nextJob();
//		stats.process();
		
		visual.addJob(job);
		visual.nextJob();
		visual.process();
	}
}
