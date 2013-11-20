package com.example.workoutguru;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.chart.PointStyle;
import org.achartengine.chart.TimeChart;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.charts.IDemoChart;
import com.example.database.MySQLiteHelper;

public class AnalyticsActivity extends ListActivity {
	 private static final int SERIES_NR = 2;

	  private String[] mMenuText;

	  private String[] mMenuSummary;
	  
	  private  MySQLiteHelper helper;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_analytics);
		
		helper =  new MySQLiteHelper (this);
		
	    mMenuText = new String[] { getText(R.string.ActivityTrend).toString(), getText(R.string.caloriesChart).toString() };
	    mMenuSummary = new String[] { getText(R.string.ActivityTrendDesc).toString(), getText(R.string.caloriesChartDesc).toString() };
	    setListAdapter(new SimpleAdapter(this, getListValues(), android.R.layout.simple_list_item_2,
	        new String[] { IDemoChart.NAME, IDemoChart.DESC }, new int[] { android.R.id.text1, android.R.id.text2 }));
	 
	}
	
	 private List<Map<String, String>> getListValues() {
		    List<Map<String, String>> values = new ArrayList<Map<String, String>>();
		    int length = mMenuText.length;
		    for (int i = 0; i < length; i++) {
		      Map<String, String> v = new HashMap<String, String>();
		      v.put(IDemoChart.NAME, mMenuText[i]);
		      v.put(IDemoChart.DESC, mMenuSummary[i]);
		      values.add(v);
		    }
		    return values;
		  }

		  private XYMultipleSeriesDataset getDemoDataset() {
		    XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		    final int nr = 10;
		    Random r = new Random();
		    for (int i = 0; i < SERIES_NR; i++) {
		      XYSeries series = new XYSeries("Demo series " + (i + 1));
		      for (int k = 0; k < nr; k++) {
		        series.add(k, 20 + r.nextInt() % 100);
		      }
		      dataset.addSeries(series);
		    }
		    return dataset;
		  }

		  private XYMultipleSeriesDataset getDateDemoDataset() {
		    XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		    final int nr = 10;
		    long value = new Date().getTime() - 3 * TimeChart.DAY;
		    Random r = new Random();
		    for (int i = 0; i < SERIES_NR; i++) {
		      TimeSeries series = new TimeSeries("Demo series " + (i + 1));
		      for (int k = 0; k < nr; k++) {
		        series.add(new Date(value + k * TimeChart.DAY / 4), 20 + r.nextInt() % 100);
		      }
		      dataset.addSeries(series);
		    }
		    return dataset;
		  }

		  private XYMultipleSeriesDataset getBarDemoDataset(XYMultipleSeriesRenderer renderer) {
		    XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		    Cursor cursor = helper.getSummaryData();
		    String seriesName = "";
		    int[] colors = {Color.BLUE, Color.GREEN,Color.YELLOW, Color.RED, Color.CYAN, Color.WHITE, Color.LTGRAY};
		    int count=0;
		    
		    HashSet<String> Xlabels = new HashSet<String>();
		    List<Date> dates = new ArrayList<Date>();
		    
		    if(cursor.moveToFirst())
		    {	
		    	CategorySeries series = null;
		    	
		    	do
		    	{	
		    		
		    		String currSeries = cursor.getString(0);
		    		if(!seriesName.equals(currSeries))
		    		{
		    			if(!cursor.isFirst())
		    			{
		    				dataset.addSeries(series.toXYSeries());
		    				SimpleSeriesRenderer seriesrenderer = new SimpleSeriesRenderer();
		    				seriesrenderer.setColor(colors[count]);
		    				renderer.addSeriesRenderer(seriesrenderer);
		    				count++;
		    			}
		    			
		    			series = new CategorySeries(currSeries);
		    		}
		    		String dateStr = cursor.getString(1);
		    		
		    		Log.d("CursorData",currSeries + " " + cursor.getString(1) + " " + cursor.getInt(2));
		    		
		    		Xlabels.add(dateStr);
		    		series.add(dateStr,cursor.getInt(2));
		    		
		    	}while(cursor.moveToNext());
		    	
		    	Object[] datesList = Xlabels.toArray();
		    	try
		    	{
		    		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
			    	for(Object date : datesList)
			    	{
			    		dates.add(dateformat.parse((String)date));
			    	}
			    	
			    	renderer.setXLabels(0);
			    	Collections.sort(dates);
			    	for(double i=0;i<dates.size();i++)
			    	{
			    		renderer.addXTextLabel(i+1, dateformat.format(dates.get((int)i)));
			    	}
			    	
		    	}catch(Exception e)
		    	{
		    		Log.e("AnalyticsActivity","" + e.getMessage());
		    	}
			    dataset.addSeries(series.toXYSeries());
		    	SimpleSeriesRenderer seriesrenderer = new SimpleSeriesRenderer();
				seriesrenderer.setColor(colors[count]);
		    	renderer.addSeriesRenderer(seriesrenderer);
		    }
		    
		    return dataset;
		  }

		  private XYMultipleSeriesRenderer getDemoRenderer() {
		    XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		    renderer.setAxisTitleTextSize(16);
		    renderer.setChartTitleTextSize(20);
		    renderer.setLabelsTextSize(15);
		    renderer.setLegendTextSize(15);
		    renderer.setPointSize(5f);
		    renderer.setMargins(new int[] {20, 30, 15, 0});
		    XYSeriesRenderer r = new XYSeriesRenderer();
		    r.setColor(Color.BLUE);
		    r.setPointStyle(PointStyle.SQUARE);
		    r.setFillBelowLine(true);
		    r.setFillBelowLineColor(Color.WHITE);
		    r.setFillPoints(true);
		    renderer.addSeriesRenderer(r);
		    r = new XYSeriesRenderer();
		    r.setPointStyle(PointStyle.CIRCLE);
		    r.setColor(Color.GREEN);
		    r.setFillPoints(true);
		    renderer.addSeriesRenderer(r);
		    renderer.setAxesColor(Color.DKGRAY);
		    renderer.setLabelsColor(Color.LTGRAY);
		    return renderer;
		  }

		  public void setActivityChartSettings(XYMultipleSeriesRenderer renderer)
		  {
			  renderer.setChartTitle("Activity Trend");
			  renderer.setXTitle("Days");
			  renderer.setYTitle("Active minutes");
			  /*
			  SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			    r.setColor(Color.BLUE);
			    renderer.addSeriesRenderer(r);
			    r = new SimpleSeriesRenderer();
			    r.setColor(Color.GREEN);
			    renderer.addSeriesRenderer(r);
			   */
		  }
		  
		  
		  private void setChartSettings(XYMultipleSeriesRenderer renderer) {
			    renderer.setAxisTitleTextSize(30);
			    renderer.setChartTitleTextSize(40);
			    renderer.setLabelsTextSize(25);
			    renderer.setLegendTextSize(40);
			    renderer.setMargins(new int[] {80, 80, 80, 10});
			    renderer.setYLabelsAlign(Align.RIGHT);
		  }

		  @Override
		  protected void onListItemClick(ListView l, View v, int position, long id) {
		    super.onListItemClick(l, v, position, id);
		    
		    XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		    setChartSettings(renderer);
		    
		    switch (position) {
		    case 0:
		      setActivityChartSettings(renderer);
		      XYMultipleSeriesDataset dataset = getBarDemoDataset(renderer);
		      
		      Intent intent = ChartFactory.getBarChartIntent(this, dataset, renderer, Type.DEFAULT);
		      startActivity(intent);
		      
		      if(dataset.getSeriesCount()==0)
		    	  Toast.makeText(this, getText(R.string.noActivityData), Toast.LENGTH_LONG).show();
		      
		      break;
		    case 1:
			  setChartSettings(renderer);
		      intent = ChartFactory.getBarChartIntent(this, getBarDemoDataset(renderer), renderer, Type.DEFAULT);
		      startActivity(intent);
		      break;
		    }
		  }


}
