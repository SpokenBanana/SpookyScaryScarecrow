package Handlers;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * This class will make it easier to have access to information on
 * what keys are being pressed.
 */
public class KeyInput implements KeyListener {
    // we have an oldkeys to keep track of the previous keys pressed, useful to tell is a button has been
    // pressed or hold.
    private boolean[] keys, oldKeys;

    public KeyInput() {
        // Java's KeyEvent.VL_[key] is an integer value that represents a key(which for me, we won't
        // use any keys over 140), we use that value as an index
        // and when the data at it's index is true, it is pressed. If false, it it released
        keys = new boolean[140];
        oldKeys = new boolean[keys.length];

    }
    /*
        We know a key is held when keys[key] is true because we override the KeyPressed() to make the index
         of the key pressed true.
     */
    public boolean isHeld(int key)
    {
        return keys[key];
    }
    /**
        This will tell use if a button was just pressed. Since oldKeys represents the old state of the keys,
        we know that in order for a key to have been just pressed, it's previous state had to be unpressed and
        the current has to be pressed. In the next iteration, both of the keys will be pressed and this method will
        return false. This will be useful so when, say, a player presses space to fire a bullet, it won't create an
        endless stream of bullets while he holds space.
    */
    public boolean isPressed(int key) {
        return keys[key] && !oldKeys[key];
    }

    public void update() {
        // this copies the data from keys to oldKeys, saving the previous key state.
        System.arraycopy(keys, 0, oldKeys, 0, keys.length);
    }
    /**
    *   A key is pressed when its corresponding index is false
    */
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() < keys.length)
            keys[e.getKeyCode()] = true;
    }

    /**
     *  A key is not pressed when its corresponding index is false
     */
    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() < keys.length)
            keys[e.getKeyCode()] = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}
