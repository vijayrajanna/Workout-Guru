package com.example.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {
	
	// Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "workout";
    
    private static final String RAWDATA = "rawdata";
   
	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);	
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// SQL statement to create book table
		String CREATE_TABLE = "CREATE TABLE rawdata ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
				"x NUMERIC, "+
				"y NUMERIC, "+
				"z NUMERIC, "+
				
				"xPeak NUMERIC, "+
				"yPeak NUMERIC, "+
				"zPeak NUMERIC ) ";
				
		
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
	
   public void getRawData()
   {
	   String query ="SELECT * From rawdata";
	   
	   SQLiteDatabase db = this.getWritableDatabase();
       Cursor cursor = db.rawQuery(query, null);
       
       if (cursor.moveToFirst()) {
           do {
        	   String row = "";
        	  row += cursor.getString(0) +",";
        	  row += cursor.getString(1) +",";
        	  row += cursor.getString(2) +",";
        	  row += cursor.getString(3) +",";
        	  row += cursor.getString(4) +",";
        	  row += cursor.getString(5) ;
        	  
        	  Log.d("Rawdata",row);
        	  
           } while (cursor.moveToNext());
       }
        
   }
    
	public void insertRawData(float x, float y, float z, float xPeak,float yPeak,float zPeak){
		
		// 1. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();
		 
		// 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put("x", x); 
        values.put("y", y); 
        values.put("z", z); 
        
        values.put("xPeak", xPeak); 
        values.put("yPeak", yPeak); 
        values.put("zPeak", zPeak); 
 
        // 3. insert
        db.insert(RAWDATA, // table
        		null, //nullColumnHack
        		values); // key/value -> keys = column names/ values = column values
        
        // 4. close
        db.close(); 
	}
}
