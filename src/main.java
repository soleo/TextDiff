import java.io.File;
import java.io.FileNotFoundException;
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
	
	public static void parallelRun(String oldFileName, String newFileName)
	{
		System.out.println ("Parallel test ...");
		long start = System.nanoTime();
		executor = Executors.newFixedThreadPool(NTHREADS);
		Report report = null;
		try {
			report = new TextDiff().compareWithThread( oldFileName, newFileName, executor);
			report.print(new PrintStream(new File("parallel")));
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
		long end = System.nanoTime();
		long microseconds = (end - start) / 1000;
		System.out.println("Latency: " + microseconds);
	}
	public static void sequentialRun(String oldFileName, String newFileName)
	{
		System.out.println ("Sequential test ...");
		long start = System.nanoTime();
		Report report = null;
		try {
			report = new TextDiff().compare( oldFileName, newFileName );
			report.print(new PrintStream(new File("sequential")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		long end = System.nanoTime();
		long microseconds = (end - start) / 1000;
		System.out.println("Latency: " + microseconds);
	}
	public static void main(String[] args) 
	{
		sequentialRun(args[0],args[1]);
		parallelRun(args[0],args[1]);
	}

}
