package ai.nettogrof.battlesnake.snakes;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.chen0040.rl.learning.qlearn.QLearner;

public class RL extends SnakeAI {
	QLearner learner ;
	int[][] board ;
    
    int heigth;
    int width; 
   
    String modelPath = "model.ai";
	public RL() {
		// TODO Auto-generated constructor stub
	}

	public RL(Logger l, String gi) {
		super(l, gi);
		
		learner = QLearner.fromJson( readModel(modelPath));
	}

	@Override
	public Map<String, String> end(JsonNode endRequest) {
		Map<String, String> response = new HashMap<>();
        
        try{
        	LOG.info("Winner is : {}", endRequest.get("board").get("snakes").get(0).get("name").asText());
        }catch(NullPointerException e) {
        	LOG.info("DRAW");
        }
        try {
			writeModel(modelPath,learner.toJson());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return response;
	}

	@Override
	public Map<String, String> move(JsonNode moveRequest) {
		
		return null;
	}

	@Override
	public Map<String, String> start(JsonNode startRequest) {
		 Map<String, String> response = new HashMap<>();
         response.put("color", "#004400");
         response.put("headType", "sand-worm");
         response.put("tailType","sharp");
         width = startRequest.get("board").get("width").asInt();
         heigth= startRequest.get("board").get("height").asInt();
         board = new int [width][heigth];
        
         return response;
	}

	
	private  String readModel(String filePath) 
	{
	    StringBuilder contentBuilder = new StringBuilder();
	    try (Stream<String> stream = Files.lines( Paths.get(filePath), StandardCharsets.UTF_8)) 
	    {
	        stream.forEach(s -> contentBuilder.append(s).append("\n"));
	    }
	    catch (IOException e) 
	    {
	        e.printStackTrace();
	    }
	    return contentBuilder.toString();
	}
	
	public static void writeModel(String filePath , String fileContent) throws IOException 
	{
	   
	     
	    BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
	    writer.write(fileContent);
	    writer.close();
	}
	public static void main(String[] args) {
		QLearner l = new QLearner(1000,4);
		 try {
			writeModel("model.ai",l.toJson());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub

	}

	@Override
	protected void setFileConfig() {
		// TODO Auto-generated method stub
		
	}

}
