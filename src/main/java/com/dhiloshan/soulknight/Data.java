package com.dhiloshan.soulknight;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Data {
	public static Player player = new Player(90, 90, 760, 455);
	public static BadPistol weapon = new BadPistol();
	public static ArrayList<Enemy> enemies = new ArrayList<Enemy>();
	public static Enemy trumpetFlower = new Enemy("Trumpet Flower", new ImageIcon(App.class.getResource("/assets/images/characters/TrumpetFlower.png")).getImage(), 55, 55, 600, 100);
	public static Map map = new Map();
}