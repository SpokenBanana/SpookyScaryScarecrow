package AssetManagers;

import javax.sound.sampled.*;
import java.io.File;

/**
 * This will contain all logic needed with sounds. We get a lot of memory leaks when working with just Clips,
 * so we handle all of those cases here.
 */
public class Sound {
    AudioInputStream inputStream;
    Clip clip;
    public Sound(String sound) {
       try{
            inputStream = AudioSystem.getAudioInputStream(new File("Assets/Sounds/" + sound));
            clip = AudioSystem.getClip();
            clip.open(inputStream);
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.CLOSE)
                    event.getLine().close();
            });
       }catch (Exception e){}
    }
    public void play() {
        stop();
        clip.start();
    }
    public void resume(){
        clip.start();
    }
    public void loop(){
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }
    public void pause() {
        clip.stop();
    }
    public void stop() {
        clip.stop();
        clip.setFramePosition(0);
    }

    public void delete() {
        stop();
        clip.close();
        try{
            inputStream.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
        inputStream = null;
        clip = null;
    }
    public boolean isRunning(){
        return clip.isRunning();
    }
    public void changeVolume(float amount) {
        FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        control.setValue(amount);
    }
}
