
public class StretchMatchRunnable implements Runnable{
	public TextDiff td;
	public int lBlockNum;
	int oldLineNum;
	int newLineNum;
	int Forward;
	public void set(int lBlockNum, int oldLineNum,int newLineNum,int Forward )
	{
		this.lBlockNum = lBlockNum;
		this.oldLineNum = oldLineNum;
		this.newLineNum = newLineNum;
		this.Forward = Forward;
	}
	public void run(){
		td.stretchOneMatch(lBlockNum, oldLineNum,
				newLineNum, Forward);
	}
	
}
