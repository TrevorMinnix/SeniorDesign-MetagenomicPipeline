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
				while(rs.next()){
					//metadata
					String jobID;
					Boolean pairedEnd, idba, megahit, metaspades;
					
					//tool parameters
					String trimParam, idbaParam, megahitParam, metaspadesParam;
					
					//file paths
					String trimmed;
					String idbaAssembly, idbaReadMap, idbaStats, idbaVisual;
					String megahitAssembly, megahitReadMap, megahitStats, megahitVisual;
					String metaspadesAssembly, metaspadesReadMap, metaspadesStats, metaspadesVisual;
					
					//get info from query results
					jobID = rs.get();
					
					db.updateJobStatus(jobID, 2);
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