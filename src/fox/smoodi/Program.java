package fox.smoodi;
import java.awt.EventQueue;

import fox.smoodi.engine.SFoxEngine;
import fox.smoodi.game.Game;
import fox.smoodi.launcher.startMenu;

public class Program {

	public static void main(String[] args) {
		
		//We create a new runnable to start our start menu.
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					startMenu frame = new startMenu();
					frame.setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
				}
			}
		});
	}
}
