package com.dhiloshan.soulknight;

import java.awt.Graphics2D;
import java.awt.Image;

public class Enemy extends Character {
	
	Image enemySprite;
	String enemyName;
	
	float health = 20;

	public Enemy(String enemyName, Image enemySprite, int width, int height, int sx, int sy) {
		super (enemySprite, width, height, sx, sy);
		this.enemyName = enemyName;
		isFacingLeft = (Math.round(Math.random()) == 1? true : false);
		Data.enemies.add(this);
	}
	
	private void enemyMove() {
		x += 5; y += 5;
	}
	
	private boolean enemyGotHit() {
		return false;
	}
	
	public void update(Graphics2D g2) {
		enemyMove();
		render(g2); 
		
		if(enemyGotHit()) {
			health -= 5;
		}
		
		if (health <= 0) { // enemy has been killed
			return;
		}
	}
	
	
}
