package pipeline;

import java.util.concurrent.ConcurrentLinkedQueue;

public class Stage extends Thread{

	protected Job currentJob;
	private ConcurrentLinkedQueue<Job> queue;
	private Stage nextStage[];
	private boolean abort;

	public Stage(Stage[] nextStage){
		queue = new ConcurrentLinkedQueue<Job>();
		this.nextStage = nextStage;
		abort = false;
	}
	
	public Stage(){
		queue = new ConcurrentLinkedQueue<Job>();
		abort = false;
	}
	
	public void run(){
		while(!abort)
		{
			if(currentJob != null)
			{
				process();
				emit();

			}else{
				nextJob();
			}
		}
	}
	
	//assigns subsequent stages
	public void init(Stage nextStage[]){
		this.nextStage = nextStage;
	}
	
	//adds job to queue
	protected <J extends Job> void addJob(J job){
		queue.add(job);
	}
	
	//to be overridden by subclass
	protected void process(){
	}
	
	//sends job to next stage(s)
	private void emit(){
		for(Stage s : nextStage)
		{
			s.addJob(currentJob);
		}
		currentJob = null;
	}
	
	//fetches next job from queue
	private void nextJob(){
		currentJob = queue.poll();
	}
	
	protected void abortStage(){
		abort = true;
	}
}
