import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This class contains the methods for file operations
 * @author Archit
 *
 */

public class FileProcessor{
	FileInputStream fileInputStream;
	BufferedReader bufferedReader;
	/**
	 * Parameterized Constructor
	 * @param fileInputStream
	 * @param file
	 * @throws FileNotFoundException
	 */
	FileProcessor(FileInputStream fileInputStreamIn, String fileIn) throws FileNotFoundException{
			fileInputStream = new FileInputStream(fileIn);
			bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
	}
	/**
	 * 
	 * @return fileInputStream
	 */
	
	public FileInputStream getFileInputStream() {
		return fileInputStream;
	}
	/**
	 * 
	 * @param fileInputStream
	 */
	public void setFileInputStream(FileInputStream fileInputStreamIn) {
		fileInputStream = fileInputStreamIn;
	}
	/**
	 * 
	 * @return BufferedReader
	 */
	public BufferedReader getBufferedReader() {
		return bufferedReader;
	}
	/**
	 * 
	 * @param bufferedReader
	 */
	public void setBufferedReader(BufferedReader bufferedReaderIn) {
		bufferedReader = bufferedReaderIn;
	}
	/**
	 * 
	 * @return each line as a String
	 */
	public String readFromLine(){
		String line = null;
		try {
			line = bufferedReader.readLine();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return line;
	}

}
