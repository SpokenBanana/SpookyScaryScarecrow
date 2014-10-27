package Entity.Bullets;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;

public class Pellet extends Bullet{
    public Pellet(Rectangle location, Point target) {
        super(location, target);
        try {
            sprite = ImageIO.read(new File("Assets/Sprites/Enemies/Shooter/pellet.png"));
        } catch (Exception e) {

        }
    }

}
