package com.dhiloshan.soulknight;

import java.util.function.BiFunction;

public class SpawnEnemies {
	
	// Description: spawns enemies in a random area in a room
    // Parameters: the current room and an "enemy factory" (takes in the dimensions of the spawn position and outputs an Enemy object)
    // Return: void
    public static void spawnGeneric(App.Room room, BiFunction<Integer, Integer, ? extends Enemy> enemyFactory) {
        int cx = (int) room.bounds.getCenterX();
        int cy = (int) room.bounds.getCenterY();
        
        // random layout
        int layoutIndex = (int) (System.currentTimeMillis() % 3);

        // layout 1: 4 corners
        if (layoutIndex == 0) { 
            Data.enemies.add(enemyFactory.apply(room.bounds.x + 120, room.bounds.y + 120));
            Data.enemies.add(enemyFactory.apply(room.bounds.x + room.bounds.width - 120, room.bounds.y + 120));
            Data.enemies.add(enemyFactory.apply(room.bounds.x + 120, room.bounds.y + room.bounds.height - 120));
            Data.enemies.add(enemyFactory.apply(room.bounds.x + room.bounds.width - 120,
                    room.bounds.y + room.bounds.height - 120));
        } 
        // layout number 2: diamond
        else if (layoutIndex == 1) { 
            Data.enemies.add(enemyFactory.apply(cx, room.bounds.y + 120));
            Data.enemies.add(enemyFactory.apply(cx, room.bounds.y + room.bounds.height - 120));
            Data.enemies.add(enemyFactory.apply(room.bounds.x + 120, cy));
            Data.enemies.add(enemyFactory.apply(room.bounds.x + room.bounds.width - 120, cy));
        } 
        // layout number 3: center group
        else { 
            Data.enemies.add(enemyFactory.apply(cx, cy));
            Data.enemies.add(enemyFactory.apply(cx - 80, cy));
            Data.enemies.add(enemyFactory.apply(cx + 80, cy));
        }

        room.currentWave++;
    }

    public static void spawnBoss(App.Room room) {
        Data.enemies.add(new GoblinPriest((int) room.bounds.getCenterX(), (int) room.bounds.getCenterY()));
    }
}
