package com.example.workoutguru;

import com.example.workoutguru.*;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.Activity;
import android.view.Menu;


public class AccelerometerReader extends ActionBarActivity implements SensorEventListener{
	
	private float mLastX, mLastY, mLastZ;
	private boolean mInitialized; 
	private SensorManager mSensorManager; 
	private Sensor mAccelerometer; 
	private final float NOISE = (float) 2.0;
	EditText editTextX = null;
	EditText editTextY = null;
	EditText editTextZ = null;
	TextView labelAccData = null ;
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        mInitialized = false;
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);
        
         labelAccData = (TextView)findViewById(R.id.labelAccData);
         labelAccData.forceLayout();
		 editTextX = (EditText)findViewById(R.id.editTextX);
		 editTextY = (EditText)findViewById(R.id.editTextY);
		 editTextZ = (EditText)findViewById(R.id.editTextZ);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_activity, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_run:
	            //openSearch();
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
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        
		editTextX.setText("vijay");
		editTextY.setText("Vinay");
		editTextZ.setText("Rajanna");
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

	/** Called when the activity is first created. */
	public void instantiateAccelerometer()
	{

	}
	
	public void registerSensor()
	{
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
	}
	public void unregisterSensor()
	{
		mSensorManager.unregisterListener(this);
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	// can be safely ignored for this demo

	}
	
	public void onSensorChanged(SensorEvent event) {
	    // TODO Auto-generated method stub
		editTextX.setText(Float.toString(event.values[0]));
		editTextY.setText(Float.toString(event.values[1]));
		editTextZ.setText(Float.toString(event.values[2]));
		
	}
}
