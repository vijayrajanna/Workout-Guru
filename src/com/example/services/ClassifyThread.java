package com.example.services;

import java.util.concurrent.BlockingQueue;

import android.util.Log;

import com.example.database.MySQLiteHelper;
import com.example.weka.J48Classifier;

public class ClassifyThread extends Thread 
{
	 BlockingQueue<FeatureData> queue;
	 private J48Classifier classifier = null;
	 private MySQLiteHelper helper = null;
	 
	 private boolean isActive = true;
	 
	 public void deactivateThread()
	 {
		 isActive = false;
	 }
	 
	public 	ClassifyThread(String name,BlockingQueue<FeatureData> queue)
	{
		super(name);
		this.queue = queue;
		helper = new MySQLiteHelper(MySQLiteHelper.getContext());
		
		if(classifier == null)
    		classifier = new J48Classifier();
	}
	
	public void run()
	{
		
		isActive = true;
		while(true)
		{
			try
			{
				//Log.d("ClassifyThread","" + queue.isEmpty());
				if(queue.isEmpty())
				{
					this.sleep(100);
					continue;
				}
				
				FeatureData data = queue.take();
				String activityName = classifier.classifySingleInstance(data);
				
				helper.addSummaryData(data.getDatestamp(), activityName, data.getDuration());
				
			}catch(Exception e)
			{
				Log.e("ClassifyThread",""+e.getMessage());
			}
			
			Log.d("ClassifyThread","Entered Run. IsAction: " + isActive);
			if(isActive!=true)
				break;
		}
	}
	
}
