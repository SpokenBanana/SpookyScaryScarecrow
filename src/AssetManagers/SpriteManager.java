package AssetManagers;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;

/*
    This class will handle all the sprites a character will use. Some characters require many sprites
    for each time he faces a direction and such. This will make it easier to change the sprites as we need to
 */
public class SpriteManager {

    // there is a separate map for sprites and animations but the user doesn't know that, he thinks it is stored in one
    // list.
    public HashMap<String, BufferedImage> sprites;
    public HashMap<String, Animation> animations;

    String current;
    public SpriteManager () {
        sprites = new HashMap<String, BufferedImage>();
        animations= new HashMap<String, Animation>();
    }
    /*
        This draws the current sprite that the user sets and at the location they specify. Bounds
        will most likely by the position field of the entity.
     */
    public void draw(Graphics2D g, Rectangle bounds) {
        if (sprites.containsKey(current))
            g.drawImage(sprites.get(current), bounds.x, bounds.y, bounds.width, bounds.height, null);
        else if (animations.containsKey(current))
            animations.get(current).draw(g, bounds);
    }
    /*
        sometimes we may want to display the image in a certain tint for some events
     */
    public void draw(Graphics2D g, Rectangle bounds, Color color) {
        if (sprites.containsKey(current))
            g.drawImage(sprites.get(current), bounds.x, bounds.y, bounds.width, bounds.height, color, null);
        else if (animations.containsKey(current))
            animations.get(current).draw(g, bounds);
    }
    public void setCurrent(String key) {
        current = key;
    }
    /*
        Adds the sprite to the HashMap where it can later be accessed as long as the
        player knows the key to the sprite
     */
    public void addSprite(String key, String spriteRelativePath) {
        BufferedImage sprite = null;
        try {
            sprite = ImageIO.read(new File("Assets/Sprites/" + spriteRelativePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (sprite != null) {
            sprites.put(key, sprite);
        }
    }
    /*
        adds an animation to the animations list.
     */
    public void addAnimation(String key, Animation animation) {
        if (!sprites.containsKey(key)) {
            animations.put(key, animation);
        }
    }
    /*
        I like the option to not always send a false to addSprite() when I know I don't want to make
        the sprite I'm adding the current sprite to draw, so I did this to replicate an optional
        parameter
     */
    public void addSprite(String key, String spriteRelativePath, boolean setToCurrent){
        addSprite(key, spriteRelativePath);
        if (setToCurrent)
            setCurrent(key);
    }
}
