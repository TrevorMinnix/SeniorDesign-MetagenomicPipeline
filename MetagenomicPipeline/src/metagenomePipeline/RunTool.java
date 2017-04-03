package metagenomePipeline;

import java.io.IOException;

public class RunTool {
	static void runProgramAndWait(String program, String args) throws IOException, InterruptedException{
		Process p = Runtime.getRuntime().exec(program + " " + args);
		p.waitFor();
	}
	
	static void runProgramAndWait(String command) throws IOException, InterruptedException{
		Process p = Runtime.getRuntime().exec(command);
		p.waitFor();
	}
}
