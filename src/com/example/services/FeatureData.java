package com.example.services;

public class FeatureData 
{
	private float xAvg;
	private float yAvg;
	private float zAvg;
	
	private float xAvgAbsDiff;
	private float yAvgAbsDiff;
	private float zAvgAbsDiff;
	
	private double xStdDev;
	private double yStdDev;
	private double zStdDev;
	
	private String datestamp;
	private int duration;
	
	public FeatureData(float xAvg, float yAvg, float zAvg,
			  float xAvgAbsDiff, float yAvgAbsDiff, float zAvgAbsDiff,
			  double xStdDev,double yStdDev,double zStdDev, String datestamp,int duration)
	{
		
		this.xAvg = xAvg;
		this.yAvg = yAvg;
		this.zAvg = zAvg;
		
		this.xAvgAbsDiff = xAvgAbsDiff;
		this.yAvgAbsDiff = yAvgAbsDiff;
		this.zAvgAbsDiff = zAvgAbsDiff;
		
		this.xStdDev = xStdDev;
		this.yStdDev = yStdDev;
		this.zStdDev = zStdDev;
		
		this.datestamp = datestamp;
		this.duration = duration;
			
	}
	
	//Thsi index should match with the attribute definition
	public double getDataValue(int attributeIndex)
	{
		double returnValue = 0;
		
		switch (attributeIndex)
		{
			case 0: returnValue = xAvg;break;
			case 1: returnValue = yAvg;break;
			case 2: returnValue = zAvg;break;
			
			case 3: returnValue = xAvgAbsDiff;break;
			case 4: returnValue = yAvgAbsDiff;break;
			case 5: returnValue = zAvgAbsDiff;break;
			
			case 6: returnValue = xStdDev;break;
			case 7: returnValue = yStdDev;break;
			case 8: returnValue = zStdDev;break;
		}
		
		
		return returnValue;
	}
	
	public String getDatestamp()
	{
		return datestamp;
	}
	
	public int getDuration()
	{
		return duration;
	}
}
