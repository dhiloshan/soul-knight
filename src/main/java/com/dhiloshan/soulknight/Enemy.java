package com.dhiloshan.soulknight;

import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

import com.dhiloshan.soulknight.Enemy.Node;
import com.dhiloshan.soulknight.Enemy.Point;


public class Enemy extends Character {

	Image enemySprite;
	String enemyName;

	float health = 20;

	public Enemy(String enemyName, Image enemySprite, int width, int height, int sx, int sy) {
		super(enemySprite, width, height, sx, sy);
		this.enemyName = enemyName;
		isFacingLeft = (Math.round(Math.random()) == 1 ? true : false);
		Data.enemies.add(this);
	}
	
	class Node {
        int c, r, g, h, f;
        Node p;

        Node(int c, int r) {
            this.c = c;
            this.r = r;
        }
    }
	
	class Point {
	    int x, y;

	    Point(int x, int y) {
	        this.x = x;
	        this.y = y;
	    }
	}

	private void enemyMove() {
		// nothing
	}

	private boolean enemyGotHit() {
		return false;
	}

	public void renderVisuals(Graphics2D g2) {
		render(g2);
	}

	public void update(Graphics2D g2) {
		enemyMove();
		render(g2);

		if (enemyGotHit()) {
			health -= 5;
		}

		if (health <= 0) { // enemy has been killed
			return;
		}
	}
	
	// Description: implements the A* search algorithm for enemies
	// Parameters: Starting and ending point coordinates
    // Return: the path for the enemy to travel
	public ArrayList<Point> findPath(int sc, int sr, int ec, int er) { 
        if (sc < 0 || sr < 0 || ec < 0 || er < 0 ||
                sc >= Map.maxWorldCol[0] || sr >= Map.maxWorldRow[0] ||
                ec >= Map.maxWorldCol[0] || er >= Map.maxWorldRow[0]) {
            return null;
        } // ensure it is in the map

        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingInt(n -> n.f));
        boolean[][] v = new boolean[Map.maxWorldCol[0]][Map.maxWorldRow[0]]; // visited array
        Node start = new Node(sc, sr);
        
        // initial f and h costs
        start.h = Math.abs(ec - sc) + Math.abs(er - sr);
        start.f = start.h;
        open.add(start); // add the starting node into the queue to start the algorithm
        int ops = 0; // # of steps

        while (!open.isEmpty() && ops++ < 500) {
            Node c = open.poll();
            
            if (c.c == ec && c.r == er) {
                ArrayList<Point> p = new ArrayList<>();
                
                while (c != null) {
                    p.add(0, new Point(c.c, c.r));
                    c = c.p;
                }
                return p;
            }
            
            if (c.c >= Map.maxWorldCol[0] || c.r >= Map.maxWorldRow[0] || c.c < 0 || c.r < 0) // ignore a point outside the map in algo
                continue;
            
            // explore all neighbours
            v[c.c][c.r] = true;
            for (int[] d : new int[][] { { 0, 1 }, { 0, -1 }, { 1, 0 }, { -1, 0 } }) { // four possible directions
                int nc = c.c + d[0], nr = c.r + d[1];
                // if new point is in bounds and not visited
                if (nc >= 0 && nr >= 0 && nc < Map.maxWorldCol[0] && nr < Map.maxWorldRow[0] && !v[nc][nr]) {
                    int t = Data.tileM.cellTileNum[nc][nr];
                    if (Data.tileM.tile[t] != null && Data.tileM.tile[t].collision)
                        continue;
                    
                    // add the g, h, and f costs into the node
                    Node n = new Node(nc, nr);
                    n.g = c.g + 1;
                    n.h = Math.abs(ec - nc) + Math.abs(er - nr);
                    n.f = n.g + n.h;
                    n.p = c;
                    open.add(n); // push into queue
                } 
            }
        }
        return null;
    }
}
