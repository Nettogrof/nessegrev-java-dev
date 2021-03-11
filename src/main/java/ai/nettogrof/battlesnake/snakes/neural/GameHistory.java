package ai.nettogrof.battlesnake.snakes.neural;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class GameHistory {
	private ArrayList<NNMove> moves = new ArrayList<NNMove>();
	private String name = "";

	public GameHistory() {
		
	}

	public void add(NNMove m) {
		moves.add(m);

	}

	public void lostGame(int nbMove) {

		while (nbMove > 0) {
			int i = moves.size() - nbMove;
			if (i > 0) {
				moves.get(i).setFinalValue(nbMove / 100);
			}
			nbMove--;
		}
		
		if (moves.size() > 150) {
			for(int i =0 ; i <moves.size()/2 ; i++) {
				moves.get(i).setFinalValue(1);
			}
		}
	}

	public void winGame(int nbMove) {

		for (int i = 0; i < moves.size(); i++) {
			moves.get(i).setFinalValue(1);
		}

	}

	public void setName(String s) {
		name = s;
	}

	public void saveDataset(int width, int heigth) throws IOException {
		System.out.println("Saving dataset");
		File file = new File(name+"DataSet" + width + "x" + heigth);
		FileWriter fr = new FileWriter(file, true);
		for (int i = 0; i < moves.size(); i++) {
			fr.write(moves.get(i).toString() + "\n");
		}
		fr.close();
	}

}
