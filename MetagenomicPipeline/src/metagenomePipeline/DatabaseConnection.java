package metagenomePipeline;

import java.io.*;
import java.sql.*;

import javax.sql.rowset.CachedRowSet;
import com.sun.rowset.CachedRowSetImpl;

public class DatabaseConnection{
	// JDBC driver name and database URL
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	private static final String DB_URL = "jdbc:mysql://localhost:3306/pipeline?useSSL=false";
	private static final String CRED = "/home/student/mysql_credentials.txt";
	
	//database credentials
	private String user;
	private String pass;
	
	//database connect and sql statement
	private Connection con;
	
	public DatabaseConnection(){
		//get user and pass from text file
		FileReader fileReader = null;
		BufferedReader bufferedReader = null;
		try{
			fileReader = new FileReader(new File(CRED));
			bufferedReader = new BufferedReader(fileReader);
			user = bufferedReader.readLine();
			pass = bufferedReader.readLine();
			bufferedReader.close();
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
		
		try{
			//register driver
			Class.forName(JDBC_DRIVER);
			
			//open connection
			System.out.println("Openning connection to database.");
			con = DriverManager.getConnection(DB_URL, user, pass);
			System.out.println("Connected.");
		}catch(SQLException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void updateTrimming(String jobID, boolean status){
		execUpdate("UPDATE `job` SET `trimStatus` = '" + (status ? 1 : 0) + 
				"' WHERE `job`.`jobID` = '" + jobID + "';");
	}
	
	public void updateAssembly(String jobID, String assembler, boolean status){
		execUpdate("UPDATE `" + assembler + "` SET `assemblyStatus` = '" + 
				(status ? 1 : 0) + "' WHERE `idba`.`jobID` = '" + jobID + "';");
	}
	
	public void updateReadMapping(String jobID, String assembler, boolean status){
		execUpdate("UPDATE `" + assembler + "` SET `readmapStatus` = '" + 
				(status ? 1 : 0) + "' WHERE `idba`.`jobID` = '" + jobID + "';");
	}
	
	public void updateStatistics(String jobID, String assembler, boolean status){
		execUpdate("UPDATE `" + assembler + "` SET `statStatus` = '" + 
				(status ? 1 : 0) + "' WHERE `idba`.`jobID` = '" + jobID + "';");
	}
	
	public void updateVisualization(String jobID, String assembler, boolean status){
		execUpdate("UPDATE `" + assembler + "` SET `visualStatus` = '" + 
				(status ? 1 : 0) + "' WHERE `idba`.`jobID` = '" + jobID + "';");
	}
	
	public void updateNewJob(String jobID, boolean status){
		execUpdate("UPDATE `job` SET `newJob` = '" + (status ? 1 : 0) + 
				"' WHERE `job`.`jobID` = '" + jobID +"'");
	}
	
	public ResultSet newJobs(){
		return execQuery("SELECT * FROM `job` WHERE `newJob` = true ORDER BY `timestamp` ASC");
	}
	
	private int execUpdate(String query){
		Statement statement;
		try {
			statement = con.createStatement();
			int result = statement.executeUpdate(query);
			statement.close();
			return result;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}
	
	private CachedRowSet execQuery(String query){
		Statement statement;
		ResultSet result;
		CachedRowSet crs;
		try {
			crs = new CachedRowSetImpl();
			statement = con.createStatement();
			result = statement.executeQuery(query);
			crs.populate(result);
			statement.close();
			result.close();
			return crs;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public void closeDatabaseConnection(){
		try {
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
