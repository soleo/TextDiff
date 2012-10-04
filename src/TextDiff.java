import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Compares two text files or arrays of strings and generates a report of edit
 * commands that would transform Old to New.
 */
public class TextDiff {
	public static final int OLD = 0;
	public static final int NEW = 1;

	private SymbolCollection symbols;
	
	private FileInfo oldFileInfo;
	private FileInfo newFileInfo;
	
	public TextDiff() {
	}

	/** Compare two named Files */
	public Report compare(String oldFileName, String newFileName)
			throws Exception {
		return compare(new File(oldFileName), new File(newFileName));
	}

	/** Compare two Files */
	public Report compare(File oldFile, File newFile) throws Exception {
		String[] lOld = new TextFileIn(oldFile).asArray();
		String[] lNew = new TextFileIn(newFile).asArray();
		// threads to do it.
		return compare(lOld, lNew);
	}
	
	public Report compareWithThread(String oldFile, String newFile, ExecutorService es){
		String[] lOld = null;
		String[] lNew = null;
		// Create the object 
		//List<Future<String[]>> list = new ArrayList<Future<String[]>>();
		Set<Callable<StrArray>> callables = new HashSet<Callable<StrArray>>();
		
		Callable<StrArray> task1 = new TextFileInCallable(oldFile);
		Callable<StrArray> task2 = new TextFileInCallable(newFile);
		
		callables.add(task1);
		callables.add(task2);
		
		List<Future<StrArray>> future = null;
		try {
			future = es.invokeAll(callables);
		} catch (InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		// Get the result from the threads
		try {
			StrArray temp = future.get(0).get();
			if ( temp.filename == oldFile)
			{
				lOld = temp.arr;
				lNew = future.get(1).get().arr;
				oldFileInfo = temp.fileInfo;
				newFileInfo = future.get(1).get().fileInfo;
			}
			else
			{
				lOld = future.get(1).get().arr;
				lNew = temp.arr;
				oldFileInfo = future.get(1).get().fileInfo;
				newFileInfo = temp.fileInfo;
			}
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return compareWithThread();
	}
	
	public Report compareWithThread(){
		//TODO make parallel, use sequential and concurrent hash map (Silviu)
		/*
		 * Interesting result: same perfomance with 1 or 2 threads. Not worth parallelizing!!!
		 */
		createSymbolsWithThread();
		//is parallel
		createLineInfoWithThread();
		//TODO Silviu and Xinjiang
		stretchMatches(oldFileInfo);
		//TODO implement some sort of time measure
		return new Report(oldFileInfo, newFileInfo);
	}
	/** Compare two string arrays */
	public Report compare(String[] oldLines, String[] newLines) {
		createFileInfo(oldLines, newLines);
		createSymbols();
		createLineInfo();
		stretchMatches(oldFileInfo);
		return new Report(oldFileInfo, newFileInfo);
	}
	/** Thread version moved in TextFIleInCallable.java **/
	private void createFileInfo(String[] oldLines, String[] newLines) {
		oldFileInfo = new FileInfo(oldLines);
		newFileInfo = new FileInfo(newLines);
		//Just created the object, no need to use two thread. by Xinjiang
	}

	/** Create a symbol for each unique string */
	private void createSymbols() {
		symbols = new SymbolCollection();
		createSymbols(oldFileInfo, OLD);
		createSymbols(newFileInfo, NEW);

	}
	/** Create a symbol for each unique string */
	private void createSymbolsWithThread() {
		ExecutorService es =main.executor;
		symbols = new SymbolCollection();
		Future f1 = es.submit(new Runnable (){
			public void run()
			{
				createSymbolsWithThread(oldFileInfo,OLD);
			}
		});
		Future f2 = es.submit(new Runnable (){
			public void run()
			{
				createSymbolsWithThread(newFileInfo,NEW);
			}
		});
		
		try {
			f1.get();
			f2.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void createSymbols(FileInfo fileInfo, int fileIx) {
		for (int line = 0; line < fileInfo.length; line++)
			symbols.registerSymbol(fileIx, fileInfo.lines[line], line);
	}
	
	private void createSymbolsWithThread(FileInfo fileInfo, int fileIx) {
		for (int line = 0; line < fileInfo.length; line++) {
			synchronized (symbols)
			{
				symbols.registerSymbol(fileIx, fileInfo.lines[line], line);
			}
		}
	}
	
	/** Initial line status is symbol status. Cross link any UniqueMatch lines */
	private void createLineInfo() {
		createLineInfo(oldFileInfo);
		createLineInfo(newFileInfo);
		
	}
	// two threads to create the line info
	private void createLineInfoWithThread(){
		ExecutorService es = main.executor;
		Runnable task1 = new LineInfoRunnable(oldFileInfo, symbols);
		Runnable task2 = new LineInfoRunnable(newFileInfo, symbols);
		List<Future<Runnable>> futures = new ArrayList<Future<Runnable>>();
	      
	   Future f1 = es.submit(task1);
	   Future f2 = es.submit(task2);
	   futures.add(f1);
	   futures.add(f2); 

	   // wait for all tasks to complete before continuing
	   for (Future<Runnable> f : futures)
	   {
	         try {
				f.get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	}
	private void createLineInfo(FileInfo fileInfo) {
		for (int line = 0; line < fileInfo.length; line++) {
			LineInfo lineInfo = new LineInfo();
			fileInfo.lineInfo[line] = lineInfo;
			Symbol symbol = symbols.getSymbolFor(fileInfo.lines[line]);
			lineInfo.lineStatus = symbol.getState();
			if (lineInfo.isMatch()) {
				lineInfo.oldLineNum = symbol.getLineNum(OLD);
				lineInfo.newLineNum = symbol.getLineNum(NEW);
			}
		}
	}

	/** Stretch each unique-match in the FileInfo. */
	private void stretchMatches(FileInfo fileInfo) {
		int FORWARD = 1;
		int BACKWARD = -1;
		int lBlockNum = 0;
		for (int line = 0; fileInfo.isValidLineNum(line); line++) {
			LineInfo lineInfo = fileInfo.lineInfo[line];
			if ((lineInfo.isMatch()) && (lineInfo.blockNum == 0)) {
				lBlockNum++;
				stretchOneMatch(lBlockNum, lineInfo.oldLineNum,
						lineInfo.newLineNum, FORWARD);
				stretchOneMatch(lBlockNum, lineInfo.oldLineNum,
						lineInfo.newLineNum, BACKWARD);
			}
		}
	}
	private void stretchMatchesWithThread(FileInfo fileInfo) {
		final int FORWARD = 1;
		final int BACKWARD = -1;
		int lBlockNum = 0;
		List<Future> futures = new ArrayList<Future>();
		ExecutorService es = Executors.newCachedThreadPool();
		for (int line = 0; fileInfo.isValidLineNum(line); line++) 
		{
			final LineInfo lineInfo = fileInfo.lineInfo[line];
			if ((lineInfo.isMatch()) && (lineInfo.blockNum == 0)) {
				lBlockNum++;
				final int lnum = lBlockNum;
				futures.add((Future) es.submit( new Runnable()
				{
					public void	run() 
					{
					stretchOneMatch(lnum, lineInfo.oldLineNum,
							lineInfo.newLineNum, FORWARD);
					}
				}));
				futures.add((Future)es.submit( new Runnable()
				{
					public void	run() 
					{
					stretchOneMatch(lnum, lineInfo.oldLineNum,
							lineInfo.newLineNum, BACKWARD);
					}
				}));
			}
		for (Future f : futures)
		   {
		         try {
					f.get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		}
	}
	/**
	 * Find more matching lines before or after a unique match and mark them as
	 * unique match, too. If unique match lines are separated by matching but
	 * non-unique lines this will merge them all into one block.
	 */
	private  void stretchOneMatch(int blockNum, int oldLineNum, int newLineNum,
			int whichWay) {
		int lOldLineNum = oldLineNum;
		int lNewLineNum = newLineNum;
		while (true) {
			oldFileInfo.setBlockNumber(lOldLineNum, blockNum);
			newFileInfo.setBlockNumber(lNewLineNum, blockNum);
			oldFileInfo.lineInfo[lOldLineNum].newLineNum = lNewLineNum;
			newFileInfo.lineInfo[lNewLineNum].oldLineNum = lOldLineNum;

			lOldLineNum += whichWay;
			lNewLineNum += whichWay;
			if (!(oldFileInfo.isValidLineNum(lOldLineNum)
					&& newFileInfo.isValidLineNum(lNewLineNum) && oldFileInfo.lines[lOldLineNum]
					.equals(newFileInfo.lines[lNewLineNum])))
				break;
		}
	}
	private  void stretchOneMatchWithThread(int blockNum, int oldLineNum, int newLineNum,
			int whichWay) {
		int lOldLineNum = oldLineNum;
		int lNewLineNum = newLineNum;
		while (true) {
			//sync
			oldFileInfo.setBlockNumber(lOldLineNum, blockNum);
			newFileInfo.setBlockNumber(lNewLineNum, blockNum);
			//sync
			oldFileInfo.lineInfo[lOldLineNum].newLineNum = lNewLineNum;
			newFileInfo.lineInfo[lNewLineNum].oldLineNum = lOldLineNum;

			lOldLineNum += whichWay;
			lNewLineNum += whichWay;
			//sync
			if (!(oldFileInfo.isValidLineNum(lOldLineNum)
					&& newFileInfo.isValidLineNum(lNewLineNum) && 
					oldFileInfo.lines[lOldLineNum]
					.equals(newFileInfo.lines[lNewLineNum])))
				break;
		}
	}
}
