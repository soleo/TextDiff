import java.io.BufferedReader;  
import java.io.File;  
import java.io.FileNotFoundException;  
import java.io.FileReader;  
import java.io.IOException;  
import java.util.ArrayList;  
import java.util.List;  
/** 
 * This class represents a text file input from disk.  Usage: 
 * <pre> 
 * TextFileIn MyFileIn = new TextFileIn( fileid ); 
 * while ( ( MyLine = MyFileIn.readLine() ) != null ) 
 * { 
 *    do something with MyLine 
 * } 
 * </pre> 
 * Note: No close is necessary if you read to end of file.  If 
 * you do not read to end of file, call close(). 
 */  
public class TextFileIn  
{  
    private BufferedReader mReader = null;  
    /** 
     * Default constructor is private. Use constructor with FileName. 
     */  
    private TextFileIn()  
    {  
    }  
    /** 
     * Constructor opens named file for input. 
     * @param aFileName java.lang.String 
     */  
    public TextFileIn(String aFileName) throws java.io.FileNotFoundException  
    {  
        mReader = new BufferedReader(new FileReader(aFileName));  
    }  
    /** 
     * Constructor opens provided file for input. 
     * @param aFile 
     */  
    public TextFileIn(File aFile) throws FileNotFoundException  
    {  
        mReader = new BufferedReader(new FileReader(aFile));  
    }  
    /** 
     * Returns contents of a file as one string.  NewLine characters that 
     * delimit lines in the file are converted to single spaces. 
     * <p> 
     * 09/26/2000 Standley New 
     * @return String 
     */  
    public String asString() throws java.io.IOException  
    {  
        String lLine;  
        StringBuffer lReturn = new StringBuffer();  
   
        while ((lLine = this.readLine()) != null)  
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
    public String[] asArray() throws IOException  
    {  
        String lLine;  
        List lList = new ArrayList();  
        while (null != (lLine = this.readLine()))  
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
    public void close()  
    {  
        if (null == mReader)  
            return;  
        try  
        {  
            mReader.close();  
            mReader = null;  
        }  
        catch (Exception e)  
        {  
        }  
    }  
    /** 
     * Main for testing - demonstrates common usage. 
     * @param args java.lang.String[] 
     */  
    public static void main(String[] args)  
    {  
        String lLine;  
        try  
        {  
            TextFileIn lFile = new TextFileIn("c:\\test.dat");  
            while ((lLine = lFile.readLine()) != null)  
            {  
                System.out.println(lLine);  
            }  
        }  
        catch (Exception e)  
        {  
            e.printStackTrace();  
        }  
    }  
    /** 
     * Read one line from input file.  On read past end of file 
     * closes the file and returns null. 
     * @return java.lang.String 
     */  
    public String readLine() throws java.io.IOException  
    {  
        if (null == mReader)  
            throw new java.io.IOException();  
        String lLine = mReader.readLine();  
        if (null == lLine)  
            this.close();  
        return lLine;  
    }  
}  