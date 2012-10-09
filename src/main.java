import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 
 */

/**
 * @author shaoxinjiang
 *
 */
public class main {
	public  static ExecutorService executor = null;
	public static final int NTHREADS = 2;
	
	public static String parallelRun(String oldFileName, String newFileName)
	{
		System.out.println ("Parallel test ...");
		String fret = "parallel";
		long start = System.currentTimeMillis();
		executor = Executors.newFixedThreadPool(NTHREADS);
		Report report = null;
		try {
			report = new TextDiff().compareWithThread( oldFileName, newFileName, executor);
			report.print(new PrintStream(new File(fret)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// This will make the executor accept no new threads
	    // and finish all existing threads in the queue
	    executor.shutdown();
	    // Wait until all threads are finish
	    while (!executor.isTerminated()) {
	      }
		long end = System.currentTimeMillis();
		long microseconds = (end - start) ;
		System.out.println("Latency: " + microseconds);
		return fret;
	}
	public static String sequentialRun(String oldFileName, String newFileName)
	{
		System.out.println ("Sequential test ...");
		String fret = "sequential";
		long start = System.currentTimeMillis();
		Report report = null;
		try {
			report = new TextDiff().compare( oldFileName, newFileName );
			report.print(new PrintStream(new File(fret)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		long microseconds = (end - start) ;
		System.out.println("Latency: " + microseconds);
		return fret;
	}
	public static String corectnessTest(String seq, String par) throws IOException
	{
		TextFileIn fseq= new TextFileIn(seq);
		TextFileIn fpar= new TextFileIn(par);
		if (fseq.asString().equals(fpar.asString()))
			return "Result files are equal"; 
		return "Result files are NOT equal";
	}
	public static void main(String[] args) 
	{
		try {
			String msg = corectnessTest(sequentialRun(args[0],args[1]), parallelRun(args[0],args[1]));
			System.out.println(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
