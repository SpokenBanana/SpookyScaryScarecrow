package AssetManagers;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.File;
import java.util.HashMap;

/**
    This will control the soundManager played in the game. We can store soundManager we want and play them using the
    methods defined here.
 */
public class SoundManager {

    // we store each sound with a key, so we can do things like soundManager.get("steps").start(); Very simple and easy to manage
    private HashMap<String, Clip> sounds;

    public SoundManager() {
        sounds = new HashMap<String, Clip>();
    }

    /**
     * This will allow the player to store a sound file with a key they can use to later play the stored sound.
     */
    public void addSound(String key, String fileName) {
        try {
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File("Assets/Sounds/" + fileName));
            Clip clip = AudioSystem.getClip();
            clip.open(inputStream);
            sounds.put(key, clip);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(fileName + " failed to load.");
        }
    }

    /**
     * Will go through our collection of soundManager and stop all soundManager that are playing
     */
    public void stopCurrentSound() {
        for (String key : sounds.keySet()) {
            if (sounds.get(key).isRunning())
                stopSound(key);
        }
    }

    /**
     * Will go through the collection and pause all currently playing sounds
     */
    public void pauseCurrentSound() {
        for (String key : sounds.keySet()) {
            if (sounds.get(key).isRunning())
                pauseSound(key);
        }
    }
    /**
     * Acts the same as the other add sound but initializes a set volume
     */
    public void addSound(String key, String filename, float volumeChange) {
        addSound(key, filename);
        changeVolume(key, volumeChange);
    }
    /**
        This method will play the desired sound.
     */
    public void playSound(String key) {
        if (sounds.containsKey(key)){
            stopSound(key);
            sounds.get(key).start();
        }
    }
    /**
        This method will play the desired sound and loop it.
     */
    public void playSound(String key, boolean loop) {
        if (sounds.containsKey(key)) {
            stopSound(key);
            if (loop)
                sounds.get(key).loop(Clip.LOOP_CONTINUOUSLY);
            else
                sounds.get(key).start();
        }
    }

    /**
     * Will change the volume of the soundManager specified
     * @param key
     * @param amount
     */
    public void changeVolume(String key, float amount) {
        if (sounds.containsKey(key)){
            FloatControl control = (FloatControl) sounds.get(key).getControl(FloatControl.Type.MASTER_GAIN);
            control.setValue(amount);
        }
    }
    /**
        This stops the playing audio.
        If we accidentally order a clip to play when it is already playing, we simply just stop the clip.
     */
    public void stopSound(String key) {
        if (sounds.containsKey(key)){
            if (sounds.get(key).isRunning()){
                sounds.get(key).stop();
            }
            // like when reading a file, the cursor is left at the end, we have to move it to the front again
            sounds.get(key).setFramePosition(0);
        }
    }

    /**
     * This resumes a paused sound
     */
    public void resumeSound(String key) {
        if (sounds.containsKey(key))
            sounds.get(key).start();
    }

    /**
     * This will resume a sound and loop it
     * @param key
     * @param loop
     */
    public void resumeSound(String key, boolean loop) {
        if (sounds.containsKey(key)) {
            // already playing, no need to resume
            if (sounds.get(key).isRunning())
                return;
            if (loop)
                sounds.get(key).loop(Clip.LOOP_CONTINUOUSLY);
            else
                resumeSound(key);
        }
    }

    /**
     * This will stop the sound right where it is at, once play() is called again, it will resume from when you stopped
     * it
     */
    public void pauseSound(String key) {
        if (sounds.containsKey(key) && sounds.get(key).isRunning())
            sounds.get(key).stop();
    }

    /**
     * Deletes the sound associated with sound
     * @param key the key to sound to delete
     */
    public void deleteSound(String key) {
        stopSound(key);
        sounds.remove(key);
    }
}
