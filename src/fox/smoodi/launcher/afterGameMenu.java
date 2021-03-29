package fox.smoodi.launcher;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import fox.smoodi.engine.SFoxEngine;
import fox.smoodi.game.Game;

import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Toolkit;

public class afterGameMenu extends JFrame {

	private JPanel contentPane;
	public JLabel label;

	/**
	 * Create the frame.
	 */
	public afterGameMenu() {
		setTitle("Zombie Arena by SmoodiFox");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 460, 293);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		//We load our game over image.
		BufferedImage img = null;
		try {
		    img = ImageIO.read(new File("./resources/sprites/gameover.png"));
		} catch (IOException e) {
		    e.printStackTrace();
		}
		
		//And assign it to a new label.
		label = new JLabel();
		label.setLocation(-30, 0);
		Image dimg = img.getScaledInstance(460, 160, Image.SCALE_SMOOTH);
		label.setIcon(new ImageIcon(dimg));
		contentPane.add(label, BorderLayout.NORTH);
		
		//We add a split pane and assign our stuff to it.
		JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		contentPane.add(splitPane, BorderLayout.SOUTH);
		
		JPanel panel = new JPanel();
		splitPane.setLeftComponent(panel);
		
		label = new JLabel("Your score:");
		label.setHorizontalAlignment(SwingConstants.LEFT);
		panel.add(label);
		
		JButton btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		splitPane.setRightComponent(btnClose);
	}

}
