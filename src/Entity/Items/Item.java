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

    // certain items can depreciate after a some uses. In this case, when depreciation is lower than 0, it is depreciated
    protected short depreciation;

    // tells about the item and how to use it
    protected String description;

    public static final byte ITEM_AMOUNT = 9, SWORD_ID = 0, KEY_ID = 1, WOOD_ID = 2, GRASS_ID = 3, STONE_ID = 4,
                                              FIRE_ID = 5, Bow_ID = 6, ARROW_ID = 7, HEALTH_ID = 8;

    // how we keep track of which item is which
    public int id;

    public abstract void action();
    public abstract void update();

    public void draw(Graphics2D g) {
        g.drawImage(icon, bounds.x, bounds.y, bounds.width, bounds.height, null);
    }
    public void draw(Graphics2D g, int x, int y) {
        if (icon != null)
            g.drawImage(icon, x, y, bounds.width, bounds.height, null);
    }

    public int getDepreciation() {
        return depreciation;
    }
    public void setDepreciation(int amount) {
        // depreciation cannot be lower than 0 or higher than 100
        if (amount < 0)
            depreciation = 0;
        else if (amount > 100)
            depreciation = 100;
        else
            depreciation = (short) amount;
    }
    public String getDescription() { return description; }

    /**
     * Add the amount given to depreciation and returns new value. Depreciation cannot be lower than 0 or higher than
     * 100
     * @param diff the amount to change depreciation by
     * @return the new depreciation value
     */
    public int changeDepreciation(int diff) {
        depreciation += diff;
        if (depreciation < 0)
            depreciation = 0;
        else if (depreciation > 100)
            depreciation = 100;
        return depreciation;
    }

    /**
     * Not all items will need to use actionDraw() so we leave it as an unimplemented method to not force any item to
     * override it
     * @param g the "brush" we use to draw with
     */
    public void actionDraw(Graphics2D g) {}
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

    /**
     * Once the item is picked up, it goes to a specific place on the screen which can be determined by the id.
     * Since the formula is the same for all items, we can just implement it here.
     */
    public void setBounds() {
        bounds = new Rectangle(620,120 + (id * 32),32,32);
    }
}
