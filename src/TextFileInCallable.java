
import java.io.BufferedReader;  
import java.io.File;  
import java.io.FileNotFoundException;  
import java.io.FileReader;  
import java.io.IOException;  
import java.util.ArrayList;  
import java.util.List; 
import java.util.concurrent.Callable;

public class TextFileInCallable implements Callable<StrArray> {
	
	private String filename;
	private BufferedReader mReader = null;

	@Override
	public StrArray call() throws Exception {
		// TODO Auto-generated method stub
		 //Run Code in Thread
    	String filename = this.filename;
    	String[] lines = null;
    	StrArray ret =  new StrArray();
    	// doing reading file and feed the data to array lines;
    	try {
			TextFileIn(filename);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			ret.arr = asArray();
			ret.filename = filename;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ret.fileInfo = createFileInfoThread(ret.arr);
		return ret;
	}  
   
    
    TextFileInCallable(String fileName){
    	this.filename = fileName;
    }
    
    
    /** 
     * Constructor opens named file for input. 
     * @param aFileName java.lang.String 
     * @return 
     */  
    private void TextFileIn(String aFileName) throws java.io.FileNotFoundException  
    {  
        this.mReader = new BufferedReader(new FileReader(aFileName));  
    }  
    /** 
     * Constructor opens provided file for input. 
     * @param aFile 
     * @return 
     */  
    private void TextFileIn(File aFile) throws FileNotFoundException  
    {  
       this.mReader = new BufferedReader(new FileReader(aFile));  
    }  
    /** 
     * Returns contents of a file as one string.  NewLine characters that 
     * delimit lines in the file are converted to single spaces. 
     * <p> 
     * 09/26/2000 Standley New 
     * @return String 
     */  
    private String asString() throws java.io.IOException  
    {  
        String lLine;  
        StringBuffer lReturn = new StringBuffer();  
   
        while ((lLine = this.mReader.readLine()) != null)  
        {  
            lReturn.append(lLine);  
            lReturn.append(" ");  
        }  
        return lReturn.toString();  
    }  
    /** 
     * Returns contents of a file as an array of Strings. 
     * @return String[] 
     */  
    private String[] asArray() throws IOException  
    {  
        String lLine;  
        List lList = new ArrayList();  
        while (null != (lLine = this.mReader.readLine()))  
        {  
            lList.add(lLine);  
        }  
        return (String[])lList.toArray(new String[] {});  
    }  
    /** 
     * Close the input file.  This is not necessary if the client 
     * reads to end of file. 
     * <p>11/14/00 JLS Made close on closed file not an error. 
     */  
    private void close()  
    {  
        if (null == this.mReader)  
            return;  
        try  
        {  
        	this.mReader.close();  
        	this.mReader = null;  
        }  
        catch (Exception e)  
        {  
        }  
    }
    
    private FileInfo createFileInfoThread(String[] lines) {
		return new FileInfo(lines);
	}


}
