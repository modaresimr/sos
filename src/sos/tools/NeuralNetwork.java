package sos.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/** we should consider what we do */
public class NeuralNetwork {
	
	private String NeuralNetworkType = "";
	private File NeuralFile; // The file that contains the neural file
	private int numOfLayers; // number of layers (without considering the input layer)
	private int[] netLayers; // the information about the size of layers
	private String[] layerFunctions; // the functions that been used in each layer
	private ArrayList<float[][]> weightsAndBiases; // weights and biases of the nn
	float[][] outMinMax; // min max of the real input values
	float[][] inMinMax; // min max of real target values
	
	// Angeh2010******************************************************************************************************************
	/** Constructor-1-*A2 */
	public NeuralNetwork() {
		float f[][] = {};
		mapminmax(f, "apply");
		System.out.println("\nERROR!\n illigal agent type.\nDefine an agent Type: ambulance, police1, police2, fire");
		throw new IllegalArgumentException("\nERROR!\n illigal agent type");
	}
	
	// Angeh2010******************************************************************************************************************
	/** loads the NeuralNetwork from its proper file-Constructor2* A2 */
	public NeuralNetwork(String NeuralNetworkType) {
		// Here we start building our network
		this.NeuralNetworkType = NeuralNetworkType;
		File folder = new File("Neurals");
		// set the proper file
		if (NeuralNetworkType.compareTo("SpecialNN") == 0) {
			NeuralFile = new File(folder.getPath() + "/" + "SpecialNN.nn");
			readNetworkFromFile();
		} else {
			System.out.println("\nERROR!\n illigal agent type\nDefine an agent Type:" + NeuralNetworkType);
			throw new IllegalArgumentException("\nERROR!\n illigal agent type");
		}
	}
	
	// Angeh2010******************************************************************************************************************
	public String getNetworkType() {
		return NeuralNetworkType;
	}
	
	// Angeh2010******************************************************************************************************************
	/** simulates the neural network */
	public float[][] simulate(float[][] inputVector) {
		
		// maps the inputs on the [-1,1] scale
		// inputVector = mapminmax(inputVector,"apply");//commented by Angeh
		
		// multiply two matrices and add the biases to them
		float[][] layerOutput;
		layerOutput = add(mult(inputVector, weightsAndBiases.get(0)), weightsAndBiases.get(1));
		layerOutput = applyFunction(layerOutput, layerFunctions[0]);
		
		for (int i = 2; i < (numOfLayers * 2); i += 2) {
			layerOutput = add(mult(layerOutput, weightsAndBiases.get(i)), weightsAndBiases.get(i + 1));
			layerOutput = applyFunction(layerOutput, layerFunctions[Math.round(i / 2)]);
		}
		
		// now layerOutput should contain the output
		// reverse the scales on the output
		// layerOutput = mapminmax(layerOutput,"reverse");//commented by Angeh
		
		// output is a 0xN(output layer) dimention
		return layerOutput;
	}
	
	// Angeh2010******************************************************************************************************************
	/** applies the functions on the input */
	private float[][] applyFunction(float[][] M, String func) {
		// if the function is purelin (y=x) then do not do anything!
		if (func.compareTo("purelin") == 0) {
			return M;
		}
		
		// check if it supports the current function
		if ((func.compareTo("tansig") != 0) && (func.compareTo("logsig") != 0)) {
			System.out.println("\nERROR! \n Cannot find " + func + " function. ");
			throw new IllegalArgumentException("Cannot find " + func + " function. ");
		}
		
		int Mx = M.length;
		int My = M[0].length;
		float[][] Mout = new float[Mx][My];
		for (int i = 0; i < Mx; i++) {
			for (int j = 0; j < My; j++) {
				if (func.compareTo("tansig") == 0) {
					Mout[i][j] = (float) (2.0F / (1.0F + Math.exp(-2.0F * M[i][j])) - 1.0F);
				} else if (func.compareTo("logsig") == 0) {
					Mout[i][j] = (float) (1.0F / (1.0F + Math.exp(-1.0 * M[i][j])));
				}
			}
		}
		return Mout;
	}
	
