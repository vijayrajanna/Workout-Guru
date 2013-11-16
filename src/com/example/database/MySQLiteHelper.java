package com.example.database;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.example.weka.J48Classifier;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {
	
	// Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "workout";
    
    private static final String RAWDATA = "rawdata";
   
    private Context context;
    
	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);	
		
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// SQL statement to create book table
		String CREATE_TABLE = "CREATE TABLE rawdata ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
				"xAvg NUMERIC, "+
				"yAvg NUMERIC, "+
				"zAvg NUMERIC, "+
				
				"xAvgAbsDiff NUMERIC, "+
				"yAvgAbsDiff NUMERIC, "+
				"zAvgAbsDiff NUMERIC, "+
				
				"xStdDev NUMERIC, "+
				"yStdDev NUMERIC, "+
				"zStdDev NUMERIC "+
				
				") ";
				
		
		// create books table
		db.execSQL(CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older books table if existed
        db.execSQL("DROP TABLE IF EXISTS rawdata");
        
        // create fresh books table
        this.onCreate(db);
	}
	
	public void exportEmailInCSV()
	{
        File folder = new File(Environment.getExternalStorageDirectory() + "/Folder");

        boolean var = false;
        if (!folder.exists())
            var = folder.mkdir();

        Log.d("exportEmailInCSV","Folder name: " + folder);
        
        final String pathToFolder = folder.toString();
        final String filename =  pathToFolder + "/" + "Test.csv";
        
        new Thread() {
            public void run() {
                try {

                    FileWriter fw = new FileWriter(filename);

                    Cursor cursor = getRawData();
                    
                    fw.append("XAVG,YAVG,ZAVG,XABSOLDEV,YABSOLDEV,ZABSOLDEV,XSTANDDEV,YSTANDDEV,ZSTANDDEV,CLASS\n");	
                    if (cursor.moveToFirst()) {
                        do {
                     	   String row = "";
                     	  row += cursor.getString(0) +",";
                     	  row += cursor.getString(1) +",";
                     	  row += cursor.getString(2) +",";
                     	  row += cursor.getString(3) +",";
                     	  row += cursor.getString(4) +",";
                     	  row += cursor.getString(5) +",";
                     	  row += cursor.getString(6) +",";
                     	  row += cursor.getString(7) +",";
                     	  row += cursor.getString(8) +",?";
                     	 
                     	 fw.append(row); 
                     	 fw.append('\n');
                     	  
                        } while (cursor.moveToNext());
                    }
                    if (cursor != null && !cursor.isClosed()) {
                        cursor.close();
                    }

                    J48Classifier classifier = new J48Classifier(context);
    	        	classifier.classify(pathToFolder,filename);
    	        	
                    // fw.flush();
                    fw.close();

                } catch (Exception e) {
                }
              
            }
        }.start();

    }

	
   public Cursor getRawData()
   {
	   String query ="SELECT xAvg,yAvg,zAvg," + 
			   		 " xAvgAbsDiff, yAvgAbsDiff, zAvgAbsDiff, "+
			   		 " xStdDev, yStdDev, zStdDev From rawdata";
	   
	   SQLiteDatabase db = this.getWritableDatabase();
       Cursor cursor = db.rawQuery(query, null);
       
       return cursor;
   }
       
       
   
    
	public void insertRawData(float xAvg, float yAvg, float zAvg,
							  float xAvgAbsDiff, float yAvgAbsDiff, float zAvgAbsDiff,
							  double xStdDev,double yStdDev,double zStdDev){
		
		// 1. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();
		 
		// 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put("xAvg", xAvg); 
        values.put("yAvg", yAvg); 
        values.put("zAvg", zAvg); 
        
        values.put("xAvgAbsDiff", xAvgAbsDiff); 
        values.put("yAvgAbsDiff", yAvgAbsDiff); 
        values.put("zAvgAbsDiff", zAvgAbsDiff); 
        
        values.put("xStdDev", xStdDev); 
        values.put("yStdDev", yStdDev); 
        values.put("zStdDev", zStdDev); 
 
        // 3. insert
        db.insert(RAWDATA, // table
        		null, //nullColumnHack
        		values); // key/value -> keys = column names/ values = column values
        
        // 4. close
        db.close(); 
	}
}
