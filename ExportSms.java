package com.makcode.smsexport;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

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
    }
    
    /**
     * Read all SMS
     * 
     * @param context
     */
    private String getSms(Context context) {
    	
    	String msgData = "";
    	
    	Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);
        
    	cursor.moveToFirst();

        if (cursor != null && cursor.moveToFirst()) {
        	do{          
                for(int idx=0;idx<cursor.getColumnCount();idx++)
                {
                    msgData += " " + cursor.getColumnName(idx) + ":" + cursor.getString(idx);
                }
             }while(cursor.moveToNext());			
		}

		return msgData;
	}

}
