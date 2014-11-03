package Entity.Items;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * This class represents an item in the game. We will only load one of every item and keep track of how much of that item
 * the player has through the amount field.
 */
public abstract class Item {
    protected BufferedImage icon;
    public Rectangle bounds;
    public int amount;
    public static final byte ITEM_AMOUNT = 5;

    // how we keep track of which item is which
    public int id;

    public abstract void action();
    public abstract void update();

    public void draw(Graphics2D g) {
        g.drawImage(icon, bounds.x, bounds.y, bounds.width, bounds.height, null);
    }
    public void draw(Graphics2D g, int x, int y) {
        g.drawImage(icon, x, y, bounds.width, bounds.height, null);
    }
    /**
     * Adds the given amount to the total amount
     * @param amnt the amount you want to add
     */
    public void add(int amnt) {
        amount += amnt;
    }

    /**
     * Moves the item to the location given
     * @param x the x-position
     * @param y the y-position
     */
    public void move(int x, int y) {
        bounds.setLocation(x, y);
    }
}
