package ai.nettogrof.battlesnake.snakes;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;

public class BasicSnake extends SnakeAI {
	
     static int maxd = 99;
     static int foodx = 99;
     static int foody = 99;
    
    
     
     public BasicSnake() {
    	 
     }
     
     public BasicSnake(Logger l, String gi) {
    	 LOG = l;
    	 gameId = gi;
     }
     
     public Map<String, String> start(JsonNode startRequest) {
         
         Map<String, String> response = new HashMap<>();
         response.put("color", "#ff00ff");
         response.put("headType", "sand-worm");
         response.put("tailType","sharp");
         return response;
     }
     
     public Map<String, String> move(JsonNode moveRequest){
    	 Map<String, String> response = new HashMap<>();
         Map<String, Integer> possiblemove = new HashMap<>();
         possiblemove.put("up",0);
         possiblemove.put("down",0);
         possiblemove.put("left",0);
         possiblemove.put("right",0);
         int bs = moveRequest.get("board").get("height").asInt();
         int[][] board = new int [moveRequest.get("board").get("width").asInt()][moveRequest.get("board").get("height").asInt()];
         for (int x =0 ; x < bs ; x++) {
         	for (int y =0 ; y < bs ; y++) {
         		board[x][y] =0;
         	}
         }
         moveRequest.get("board").withArray("snakes").forEach(s ->{
         	s.withArray("body").forEach(c->{
         		board[c.get("x").asInt()][c.get("y").asInt()] = -99;
         		
         	});
         	
         	
         });
         
         int snakex = moveRequest.get("you").withArray("body").get(0).get("x").asInt();
         int snakey = moveRequest.get("you").withArray("body").get(0).get("y").asInt();
        
         maxd=99;
         
         
         
         moveRequest.get("board").withArray("food").forEach(f ->{
         	if (Math.abs(f.get("x").asInt() - snakex) + Math.abs(f.get("y").asInt() - snakey)  <  maxd){
         		maxd = Math.abs(f.get("x").asInt() - snakex) + Math.abs(f.get("y").asInt() - snakey);
         		foodx = f.get("x").asInt();
         		foody  = f.get("y").asInt();
         	}
         	
         });	
         		
         
         possiblemove.put("up",snakey-foody);
         possiblemove.put("down",foody-snakey);
         possiblemove.put("left",snakex-foodx);
         possiblemove.put("right",foodx-snakex);
         
         
         if (snakey == 0) {
         	 possiblemove.put("up",-90);
         }else {
         	possiblemove.put("up", possiblemove.get("up") + board[snakex][snakey-1]);
         }
         
         if (snakey == bs-1) {
        	 possiblemove.put("down",-90);
        }else {
        	possiblemove.put("down", possiblemove.get("down") + board[snakex][snakey+1]);
        }
        
         
         if (snakex == 0) {
        	 possiblemove.put("left",-90);
        }else {
        	possiblemove.put("left", possiblemove.get("left") + board[snakex-1][snakey]);
        }
        
        if (snakex == bs-1) {
       	 possiblemove.put("right",-90);
       }else {
       	possiblemove.put("right", possiblemove.get("right") + board[snakex+1][snakey]);
       }
         
        
        String res = "up";
        int value =  possiblemove.get("up");
        
        if (possiblemove.get("down") > value) {
     	   value = possiblemove.get("down");
     	   res = "down";
        }
     	
        if (possiblemove.get("left") > value) {
     	   value = possiblemove.get("left");
     	   res = "left";
        }
        if (possiblemove.get("right") > value) {
     	   value = possiblemove.get("right");
     	   res = "right";
        }
         
        if (res.equals("up")) {
			res = "down";
		}else if (res.equals("down")) {
			res = "up";
		}
	
         
           response.put("move", res);
           return response;
     }
     
     public Map<String, String> end(JsonNode endRequest) {
 		Map<String, String> response = new HashMap<>();
          
          try{
          	LOG.info("Winner is : {}", endRequest.get("board").get("snakes").get(0).get("name").asText());
          }catch(NullPointerException e) {
          	LOG.info("DRAW");
          }
          return response;
 	}
     
     public String getId() {
    	 return gameId;
     }

	public static Map<String, String> getInfo() {
		
		fileConfig = "Basic.properties";
		Map<String, String> response = new HashMap<>();
		try (InputStream input = new FileInputStream(fileConfig)) {

            Properties prop = new Properties();

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            
           
        	response.put("apiversion", prop.getProperty("apiversion"));
    		response.put("head",  prop.getProperty("headType"));
    		response.put("tail",  prop.getProperty("tailType"));
    		response.put("color",  prop.getProperty("color"));
    		response.put("author", "nettogrof");

            

        } catch (IOException ex) {
            ex.printStackTrace();
        }

	
		return response;
	}

	@Override
	protected void setFileConfig() {
		// TODO Auto-generated method stub
		
	}

}
