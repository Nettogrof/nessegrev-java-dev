package ai.nettogrof.battlesnake.snakes.training;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LengthTraining extends Train implements Runnable {
	 private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
	 private static Map<String, Integer> winner = new HashMap<>();
	 private String snakes ="{\"name\":\"Neural\",\"url\":\"http://127.0.0.1:8082\"}";
	 private static  int gameSize =1;
	 public static  void startTraining() {
		 winner.put("turn",0);
		 winner.put("length",0);
		 
		
		 for (int x = 0 ; x < gameSize ; x++) {
				LengthTraining t=new LengthTraining();  
				Thread t1 =new Thread(t);  
				t1.start();  
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (x % 10  == 0 && x !=0 ) {
					showScore(x);
				}
			}
			
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			showScore();
			
		//	}

	 }
	public LengthTraining() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		try {
			String ID = post("http://127.0.0.1:3005/games",
			        "{\"width\":11,\"height\":11,\"food\":1,\"MaxTurnsToNextFoodSpawn\":0,\"snakes\":["+snakes+"]}");
			  postStart("http://localhost:3005/games/" + ID+ "/start");
			  boolean running = true;
			  String status ="";
			  while (running) {
				  Thread.sleep(3000);
				  status = getStatus("http://localhost:3005/games/" + ID);
				  running =  !status.contains("\"Status\":\"complete\"");
			  }
			  
			  JsonNode stats = JSON_MAPPER.readTree(status);
			  int turn = stats.get("LastFrame").get("Turn").asInt();
				 winner.put("turn",winner.get("turn") + turn );
			  stats.get("LastFrame").withArray("Snakes").forEach(s -> {
				 
					 
					  int length = s.get("Body").size();
						 winner.put("length",winner.get("length") + length );
				 
			  });
			  
			 
	          	
	          
			  
			 // System.out.println(status);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void showScore() {
		 System.out.println("----------------");
         
		 System.out.println("Result:");
         winner.forEach((k, v) -> {
             System.out.println(k + " : "+ v/gameSize);
         });
         
         System.out.println("----------------");
	}
	
	public static void showScore(int x) {
		 System.out.println("----------------");
        
		 System.out.println("Result:");
        winner.forEach((k, v) -> {
            System.out.println(k + " : "+ v/x);
        });
        
        System.out.println("----------------");
	}
	
	public static void main(String[] args) {
		startTraining();
	}

}
