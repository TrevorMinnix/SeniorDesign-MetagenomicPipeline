package pipeline;
import metagenomePipeline.*;

public class Pipeline {
	private Stage[] stages;
	private Stage initialStage;
	
	//get stages and subsequent stages
	public Pipeline (Stage[] stages, Stage initialStage){
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
	
//	public static void main(String[] args){
//		//create stages
//		Stage s1 = new TrimmingStage();
//		Stage s2 = new Stage();
//		Stage s3 = new Stage();
//		
//		s1.init(new Stage[]{s2, s3});
//		s2.init(new Stage[]{});
//		s3.init(new Stage[]{});
//		
//		Job j = new Job("job");
//		
//		Pipeline pipe = new Pipeline(new Stage[]{s1, s2, s3}, s1);
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
