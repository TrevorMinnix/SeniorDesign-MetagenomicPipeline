package metagenomePipeline;

import java.sql.*;
import pipeline.*;

public class JobGetter extends Thread{
	private DatabaseConnection db;
	protected boolean abort = false;
	private Pipeline<MetagenomeJob> pipeline;
	
	public JobGetter(Pipeline<MetagenomeJob> pipeline, DatabaseConnection db){
		this.db = db;
		this.pipeline = pipeline;
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
					jobID = rs.getString("job.jobID");
					pairedEnd = rs.getBoolean("job.pairedEnd");
					idba = rs.getBoolean("job.idba");
					megahit = rs.getBoolean("job.megahit");
					metaspades = rs.getBoolean("job.metaspades");
					
					trimParam = rs.getString("job.trimParam");
					idbaParam = rs.getString("idba.param");
					megahitParam = rs.getString("megahit.param");
					metaspadesParam = rs.getString("metaspades.param");
					
					trimmed = rs.getString("job.trimmed");
					
					idbaAssembly = rs.getString("idba.assembly");
					idbaReadMap = rs.getString("idba.readMap");
					idbaStats = rs.getString("idba.stats");
					idbaVisual = rs.getString("idba.visual");
					
					megahitAssembly = rs.getString("megahit.assembly");
					megahitReadMap = rs.getString("megahit.readMap");
					megahitStats = rs.getString("megahit.stats");
					megahitVisual = rs.getString("megahit.visual");
					
					metaspadesAssembly = rs.getString("metaspades.assembly");
					metaspadesReadMap = rs.getString("metaspades.readMap");
					metaspadesStats = rs.getString("metaspades.stats");
					metaspadesVisual = rs.getString("metaspades.visual");
					
					//instantiate new job and enqueue in pipeline
					MetagenomeJob j = new MetagenomeJob(jobID, pairedEnd, idba, megahit, metaspades, trimParam, 
							idbaParam, megahitParam, metaspadesParam, trimmed, idbaAssembly, idbaReadMap, idbaStats, 
							idbaVisual, megahitAssembly, megahitReadMap, megahitStats, megahitVisual, 
							metaspadesAssembly, metaspadesReadMap, metaspadesStats, metaspadesVisual);
					db.updateJobStatus(jobID, 2);
					pipeline.submitJob(j);
					
					
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			
			//wait before repeating
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void abort(){
		abort = true;
	}
}