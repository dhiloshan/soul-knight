package com.dhiloshan.soulknight;

public class CollisionChecker {
	
	// Description: checks if the character is colliding with anything
    // Parameters: Current character c
    // Return: void
    public void checkTile(Character c) {
        // coordinates of solid area rectangle on the screen
        int lx = (int) (c.worldX + c.solidArea.x);
        int rx = (int) (c.worldX + c.solidArea.x + c.solidArea.width);
        int ty = (int) (c.worldY + c.solidArea.y);
        int by = (int) (c.worldY + c.solidArea.y + c.solidArea.height);

        if (c.isFacingLeft) {
            lx -= Data.weapon.width;
        } else {
            rx += Data.weapon.width;
        }

        int lc = lx / App.tileSize;
        int rc = rx / App.tileSize;
        int tr = ty / App.tileSize;
        int br = by / App.tileSize;
        
        
        // checks for collision in all four directions
        if (c.ly < 0) { // down
            int nextRow = (int) (ty + c.ly * c.speed) / App.tileSize;
            for (int col = lc; col <= rc; col++) {
                if (col < 0 || col >= Map.maxWorldCol[0] || nextRow < 0 || nextRow >= Map.maxWorldRow[0] ||
                        Data.tileM.tile[Data.tileM.cellTileNum[col][nextRow]].collision)
                    c.collisionOn = true;
            }
        }
        if (c.ly > 0) { // up
            int nextRow = (int) (by + c.ly * c.speed) / App.tileSize;
            for (int col = lc; col <= rc; col++) {
                if (col < 0 || col >= Map.maxWorldCol[0] || nextRow < 0 || nextRow >= Map.maxWorldRow[0] ||
                        Data.tileM.tile[Data.tileM.cellTileNum[col][nextRow]].collision)
                    c.collisionOn = true;
            }
        }
        if (c.lx < 0) { // left
            int nextCol = (int) (lx + c.lx * c.speed) / App.tileSize;
            for (int row = tr; row <= br; row++) {
                if (nextCol < 0 || nextCol >= Map.maxWorldCol[0] || row < 0 || row >= Map.maxWorldRow[0] ||
                        Data.tileM.tile[Data.tileM.cellTileNum[nextCol][row]].collision)
                    c.collisionOn = true;
            }
        }
        if (c.lx > 0) { // right
            int nextCol = (int) (rx + c.lx * c.speed) / App.tileSize;
            for (int row = tr; row <= br; row++) {
                if (nextCol < 0 || nextCol >= Map.maxWorldCol[0] || row < 0 || row >= Map.maxWorldRow[0] ||
                        Data.tileM.tile[Data.tileM.cellTileNum[nextCol][row]].collision)
                    c.collisionOn = true;
            }
        }
    }

}