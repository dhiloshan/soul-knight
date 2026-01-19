package com.dhiloshan.soulknight;

import java.awt.Graphics2D;

public class CollisionChecker {

	public CollisionChecker() {

	}
	
	public void checkTile(Character character) {
	    int characterLeftWorldX = (int) (character.worldX + character.solidArea.x);
	    int characterRightWorldX = (int) (character.worldX + character.solidArea.x + character.solidArea.width);
	    int characterTopWorldY = (int) (character.worldY + character.solidArea.y);
	    int characterBottomWorldY = (int) (character.worldY + character.solidArea.y + character.solidArea.height);

	    int characterLeftCol = characterLeftWorldX / App.tileSize;
	    int characterRightCol = characterRightWorldX / App.tileSize;
	    int characterTopRow = characterTopWorldY / App.tileSize;
	    int characterBottomRow = characterBottomWorldY / App.tileSize;

	    int tileNum1, tileNum2;

	    if (Data.player.ly < 0) {
	        characterTopRow = (int) (characterTopWorldY + Player.ly * character.speed) / App.tileSize;
	        tileNum1 = Data.tileM.cellTileNum[characterLeftCol][characterTopRow];
	        tileNum2 = Data.tileM.cellTileNum[characterRightCol][characterTopRow];
	        if (Data.tileM.tile[tileNum1].collision || Data.tileM.tile[tileNum2].collision) {
	            character.collisionOn = true;
	        }
	    }
	    if (Data.player.ly > 0) {
	        characterBottomRow = (int) (characterBottomWorldY + Player.ly * character.speed) / App.tileSize;
	        tileNum1 = Data.tileM.cellTileNum[characterLeftCol][characterBottomRow];
	        tileNum2 = Data.tileM.cellTileNum[characterRightCol][characterBottomRow];
	        if (Data.tileM.tile[tileNum1].collision || Data.tileM.tile[tileNum2].collision) {
	            character.collisionOn = true;
	        }
	    }
	    if (Data.player.lx < 0) {
	        characterLeftCol = (int) (characterLeftWorldX + Player.lx * character.speed) / App.tileSize;
	        tileNum1 = Data.tileM.cellTileNum[characterLeftCol][characterTopRow];
	        tileNum2 = Data.tileM.cellTileNum[characterLeftCol][characterBottomRow];
	        if (Data.tileM.tile[tileNum1].collision || Data.tileM.tile[tileNum2].collision) {
	            character.collisionOn = true;
	        }
	    }
	    if (Data.player.lx > 0) {
	        characterRightCol = (int) (characterRightWorldX + Player.lx * character.speed) / App.tileSize;
	        tileNum1 = Data.tileM.cellTileNum[characterRightCol][characterTopRow];
	        tileNum2 = Data.tileM.cellTileNum[characterRightCol][characterBottomRow];
	        if (Data.tileM.tile[tileNum1].collision || Data.tileM.tile[tileNum2].collision) {
	            character.collisionOn = true;
	        }
	    }
	}
}