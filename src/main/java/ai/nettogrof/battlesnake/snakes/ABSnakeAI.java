package ai.nettogrof.battlesnake.snakes;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import org.slf4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class ABSnakeAI extends SnakeAI {
	protected int width;
	protected int heigth;
	
	public int timeout = 300;
	protected int apiversion =0;
	protected boolean multiThread=false;
	protected String losing ="I have a bad feeling about this";
	protected String winning = "I'm your father";
	protected int minusbuffer = 250;
	protected long nodeTotalCount =0;
	protected long timeTotal =0 ;
	protected static final ObjectMapper JSON_MAPPER = new ObjectMapper();
	
	public ABSnakeAI() {
		
	}

	public ABSnakeAI(Logger l, String gi) {
		super(l, gi);
		setFileConfig();
		try (InputStream input = new FileInputStream(getFileConfig())) {

            Properties prop = new Properties();

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            apiversion= Integer.parseInt(prop.getProperty("apiversion"));
            minusbuffer= Integer.parseInt(prop.getProperty("minusbuffer"));
            multiThread = Boolean.parseBoolean(prop.getProperty("multiThread"));
            
            String[] lose = {"I have a bad feeling about this","help me obiwan you're my only hope", "Nooooo!!","Sorry master, I failed"};
            String[] win = {"You gonna die", "I'm your father","All your base belong to me","42 is the answer of life","Astalavista baby!"};
            Random r = new Random();
            losing = lose[r.nextInt(lose.length)];
            winning = win[r.nextInt(win.length)];
           
            

        } catch (IOException ex) {
            ex.printStackTrace();
        }
	}
	
	

	protected abstract String getFileConfig();

	@Override
	public Map<String, String> end(JsonNode endRequest) {
		Map<String, String> response = new HashMap<>();

		try {
			LOG.info("Winner is : {}", endRequest.get("board").get("snakes").get(0).get("name").asText());
			
		} catch (NullPointerException e) {
			LOG.info("DRAW");

		}
				
		LOG.info("Average node/s : "+(nodeTotalCount/timeTotal*1000));

		return response;
	}

}
