package pipeline;
import java.nio.file.Path;

import metagenomePipeline.*;

public class Pipeline<J extends Job>{
	private Stage[] stages;
	private Stage initialStage;
	
	//get stages and subsequent stages
	public Pipeline (Stage<J>[] stages, Stage<J> initialStage){
		this.stages = stages;
		this.initialStage = initialStage; //TODO error handling for initial stage is not in stages array
	}
	
	public void submitJob(Job newJob){
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
//	public static void main(String[] args){
//		//create stages
//		MetagenomeStage s1 = new TrimmingStage();
//		MetagenomeStage s2 = new TrimmingStage();
//		MetagenomeStage s3 = new TrimmingStage();
//		
//		s1.init(new Stage[]{s2, s3});
//		s2.init(new Stage[]{});
//		s3.init(new Stage[]{});
//		
//		Job j = new MetagenomeJob("id", null, null, null);
//		
//		Pipeline<MetagenomeJob> pipe = new Pipeline<MetagenomeJob>(new MetagenomeStage[]{s1, s2, s3}, s1);
//		
//		pipe.submitJob(j);
//		pipe.runPipeline();
//		
//		try {
//			Thread.sleep(100);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		
//		pipe.abortPipeline();
//	}
}
