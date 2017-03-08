package metagenomePipeline;

import java.sql.*;

public class JobGetter extends Thread{
	private DatabaseConnection db;
	protected boolean abort = false;
	
	public JobGetter(){
		db = new DatabaseConnection();
	}
	
	public void run(){
		ResultSet rs;
		
		//loop on abort
		while(!abort){
			//check database for new jobs
			rs = db.newJobs();
			
			//add jobs to pipeline and update database
			try {
				while(rs.next()){	//while there are rows in set
					//TODO: extract information necessary for MetagenomeJob
					//db.updateNewJob(jobID, false);
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			
			//wait before repeating
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void abort(){
		abort = true;
	}
}
