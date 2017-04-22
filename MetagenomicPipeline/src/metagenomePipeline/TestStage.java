package metagenomePipeline;

public class TestStage extends MetagenomeStage{
	public TestStage(){
		super();
	}
	
	public TestStage(MetagenomeStage[] nextStage, DatabaseConnection db) {
		super(nextStage, db);
	}

	private void test(){
		System.out.println("Job submitted: " + currentJob.jobID);
		return;
	}
	
	@Override
	protected void process(){
		test();
	}
}
