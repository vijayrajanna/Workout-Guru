package com.example.workoutguru;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.example.database.MySQLiteHelper;
import com.example.weka.J48Classifier;


public class AccelerometerReader extends ActionBarActivity
{
	
	private float mLastX, mLastY, mLastZ;
	private boolean mInitialized; 
	
	private final float NOISE = (float) 2.0;
	EditText editTextX = null;
	EditText editTextY = null;
	EditText editTextZ = null;
	TextView labelAccData = null ;
	
	Menu menu = null;
	private MySQLiteHelper helper = null;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        mInitialized = false;
         
	     labelAccData = (TextView)findViewById(R.id.labelAccData);
	     labelAccData.forceLayout();
		 editTextX = (EditText)findViewById(R.id.editTextX);
		 editTextY = (EditText)findViewById(R.id.editTextY);
		 editTextZ = (EditText)findViewById(R.id.editTextZ);
		 
		 helper = new MySQLiteHelper (this);
		 helper.getRawData();
	}

	public boolean onCreateOptionsMenu(Menu menu) 
	{
		this.menu = menu;
		   	
		// Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_activity, menu);
	    
	    MenuItem actionRun = menu.findItem(R.id.action_stop);
    	actionRun.setVisible(false);
	    
    	return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_run:
	        	startService(new Intent(this,MotionDetectorService.class));
	        	item.setVisible(false);
	        	
	        	MenuItem actionstop = menu.findItem(R.id.action_stop);
	        	actionstop.setVisible(true);
	        	
	        	//this.invalidateOptionsMenu();
	            return true;
	        case R.id.action_stop:
	        	MenuItem actionRun = menu.findItem(R.id.action_run);
	        	actionRun.setVisible(true);
	        	
	        	stopService(new Intent(this,MotionDetectorService.class));
	        	item.setVisible(false);
	        	helper.exportEmailInCSV();
	        	
	        	
	        	//this.invalidateOptionsMenu();
	            return true;
	        case R.id.action_setGoals:
	            //composeMessage();
	            return true;
	        case R.id.action_reports:
	            //composeMessage();
	            return true;
	        case R.id.action_recommendation:
	            //composeMessage();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
    protected void onResume() {
        super.onResume();
        /*
        //SENSOR_DELAY_NORMAL = 5 times per second
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        
		editTextX.setText("vijay");
		editTextY.setText("Vinay");
		editTextZ.setText("Rajanna");
		*/
    }

    protected void onPause() {
        super.onPause();
        //mSensorManager.unregisterListener(this);
    }
    
    protected void onDestroy() { 
    	super.onDestroy();
    	
    	 if (helper != null) {
	          helper.close();
	    }
    }

	/** Called when the activity is first created. */
	public void instantiateAccelerometer()
	{

	}
	
}
