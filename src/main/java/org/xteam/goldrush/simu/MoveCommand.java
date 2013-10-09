package org.xteam.goldrush.simu;

public class MoveCommand extends Command {
	
	private Direction direction;

	public MoveCommand(String code, Direction direction) {
		super(code);
		this.direction = direction;
	}

	@Override
	public void execute(GoldRushMap map, Player player) {
		map.move(player, direction);
	}

}
