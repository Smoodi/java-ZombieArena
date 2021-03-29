package fox.smoodi.launcher;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fox.smoodi.engine.SFoxEngine;
import fox.smoodi.game.Game;

public class startMenu extends JFrame {

	private JPanel contentPane;

	/**
	 * We create a frame in our constructor.
	 */
	
	public startMenu() {
		setResizable(false);
		setTitle("Zombie Arena by SmoodiFox");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 409, 391);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		//We load our logo.
		BufferedImage img = null;
		try {
		    img = ImageIO.read(new File("./resources/sprites/logo.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Image dimg = img.getScaledInstance(409, 160, Image.SCALE_SMOOTH);
		
		//And assign it to a label.
		JLabel imgLb = new JLabel("");
		imgLb.setBounds(0,0,409,180);
		imgLb.setIcon(new ImageIcon(dimg));
		contentPane.add(imgLb, BorderLayout.NORTH);
		
		//Adding a version label.
		JLabel lblZombieShooterVersion = new JLabel("Zombie Arena Version 1.0c");
		lblZombieShooterVersion.setBounds(10, 332, 242, 14);
		lblZombieShooterVersion.setHorizontalAlignment(SwingConstants.LEFT);
		contentPane.add(lblZombieShooterVersion);
		
		//Adding a close button. 
		JButton btnNewButton = new JButton("Close");
		btnNewButton.setBounds(272, 328, 121, 23);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		
		contentPane.add(btnNewButton);
		
		//Adding a percent label in order to show the current sound volume.
		JLabel percentLabel = 
				new JLabel("100 %");
		percentLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		percentLabel.setBounds(281, 207, 112, 14);
		contentPane.add(percentLabel);
		
		//Adding a comboBox to select the desired resolution.
		JComboBox comboBox = new JComboBox();
		//Adding a string array containing the items for the comboBox.
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"640 x 480", "1024 x 768", "1280 x 720", "1280 x 800", "1440 x 900", "1920 x 1080"}));
		comboBox.setSelectedIndex(0);
		comboBox.setBounds(10, 275, 189, 20);
		contentPane.add(comboBox);
				
		//Adding a slider for the volume.
		JSlider slider = new JSlider();
		slider.setBackground(Color.WHITE);
		slider.setValue(100);
		slider.setBounds(10, 221, 383, 26);
		slider.addChangeListener(new ChangeListener() {

			//We add a change listener to change our percent label's text every time the value of the slider gets changed.
			@Override
			public void stateChanged(ChangeEvent e) {
				percentLabel.setText(slider.getValue() + " %");
				
			}
							
			}
		);
		contentPane.add(slider);
		
		//We add a checkBox for fullscreen support.
		JCheckBox checkBox = new JCheckBox("Fullscreen");
		checkBox.setBackground(Color.WHITE);
		checkBox.setBounds(102, 302, 97, 23);
		contentPane.add(checkBox);
		
		//We add our play button
		JButton button = new JButton("Play");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				//If the button is pressed we initialize our window height and width to a standard.
				int width = 640;
				int height = 480;
				
				int index = comboBox.getSelectedIndex();
				
				//Now we check our index and set our width and height according to the resolution selected.
				switch (index) {
				case 0:
					width = 640;
					height = 480;
					break;
				case 1:
					width = 1024;
					height = 768;
					break;
				case 2:
					width = 1280;
					height = 720;
					break;
				case 3:
					width = 1280;
					height = 800;
					break;
				case 4:
					width = 1440;
					height = 900;
					break;
				case 5:
					width = 1920;
					height = 1080;
					break;
				}
				
				//Now we close the frame.
				dispose();
				
				//And create a new Game object where we set our master volume to our sliders value.
				Game game = new Game();
				game.masterVolume = (float)slider.getValue()/100;
				
				//Now we tell the SFoxEngine to start a new game with the given height, the window title "Zombie Arena",
				//our Game object and tell it to be run at 60FPS and whether it should be fullscreen or not.
				SFoxEngine.startGame(width, height, "Zombie Arena", game, 60, checkBox.isSelected());
			}
		});
		button.setBounds(204, 268, 189, 54);
		contentPane.add(button);
				
		//We add a resolution instruction label
		JLabel lblResolution = new JLabel("Choose your resolution");
		lblResolution.setBounds(10, 250, 189, 14);
		contentPane.add(lblResolution);
		
		//We add another instruction label
		JLabel lblOrUse = new JLabel("Mode:");
		lblOrUse.setBounds(10, 307, 85, 14);
		contentPane.add(lblOrUse);
		
		//And another
		JLabel lblMasterVolume = new JLabel("Master volume:");
		lblMasterVolume.setBounds(13, 207, 202, 14);
		contentPane.add(lblMasterVolume);
		


	}
}
