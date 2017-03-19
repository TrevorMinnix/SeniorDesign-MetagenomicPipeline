package metagenomePipeline;

import pipeline.Stage;

public class TrimmingStage extends MetagenomeStage{
	public TrimmingStage(){
		super();
	}
	
	public TrimmingStage(MetagenomeStage[] nextStage, DatabaseConnection db) {
		super(nextStage, db);
	}

	private void trim(){
			
	}
	
	@Override
	protected void process(){
		trim();
	}
}
