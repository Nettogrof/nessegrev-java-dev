package ai.nettogrof.battlesnake.snakes.training;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CompetitiveTrain extends Train implements Runnable {
	private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
	private static Map<String, Integer> winner = new HashMap<>();
	private String snakes = "{\"name\":\"Gamma\",\"url\":\"http://127.0.0.1:8084\"},"
			
					
					+ "{\"name\":\"Old\",\"url\":\"http://127.0.0.1:8080\"}";

	public static void startTraining() {

	
		for (int x = 0; x < 10; x++) {
			CompetitiveTrain t = new CompetitiveTrain();
			t.run();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
			
				showScore();
			
		}

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
		showScore();

		winner = new HashMap<>();
	

	}

	/*
	 * private static void genEvolution() { int bestscore =winner.get("flood");
	 * String bestSnake="flood"; if (winner.get("GFF") > bestscore) { bestSnake =
	 * "GFF"; bestscore=winner.get("GFF"); } if (winner.get("GFF2") > bestscore) {
	 * bestSnake = "GFF2"; bestscore=winner.get("GFF2"); } if (winner.get("GFF3") >
	 * bestscore) { bestSnake = "GFF3"; bestscore=winner.get("GFF3"); }
	 * 
	 * if (!bestSnake.equals("flood")) { File source = new
	 * File(bestSnake+".properties"); // File dest = new File("config.properties");
	 * try { Files.copy(source.toPath(),new
	 * File("config.properties").toPath(),StandardCopyOption.REPLACE_EXISTING);
	 * Files.copy(source.toPath(),new
	 * File("GFF.properties").toPath(),StandardCopyOption.REPLACE_EXISTING);
	 * Files.copy(source.toPath(),new
	 * File("GFF2.properties").toPath(),StandardCopyOption.REPLACE_EXISTING);
	 * Files.copy(source.toPath(),new
	 * File("GFF3.properties").toPath(),StandardCopyOption.REPLACE_EXISTING);
	 * 
	 * } catch (IOException e) {
	 * 
	 * e.printStackTrace(); }
	 * 
	 * }
	 * 
	 * 
	 * 
	 * }
	 */

	/*
	 * private static void randomize(String name) { int floodEnemyBigger=0; int
	 * floodEnemySmaller=0; int foodValue=0; int floodEnemyGap=0; try (InputStream
	 * input = new FileInputStream(name+".properties")) {
	 * 
	 * Properties prop = new Properties();
	 * 
	 * // load a properties file prop.load(input);
	 * 
	 * // get the property value and print it out floodEnemyBigger
	 * =Integer.parseInt(prop.getProperty("floodEnemyBigger")); floodEnemySmaller
	 * =Integer.parseInt(prop.getProperty("floodEnemySmaller")); foodValue
	 * =Integer.parseInt(prop.getProperty("foodValue")); floodEnemyGap
	 * =Integer.parseInt(prop.getProperty("floodEnemyGap"));
	 * 
	 * 
	 * } catch (IOException ex) { ex.printStackTrace(); } Random r = new Random();
	 * int test = floodEnemyBigger/2;
	 * 
	 * floodEnemyBigger = floodEnemyBigger +
	 * r.nextInt(Math.abs(floodEnemyBigger/2))-(floodEnemyBigger/4);
	 * floodEnemySmaller = floodEnemySmaller +
	 * r.nextInt(floodEnemySmaller/2)-(floodEnemySmaller/4); foodValue = foodValue +
	 * r.nextInt(3)-1; floodEnemyGap = floodEnemyGap +
	 * r.nextInt(floodEnemyGap/4)-(floodEnemyGap/4);
	 * 
	 * try (OutputStream output = new FileOutputStream(name+".properties")) {
	 * 
	 * Properties prop = new Properties();
	 * 
	 * // set the properties value prop.setProperty("floodEnemyBigger",
	 * ""+floodEnemyBigger); prop.setProperty("floodEnemySmaller", ""
	 * +floodEnemySmaller); prop.setProperty("foodValue", "" +foodValue);
	 * prop.setProperty("floodEnemyGap", "" +floodEnemyGap);
	 * 
	 * // save properties to project root folder prop.store(output, null);
	 * 
	 * System.out.println(prop);
	 * 
	 * } catch (IOException io) { io.printStackTrace(); }
	 * 
	 * }
	 */

	@Override
	public void run() {
		try {
			String ID = post("http://127.0.0.1:3005/games",
					"{\"width\":11,\"height\":11,\"food\":2,\"MaxTurnsToNextFoodSpawn\":5,\"snakes\":[" + snakes
							+ "]}");
			postStart("http://localhost:3005/games/" + ID + "/start");
			boolean running = true;
			String status = "";
			while (running) {
				Thread.sleep(5000);
				status = getStatus("http://localhost:3005/games/" + ID);
				running = !status.contains("\"Status\":\"complete\"");
			}
			JsonNode stats = JSON_MAPPER.readTree(status);

			stats.get("LastFrame").withArray("Snakes").forEach(s -> {
				if (s.get("Death").isNull()) {
					String winName = s.get("Name").asText();
					if (winner.containsKey(winName)) {
						winner.put(winName, winner.get(winName) + 1);
					} else {
						winner.put(winName, 1);
					}
				}
			});

		//	System.out.println(stats.get("LastFrame").get("Turn").asText());

			// System.out.println(status);
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	public static void showScore() {
		System.out.println("----------------");

		System.out.println("Result:");
		winner.forEach((k, v) -> {
			System.out.println(k + " : " + v);
		});

		System.out.println("----------------");
	}

	public static void main(String[] args) {

		startTraining();
	}
}
