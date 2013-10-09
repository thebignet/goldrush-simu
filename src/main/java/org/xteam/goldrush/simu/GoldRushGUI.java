package org.xteam.goldrush.simu;

import javax.swing.JFrame;

public class GoldRushGUI extends JFrame {

	private static final long serialVersionUID = 8764102704151014674L;

	public GoldRushGUI(GoldRushMap map) {
		setTitle("GoldRush");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().add(new GoldRushPanel(map));
		pack();
	}

}
