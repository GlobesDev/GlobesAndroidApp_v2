package il.co.globes.android;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;


public class UtilsFiles {

	// TODO ************** public methods **************/

	private static final String TAG = "UtilsFiles";

	/**
	 * Checks for file existence
	 * 
	 * @param context
	 *            - use application context to avoid memory leaks
	 * @param fileName
	 *            - the filename
	 * @return true if this file exists, false otherwise
	 */
	public static boolean existsFile(Context context, String fileName) {
		// String path = context.getFilesDir() + "\\" + fileName;
		String path = context.getFileStreamPath(fileName).getAbsolutePath();
		return new File(path).exists();
	}

	/**
	 * Deletes a file
	 * 
	 * @param context
	 *            - use application context to avoid memory leaks
	 * @param fileName
	 *            - the filename
	 */
	public static void deletefile(Context context, String fileName) {
		String path = context.getFileStreamPath(fileName).getAbsolutePath();
		new File(path).delete();
	}

	/**
	 * reads the file data from Assets.
	 * @param context
	 *            - use application context to avoid memory leaks
	 * @param fileName
	 *            could be full path of the file. BUT you should supply it in
	 *            style like xyz/abc insted a\b
	 * @return byte[] data of assetfile
	 * @throws Exception
	 */
	public static byte[] readAssetsFile(Context context, String fileName)
			throws Exception {
		InputStream in = null;
		try {
			AssetManager assetManager = context.getAssets();
			in = assetManager.open(fileName);
			byte[] data = UtilsWebServices.convertStreamToByteArray(in);
			return data;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					e.printStackTrace();
					logMsg(e.getMessage());
				}
			}
		}
	}

	/**
	 * read a private file associated with this Context's application package
	 * (used to read from internal storage).
	 * 
	 * @param context
	 *            - use application context to avoid memory leaks
	 * @param fileName
	 *            - the filename
	 * @return String - data of the file or null if no data.
	 * */
	public static String readTextFile(Context context, String fileName)
			throws Exception {
		if (existsFile(context, fileName)) {
			byte[] data = readFile(context, fileName);
			if (data != null) {
				return new String(data);
			}
		}
		return null;
	}

	/**
	 * read a File from File object , checks if it exists on the underlying file
	 * system.
	 * 
	 * @param context
	 *            - use application context to avoid memory leaks
	 * @param file
	 *            - File to read from using input stream
	 * @return String - data of the file or null if no data.
	 * */
	public static String readTextFile(Context context, File file)
			throws Exception {
		if (file.exists()) {
			byte[] data;
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(file);
				data = UtilsWebServices.convertStreamToByteArray(fis);
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (Exception e) {
						e.printStackTrace();
						logMsg(e.getMessage());
					}
				}
			}
			if (data != null) {
				return new String(data);
			}
		}
		return null;
	}

	/**
	 * write only to internal storage MODE_PRIVATE
	 * 
	 * @param context
	 *            - use application context to avoid memory leaks
	 * @param fileName
	 *            - the filename
	 * @param data
	 *            - the String data to be written
	 * @throws Exception
	 */
	public static void writeTextFile(Context context, String fileName,
			String text) throws Exception {
		writeFile(context, fileName, text.getBytes());
	}

	/**
	 * writes file to any location
	 * 
	 *  @param location
	 *            - File object to write to
	 * @param data
	 *            - the String data to be written
	 * @throws Exception
	 */
	public static void writeTextDataToAnyFileLocation(File location, String data)
			throws Exception {
		Writer out = null;
		try {
			String str = new String(data.getBytes("UTF-8"), "UTF-8");
			out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(location), "UTF-8"));
			byte[] bom = new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };

			out.write(new String(bom));
			out.write(str);
		} finally {
			try{
				out.close();
			}
			catch(Exception e){
				e.printStackTrace();
				logMsg(e.getMessage());
			}
		}
	}

	// TODO ************** private methods **************/

	/**
	 * write only to internal storage
	 * 
	 * @param context
	 *            - use application context to avoid memory leaks
	 * @param fileName
	 *            - the filename
	 * @param data
	 *            - the byte[] data to be written
	 * @throws Exception
	 */
	private static void writeFile(Context context, String fileName, byte[] data)
			throws Exception {

		FileOutputStream fos = null;
		try {
			fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);

			fos.write(data);
			fos.flush();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (Exception e) {
					e.printStackTrace();
					logMsg(e.getMessage());
				}
			}
		}

	}

	/**
	 * read a private file associated with this Context's application package.
	 * 
	 * @param context
	 *            - use application context to avoid memory leaks
	 * @param fileName
	 *            - the filename
	 * @return byte[] data of the desired file
	 * 
	 * @throws Exception
	 * */
	private static byte[] readFile(Context context, String fileName)
			throws Exception {

		FileInputStream fis = null;
		try {
			fis = context.openFileInput(fileName);
			byte[] data = UtilsWebServices.convertStreamToByteArray(fis);
			return data;
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (Exception e) {
					e.printStackTrace();
					logMsg(e.getMessage());
				}
			}
		}

	}

	/**
	 * logs error msg's
	 * 
	 * @param theMsg
	 */
	private static void logMsg(String theMsg) {
		Log.e(TAG, theMsg);
	}
}
