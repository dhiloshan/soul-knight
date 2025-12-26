package com.dhiloshan.soulknight;

import com.studiohartman.jamepad.ControllerIndex;
import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;
import com.studiohartman.jamepad.ControllerUnpluggedException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class App extends JPanel {

	public static ControllerManager pads;
	public final Timer timer;
	public static ControllerIndex controller;

	public float x = 200, y = 150;
	public boolean wasAPressed = false;
	

	public App() { // constructor
		setPreferredSize(new Dimension(900, 550));
		setBackground(Color.BLACK);
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
		ControllerState s = pads.getState(0);
		if (!s.isConnected) {
			repaint();
			return;
		}

		// -- Move with left joy stick stick
		float lx = deadzone(s.leftStickX, 0.15f);
		float ly = deadzone(s.leftStickY, 0.15f);

		float speed = 3f;
		x += lx * speed;
		y += ly * speed;

		// stay in bound [20, w or height - 20]
		x = Math.max(20, Math.min(getWidth() - 20, x));
		y = Math.max(20, Math.min(getHeight() - 20, y));
		
		// -- if a is pressed ONLY now
		boolean a = s.a; 
		if (a && !wasAPressed) { 
			rumble(1.0f, 1.0f, 250);
		}
		wasAPressed = a;
		
		repaint();
	}

	public static float deadzone(float v, float dz) { // very minor joy stick movement shouldn't create player movement
		if(Math.abs(v) < dz) {
			return 0f;
		}
		else {
			return v;
		}
	}
	
	public void rumble(float left, float right, int ms) { // helper function
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
		Graphics2D g2 = (Graphics2D) g.create();

		g2.setColor(Color.WHITE);
		g2.drawString("Controller #0: left stick moves, A rumbles.", 18, 24);

		g2.setColor(Color.GREEN);
		g2.fillOval(Math.round(x) - 18, Math.round(y) - 18, 36, 36);

		g2.dispose();
	}

	public static void main(String[] args) {
		System.out.println("Soul Knight (Swing) started!");

		JFrame frame = new JFrame("Soul Knight");
		
		loadController();
		
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