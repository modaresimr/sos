package sos.base.worldGraph;

import java.util.Arrays;

import sos.tools.GraphWeightInterface;

/**
 * Created by IntelliJ IDEA.
 * User: Aramik
 * Date: Jul 27, 2010
 * Time: 11:40:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class GraphWeight implements GraphWeightInterface{
    private short len;
    private int[] weights;

    public GraphWeight(short size){
        this.len=size;
        weights=new int[len];
        setAllWeightsTo(0);
    }
    public void setAllWeightsTo(int weight){
        Arrays.fill(weights,weight);
    }
    public void setWeight(short index,int weight){
        weights[index]=weight;
    }
    @Override
	public int getWeight(short index){
        return weights[index];
    }
    @Override
	public short getSize(){
        return len;
    }
}
