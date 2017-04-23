package metagenomePipeline;

//import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
//import java.io.InputStreamReader;

public class RunTool {
	static void runProgramAndWait(String program, String args) throws IOException, InterruptedException{
		Process p = Runtime.getRuntime().exec(program + " " + args);
		p.waitFor();
	}
	
	static int runProgramAndWait(String command) throws IOException, InterruptedException{
		System.out.println(command);
		String[] splitCommand = command.split("\\s+");
		final ProcessBuilder builder = new ProcessBuilder(splitCommand);
		builder.redirectErrorStream(true);
		final Process process = builder.start();
		//InputStream is = process.getInputStream();
		//BufferedReader reader = new BufferedReader(new InputStreamReader(is));

		//process.waitFor();
		
		//String line = null;
		//while ((reader.readLine()) != null) {
		//}
		
		final Thread reader = new Thread(new Runnable() {
			@Override
			public void run(){
				try{
					final InputStream is = process.getInputStream();
					int c;
					while((c = is.read()) != 1){
						
					}
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		});
		
		reader.start();
		
		final int exitCode = process.waitFor();
		reader.join();
		
		return exitCode;
	}
	
	
}
