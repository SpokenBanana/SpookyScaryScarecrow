package AssetManagers;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.File;
import java.util.HashMap;

/**
    This will control the sounds played in the game. We can store sounds we want and play them using the
    methods defined here.
 */
public class SoundManager {

    // I don't want to keep writing it so we have set up here
    private String path = "Assets/Sounds/";

    // we store each sound with a key, so we can do things like sounds.get("steps").start(); Very simple and easy to manage
    private HashMap<String, Clip> sounds;

    public SoundManager() {
        sounds = new HashMap<String, Clip>();
    }

    /**
     * This will allow the player to store a sound file with a key they can use to later play the stored sound.
     */
    public void addSound(String key, String fileName) {
        try {
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File(path + fileName));
            Clip clip = AudioSystem.getClip();
            clip.open(inputStream);
            sounds.put(key, clip);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(fileName + " failed to load.");
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
        stopSound(key);
        sounds.get(key).start();
    }

    /**
     * Will change the volume of the sounds specified
     * @param key
     * @param amount
     */
    public void changeVolume(String key, float amount) {
        FloatControl control = (FloatControl) sounds.get(key).getControl(FloatControl.Type.MASTER_GAIN);
        control.setValue(amount);
    }
    /**
        This stops the playing audio.
        If we accidentally order a clip to play when it is already playing, we simply just stop the clip.
     */
    private void stopSound(String key) {
        if (sounds.get(key).isRunning())
            sounds.get(key).stop();

        // like when reading a file, the cursor is left at the end, we have to move it to the front again
        sounds.get(key).setFramePosition(0);
    }
}
