package com.example.services;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;

import com.example.workoutguru.R;

public class MotionDetectorService extends IntentService implements SensorEventListener
{
	
	//private MySQLiteHelper helper = null;
	private SensorManager mSensorManager; 
	private Sensor mAccelerometer; 
	
	private float[] xArr = new float[200];
	private float[] yArr = new float[200];
	private float[] zArr = new float[200];
	
	private float XSum=0, YSum=0, ZSum=0;
	//private float XMax = 0, YMax=0,ZMax=0;
	
	private int currSecond = 0;
	private int count = 0;
	private final int points = 200;
	private ClassifyThread classificationThread; 
	private BlockingQueue<FeatureData> queue;
	private long previousStamp;
	
	
	public MotionDetectorService() {
	      super("MotionDetectorService");
	  }
	
	private void resetValues()
	{
		xArr = new float[points];
		yArr = new float[points];
		zArr = new float[points];
	}
	@Override
    public void onCreate() {
        super.onCreate();
        
        Toast.makeText(this,getText(R.string.serviceInit), Toast.LENGTH_SHORT).show();
        
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);
        
        //helper = new MySQLiteHelper (this);
        queue = new ArrayBlockingQueue<FeatureData>(10);
        classificationThread = new ClassifyThread("ClassifyThread",queue); 
        classificationThread.start();
        //Show the service has been started.
        Toast.makeText(this, getText(R.string.serviceStarted), Toast.LENGTH_SHORT).show();
        previousStamp = System.currentTimeMillis();
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
	public void onDestroy()
	{
		//classificationThread.deactivateThread();
	}
	
	private void publishResults(float x, float y, float z) {
	    Intent intent = new Intent("com.example.workoutguru");
	    intent.putExtra("x", x);
	    intent.putExtra("y", y);
	    intent.putExtra("z", z);
	    sendBroadcast(intent);
	  }
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		
		
		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];
		
		publishResults(x,y,z);
		
		/*
		XMax = (XMax < x)? x:XMax;
		YMax = (YMax < y)? y:YMax;
		ZMax = (ZMax < z)? z:ZMax;
		*/
		
		
		//int seconds = c.get(Calendar.SECOND);
			
		if(count < points)
		{
			xArr[count] = x;
			yArr[count] = y;
			zArr[count] = z;
			
			this.XSum += x;
			this.YSum += y;
			this.ZSum += z;
			
			count++;
		}
		else
		{
			
			
			float xAvg = XSum/count;
			float yAvg = YSum/count;
			float zAvg = ZSum/count;
			
			float[] xAvgDiff = new float[points];
			float[] yAvgDiff = new float[points];
			float[] zAvgDiff = new float[points];
			
			float xAvgAbsDiff=0,yAvgAbsDiff=0,zAvgAbsDiff=0;
			double xStdDev = 0,yStdDev=0,zStdDev=0; 
			//LS: find the absolute difference of each point with the mean
			for(int i=0;i<points;i++)
			{
				float xDiff = Math.abs(xArr[i] - xAvg);
				float yDiff = Math.abs(yArr[i] - yAvg);
				float zDiff = Math.abs(zArr[i] - zAvg);
				
				xAvgDiff[i] = xDiff;
				yAvgDiff[i] = yDiff;
				zAvgDiff[i] = zDiff;
				
				xAvgAbsDiff += xDiff;
				yAvgAbsDiff += yDiff;
				zAvgAbsDiff += zDiff;
				
				xStdDev += xDiff*xDiff;
				yStdDev += yDiff*yDiff;
				zStdDev += zDiff*zDiff;
			}
			//Finally calculate the mean absolute difference
			xAvgAbsDiff = xAvgAbsDiff/points;
			yAvgAbsDiff = yAvgAbsDiff/points;
			zAvgAbsDiff = zAvgAbsDiff/points;
			
			//Standard deviation is sqrt of the sum of square of the absolute difference values divided by N
			
			xStdDev = Math.sqrt(xStdDev/points);
			yStdDev = Math.sqrt(yStdDev/points);
			zStdDev = Math.sqrt(zStdDev/points);
			
			//Date date = new Date();
			long currStamp = System.currentTimeMillis();
			int seconds = (int)((currStamp - previousStamp)/1000);
			String formattedDate = (new SimpleDateFormat("yyyy/MM/dd")).format(new Date()); 
			
			//helper.insertRawData(xAvg,yAvg, zAvg,xAvgAbsDiff,yAvgAbsDiff,zAvgAbsDiff,xStdDev,yStdDev,zStdDev);
			FeatureData data = new FeatureData(xAvg,yAvg, zAvg,xAvgAbsDiff,yAvgAbsDiff,zAvgAbsDiff,
												xStdDev,yStdDev,zStdDev,formattedDate,seconds);
			queue.add(data);
			
			Log.d("MotionDetectorSensor","Added datapoint to queue");
			//mBoundService.classifyData(data);
			//Log.d("MotionDetectorSensor","Call after classfify");
			count = 0;
			this.XSum = x;
			this.YSum = y;
			this.ZSum = z;
			
			xArr = new float[points];
			yArr = new float[points];
			zArr = new float[points];
			
			previousStamp = currStamp;
		}
		
	}
}
