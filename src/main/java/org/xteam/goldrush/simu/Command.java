package org.xteam.goldrush.simu;

import java.util.HashMap;
import java.util.Map;

public abstract class Command {
	
	public static final Command NORTH = new MoveCommand("NORTH", Direction.NORTH);
	public static final Command SOUTH = new MoveCommand("SOUTH", Direction.SOUTH);
	public static final Command EAST = new MoveCommand("EAST", Direction.EAST);
	public static final Command WEST = new MoveCommand("WEST", Direction.WEST);
	
	public static final Command SHOOT = new ShootCommand();
	public static final Command PICK = new PickCommand();
	public static final Command DROP = new DropCommand();
	
	private static Map<String, Command> commands = new HashMap<String, Command>();
	
	static {
		commands.put(NORTH.getCode(), NORTH);
		commands.put(SOUTH.getCode(), SOUTH);
		commands.put(EAST.getCode(), EAST);
		commands.put(WEST.getCode(), WEST);
		commands.put(SHOOT.getCode(), SHOOT);
		commands.put(PICK.getCode(), PICK);
		commands.put(DROP.getCode(), DROP);
	}
	
	private String code;

	public Command(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}

	public static Command getFrom(String code) {
		return commands.get(code);
	}

	public abstract void execute(GoldRushMap map, Player player);

}
