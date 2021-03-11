package ai.nettogrof.battlesnake.snakes;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;

import ai.nettogrof.battlesnake.snakes.neural.GameHistory;
import ai.nettogrof.battlesnake.snakes.neural.NN;
import ai.nettogrof.battlesnake.snakes.neural.NNMove;

public class NNSnake extends SnakeAI {
	private NN n;
	//private NeuralNetwork<ResilientPropagation> nn;
	private int width;
	private int heigth;
	private double board[][];
	private double food[][];
	private double head[][];
	private GameHistory hist = new GameHistory();


	public NNSnake() {
		// TODO Auto-generated constructor stub
	}

	public NNSnake(Logger l, String gi) {
		super(l, gi);
		// TODO Auto-generated constructor stub
	}

	public Map<String, String> end(JsonNode endRequest) {
	
		Map<String, String> response = new HashMap<>();

		try{
			LOG.info("CARPARP Winner is : {}", endRequest.get("board").get("snakes").get(0).get("name").asText());
			
			if (endRequest.get("board").get("snakes").get(0).get("name").asText().equals(endRequest.get("you").get("name").asText())) {
				hist.winGame(50);
			}else {
				hist.lostGame(50);
			}
		}catch(NullPointerException e) {
			LOG.info("DRAW");
			hist.lostGame(25);
		}
		
		try {
			hist.saveDataset(width,heigth);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public Map<String, String> move(JsonNode moveRequest) {

		Map<String, String> response = new HashMap<>();
        Map<String, Double> possiblemove = new HashMap<>();
        possiblemove.put("up",0.5);
        possiblemove.put("down",0.5);
        possiblemove.put("left",0.5);
        possiblemove.put("right",0.5);
       
        
        for (int x =0 ; x < moveRequest.get("board").get("width").asInt() ; x++) {
        	for (int y =0 ; y < moveRequest.get("board").get("height").asInt() ; y++) {
        		board[x][y] =0;
        		food[x][y] =0;
        		head[x][y] = 0;
        	}
        }
        String name = moveRequest.get("you").get("name").asText();
        int mysnakelength = moveRequest.get("you").get("body").size();
        int turn =  moveRequest.get("turn").asInt();
        moveRequest.get("board").withArray("snakes").forEach(s ->{
        	int enemylength = s.get("body").size();
        	
        	if (!s.get("name").asText().equals(name) && enemylength >= mysnakelength) {
        		board[s.get("body").get(0).get("x").asInt()][s.get("body").get(0).get("y").asInt()] = 1;
        	}else if (!s.get("name").asText().equals(name)){
        		food[s.get("body").get(0).get("x").asInt()][s.get("body").get(0).get("y").asInt()] = 0.5;
        	}
        	
        	
        	s.withArray("body").forEach(c->{
        		
        		board[c.get("x").asInt()][c.get("y").asInt()] = board[c.get("x").asInt()][c.get("y").asInt()] == 0 ? 0.5 : board[c.get("x").asInt()][c.get("y").asInt()];
        		
        	});
        	
        	if (s.get("health").asInt() < 100 && turn > 3) {
        		board[s.get("body").get(enemylength-1).get("x").asInt()][s.get("body").get(enemylength-1).get("y").asInt()] = 0;
        	}
        	
        	
        	
        });
        
        int snakex = moveRequest.get("you").withArray("body").get(0).get("x").asInt();
        int snakey = moveRequest.get("you").withArray("body").get(0).get("y").asInt();
        head[snakex][snakey] = 1;
        
        moveRequest.get("board").withArray("food").forEach(f ->{
        	
        	
        	food[f.get("x").asInt()][f.get("y").asInt()]=1;
        	
        });	
        
        double[] result = n.brain(board,food,head);
       // System.out.println(result[0] + " " +result[1] + " " +result[2] + " " +result[3]);
        if (snakey == 0  || board[snakex][snakey-1] !=0) {
        	possiblemove.put("up",0.0);	
        	result[0]=0.0;
        }else {
        	possiblemove.put("up",result[0]);	
        }
        if (snakey == heigth - 1  || board[snakex][snakey+1] !=0) {
        	possiblemove.put("down",0.0);	
        	result[1]=0.0;
        }else {
        	possiblemove.put("down",result[1]);	
        }
        
        if (snakex == 0   || board[snakex-1][snakey] !=0) {
        	possiblemove.put("left",0.0);	
        	result[2]=0.0;
        }else {
        	possiblemove.put("left",result[2]);	
        }
        if (snakex == width - 1  || board[snakex+1][snakey] !=0) {
        	possiblemove.put("right",0.0);
        	result[3]=0.0;
        }else {
        	possiblemove.put("right",result[3]);	
        }
        
      //  System.out.println(snakex +" == "+ (width -1 ));
        
        String bm = bestMove(possiblemove);
        NNMove m =  new NNMove(transforme(board,food,head),result,0);
        switch(bm) {
        case "up" : m = new NNMove(transforme(board,food,head),result,0);break;
        case "down" : m = new NNMove(transforme(board,food,head),result,1);break;
        case "left" : m = new NNMove(transforme(board,food,head),result,2);break;
        case "right" : m = new NNMove(transforme(board,food,head),result,3);break;
        }
        
        hist.add(m);
        
        response.put("move", bm);
		return response;
	}
	
	private String bestMove(Map<String, Double> possiblemove) {
		double value = -1;
		String res="";
		if (possiblemove.get("up") > value) {
	    	   value = possiblemove.get("up");
	    	   res = "up";
	       } 
			
		 if (possiblemove.get("right") > value) {
	    	   value = possiblemove.get("right");
	    	   res = "right";
	       }
	       if (possiblemove.get("down") > value) {
	    	   value = possiblemove.get("down");
	    	   res = "down";
	       }
	    	
	       if (possiblemove.get("left") > value) {
	    	   value = possiblemove.get("left");
	    	   res = "left";
	       }
	      System.out.println("up"+possiblemove.get("up"));
	      System.out.println("down"+possiblemove.get("down"));
	      System.out.println("left"+possiblemove.get("left"));
	      System.out.println("right"+possiblemove.get("right"));
	     return res;
	}

	@Override
	public Map<String, String> start(JsonNode startRequest) {
		Map<String, String> response = new HashMap<>();
		response.put("color", "#00FF00");
		response.put("headType", "sand-worm");
		response.put("tailType","sharp");
		width = startRequest.get("board").get("width").asInt();
		heigth= startRequest.get("board").get("height").asInt();
		board = new double [width][heigth];
		food = new double [width][heigth];
		head = new double [width][heigth];
		n = new NN(width);
		
		hist.setName(startRequest.get("you").get("name").asText());
	//	System.out.println("width "+ width );
		return response;
	}
	
	private double[] transforme(double[][] board, double[][] food, double[][] head) {
		//System.out.println(nn.getInputsCount());
		double maps[] = new double[width*heigth*3];
		int i =0;
		
		for (int x =0 ; x < width ; x++) {
			for(int y =0 ; y <heigth; y++) {
				maps[i] =board[x][y];
				i++;
			}
		}
		
		for (int x =0 ; x < width ; x++) {
			for(int y =0 ; y <heigth; y++) {
				maps[i] =food[x][y];
				i++;
			}
		}
		
		for (int x =0 ; x < width ; x++) {
			for(int y =0 ; y <heigth; y++) {
				maps[i] =head[x][y];
				i++;
			}
		}
		return maps;
	}
	public static void main(String[] args) {
		/*NN n = new NN();
		//String[] out = { "up","down","left","right"};
		NeuralNetwork nn = n.getNN();
		nn = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, 11*11*3,128, 4);
		 nn.setLearningRule(new BackPropagation());*/
		/*int is = 11*11*3;
		Layer in = new Layer(is);
		
		
		for(int x =0 ; x <is ; x ++) {
			in.addNeuron(new Neuron());
		}
		nn.addLayer(in);
		Layer hidden = new Layer(128);
		for (int x =0 ; x < 128; x++) {
			hidden.addNeuron(new Neuron());
		}
		nn.addLayer(hidden);
		
		Layer out = new Layer(4);
		for (int x =0 ; x < 4; x++) {
			Neuron beu = new Neuron();
			beu.setOutput(0.5);
			out.addNeuron(beu);
		}
		nn.addLayer(out);
		*/
		/*ConnectionFactory.fullConnect(nn.getLayerAt(0), nn.getLayerAt(1));
        ConnectionFactory.fullConnect(nn.getLayerAt(1), nn.getLayerAt(2));*/

       /* nn.setInputNeurons(nn.getLayerAt(0).getNeurons());
        nn.setOutputNeurons( nn.getLayerAt(2).getNeurons());
       */
	/*	nn.randomizeWeights();
		System.out.println("input:"+ nn.getInputsCount());
		n.setSize(11);
		n.save();*/

	}

	@Override
	protected void setFileConfig() {
		// TODO Auto-generated method stub
		
	}

}
