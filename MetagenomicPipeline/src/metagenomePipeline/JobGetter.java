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
					String input, inputPE, trimmed;
					String idbaAssembly, idbaReadMap, idbaStats, idbaVisual;
					String megahitAssembly, megahitReadMap, megahitStats, megahitVisual;
					String metaspadesAssembly, metaspadesReadMap, metaspadesStats, metaspadesVisual;
					
					//get info from query results
					jobID = rs.getString("jobID");
					pairedEnd = rs.getBoolean("pairedEnd");
					idba = rs.getBoolean("idba");
					megahit = rs.getBoolean("megahit");
					metaspades = rs.getBoolean("metaspades");
					
					trimParam = rs.getString("trimParam");
					idbaParam = rs.getString("idbaParam");
					megahitParam = rs.getString("megahitParam");
					metaspadesParam = rs.getString("metaspadesParam");
					
					input = rs.getString("input");
					inputPE = rs.getString("inputPE");
					trimmed = rs.getString("trimmed");
					
					idbaAssembly = rs.getString("idbaAssembly");
					idbaReadMap = rs.getString("idbaReadmap");
					idbaStats = rs.getString("idbaStat");
					idbaVisual = rs.getString("idbaVisual");
					
					megahitAssembly = rs.getString("megahitAssembly");
					megahitReadMap = rs.getString("megahitReadmap");
					megahitStats = rs.getString("megahitStat");
					megahitVisual = rs.getString("megahitVisual");
					
					metaspadesAssembly = rs.getString("metaspadesAssembly");
					metaspadesReadMap = rs.getString("metaspadesReadmap");
					metaspadesStats = rs.getString("metaspadesStat");
					metaspadesVisual = rs.getString("metaspadesVisual");
					
					//instantiate new job and enqueue in pipeline
					MetagenomeJob j = new MetagenomeJob(jobID, pairedEnd, idba, megahit, metaspades, trimParam, 
							idbaParam, megahitParam, metaspadesParam, input, inputPE, trimmed, idbaAssembly, 
							idbaReadMap, idbaStats, idbaVisual, megahitAssembly, megahitReadMap, megahitStats, 
							megahitVisual, metaspadesAssembly, metaspadesReadMap, metaspadesStats, metaspadesVisual);
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