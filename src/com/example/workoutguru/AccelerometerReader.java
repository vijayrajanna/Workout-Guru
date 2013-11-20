package com.example.workoutguru;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.database.MySQLiteHelper;
import com.example.services.MotionDetectorService;


public class AccelerometerReader extends ActionBarActivity implements SensorEventListener
{
	
	private SensorManager mSensorManager = null; 
    private Sensor mAccelerometer = null;
    
	EditText editTextX = null;
	EditText editTextY = null;
	EditText editTextZ = null;
	TextView labelAccData = null ;
	
	Menu menu = null;
	private MySQLiteHelper helper = null;
	/*
	private BroadcastReceiver receiver = new BroadcastReceiver() {

	    @Override
	    public void onReceive(Context context, Intent intent) {
	      Bundle bundle = intent.getExtras();
	      if (bundle != null) {
	        String x = bundle.getString("x");
	        String y = bundle.getString("y");
	        String z = bundle.getString("z");	
	        
	        editTextX.setText(x);
	        editTextY.setText(y);
	        editTextZ.setText(z);
	      }
	    }
	  };
	  */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		    
		helper = new MySQLiteHelper (this);
		if(!helper.isProfileSet())
		{
			Intent intent = new Intent(this,RegisterUser.class);
		    startActivity(intent);	
		    return;
		}
		
        
		 mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
	     mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	     mSensorManager.registerListener(this, mAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);
	     
	     labelAccData = (TextView)findViewById(R.id.labelAccData);
	     labelAccData.forceLayout();
		 editTextX = (EditText)findViewById(R.id.editTextX);
		 editTextY = (EditText)findViewById(R.id.editTextY);
		 editTextZ = (EditText)findViewById(R.id.editTextZ);
		 
		 //helper.getRawData();
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
	        	Toast.makeText(this,getText(R.string.serviceInit), Toast.LENGTH_SHORT).show();
	            
	        	startService( new Intent(this,MotionDetectorService.class));
	        	item.setVisible(false);
	        	
	        	MenuItem actionstop = menu.findItem(R.id.action_stop);
	        	actionstop.setVisible(true);
	        	
	        	//registerReceiver(receiver,new IntentFilter("com.example.workoutguru"));
	        	//this.invalidateOptionsMenu();
	            return true;
	        case R.id.action_stop:
	        	MenuItem actionRun = menu.findItem(R.id.action_run);
	        	actionRun.setVisible(true);
	        	
	        	Log.d("AccelerometerAcivity","Entered stop event");
	        	
	        	stopService( new Intent(this,MotionDetectorService.class));
	        	item.setVisible(false);
	        	
	        	//unregisterReceiver(receiver);
	        	//helper.exportEmailInCSV();
	        	
	        	
	        	//this.invalidateOptionsMenu();
	            return true;
	        case R.id.action_clear:
	        	helper.clearData();
	        	return true;
	        case R.id.action_setGoals:
	            //composeMessage();
	            return true;
	        case R.id.action_reports:
	        	Intent intent = new Intent(this,AnalyticsActivity.class);
			    startActivity(intent);
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
        
	    
        //registerReceiver(receiver,new IntentFilter("com.example.workoutguru"));
        
        //SENSOR_DELAY_NORMAL = 5 times per second
        if(mSensorManager!=null)
        	mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        /*
		editTextX.setText("vijay");
		editTextY.setText("Vinay");
		editTextZ.setText("Rajanna");
		*/
    }

    protected void onPause() {
        super.onPause();
        //unregisterReceiver(receiver);
        
        if(mSensorManager!=null)
        	mSensorManager.unregisterListener(this);
    }
    
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub
        editTextX.setText(Float.toString(event.values[0]));
        editTextY.setText(Float.toString(event.values[1]));
        editTextZ.setText(Float.toString(event.values[2]));
    
                
    }
    
    @Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
    
    protected void onDestroy() { 
    	super.onDestroy();
    	stopService(new Intent(this,MotionDetectorService.class));
    	
    	 if (helper != null) {
	          helper.close();
	    }
    }

	

	
}
