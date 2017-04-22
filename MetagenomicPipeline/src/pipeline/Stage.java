package pipeline;

import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class Stage <J extends Job> extends Thread{

	protected J currentJob;
	private ConcurrentLinkedQueue<J> queue;
	private Stage<J> nextStage[];
	private boolean abort;

	public Stage(Stage<J>[] nextStage){
		queue = new ConcurrentLinkedQueue<J>();
		this.nextStage = nextStage;
		abort = false;
	}
	
	public Stage(){
		queue = new ConcurrentLinkedQueue<J>();
		abort = false;
	}
	
	@Override
	public void run(){
		while(!abort)
		{
			if(currentJob != null)
			{
				process();
				emit();

			}else{
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				nextJob();
			}
		}
	}
	
	//assigns subsequent stages
	public void init(Stage<J> nextStage[]){
		this.nextStage = nextStage;
	}
	
	//adds job to queue
	protected void addJob(J job){
		queue.add(job);
	}
	
	//to be overridden by subclass
	protected abstract void process();
	
	//sends job to next stage(s)
	private void emit(){
		for(Stage<J> s : nextStage)
		{
			s.addJob(currentJob);
		}
		currentJob = null;
	}
	
	//fetches next job from queue
	protected void nextJob(){
		currentJob = queue.poll();
	}
	
	protected void abortStage(){
		abort = true;
	}
}
