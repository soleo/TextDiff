/**
 * 
 */

/**
 * @author shaoxinjiang
 *
 */
public class main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String oldFileName = "oldfile";
		String newFileName = "newfile";
		
		Report report = null;
		try {
			report = new TextDiff().compare( oldFileName, newFileName );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		report.print( );
	}

}
