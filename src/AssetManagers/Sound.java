package AssetManagers;

import javax.sound.sampled.*;
import java.io.File;

/**
 * This will contain all logic needed with sounds. We get a lot of memory leaks when working with just Clips,
 * so we handle all of those cases here.
 */
public class Sound {
    private AudioInputStream inputStream;
    private Clip clip;
    public Sound(String sound) {
       try{
            inputStream = AudioSystem.getAudioInputStream(new File("Assets/Sounds/" + sound));
            clip = AudioSystem.getClip();
            clip.open(inputStream);

            // keep having the clip listen if it is trying to be closed, if so, close the audio line.
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.CLOSE)
                    event.getLine().close();
            });
       }catch (Exception e){
           e.printStackTrace();
           clip = null;
           inputStream = null;
       }
    }

    /**
     * Plays the desired sound from the start
      */
    public void play() {
        stop();
        clip.start();
    }

    /**
     * Will resume the sound from where the cursor leaves off.
     */
    public void resume(){
        clip.start();
    }

    /**
     * Will play the sound and have it loop continuously
      */
    public void loop(){
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    /**
     * Will pause the sound right where it is at
      */
    public void pause() {
        clip.stop();
    }

    /**
     * Will stop the sound and reset it's cursor back to the beginning of the clip. Just like we would with file.
     */
    public void stop() {
        clip.stop();
        clip.setFramePosition(0);
    }

    /**
     * Will close all streams and allow the garbage collector do its work on them
     */
    public void delete() {
        stop();
        // close all streams and then set them to null for garbage collection to eat up
        clip.close();
        try{
            inputStream.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
        inputStream = null;
        clip = null;
    }

    /**
     * Tells whether or not the clip is playing
      * @return whether or not the clip is playing
     */
    public boolean isRunning(){
        return clip.isRunning();
    }

    /**
     * Changes the volume of the sound
      * @param amount the amount to change the volume by
     */
    public void changeVolume(float amount) {
        FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        control.setValue(amount);
    }
}
