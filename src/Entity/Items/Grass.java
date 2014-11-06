package Entity.Items;

import javax.imageio.ImageIO;
import java.io.File;

/**
 * This represents a grass object. It is mainly used to craft into something more useful.
 */
public class Grass extends Item{
    public Grass() {
        id = Item.GRASS_ID;
        setBounds();
        try{
            icon = ImageIO.read(new File("Assets/Sprites/items/grass.png"));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void action() {

    }

    @Override
    public void update() {

    }

}
