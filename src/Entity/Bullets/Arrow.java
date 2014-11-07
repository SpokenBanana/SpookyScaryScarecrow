package Entity.Bullets;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * This is an arrow, it deals more damage than any other bullet.
 */
public class Arrow extends Bullet {
    // draws our image rotated
    AffineTransformOp rotationArtist;
    public Arrow(Rectangle location, Point target) {
        super(location, target);
        damage = 15;

        // we want to rotate with respect to the middle of the image
        int pivotx = (position.width / 2);
        int pivoty = (position.height / 2);

        // after many trials or error, the math finally works to get the arrow to rotate to the correct angle
        AffineTransform transform = AffineTransform.getRotateInstance(Math.toRadians(-angle * (180.0 / Math.PI)), pivotx, pivoty);
        rotationArtist = new AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

        try {
            sprite = new BufferedImage(position.width, position.height, BufferedImage.TYPE_INT_ARGB);

            // gets the image from the location we want, rotates it, and places the rotated image in sprite
            rotationArtist.filter(ImageIO.read(new File("Assets/Sprites/items/arrow.png")), sprite);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
