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
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		executor = Executors.newFixedThreadPool(NTHREADS);
		
		String oldFileName = "oldfile";
		String newFileName = "newfile";
		
		Report report = null;
		try {
			//report = new TextDiff().compare( oldFileName, newFileName );
			report = new TextDiff().compareWithThread( oldFileName, newFileName, executor);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		report.print();
	}

}
