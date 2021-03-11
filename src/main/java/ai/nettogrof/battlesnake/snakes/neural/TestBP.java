package ai.nettogrof.battlesnake.snakes.neural;

import org.neuroph.core.data.DataSet;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.util.TransferFunctionType;

public class TestBP {

	public static void main(String[] args) {

		// create training set (logical XOR function)
		        DataSet trainingSet = new DataSet(2, 1);
		        double[][] input = {{0,0},{0,1},{1,0},{1,1}};
		        
		        double[]  zero = {0};
		        double[]  one = {1};
		        
		        
		        trainingSet.add(input[0],zero);
		        trainingSet.add(input[1],one);
		        trainingSet.add(input[2],one);
		        trainingSet.add(input[3],zero);
		        trainingSet.add(input[0],zero);
		        trainingSet.add(input[1],one);
		        trainingSet.add(input[2],one);
		        trainingSet.add(input[3],zero);
		        trainingSet.add(input[0],zero);
		        trainingSet.add(input[1],one);
		        trainingSet.add(input[2],one);
		        trainingSet.add(input[3],zero);
		        trainingSet.add(input[0],zero);
		        trainingSet.add(input[1],one);
		        trainingSet.add(input[2],one);
		        trainingSet.add(input[3],zero);
		      // trainingSet.add(input, output);
		        
		        
		// create multi layer perceptron
		        MultiLayerPerceptron ml = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, 2, 3, 1);
		        ml.setInput(input[1]);
		        ml.calculate();
		        double[] res = ml.getOutput();
		        System.out.println("1 " + res[0]);
		        
		        ml.setLearningRule(new BackPropagation());
		// learn the training set
		        ml.learn(trainingSet);
		       
		// test perceptron
		        System.out.println("Testing trained neural network");
		       // testNeuralNetwork(myMlPerceptron, trainingSet);

		// save trained neural network
		      //  ml.save("myMlPerceptron.nnet");

		        ml.learn(trainingSet);
		        ml.setInput(input[1]);
		        ml.calculate();
		        double[] rest = ml.getOutput();
		        System.out.println("2 " + rest[0]);
		        
		        ml.learn(trainingSet);
		        ml.setInput(input[1]);
		        ml.calculate();
		        rest = ml.getOutput();
		        System.out.println("3 " + rest[0]);
		        
		        ml.learn(trainingSet);
		        ml.setInput(input[1]);
		        ml.calculate();
		        rest = ml.getOutput();
		        System.out.println("4 " + rest[0]);
		        
		// load saved neural network
		   //     NeuralNetwork loadedMlPerceptron = NeuralNetwork.createFromFile("myMlPerceptron.nnet");

		// test loaded neural network
		        System.out.println("Testing loaded neural network");
		     //   testNeuralNetwork(loadedMlPerceptron, trainingSet);

		    }
}
