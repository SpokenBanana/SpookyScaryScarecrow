package Entity.Player;

import AssetManagers.Animation;
import AssetManagers.SoundManager;
import Entity.Entity;
import Handlers.KeyInput;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Random;

/**
 * The player is the main character the player will play as. He is the spooky scarecrow!
 */
public class Player extends Entity {
    protected KeyInput keyInput;
    protected SoundManager soundManager;

    // we want to wait before we play another sound
    protected int soundDelay;

    public Player(KeyInput keys) {
        // we want the player to have his own input handler,
        // to not use too much memory, we will reference the one the game has created
        keyInput = keys;
        soundManager = new SoundManager();
        soundManager.addSound("footStep1", "footStep1.wav", -20f);
        soundManager.addSound("footStep2", "footStep2.wav", -20f);
        soundManager.addSound("footStep3", "footStep3.wav", -20f);

        health = 100;
        position = new Rectangle(32,32,32,64);
        oldPosition = new Point(position.x, position.y);
        targetPosition = new Point(position.x, position.y);
        velocity = 10;
        sprites.addSprite("rightStanding", "Player/rightstanding.png", true);
        sprites.addSprite("leftStanding", "Player/leftstanding.png");
        sprites.addSprite("upStanding", "Player/upstanding.png");
        sprites.addSprite("downStanding", "Player/downstanding.png");

        sprites.addAnimation("leftWalking", new Animation("Player/leftWalking.png", 3, 200));
        sprites.addAnimation("rightWalking", new Animation("Player/rightWalking.png", 3, 200));
        sprites.addAnimation("upWalking", new Animation("Player/upWalking.png", 3, 200));
        sprites.addAnimation("downWalking", new Animation("Player/downwalking.png", 3, 200));
    }
    public void update() {
        if (keyInput.isHeld(KeyEvent.VK_RIGHT) || keyInput.isHeld(KeyEvent.VK_D)) {
            playFootStepSound();
            facingDirection = currentDirection = Direction.Right;
            sprites.setCurrent("rightWalking");
        }
        else if (keyInput.isHeld(KeyEvent.VK_LEFT) || keyInput.isHeld(KeyEvent.VK_A)) {
            playFootStepSound();
            facingDirection = currentDirection = Direction.Left;
            sprites.setCurrent("leftWalking");
        }
        else if (keyInput.isHeld(KeyEvent.VK_DOWN) || keyInput.isHeld(KeyEvent.VK_S)) {
            playFootStepSound();
            facingDirection = currentDirection = Direction.Down;
            sprites.setCurrent("downWalking");
        }
        else if (keyInput.isHeld(KeyEvent.VK_UP) || keyInput.isHeld(KeyEvent.VK_W)) {
            playFootStepSound();
            facingDirection = currentDirection = Direction.Up;
            sprites.setCurrent("upWalking");
        }
        else {
            currentDirection = Direction.Standing;
            switch (facingDirection) {
                case Left:
                    sprites.setCurrent("leftStanding");
                    break;
                case Right:
                    sprites.setCurrent("rightStanding");
                    break;
                case Up:
                    sprites.setCurrent("upStanding");
                    break;
                case Down:
                    sprites.setCurrent("downStanding");
                    break;
            }
        }
        super.update();
    }

    /**
     * This plays the sound as the player moves, it makes the sound we play random to create more of a
     * realistic movement sound
     */
    protected void playFootStepSound(){
        // sound delay is necessary because otherwise the sounds will just over lap
        if (soundDelay <= 0){
            // we want to play a random one because then it will make a realistic sounding movement.
            soundManager.playSound("footStep" + (new Random().nextInt(3) + 1));
            soundDelay = 30;
        }
        else
            soundDelay--;
    }
}
