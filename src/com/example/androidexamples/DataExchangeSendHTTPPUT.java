package com.example.androidexamples;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;

public class DataExchangeSendHTTPPUT extends Activity {

	private TextView ResultContentTextView;
	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		StrictMode.ThreadPolicy policy = new StrictMode.
				ThreadPolicy.Builder().permitAll().build();
				StrictMode.setThreadPolicy(policy); 
				
		setContentView(R.layout.activity_data_exchange_send_httpput);
		// Show the Up button in the action bar.
		setupActionBar();
		
		
		ResultContentTextView = (TextView) findViewById(R.id.txt_data_exchange_http_result_contents);
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.data_exchange_send_httpput, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void httpput_send(View view) {
		String dataDir = android.os.Environment.getExternalStorageDirectory () + File.separator;
		String picsPath = dataDir + android.os.Environment.DIRECTORY_DCIM + File.separator + "Camera";
		File pictureDir = new File(picsPath);
		File[] pictures = pictureDir.listFiles();
		
		try {
			
			for (int i=0; i<10; i++) {
			
				DataExchangeSendHTTPRequests uploadFile = new DataExchangeSendHTTPRequests(
						"PUT",
						pictures[i].getAbsolutePath(),
						"http://alexnedelcu.com/tests/java/test_http_put_request.php?pict_name="+pictures[i].getName());
				uploadFile.send();
				
				  
				readStream(uploadFile.getInputStreamResponse());
			}
			  } catch (Exception e) {
			  e.printStackTrace();
			}
	}
	private void readStream(InputStream in) {
		  BufferedReader reader = null;
		  try {
		    reader = new BufferedReader(new InputStreamReader(in));
		    String line = "";
		    while ((line = reader.readLine()) != null) {
		    	ResultContentTextView.append(line);
		      //System.out.println(line);
		    }
		  } catch (IOException e) {
		    e.printStackTrace();
		  } finally {
		    if (reader != null) {
		      try {
		        reader.close();
		      } catch (IOException e) {
		        e.printStackTrace();
		        }
		    }
		  }
		} 
}
