package com.example.weka;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import android.util.Log;

import com.example.database.MySQLiteHelper;
import com.example.services.FeatureData;
import com.example.utils.Constants;

public class J48Classifier 
{
	private J48 tree;
	
	public J48Classifier()
	{
		tree = new J48();         // new instance of tree
		
		ArffLoader loader = new ArffLoader();
		
		try{
			loader.setSource(MySQLiteHelper.getTrainingSetIS());
			Instances instances = loader.getDataSet();
			instances.setClassIndex(instances.numAttributes() - 1);
			tree.buildClassifier(instances);
		}catch(Exception e)
		{
			Log.e("J48classify","Error initializing the classifier: " + e.getMessage());
		}
	}
	
	public void classify(String pathToCSVfFile, String fileName)
	{
		BufferedWriter writer = null;
		//String pathToCSVfFile = Environment.getExternalStorageDirectory() + "/Folder/";
		Log.d("J48classify","Starting classify method");
		Log.d("J48classify","pathToCSVfFile: " + pathToCSVfFile);
		Log.d("J48classify","fileName: " + fileName);
		
		try
		{
			ArffLoader loader = new ArffLoader();
			loader.setSource(new File(fileName));
			Instances unlabeled = loader.getDataSet();
			
			Log.d("J48Classify","Done loading CSV file");
			// set class attribute
			 unlabeled.setClassIndex(unlabeled.numAttributes() - 1);
			 Log.d("J48Classify","Done 1");
			 // create copy
			 Instances labeled = new Instances(unlabeled);
			 Log.d("J48Classify","Done 2");
			// label instances
			 for (int i = 0; i < unlabeled.numInstances(); i++) {
			   double clsLabel = tree.classifyInstance(unlabeled.instance(i));
			   labeled.instance(i).setClassValue(clsLabel);
			 }
			 Log.d("J48Classify","Done 3");
			 // save labeled data
			 writer = new BufferedWriter(new FileWriter(pathToCSVfFile + "labeled.arff"));
			 writer.write(labeled.toString());
			 writer.newLine();
			 writer.flush();
			 writer.close();
			 Log.d("J48Classify","Done 4");
		}catch(Exception e)
		{
			Log.e("J48classify",e.getMessage());
		}
		
	}
	
	public void classifySingleInstance(FeatureData dataPoint)
	{
		ArrayList<Attribute> attributeList = new ArrayList<Attribute>(2);
		int lastAttrIndex = Constants.ATTRIBUTES.length - 1;
		
		for(int i=0;i<lastAttrIndex; i++)
		{
			Attribute attr = new Attribute(Constants.ATTRIBUTES[i]);
			attributeList.add(attr);
		}

		ArrayList<String> classVal = new ArrayList<String>();
		for(int j=0;j<Constants.CLASSES.length;j++)
		{
			classVal.add(Constants.CLASSES[j]);
		}
        
		attributeList.add(new Attribute("@@class@@",classVal));

        Instances data = new Instances("TestInstances",attributeList,0);
        data.setClass(attributeList.get(attributeList.size()-1));

        // Create instances for each pollutant with attribute values latitude,
        // longitude and pollutant itself
        DenseInstance inst_co = new DenseInstance(data.numAttributes());
        inst_co.setDataset(data);
        data.add(inst_co);

       for(int i=0;i<lastAttrIndex;i++)
       {
    	   inst_co.setValue(attributeList.get(i), dataPoint.getDataValue(i));
       }
       
       try {
    	   double result = tree.classifyInstance(inst_co);
    	   Log.d("J48classify","Single Instance Classification Result " + result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
