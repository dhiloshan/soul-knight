package com.dhiloshan.soulknight;

import java.awt.Image;

public class Weapon {
    
    public Image sprite;
    public String weaponName;
    public long prevShot = 0L;
    public int height = 0;
    public int width;
    
    public Weapon(String weaponName, Image sprite, int width, int height) {
        this.weaponName = weaponName;
        this.sprite = sprite;
        this.width = width;
        this.height = height;
    }
}