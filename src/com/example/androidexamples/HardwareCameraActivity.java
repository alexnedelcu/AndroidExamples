package com.example.androidexamples;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.support.v4.app.NavUtils;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Build;

public class HardwareCameraActivity extends Activity {

    private static Camera mCamera;
    private HardwareCameraPreview mPreview;
    private static final String TAG = "com.example.androidexamples:HardwareCameraActivity";
    private static final int MEDIA_TYPE_IMAGE = 1;
	private static final int MEDIA_TYPE_VIDEO = 2;
	private static MediaRecorder mMediaRecorder;
	private boolean isRecording = false;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hardware_camera);
		
		// Show the Up button in the action bar.
		setupActionBar();
		
		// Check if the phone has camera
		if (checkCameraHardware(this)) {
			//final Camera mCamera = getCameraInstance();
			mCamera = getCameraInstance();
			if(mCamera == null) {
				// showing error dialog if the class does not exist
				Util.showAlert(this, R.string.dialog_missing_camera, R.string.dialog_title);
			} else {
				//System.out.println(mCamera.getParameters());
		        // Create our Preview view and set it as the content of our activity.
		        mPreview = new HardwareCameraPreview(this, mCamera);
		        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		        preview.addView(mPreview);
				mCamera.startPreview();
				
				// Add a listener to the Capture button
				Button captureButton = (Button) findViewById(R.id.button_capture_picture);
				captureButton.setOnClickListener(
				    new View.OnClickListener() {
				        @Override
				        public void onClick(View v) {
				            // get an image from the camera
				            mCamera.takePicture(null, null, mPicture);
				            
				            // pause introduced to simulate saving the picture
				            try {
								Thread.sleep(700); // keeping the image locked for 0.7 seconds
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
				            mCamera.stopPreview();
							mCamera.startPreview();
				        }
				    }
				);
				

				// Add a listener to the Capture button
				Button captureVideoButton = (Button) findViewById(R.id.button_capture_video);
				captureVideoButton.setOnClickListener(
				    new View.OnClickListener() {
				        @Override
				        public void onClick(View v) {
				            if (isRecording) {
				                // stop recording and release camera
				                mMediaRecorder.stop();  // stop the recording
				                releaseMediaRecorder(); // release the MediaRecorder object
				                mCamera.lock();         // take camera access back from MediaRecorder

				                // inform the user that recording has stopped
				                //v.
				                //setCaptureButtonText("Video");
				                isRecording = false;
				            } else {
				                // initialize video camera
				            	System.out.println("MESSAGE 1");
				                if (prepareVideoRecorder()) {
				                    // Camera is available and unlocked, MediaRecorder is prepared,
				                    // now you can start recording
				                	System.out.println("MESSAGE 2");
				                    mMediaRecorder.start();
				                    System.out.println("MESSAGE 3");

				                    // inform the user that recording has started
				                    //setCaptureButtonText("Stop");
				                    isRecording = true;
				                } else {
				                    // prepare didn't work, release the camera
				                    releaseMediaRecorder();
				                    // inform user
				                }
				            }
				        }
				    }
				);
			}
		}
		else {
			//Util.showAlert(this, R.string.dialog_missing_camera, R.string.dialog_title);
			System.out.println("camera not is there");
		}
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
		getMenuInflater().inflate(R.menu.hardware_camera, menu);
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
	
	/** Check if this device has a camera */
	private boolean checkCameraHardware(Context context) {
	    if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
	        // this device has a camera
	        return true;
	    } else {
	        // no camera on this device
	        return false;
	    }
	}
	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance(){
	    Camera c = null;
	    try {
	        c = Camera.open(); // attempt to get a Camera instance
	        
	    }
	    catch (Exception e){
	        // Camera is not available (in use or does not exist)
	    }
	    return c; // returns null if camera is unavailable
	}
	
	private PictureCallback mPicture = new PictureCallback() {

	    @Override
	    public void onPictureTaken(byte[] data, Camera camera) {

	        File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
	        if (pictureFile == null){
	            Log.d(TAG, "Error creating media file, check storage permissions: ");
	            return;
	        }

	        try {
	            FileOutputStream fos = new FileOutputStream(pictureFile);
	            fos.write(data);
	            fos.close();
	        } catch (FileNotFoundException e) {
	            Log.d(TAG, "File not found: " + e.getMessage());
	        } catch (IOException e) {
	            Log.d(TAG, "Error accessing file: " + e.getMessage());
	        }
	    }
	};
	
	/** Create a file Uri for saving an image or video */
	@SuppressWarnings("unused")
	private static Uri getOutputMediaFileUri(int type){
	      return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	@SuppressLint("SimpleDateFormat")
	private static File getOutputMediaFile(int type){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.

	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "AndroidExamples");
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("AndroidExamples", "failed to create directory");
	            return null;
	        }
	    }

	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    File mediaFile;
	    if (type == MEDIA_TYPE_IMAGE) {
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "IMG_"+ timeStamp + ".jpg");
	        Log.d("AndroidExamples","Setting the path of the image: "+mediaStorageDir.getPath() + File.separator +
	        		"IMG_"+ timeStamp + ".jpg");
	        System.out.println("AndroidExamples: Setting the path of the image: "+mediaStorageDir.getPath() + File.separator +
	    	        "IMG_"+ timeStamp + ".jpg");
	    } else if(type == MEDIA_TYPE_VIDEO) {
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "VID_"+ timeStamp + ".mp4");
	    } else {
	        return null;
	    }

	    return mediaFile;
	}
	private boolean prepareVideoRecorder(){

	    //mCamera = getCameraInstance();
	    mMediaRecorder = new MediaRecorder();

	    System.out.println("MESSAGE2");
	    // Step 1: Unlock and set camera to MediaRecorder
	    mCamera.unlock();
	    System.out.println("MESSAGE3");
	    mMediaRecorder.setCamera(mCamera);

	    System.out.println("MESSAGE4");
	    // Step 2: Set sources
	    mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
	    System.out.println("MESSAGE5");
	    mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

	    System.out.println("MESSAGE6");
	    // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
	    mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

	    System.out.println("MESSAGE7");
	    // Step 4: Set output file
	    mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());

	    System.out.println("MESSAGE8");
	    // Step 5: Set the preview output
	    mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());

	    // Step 6: Prepare configured MediaRecorder
	    try {
	        mMediaRecorder.prepare();
	    } catch (IllegalStateException e) {
	        Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
	        releaseMediaRecorder();
	        return false;
	    } catch (IOException e) {
	        Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
	        releaseMediaRecorder();
	        return false;
	    }
	    return true;
	}

    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaRecorder();       // if you are using MediaRecorder, release it first
        releaseCamera();              // release the camera immediately on pause event
    }

    private void releaseMediaRecorder(){
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.lock();           // lock camera for later use
        }
    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

}
