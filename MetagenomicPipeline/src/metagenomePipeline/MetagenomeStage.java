package metagenomePipeline;

import pipeline.Stage;

public abstract class MetagenomeStage extends pipeline.Stage<MetagenomeJob>{
	private DatabaseConnection database;
	
	public MetagenomeStage(){
		super();
	}

	public MetagenomeStage(Stage<MetagenomeJob>[] nextStage, DatabaseConnection database) {
		super(nextStage);
		this.database = database;
	}
}
