package metagenomePipeline;

import pipeline.Stage;

public class MetagenomeStage extends pipeline.Stage{
	private DatabaseConnection database;
	
	public MetagenomeStage(){
		super();
	}

	public MetagenomeStage(Stage[] nextStage, DatabaseConnection database) {
		super(nextStage);
		this.database = database;
	}

	@Override
	protected void process(){
		
	}
}
