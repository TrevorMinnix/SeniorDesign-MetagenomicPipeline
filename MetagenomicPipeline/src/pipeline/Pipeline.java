package pipeline;

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
}
