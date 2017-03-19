package metagenomePipeline;

import pipeline.Stage;

public abstract class MetagenomeStage extends pipeline.Stage<MetagenomeJob>{
	private DatabaseConnection db;
	
	public MetagenomeStage(){
		super();
	}

	public MetagenomeStage(MetagenomeStage[] nextStage, DatabaseConnection db) {
		super(nextStage);
		this.db = db;
	}
}
