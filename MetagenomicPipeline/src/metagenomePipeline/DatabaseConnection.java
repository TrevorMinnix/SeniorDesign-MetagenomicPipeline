package metagenomePipeline;

import java.io.*;
import java.sql.*;

public class DatabaseConnection{
	// JDBC driver name and database URL
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	private static final String DB_URL = "jdbc:mysql://10.171.204.144:3306/pipeline";
	private static final String CRED = "~/mysql_credentials.txt";
	
	//database credentials
	private String user;
	private String pass;
	 
	Connection con;
	
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
}
