package org.xteam.goldrush.simu;

public class DropCommand extends Command {

	public DropCommand() {
		super("DROP");
	}

	@Override
	public void execute(GoldRushMap map, Player player) {
		map.dropGold(player);
	}

}
