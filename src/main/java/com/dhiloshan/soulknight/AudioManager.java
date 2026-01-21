package com.dhiloshan.soulknight;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AudioManager {

    private static AudioManager instance;
    private Map<String, Clip> clips = new HashMap<>();
    private float musicVolume = 0.5f;
    private float sfxVolume = 0.8f;

    // track music currently playing to be able to switch
    private Clip currentMusic;

    private AudioManager() {
        loadClips();
    }

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }
    
    // Description: loads the audio clips
    // Parameters: none
    // Return: void
    private void loadClips() {
        // SFX
        loadClip("fx_gun_1", "/assets/audio/sound-effects/fx_gun_1.wav");
        loadClip("fx_heart", "/assets/audio/sound-effects/fx_heart.wav");
        loadClip("shield_break", "/assets/audio/sound-effects/shield_break.wav");
        loadClip("crate_break", "/assets/audio/sound-effects/crate_break.wav");
        loadClip("victory", "/assets/audio/sound-effects/victory.wav");

        // Music
        loadClip("intro_bg", "/assets/audio/background-music/intro.wav");
        loadClip("level_bg", "/assets/audio/background-music/level-3-bg.wav");
        loadClip("boss_bg", "/assets/audio/background-music/finale.wav");
    }

    // Description: loads a specific audio clip
    // Parameters: none
    // Return: void
    private void loadClip(String name, String path) {
        try {
            URL url = getClass().getResource(path);
            if (url == null) {
                System.out.println("Audio missing: " + path);
                return;
            }
            AudioInputStream ais = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            clips.put(name, clip);
        } catch (Exception e) {
            System.err.println("Failed to load audio: " + path);
            // e.printStackTrace(); // Optional: reduce spam
        }
    }
    
    // Description: plays a specific sound effect
    // Parameters: none
    // Return: void
    public void playSfx(String name) {
        Clip c = clips.get(name);
        if (c != null) {
            if (c.isRunning())
                c.stop();
            c.setFramePosition(0);
            setVolume(c, sfxVolume);
            c.start();
        }
    }
    
    // Description: plays a specific background music track
    // Parameters: none
    // Return: void
    public void playMusic(String name, boolean loop) {
        Clip c = clips.get(name);
        if (c != null) {
            if (currentMusic != null && currentMusic != c) {
                currentMusic.stop();
            }
            currentMusic = c;
            setVolume(c, musicVolume);
            if (!c.isRunning()) {
                c.setFramePosition(0);
                if (loop)
                    c.loop(Clip.LOOP_CONTINUOUSLY);
                c.start();
            }
        }
    }

    public void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
        }
    }

    public void stopAllMusic() {
        stopMusic();
    }

    public void setMusicVolume(float v) {
        this.musicVolume = Math.max(0, Math.min(1, v));
        if (currentMusic != null) {
            setVolume(currentMusic, musicVolume);
        }
    }

    public void setSfxVolume(float v) {
        this.sfxVolume = Math.max(0, Math.min(1, v));
    }

    public float getMusicVolume() {
        return musicVolume;
    }

    public float getSfxVolume() {
        return sfxVolume;
    }

    // Description: controls the volume of an audio clip
    // Parameters: none
    // Return: void
    private void setVolume(Clip clip, float volume) {
        if (clip == null)
            return;
        
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        // convert 0.0 to 1.0 linear scale to dB
        
        // -80dB is pretty much silent
        float range = gainControl.getMaximum() - gainControl.getMinimum();
        float gain = (range * volume) + gainControl.getMinimum();
        
        // formula I found to accurately control volume
        float dB = (float) (Math.log10(Math.max(0.0001, volume)) * 20.0);
        gainControl.setValue(Math.max(gainControl.getMinimum(), Math.min(gainControl.getMaximum(), dB)));
    }
}
