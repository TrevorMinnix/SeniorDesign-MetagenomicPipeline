package metagenomePipeline;

import pipeline.Stage;

public class TrimmingStage extends MetagenomeStage{
	public TrimmingStage(){
		super();
	}
	
	public TrimmingStage(Stage[] nextStage, DatabaseConnection database) {
		super(nextStage, database);
	}

	private void trim(){
			
	}
	
	@Override
	protected void process(){
		trim();
	}
}
