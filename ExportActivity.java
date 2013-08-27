package com.example.exportmysmszero;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class ExportActivity extends FragmentActivity {
	
	private static final int NUM_PAGES = 2;
	
	private static Handler mHandler;
	
	private static String textResult; // Status
	private static String smsData;

	Fragment mFragment = null;

	/**
	 * The pager widget, which handles animation and allows swiping horizontally
	 * to access previous and next wizard steps.
	 */
	private ViewPager mPager;

	/**
	 * The pager adapter, which provides the pages to the view pager widget.
	 */
	private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);
        
     // Instantiate a ViewPager and a PagerAdapter.
     		mPager = (ViewPager) findViewById(R.id.pager);
     		mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
     		mPager.setAdapter(mPagerAdapter);
     		
     		
    		// Create a handler to update the UI
     		mHandler = new Handler() {
    	      @Override
    	      public void handleMessage(Message msg) {
    	    	TextView text = (TextView) findViewById(R.id.tv_result);
    	  		text.setText(textResult);
    	        //imageView.setImageBitmap(downloadBitmap);
    	    	 
    	      }
    	    };
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.export, menu);
        return true;
    }
    
    /**
     * Button listener - Slide to next screen
     */
    public void onClickExportMySms(View view) {
		mPager.setCurrentItem(mPager.getCurrentItem() + 1, true);
	}
    
    /**
	 * Button listener - Export SMS
	 * 
	 */
	public void onClickExportStart(View view) {
		
		/* // initializing and starting a new local Thread object
		Thread backgroundThread = new Thread(new Runnable() {
	        public void run() {
	            final Bitmap bitmap = loadImageFromNetwork("http://example.com/image.png");
	            mImageView.post(new Runnable() {
	            	// method executed by the Thread
	            	public void run() {
	            		// all the stuff we want our Thread to do goes here
	                    mImageView.setImageBitmap(bitmap);
	                }
	            });
	        }
	    })
		
		backgroundThread.start(); */
		
		
		String smsData = getSms(getApplicationContext());
		String textResult = null;
		if (smsData != "" && smsData != null) {
			// initializing and starting a new local Thread object
			MyThread mt = new MyThread();
			Thread currentThread = new Thread(mt);
			currentThread.start();
			//textResult = smsData;
		} else {
			textResult = "No SMS in inbox!";
		}
		TextView text = (TextView) findViewById(R.id.tv_result);
		text.setText(textResult);
		
		// Save to file
		saveDataExStorage(textResult);
	}

	/**
	 * A simple pager adapter that represents 5 ScreenSlidePageFragment objects,
	 * in sequence.
	 */
	private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
		public ScreenSlidePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// return new ExpFragment1(); // in case all same

			switch (position) {
			case 0:
				mFragment = new ExpFragment1();
				break;
			case 1:
				mFragment = new ExpFragment2();
				break;
			}
			return mFragment;

		}

		@Override
		public int getCount() {
			return NUM_PAGES;
		}
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
	
	static public class MyThread implements Runnable {
		// Method you must override to control what the Thread is doing
		@Override
        public void run() {
            textResult = smsData;			
			mHandler.sendEmptyMessage(0);
        }
		
		
	}
    
}
