package com.example.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.weka.J48Classifier;



public class ClassificationService extends Service 
{

	// Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    private J48Classifier classifier = null;
    
       
    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
    	ClassificationService getService() {
            // Return this instance of LocalService so clients can call public methods
            return ClassificationService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) 
    {
    	if(classifier == null)
    		classifier = new J48Classifier();
    	
        return mBinder;
    }
    
    /** method for clients */
    public void classifyData(FeatureData data)
    {
    	Log.d("ClassificationService","classifyData Called");
    }
}
