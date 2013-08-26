package com.makcode.smsexport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	// Content URI for SMS may be changed in later Android version
	// public static final Uri SMS_URI_INBOX = Uri.parse("content://sms/inbox");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Button listener - Export SMS
	 * 
	 */
	public void onClickExportSms(View view) {
		String smsData = getSms(getApplicationContext());
		String textResult;
		if (smsData != "" && smsData != null) {
			textResult = smsData;
		} else {
			textResult = "No SMS in inbox!";
		}
		TextView text = (TextView) findViewById(R.id.tv_result);
		text.setText(textResult);
		
		// Save to file
		saveDataExStorage(textResult);
	}

	/**
	 * Read all SMS
	 * 
	 * @param context
	 */
	private String getSms(Context context) {

		String msgData = "";

		Cursor cursor = getContentResolver().query(
				Uri.parse("content://sms/inbox"), null, null, null, null);

		cursor.moveToFirst();

		if (cursor != null && cursor.moveToFirst()) {
			do {
				for (int idx = 0; idx < cursor.getColumnCount(); idx++) {
					msgData += " " + cursor.getColumnName(idx) + ":"
							+ cursor.getString(idx);
				}
			} while (cursor.moveToNext());
		}

		return msgData;
	}

	/**
	 * Save file to external storage (available throw USB connection. SD card
	 * for example)
	 * 
	 * @param dataToSave
	 *            //data that should be saved in file
	 */
	private void saveDataExStorage(String dataToSave) {

		/*
		 * Check storage availability
		 */
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
		} else {
			// Something else is wrong. It may be one of many other states, but
			// all we need
			// to know is we can neither read nor write
			mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
		
		/*
		 * Write file
		 */
		if (mExternalStorageWriteable) {
			File path = Environment.getExternalStoragePublicDirectory(
		            Environment.DIRECTORY_PICTURES);
		    File file = new File(path, "mySMSsave.txt");
		    
		    
		    try {
		        // Make sure the Pictures directory exists.
		        path.mkdirs();
		        
		        // Create file
			    file.createNewFile();

		        // Very simple code to copy a picture from the application's
		        // resource into the external file.  Note that this code does
		        // no error checking, and assumes the picture is small (does not
		        // try to copy it in chunks).  Note that if external storage is
		        // not currently mounted this will silently fail.
		        //InputStream is = getResources().openRawResource(R.drawable.balloons);
		        OutputStream os = new FileOutputStream(file);
		        byte[] data = dataToSave.getBytes(Charset.forName("UTF-8")); //String to bytes
		        //is.read(data);
		        os.write(data);
		        //is.close();
		        os.close();
		        Toast.makeText(getApplicationContext(), "File saved " + path, Toast.LENGTH_SHORT).show();

		    } catch (IOException e) {
		        // Unable to create file, likely because external storage is
		        // not currently mounted.
		        Log.w("ExternalStorage", "Error writing " + file, e);
		        Toast.makeText(getApplicationContext(), "Save failed" + e, Toast.LENGTH_LONG).show();
		    }
			
		} else {
			Toast.makeText(getApplicationContext(), R.string.err_cant_write_sdcard, Toast.LENGTH_SHORT).show();
		}
	}

}
