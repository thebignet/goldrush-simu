package org.xteam.goldrush.simu;

public class ShootCommand extends Command {

	public ShootCommand() {
		super("SHOOT");
	}

	@Override
	public void execute(GoldRushMap map, Player player) {
		map.shoot(player);
	}

}