	// Angeh2010******************************************************************************************************************
	/**
	 * multiplies two matrices
	 */
	private float[][] mult(float[][] M1, float[][] M2) {
		int m1rows = M1.length;
		int m1cols = M1[0].length;
		int m2rows = M2.length;
		int m2cols = M2[0].length;
		if (m1cols != m2rows) {
			System.out.println("\nERROR!\n Matrices don't match: " + m1cols + " != " + m2rows);
			throw new IllegalArgumentException("matrices don't match: " + m1cols + " != " + m2rows);
		}
		float[][] M3 = new float[m1rows][m2cols];
		
		// multiply
		for (int i = 0; i < m1rows; i++)
			for (int j = 0; j < m2cols; j++)
				for (int k = 0; k < m1cols; k++)
					M3[i][j] += M1[i][k] * M2[k][j];
		
		return M3;
	}
	
	// Angeh2010******************************************************************************************************************
	/** adds two matrices with the same dimension */
	private float[][] add(float[][] M1, float[][] M2) {
		int Mx = M1.length;
		int My = M1[0].length;
		float[][] M3 = new float[Mx][My];
		
		for (int i = 0; i < Mx; i++) {
			for (int j = 0; j < My; j++) {
				M3[i][j] = M1[i][j] + M2[i][j];
			}
		}
		
		return M3;
	}
	
	// Angeh2010******************************************************************************************************************
	/** transposes the current matrix a(i,j)=a(j,i) */
	private float[][] transpose(float[][] in) {
		float[][] out = new float[in[0].length][in.length];
		for (int i = 0; i < in.length; ++i) {
			for (int j = 0; j < in[0].length; ++j) {
				out[j][i] = in[i][j];
			}
		}
		
		return out;
	}
	
	// Angeh2010******************************************************************************************************************
	/**
	 * this functions operates as mapminmax in MATLAB
	 */
	private float[][] mapminmax(float[][] input, String method) {
		int Mx = input.length;
		int My = input[0].length;
		float[][] output = new float[Mx][My];
		float ymin = -1.0F;
		float ymax = 1.0F;
		float ymax_ymin = ymax - ymin;
		if (method.compareTo("apply") == 0) // apply mapminmax on inputs for the range of [-1,1]
		{
			for (int i = 0; i < Mx; i++) {
				for (int j = 0; j < My; j++) {
					output[i][j] = (ymax_ymin * (input[i][j] - inMinMax[j][0]/* xmin */) / (inMinMax[j][1]/* xmax */- inMinMax[j][0]/* xmin */) + ymin);
				}
			}
		} else if (method.compareTo("reverse") == 0) // apply mapminmax on outputs
		{
			for (int i = 0; i < Mx; i++) {
				for (int j = 0; j < My; j++) {
					output[i][j] = (((input[i][j] - ymin) * (outMinMax[j][1] - outMinMax[j][0])) / ymax_ymin) + outMinMax[j][0];
				}
			}
		} else {
			System.out.println("\nERROR!\n invalid argument. you should use apply or reverse");
			throw new IllegalArgumentException("invalid argument. you should use apply or reverse");
		}
		
		return output;
	}
	
