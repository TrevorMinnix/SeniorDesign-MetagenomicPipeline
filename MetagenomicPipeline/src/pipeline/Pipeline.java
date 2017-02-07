package pipeline;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import metagenomePipeline.*;

public class Pipeline<J extends Job>{
	private Stage<J>[] stages;
	private Stage<J> initialStage;
	
	//get stages and subsequent stages
	public Pipeline (Stage<J>[] stages, Stage<J> initialStage){
		this.stages = stages;
		this.initialStage = initialStage; //TODO error handling for initial stage is not in stages array
	}
	
	public void submitJob(J newJob){
		initialStage.addJob(newJob);
	}
	
	public void abortPipeline(){
		for(int i = 0; i < stages.length; ++i){
			stages[i].abortStage();
		}
	}
	
	public void runPipeline(){
		for(int i = 0; i < stages.length; ++i){
			stages[i].start();
		}
	}

	//testing
	public static void main(String[] args){
		//create stages
//		MetagenomeStage s1 = new TrimmingStage();
//		MetagenomeStage s2 = new TrimmingStage();
//		MetagenomeStage s3 = new TrimmingStage();
//		
//		//initialize nextStage for each
//		s1.init(new MetagenomeStage[]{s2, s3});
//		s2.init(new MetagenomeStage[]{});	//empty array is a terminal stage
//		s3.init(new MetagenomeStage[]{});
//		
//		//initialize a job
//		MetagenomeJob j = new MetagenomeJob("id", null, null, null);
//		
//		//create pipeline parameterized to correct job subclass
//		Pipeline<MetagenomeJob> pipe = new Pipeline<MetagenomeJob>(new MetagenomeStage[]{s1, s2, s3}, s1);
//		
//		//submit job and run pipeline
//		pipe.submitJob(j);
//		pipe.runPipeline();
//		
//		//sleep
//		try {
//			Thread.sleep(100);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		
//		//abort pipeline to end execution
//		pipe.abortPipeline();
		
		DatabaseConnection db;
		
		db = new DatabaseConnection();
		ResultSet rs = db.execQuery("INSERT INTO `job` (`jobID`, `email`, `timestamp`, `input`, `trimParam`, `trimStatus`, `trimmed`) VALUES ('1cbed4d9-ecb3-11e6-be4f-00155dcc8a23', '', CURRENT_TIMESTAMP, '', '', '0', '');");
		
		db.closeDatabaseConnection();
	}
}
