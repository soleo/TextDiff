
public class LineInfoRunnable implements Runnable {
	
	public FileInfo fileInfo;
    public SymbolCollection symbols;
    public static final int OLD = 0;
	public static final int NEW = 1;
    
	@Override
	public void run() {
		// TODO Auto-generated method stub
		createLineInfo(this.fileInfo);
	}

	LineInfoRunnable(FileInfo fileInfo, SymbolCollection symbols){
		this.fileInfo = fileInfo;
		this.symbols = symbols;
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

	
}
