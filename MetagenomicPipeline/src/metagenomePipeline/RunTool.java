package metagenomePipeline;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RunTool {
	static String runProgramAndWait(String command) throws IOException, InterruptedException{
		System.out.println(command);
		String[] splitCommand = command.split("\\s+");
		ProcessBuilder builder = new ProcessBuilder(splitCommand);
		builder.redirectErrorStream(true);
		Process process = builder.start();
		InputStream is = process.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));

		//process.waitFor();
		
		String line = null;
		String output = "";
		while ((line = reader.readLine()) != null) {
		   output += line + "\n";
		}
		
		return output;
	}
}
