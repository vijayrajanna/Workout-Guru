package com.example.workoutguru;



import com.example.database.MySQLiteHelper;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class RegisterUser extends Activity 
{
	private MySQLiteHelper helper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_user);
		
		helper = new MySQLiteHelper(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register_user, menu);
		return true;
	}

	public void saveUser(View view)
	{
		TextView AgeTxt = (TextView) findViewById(R.id.txtAge);
		TextView WeightTxt = (TextView) findViewById(R.id.txtWeight);
		TextView HeightTxt = (TextView) findViewById(R.id.txtHeight);
		Spinner genderSpin = (Spinner) findViewById(R.id.spinner1);
		
		int age = Integer.parseInt(AgeTxt.getText().toString());
		float weight = Float.parseFloat(WeightTxt.getText().toString());
		float height = Float.parseFloat(HeightTxt.getText().toString());
		
		Log.d("RegisterUser",""+age);
		Log.d("RegisterUser",""+weight);
		Log.d("RegisterUser",""+height);
		Log.d("RegisterUser",""+genderSpin.getSelectedItem());
		
		helper.updateUserProfile(age,weight,height,genderSpin.getSelectedItem().toString());
		
		Intent intent = new Intent(this,AccelerometerReader.class);
		startActivity(intent);
	}
}
