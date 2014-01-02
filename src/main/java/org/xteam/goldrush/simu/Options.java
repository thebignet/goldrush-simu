package org.xteam.goldrush.simu;

public class Options {
	
	public static final int DEFAULT_DURATION = 250;
	
	private static final int MAX_NUMBER_OF_ROUND = 1000;
	
	private static final StartMode DEFAULT_START_MODE = StartMode.Run;

	public int duration = DEFAULT_DURATION;
	
	public int maxRound = MAX_NUMBER_OF_ROUND;
	
	public StartMode startMode = DEFAULT_START_MODE;

	public int width;

	public int height;
}
