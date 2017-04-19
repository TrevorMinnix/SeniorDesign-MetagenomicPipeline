package metagenomePipeline;

import java.sql.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
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
		Properties config = new Properties();
		InputStream input;
		try{
			input = new FileInputStream(CRED);
			config.load(input);
			
			//get properties
			user = config.getProperty("user");
			pass = config.getProperty("pass");
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
			con = DriverManager.getConnection(DB_URL + "&useOldAliasMetadataBehavior=true", user, pass);
			System.out.println("Connected.");
		}catch(SQLException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public int updateTrimming(String jobID, boolean status){
		return execUpdate("UPDATE `job` SET `trimStatus` = '" + (status ? 1 : 0) + 
				"' WHERE `job`.`jobID` = '" + jobID + "';");
	}
	
	public int updateAssembly(String jobID, String assembler, boolean status){
		return execUpdate("UPDATE `" + assembler + "` SET `assemblyStatus` = '" + 
				(status ? 1 : 0) + "' WHERE `idba`.`jobID` = '" + jobID + "';");
	}
	
	public int updateReadMapping(String jobID, String assembler, boolean status){
		return execUpdate("UPDATE `" + assembler + "` SET `readmapStatus` = '" + 
				(status ? 1 : 0) + "' WHERE `idba`.`jobID` = '" + jobID + "';");
	}
	
	public int updateStatistics(String jobID, String assembler, boolean status){
		return execUpdate("UPDATE `" + assembler + "` SET `statStatus` = '" + 
				(status ? 1 : 0) + "' WHERE `idba`.`jobID` = '" + jobID + "';");
	}
	
	public int updateVisualization(String jobID, String assembler, boolean status){
		return execUpdate("UPDATE `" + assembler + "` SET `visualStatus` = '" + 
				(status ? 1 : 0) + "' WHERE `idba`.`jobID` = '" + jobID + "';");
	}
	
	public int updateJobStatus(String jobID, int i){
		return execUpdate("UPDATE `job` SET `jobStatus` = '" + i + "' WHERE `job`.`jobID` = '" + jobID +"'");
	}
	
	public ResultSet newJobs(){
		return execQuery("SELECT * FROM readyJobs");
	}
	
	private int execUpdate(String query){
		Statement statement;
		try {System.out.println("here");
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
			result.close();
			statement.close();
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