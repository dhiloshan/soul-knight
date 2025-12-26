package com.dhiloshan.soulknight;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Data {

	public static Image cactus;

	static { // runs the first time when it is called (ex. Data.cactus) and runs EVERYTHING here. no need for new Data()
		try {
			cactus = ImageIO.read(Data.class.getResource("/assets/images/characters/lvl-1/Boomerang_Cactus.gif")); // ensures it works when compiled to JAR
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
