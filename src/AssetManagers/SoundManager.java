package AssetManagers;

import java.util.HashMap;

/**
  *  This will control the soundManager played in the game. We can store soundManager we want and play them using the
  *  methods defined here.
 */
public class SoundManager {

    // we store each sound with a key, so we can do things like soundManager.get("steps").start(); Very simple and easy to manage
    private HashMap<String, Sound> sounds;

    public SoundManager() {
        sounds = new HashMap<>();
    }

    /**
     * This will allow the player to store a sound file with a key they can use to later play the stored sound.
     */
    public void addSound(String key, String fileName) {
        // if we have already got this sound, then we don't want to open another clip or we will use too much memory.
        if (sounds.containsKey(key))
            return;
        sounds.put(key, new Sound(fileName));
    }

    /**
     * Will go through our collection of soundManager and stop all soundManager that are playing
     */
    public void stopCurrentSound() {
        sounds.keySet().forEach(this::stopSound);
    }

    /**
     * Will go through the collection and pause all currently playing sounds
     */
    public void pauseCurrentSound() {
        sounds.keySet().forEach(this::pauseSound);
    }
    /**
     * Acts the same as the other add sound but initializes a set volume
     */
    public void addSound(String key, String filename, float volumeChange) {
        addSound(key, filename);
        changeVolume(key, volumeChange);
    }
    /**
      *  This method will play the desired sound.
     */
    public void playSound(String key) {
        if (sounds.containsKey(key)){
            stopSound(key);
            sounds.get(key).play();
        }
    }
    /**
       * This method will play the desired sound and loop it.
     */
    public void playSound(String key, boolean loop) {
        if (sounds.containsKey(key)) {
            stopSound(key);
            if (loop)
                sounds.get(key).loop();
            else
                sounds.get(key).play();
        }
    }

    /**
     * Will change the volume of the soundManager specified
     * @param key the key of the sound to change
     * @param amount the amount to change the volume
     */
    public void changeVolume(String key, float amount) {
        if (sounds.containsKey(key)){
            sounds.get(key).changeVolume(amount);
        }
    }
    /**
      *  This stops the playing audio.
      * If we accidentally order a clip to play when it is already playing, we simply just stop the clip.
     */
    public void stopSound(String key) {
        if (sounds.containsKey(key)){
            sounds.get(key).stop();
        }
    }

    /**
     * This resumes a paused sound. The difference between this and playSound() is that playSound() will attempt to
     * reset the sound to the beginning of the clip.
     */
    public void resumeSound(String key) {
        if (sounds.containsKey(key))
            sounds.get(key).play();
    }

    /**
     * This will resume a sound and loop it
     * @param key the key of the sound to play
     * @param loop whether or not to loop the sound
     */
    public void resumeSound(String key, boolean loop) {
        if (sounds.containsKey(key)) {
            // already playing, no need to resume
            if (sounds.get(key).isRunning())
                return;
            // check if the user wants to loop or not
            if (loop)
                sounds.get(key).loop();
            else
                sounds.get(key).resume();
        }
    }

    /**
     * This will stop the sound right where it is at, once play() is called again, it will resume from when you stopped
     * it
     */
    public void pauseSound(String key) {
        if (sounds.containsKey(key) && sounds.get(key).isRunning())
            sounds.get(key).pause();
    }

    /**
     * Deletes the sound associated with sound
     * @param key the key to sound to delete
     */
    public void deleteSound(String key) {
        if (sounds.containsKey(key)) {
            sounds.get(key).delete();
            sounds.remove(key);
        }
    }

    /**
     * Will make sure no sound remains in memory
     */
    public void clearAllSounds() {
        for (String clip : sounds.keySet()) {
            sounds.get(clip).delete();
        }
        sounds.clear();
    }
}
