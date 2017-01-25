package metagenomePipeline;

import pipeline.Stage;

public class MetagenomeStage extends pipeline.Stage{
	DatabaseConnection database;

	public MetagenomeStage(Stage[] nextStage) {
		super(nextStage);
	}

}
