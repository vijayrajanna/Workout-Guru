package com.example.weka;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.CSVLoader;

public class J48Classifier 
{
	private J48 tree;
	
	public J48Classifier(Context context)
	{
		tree = new J48();         // new instance of tree
		
		ArffLoader loader = new ArffLoader();
		
		try{
			loader.setSource(context.getAssets().open("WISDM_min.arff"));
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
			CSVLoader loader = new CSVLoader();
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
		}catch(Exception e)
		{
			Log.e("J48classify",e.getMessage());
		}
		
	}
	
}
