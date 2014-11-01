package sos.tools;

//Angeh2010*************************************************************************************************************
public class NNReclain {
	
	public boolean getValidResult(float input[][]) {
		if (input[0][0] < 0.5)
			return false;
		else
			return true;
	}
	
	public boolean getValidResult(double in) {
		if (in < 0.5)
			return false;
		else
			return true;
	}
	
	public boolean getNeighborValidResult(float input[][]) {
		if (input[0][0] < 0.40)
			return false;
		else
			return true;
	}
}
