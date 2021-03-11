package ai.nettogrof.battlesnake.snakes.neural;

public class NNMove {
	private double input[];
	private double output[] = {0.5,0.5,0.5,0.5};
	private double finalValue;
	private int choice;
	
	public NNMove(double[] i , double[] o , int c ) {
		finalValue=0.5;
		input=i;
		output = o ;
		choice = c;
	}

	public double[] getInput() {
		return input;
	}

	public void setInput(double[] input) {
		this.input = input;
	}

	public double[] getOutput() {
		return output;
	}

	public void setOutput(double[] output) {
		this.output = output;
	}

	public double getFinalValue() {
		return finalValue;
	}

	public void setFinalValue(double finalValue) {
		this.finalValue = finalValue;
		output[choice]=finalValue;
		
	}

	public int getChoice() {
		return choice;
	}

	public void setChoice(int choice) {
		this.choice = choice;
	}
	
	public String toString() {
		String out = "";
		for(int i = 0 ;i <input.length; i++) {
			out +=input[i] +",";
		}
		for(int i = 0 ;i <output.length-1; i++) {
			out +=output[i] +",";
		}
		
		out+=""+output[3];
		return out;
	}

}
