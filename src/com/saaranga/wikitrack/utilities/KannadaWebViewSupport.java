package com.saaranga.wikitrack.utilities;

import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.Context;
import android.util.Log;


/**
 * Utitlity class - kannada typeface support for webviews
 * 
 * <br>
 * PURPOSE: Setting type for the webview is not as straight forward as the text
 * view. Inorder to achieve this, the font present in the assets folder should
 * be copied to the application pacakge folder. Then it should be accessed via
 * CSS of the html as src: url('file://"+
 * context.getFilesDir().getAbsolutePath())
 * 
 * USAGE: Instantiate the object from the calling function. call copyfile with
 * the font, on getting result as true, call get html data to get the neatly
 * formatted html string to display in webview of the calling class. Make sure
 * that the english text is escaped for webview and css for class .myen is added
 * on case of ascii text.
 * 
 * @author supreeth
 * @version 1.0 30-05-2012
 * 
 *          Copyright Saaranga Infotech
 */
public class KannadaWebViewSupport {

	public boolean copyFile(Context context, String fileName) {
		boolean status = false;
		try {
			FileOutputStream out = context.openFileOutput(fileName,
					Context.MODE_PRIVATE);
			InputStream in = context.getAssets().open(fileName);
			// Transfer bytes from the input file to the output file
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			// Close the streams
			out.close();
			in.close();
			status = true;
		} catch (Exception e) {
			System.out.println("Exception in copyFile:: " + e.getMessage());
			status = false;
		}
		Log.i("test", "copyFile Status:: " + status);
		return status;
	}
	
}
