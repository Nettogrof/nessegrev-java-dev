package ai.nettogrof.battlesnake.snakes.neural;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.nnet.learning.ResilientPropagation;


public class NN {

	NeuralNetwork<ResilientPropagation> nn;
	private int size;
	public NN() {
		nn=new NeuralNetwork<ResilientPropagation>();
	}

	@SuppressWarnings("unchecked")
	public NN(int s) {
		size = s;
		nn= NeuralNetwork.createFromFile("N3N"+size+"x"+size);
		
		
	}
	
	public void save() {
		nn.save("N3N"+size+"x"+size);
	}
	
	public NeuralNetwork<?> getNN() {
		return nn;
	}
	
	public void setNN(NeuralNetwork<ResilientPropagation> net) {
		 nn = net;
	}
	public void setSize(int s) {
		size = s;
	}
	
	public double[] input(double[] inputs){
		nn.setInput(inputs);
		nn.calculate();
		return nn.getOutput();
	}

	public double[] brain(double[][] board, double[][] food, double[][] head) {
		
		double maps[] = new double[size*size*3];
		int i =0;
		
		for (int x =0 ; x < size ; x++) {
			for(int y =0 ; y <size; y++) {
				maps[i] =board[x][y];
				i++;
			}
		}
		
		for (int x =0 ; x < size ; x++) {
			for(int y =0 ; y <size; y++) {
				maps[i] =food[x][y];
				i++;
			}
		}
		
		for (int x =0 ; x < size ; x++) {
			for(int y =0 ; y <size; y++) {
				maps[i] =head[x][y];
				i++;
			}
		}
		
		return input(maps);
	}
}
