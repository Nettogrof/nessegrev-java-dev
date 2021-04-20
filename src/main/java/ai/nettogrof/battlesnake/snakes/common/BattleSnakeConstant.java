package ai.nettogrof.battlesnake.snakes.common;

public final class BattleSnakeConstant {
	
	public final static int HEALTH_LOST_HAZARD = -15;
	public final static float MAX_SCORE=99_999f;
	public final static float INVALID_SCORE= -500f;
	public final static float STOP_EXPAND_LIMIT = 50f;
	
	public final static String[] LOSE_SHOUT = {"I have a bad feeling about this","help me obiwan you're my only hope", "Nooooo!!","Sorry master, I failed"};
	public final static String[] WIN_SHOUT = {"You gonna die", "I'm your father","All your base belong to me","42 is the answer of life","Astalavista baby!"};
	public final static float MCTS_BIAS = 0.7f;
}
