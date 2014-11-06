package Entity.Player;

import AssetManagers.Animation;
import AssetManagers.SoundManager;
import Entity.Enemies.Enemy;
import Entity.Entity;
import Entity.Items.*;
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

    private Item[] items;
    private short currentItem;

    // we want to wait before we play another sound
    protected int soundDelay;

    public Player(KeyInput keys) {
        items = new Item[Item.ITEM_AMOUNT];

        // -1 means no item currently equipped
        currentItem = -1;

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
        // adding standing sprites
        sprites.addSprite("rightStanding", "Player/rightstanding.png", true);
        sprites.addSprite("leftStanding", "Player/leftstanding.png");
        sprites.addSprite("upStanding", "Player/upstanding.png");
        sprites.addSprite("downStanding", "Player/downstanding.png");

        // adding punching sprites
        sprites.addSprite("downPunching", "Player/downPunching.png");
        sprites.addSprite("upPunching", "Player/upPunching.png");
        sprites.addSprite("leftPunching", "Player/leftPunching.png");
        sprites.addSprite("rightPunching", "Player/rightPunching.png");

        // adding walking animations
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
        else if (keyInput.isHeld(KeyEvent.VK_SPACE))
        {
            setPunchingSprites();
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

    public void setHealth(int amount) {
        health = amount;
    }
    public void setCurrentItem(short id) {
        // make sure we are setting a valid id, then if we have any of that item left, only then can we equip it
        if (id < Item.ITEM_AMOUNT && id >= 0 && items[id].amount > 0)
            currentItem = id;
        // if we are trying to un-equip an item
        else if (id == -1)
            currentItem = id;
    }
    public Item getItem(int id) {
        return items[id];
    }
    public short getCurrentItem() {
        return currentItem;
    }

    /**
     * adds the item to the player's inventory
     * @param id id of the item
     */
    public void addItem(int id) {
        if (items[id] == null)
            items[id] = createItem(id);
        items[id].add(1);
    }

    /**
     * Reduces the item count by one
     * @param id the id of the item to reduce
     */
    public void removeItem(int id) {
        if (items[id] != null)
            items[id].amount--;
    }

    public void useCurrentItem() {
        // no item equipped
        if (currentItem == -1)
            return;

        items[currentItem].action();

        // last of item used, cannot be equipped
        if (items[currentItem].amount == 0)
            currentItem = -1;
    }

    /**
     * Tells you whether or not the player as this item
     * @param id id of the item
     * @return true if the player has the item
     */
    public boolean hasItem(int id) {
        return items[id] != null && items[id].amount != 0;
    }


    /**
     * This will add a certain amount of items to the players inventory
     * @param id id of the item
     * @param amount the amount of items to add
     */
    public void addItem(int id, int amount) {
        if (items[id] == null)
            items[id] = createItem(id);
        items[id].add(amount);
    }

    /**
     * This will do all the logic that goes with punching such as changing the sprite and dealing damage to the
     * enemy
     */
    public void punch(Enemy enemy) {
        enemy.hit(10);
    }

    /**
     * Sets the punching sprites for the player
     */
    protected void setPunchingSprites() {
        switch (facingDirection) {
            case Left:
                sprites.setCurrent("leftPunching");
                break;
            case Right:
                sprites.setCurrent("rightPunching");
                break;
            case Up:
                sprites.setCurrent("upPunching");
                break;
            case Down:
                sprites.setCurrent("downPunching");
                break;
        }
    }

    /**
     * This plays the sound as the player moves, it makes the sound we play random to create more of a
     * realistic movement sound
     */
    protected void playFootStepSound(){
        // sound delay is necessary because otherwise the soundManager will just over lap
        if (soundDelay <= 0){
            // we want to play a random one because then it will make a realistic sounding movement.
            soundManager.playSound("footStep" + (new Random().nextInt(3) + 1));
            soundDelay = 30;
        }
        else
            soundDelay--;
    }

    /**
     * Created the item associated with the id and returns it
     * @param id id of the item
     * @return the item associated with the id
     */
    protected Item createItem(int id) {
        switch (id) {
            case Item.SWORD_ID:
                return new Sword();
            case Item.KEY_ID:
                return new Key();
            case Item.WOOD_ID:
                return new Wood();
            case Item.GRASS_ID:
                return new Grass();
            case Item.STONE_ID:
                return new Stone();
            default:
                return null;
        }
    }
    public Item[] getItems() {
        return items;
    }

}
