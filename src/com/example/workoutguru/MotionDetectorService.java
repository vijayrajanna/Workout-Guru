package com.example.workoutguru;


import android.app.IntentService;
import android.content.Intent;
import android.widget.Toast;

public class MotionDetectorService extends IntentService 
{

	public MotionDetectorService() {
	      super("MotionDetectorService");
	  }
	
	@Override
    public void onCreate() {
        super.onCreate();
        
        //Show the service has been started.
        Toast.makeText(this, getText(R.string.serviceStarted), Toast.LENGTH_SHORT).show();
        
	}
	
	@Override
	  protected void onHandleIntent(Intent intent) 
	  {
	      
	  }
}
