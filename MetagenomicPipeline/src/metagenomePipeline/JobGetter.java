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
		}
	}
	
	public void abort(){
		abort = true;
	}
}
