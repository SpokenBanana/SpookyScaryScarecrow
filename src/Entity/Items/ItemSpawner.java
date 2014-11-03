package Entity.Items;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * To not take up so much memory, this class will be used by the map. It will carry the id and sprite of the item
 * he is meant to give to the player. The Player will be given the id and will know what item to place into his
 * inventory
 */
public class ItemSpawner extends Rectangle{
    private BufferedImage sprite;
    private int id;
    public ItemSpawner(int id, Rectangle bounds) {
        super(bounds);
        try{
            switch (id) {
                case 0:
                    sprite = ImageIO.read(new File("Assets/Sprites/items/sword.png"));
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * gives you back the id of the item to spawn
     * @return the id of the item
     */
    public int getId(){
        return id;
    }

    /**
     * Draws the item on the screen
     * @param g the class we use to draw
     */
    public void draw(Graphics2D g) {
        g.drawImage(sprite, x, y, width, height, null);
    }
}
