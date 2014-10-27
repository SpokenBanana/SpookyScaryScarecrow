package Entity.Bullets;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;

public class FireBall extends Bullet {
    public FireBall(Rectangle location, Point target) {
        super(location, target);
        try {
            sprite = ImageIO.read(new File("Assets/Sprites/Enemies/Ghost/fireball.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
