package com.example.workoutguru;


import java.util.Calendar;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Toast;

import com.example.database.MySQLiteHelper;

public class MotionDetectorService extends IntentService implements SensorEventListener
{

	private MySQLiteHelper helper = null;
	private SensorManager mSensorManager; 
	private Sensor mAccelerometer; 
	
	private float XSum=0, YSum=0, ZSum=0;
	private float XMax = 0, YMax=0,ZMax=0;
	
	private int currSecond = 0;
	
	public MotionDetectorService() {
	      super("MotionDetectorService");
	  }
	
	@Override
    public void onCreate() {
        super.onCreate();
        
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);
        
        helper = new MySQLiteHelper (this);
        
        //Show the service has been started.
        Toast.makeText(this, getText(R.string.serviceStarted), Toast.LENGTH_SHORT).show();
        
	}
	
	@Override
	  protected void onHandleIntent(Intent intent) 
	  {
	      
	  }

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];
		
		XMax = (XMax < x)? x:XMax;
		YMax = (YMax < y)? y:YMax;
		ZMax = (ZMax < z)? z:ZMax;
		
		Calendar c = Calendar.getInstance(); 
		int seconds = c.get(Calendar.SECOND);
			
		if(seconds - currSecond <= 10)
		{
			this.XSum += x;
			this.YSum += y;
			this.ZSum += z;
		}
		else
		{
			helper.insertRawData(x, y, z,XMax,YMax,ZMax);
			
			this.XSum = XMax = x;
			this.YSum = YMax = y;
			this.ZSum = ZMax = z;
		}
		
		
	}
}
