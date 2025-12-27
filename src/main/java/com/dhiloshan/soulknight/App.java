package com.dhiloshan.soulknight;

import com.studiohartman.jamepad.ControllerIndex;
import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;
import com.studiohartman.jamepad.ControllerUnpluggedException;

import javax.swing.*;
import javax.imageio.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;


public class App extends JPanel {

	public static ControllerManager pads;
	public static Timer timer;
	public static ControllerIndex controller;
	public static ControllerState s;
	

	public static int screenWidth = 1200, screenHeight = 800;
	
	Data data; // contains all the assets and classes
	

	public App() { // constructor
		setPreferredSize(new Dimension(screenWidth, screenHeight));
		setBackground(Color.WHITE);
		
		loadController();
		
		// 60 FPS (1000 ms / 16 ms)
		timer = new Timer(16, new ActionListener() {   // have to create an actionlistener object to run the update method (part of timer api)
			@Override
			public void actionPerformed(ActionEvent e) {
				update();
			}
		}); 
	}
	
	public static void loadController() {
		pads = new ControllerManager();

		try {
			pads.initSDLGamepad();
		} catch (Throwable t) {
			System.out.println("initSDLGamepad() warning! Continuing...");
			t.printStackTrace();
		}
		
		controller = pads.getControllerIndex(0); // only one controller is connected (1 player) -> 0th index

		try {
			if (!controller.canVibrate()) {
				System.out.println("Controller does not support rumble :(");
				return;
			}
		} catch (ControllerUnpluggedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// -- start and stop the timer (game)
	void startLoop() {
		timer.start();
	}

	void stopLoop() {
		timer.stop();
	}

	public void update() {
		// -- check if controller is connected
		pads.update();
		s = pads.getState(0);
		if (!s.isConnected) {
			repaint();
			return;
		}
		
		data.player.move();
		
		repaint();
	}
	
	public static void rumble(float left, float right, int ms) { // helper function
		try {
			boolean vibrateCur = controller.doVibration(left, right, ms);
			if(vibrateCur) {
				System.out.println("Rumble triggered!");
			}
			else {
				System.out.println("Rumble failed (SDL/controller refused).");
			}
		} catch (ControllerUnpluggedException e) {
			System.out.println("Controller unplugged.");
		} catch (Throwable t) {
			System.out.println("Rumble error: " + t);
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g.create(); // encompasses all the graphics for one frame

		data.player.render(g2); // render the player
		
		try { // display player stats
			data.player.displayStatBar(g2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		g2.dispose(); // get rid of all the graphics and redraw them in their new positions in the next frame
	}

	public static void main(String[] args) {
		System.out.println("Soul Knight (Swing) started!");

		JFrame frame = new JFrame("Soul Knight");
		
		App panel = new App();
		frame.setContentPane(panel);
		frame.pack();
	

		// -- center the window
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				panel.stopLoop();
				try {
					pads.quitSDLGamepad();
				} catch (Throwable ignored) {
				}
				frame.dispose();
				System.exit(0);
			}
		});

		frame.setVisible(true);
		panel.startLoop();
	}

}