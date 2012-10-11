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
	
	public static double parallelRun(String oldFileName, String newFileName) throws FileNotFoundException
	{
		long start = System.nanoTime();
		executor = Executors.newFixedThreadPool(NTHREADS);
		Report report = new TextDiff().compareWithThread( oldFileName, newFileName, executor);
		report.print(new PrintStream(new File("parallel")));

		// This will make the executor accept no new threads
	    // and finish all existing threads in the queue
	    executor.shutdown();
	    // Wait until all threads are finish
	    while (!executor.isTerminated()) {
	      }
		long end = System.nanoTime();;
		System.out.println("Parallel->"+(end - start)/(double)1000);
		return (end - start)/(double)1000000;
	}
	public static double sequentialRun(String oldFileName, String newFileName) throws Exception
	{
		long start = System.nanoTime();
		Report report = new TextDiff().compare( oldFileName, newFileName );
		report.print(new PrintStream(new File("sequential")));
		long end = System.nanoTime();
		System.out.println("Sequential->"+(end - start)/(double)1000);
		return (end - start)/(double)1000000;
	}
	
	public static double corectnessTest(String f1, String f2) throws Exception
	{

		double par = parallelRun(f1,f2);
		double seq = sequentialRun(f1,f2);
		TextFileIn fseq= new TextFileIn("sequential");
		TextFileIn fpar= new TextFileIn("parallel");
		if (!fseq.asString().equals(fpar.asString()))
			throw new RuntimeException("Result Files not Equal!");
		double improv = ((double)seq-(double)par)/(double)seq;
		return improv;
	}
	public static void main(String[] args) 
	{ 
		int n = 10;
		double diff = 0.0;
		try {
			for (int i=0; i<n; i++)
			{
				System.out.print(". ");
				double improv = 0.0;
				improv = corectnessTest(args[0],args[1]);
				diff += improv;
				
				System.out.println("Current Improvement from parallelism -> " + improv*100+"%");
			}
			System.out.println();
			System.out.println("Improvement from parallelism -> " + (diff/n)*100+"%");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			e.printStackTrace();

		}
	}

}
