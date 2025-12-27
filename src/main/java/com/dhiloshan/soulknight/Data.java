package com.dhiloshan.soulknight;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;

public class Data {

	public static Player player;
	
	static { // runs the first time when it is called (ex. Data.cactus) and runs EVERYTHING here. no need for new Data()
		// create Image Icon objects (find the gif or image in the folder first)
		// ensures it works when compiled to JAR

		// create Character objects
		player = new Player(80, 80, 500, 500);
	}
}