	// Angeh2010******************************************************************************************************************
	/** READING THE NEURAL FILE */
	private void readNetworkFromFile() {
		// read the neural specifications form the file
		String line = null;
		
		BufferedReader input = null;
		try {
			input = new BufferedReader(new FileReader(NeuralFile));
		} catch (IOException ex) {
			System.out.println("\n!\nError is Opening from file.\n");
			ex.printStackTrace();
		}
		
		// read the lines
		line = getOneLine(input);
		
		numOfLayers = Integer.parseInt(line);
		
		/*
		 * Reading the sizes of the layers of the network the 0 will be the input layer
		 */
		// skip the new line
		getOneLine(input);
		
		netLayers = new int[numOfLayers + 1];
		
		for (int i = 0; i < numOfLayers + 1; i++) {
			line = getOneLine(input);
			netLayers[i] = Integer.parseInt(line);
		}
		
		getOneLine(input);
		
		layerFunctions = new String[numOfLayers];
		for (int i = 0; i < numOfLayers; i++) {
			layerFunctions[i] = getOneLine(input);
		}
		
		getOneLine(input);
		
		// Find the range of minimum and maximum
		// of the input layer
		int inputLayerSize = netLayers[0];
		inMinMax = new float[inputLayerSize][2];
		
		// set the minimum range
		for (int i = 0; i < inputLayerSize; i++) {
			line = getOneLine(input);
			inMinMax[i][0] = Float.parseFloat(line);
		}
		
		getOneLine(input);
		
		// set the maximum range
		for (int i = 0; i < inputLayerSize; i++) {
			line = getOneLine(input);
			inMinMax[i][1] = Float.parseFloat(line);
		}
		
		/*
		 * Reading the target size
		 */

		getOneLine(input);
		
		int outputLayerSize = netLayers[netLayers.length - 1];
		outMinMax = new float[outputLayerSize][2];
		
		for (int i = 0; i < outputLayerSize; i++) {
			line = getOneLine(input);
			outMinMax[i][0] = Float.parseFloat(line);
		}
		
		getOneLine(input);
		
		for (int i = 0; i < outputLayerSize; i++) {
			line = getOneLine(input);
			outMinMax[i][1] = Float.parseFloat(line);
		}
		
		/*
		 * Reading the arrays and biases each line of the array represents one line
		 */

		weightsAndBiases = new ArrayList<float[][]>();
		for (int i = 0; i < numOfLayers; i++) {
			getOneLine(input);
			// create the array reverse of course as it is in MATLAB
			float[][] readArray = readArrayFromInput(input, netLayers[i + 1], netLayers[i]);
			
			readArray = transpose(readArray);
			weightsAndBiases.add(readArray);
			
			getOneLine(input);
			// read the biases
			float[][] biases = readArrayFromInput(input, netLayers[i + 1], 1);
			biases = transpose(biases);
			weightsAndBiases.add(biases);
		}
		
		// Close the file
		try {
			input.close();
		} catch (IOException ex) {
			System.out.println("\n!\nError is Closing from file.\n");
			ex.printStackTrace();
		}
	}
	
	// Angeh2010******************************************************************************************************************
	/** reads one array from the input file */
	private float[][] readArrayFromInput(BufferedReader input, int m, int n) {
		String line = null;
		float[][] readArray = new float[m][n];
		
		for (int j = 0; j < m; j++) {
			
			// read one line the array
			line = getOneLine(input);
			// remove the space from the first of array
			line = line.trim();
			// Separate the line with spaces
			String[] arrayNums = line.split(" ");
			
			int k = 0, l = 0;
			while (k < n) {
				// remove [ or ] or ;
				if (k == 0 || k == (n - 1)) {
					arrayNums[k] = removeUnwantedValues(arrayNums[k]);
				}
				
				// sometimes the first space should be ignored
				if (arrayNums[k] != "") {
					readArray[j][l] = Float.parseFloat(arrayNums[k]);
					l++;
				}
				k++;
			}
			
		}
		
		return readArray;
	}
	
	// Angeh2010******************************************************************************************************************
	/** removes non-numerical values form a string */
	private String removeUnwantedValues(String str) {
		String output = "";
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == '0' || str.charAt(i) == '1' || str.charAt(i) == '2' || str.charAt(i) == '3' || str.charAt(i) == '4' || str.charAt(i) == '5' || str.charAt(i) == '6' || str.charAt(i) == '7' || str.charAt(i) == '8' || str.charAt(i) == '9' || str.charAt(i) == '+' || str.charAt(i) == '-' || str.charAt(i) == '.')
				output = output.concat("" + str.charAt(i));
		}
		
		return output;
	}
	
	// Angeh2010******************************************************************************************************************
	/** reads one line from input */
	private String getOneLine(BufferedReader input) {
		String line = null;
		
		try {
			line = input.readLine();
		} catch (IOException ex) {
			System.out.println("\n!\nError is Reading from file.\n");
			ex.printStackTrace();
		}
		
		return line;
	}
}
