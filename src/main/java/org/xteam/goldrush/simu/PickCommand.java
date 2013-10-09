package org.xteam.goldrush.simu;

public class PickCommand extends Command {

	public PickCommand() {
		super("PICK");
	}

	@Override
	public void execute(GoldRushMap map, Player player) {
		map.pickGold(player);
	}

}
