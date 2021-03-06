package com.example.database;

import java.io.InputStream;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.utils.Constants;

public class MySQLiteHelper extends SQLiteOpenHelper {
	
	// Database Version
    private static final int DATABASE_VERSION = 4;
    // Database Name
    private static final String DATABASE_NAME = "workout";
    
    //private static final String RAWDATA = "rawdata";
    private static final String SUMMARYTBL = "SUMMARY";
   
    private static Context context;
    
	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);	
		
		if(context!=null)
			this.context = context;
	}

	public static InputStream getTrainingSetIS()
	{
		InputStream is = null;
		try
		{
			is = context.getAssets().open(Constants.TRAIING_SET_FILE);
		}
		catch(Exception e)
		{
			Log.e("MySQLiteHelper","Exception occured when getting Training set input stream " + e.getMessage());
		}
		
		return is;
	}
	
	public static Context getContext()
	{
		return context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// SQL statement to create book table
		/*
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
		*/		
		
		String CREATE_SUMM_TBL = "CREATE TABLE SUMMARY ( " +
						        "_id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
								"workout_date TEXT, "+
								"seconds REAL, "+
								"activity_name TEXT ) ";
		
		String USER_PROFILE = "CREATE TABLE USER_PROFILE ( " +
		        "AGE REAL, " + 
				"GENDER TEXT, "+
				"WEIGHT REAL, "+
				"HEIGHT REAL ) ";
		
		// create rawdata table
		//db.execSQL(CREATE_TABLE);
		db.execSQL(CREATE_SUMM_TBL);
		db.execSQL(USER_PROFILE);
	}
	
	public boolean isProfileSet()
	{
		boolean isSet = false;
		SQLiteDatabase db = this.getWritableDatabase();
		
		String searchQuery ="SELECT 1 From USER_PROFILE";       
        Cursor cursor = db.rawQuery(searchQuery, null);
        if(cursor.moveToFirst())
        	isSet = true;
        
        return isSet;
        
	}
	public void updateUserProfile(int age, float weight, float height, String gender)
	{
		
		ContentValues values = new ContentValues();
        values.put("AGE", age);
        values.put("GENDER", gender);
        values.put("WEIGHT", weight);
        values.put("HEIGHT", height);
        
		SQLiteDatabase db = this.getWritableDatabase();
		
        if(isProfileSet())
        {
        	db.update("USER_PROFILE", values, null, null);
        }
        else
        {
        	db.insert("USER_PROFILE", null, values);
        }
        	
        db.close(); 
	}
	
	public void addSummaryData(String date, String activityName, int seconds)
	{
		
		boolean doUpdate = false;
		boolean doInsert = false;
		
		SQLiteDatabase db = this.getWritableDatabase();
		
        String whereClause = " workout_date=? and activity_name=?";
        String[] whereargs = {date,activityName};
        
        // 1. Check if record exists
        //Log.d("MySQLiteHelper","2: " + whereargs[0] + whereargs[1]);
        String searchQuery ="SELECT seconds From " + SUMMARYTBL + " WHERE " + whereClause;        
        Cursor cursor = db.rawQuery(searchQuery, whereargs);
        if(cursor.moveToFirst())
        {
        	doUpdate = true;
        	seconds += cursor.getInt(0);
        }
        else 
        	doInsert = true;
        //Log.d("MySQLiteHelper","3");
        ContentValues values = new ContentValues();
        values.put("seconds", seconds); 
        //Log.d("MySQLiteHelper","4");
        // 2. update
        if(doUpdate)
        	db.update(SUMMARYTBL, values, whereClause, whereargs);
        //Log.d("MySQLiteHelper","5");
        // 3. insert
        if(doInsert)
        {
        	//Add these too if its insert
        	values.put("workout_date", date); 
            values.put("activity_name", activityName); 
            
        	db.insert(SUMMARYTBL, null, values);
        }
        //Log.d("MySQLiteHelper","6");
        // 4. close
        db.close(); 
	}
	
	public Cursor getSummaryData()
	{
		String query ="SELECT activity_name,workout_date,CAST(seconds as REAL)/60.0 as duration From " + SUMMARYTBL + " ORDER BY activity_name,date(workout_date)";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);

		return cursor;
	}
	
	public Cursor getCalorieData()
	{
		String query ="SELECT mainTbl.activity_name,mainTbl.workout_date," +
					  " ROUND(mainTbl.wInKg * mainTbl.timeInHours * (CASE WHEN activity_name IN ('Sitting') THEN 1.0/mainTbl.BMRNorm " +
				           " WHEN activity_name IN ('Walking','Standing') THEN 3.0/mainTbl.BMRNorm" +
				           " WHEN activity_name IN ('Upstairs','Downstairs') THEN 2.5/mainTbl.BMRNorm" +
				           " ELSE 7.0/mainTbl.BMRNorm END),2)  " +
					" FROM (SELECT activity_name,workout_date, weight * 0.0454 as wInKg, seconds/(60.0*60.0) as timeInHours, " + 
					  "CASE WHEN GENDER='Male' THEN ((13.75 * weight * 0.0454) + (5 * height * 2.54) - (6.76 * age) + 66)/(weight * 0.0454 * 24) " +
				          " WHEN GENDER ='Female' THEN ((9.56 * weight * 0.0454) + (1.85 * height * 2.54) - (4.68 * age) + 655)/(weight * 0.0454 * 24) END as BMRNorm " +
					  "From " + SUMMARYTBL + ", USER_PROFILE) as mainTbl " +
					  " ORDER BY activity_name,date(workout_date)";
		Log.d("CalorieQuery",query);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);

		return cursor;
	}
	
	public void clearData()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		db.delete(SUMMARYTBL, null, null);
		
		db.close();
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older books table if existed
        db.execSQL("DROP TABLE IF EXISTS " + SUMMARYTBL);
        db.execSQL("DROP TABLE IF EXISTS rawdata");
        db.execSQL("DROP TABLE IF EXISTS USER_PROFILE");
        
        // create fresh books table
        this.onCreate(db);
	}
	/*
	public void exportEmailInCSV()
	{
        File folder = new File(Environment.getExternalStorageDirectory() + "/Folder");

        boolean var = false;
        if (!folder.exists())
            var = folder.mkdir();

        Log.d("exportEmailInCSV","Folder name: " + folder);
        
        final String pathToFolder = folder.toString();
        final String filename =  pathToFolder + "/" + "Test.arff";
        
        new Thread() {
            public void run() {
                try {

                    FileWriter fw = new FileWriter(filename);
                    
                    Cursor cursor = getRawData();
                    fw.append("@relation activity_recognition_test\n\n");
                    
                    String[] attributes = {"XAVG","YAVG","ZAVG","XABSOLDEV","YABSOLDEV","ZABSOLDEV","XSTANDDEV","YSTANDDEV","ZSTANDDEV","CLASS"};
                    for(int i=0;i<attributes.length;i++)
                    {                    	
                    	fw.append("@attribute " + attributes[i]);
                    	if(attributes[i].equalsIgnoreCase("CLASS"))
                    		fw.append(" {Walking,Jogging,Upstairs,Downstairs,Sitting,Standing}\n");
                    	else
                    		fw.append(" numeric\n");
                    }
                    
                    
                    fw.append("\n");	
                    fw.append("@data\n");
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

                    fw.flush();
                    fw.close();
                    
                    J48Classifier classifier = new J48Classifier();
    	        	classifier.classify(pathToFolder,filename);
    	        	
                    

                } catch (Exception e) {
                }
              
            }
        }.start();

    }

	
   public Cursor getRawData()
   {
	   String query ="SELECT id,xAvg,yAvg,zAvg," + 
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
	*/
}
