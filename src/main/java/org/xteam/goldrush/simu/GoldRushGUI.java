package org.xteam.goldrush.simu;

import javax.swing.JFrame;

public class GoldRushGUI extends JFrame {

	private static final long serialVersionUID = 8764102704151014674L;

	public GoldRushGUI(GoldRushMap map, boolean isEditMode = false) {
		setTitle("GoldRush");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		if(isEditMode) {
			getContentPane().add(new MapEditorPanel(map));
		} else {
			getContentPane().add(new MapViewPanel(map));
		}

		pack();
	}

}
