package AssetManagers;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
    This class make it easier to implement animations. When given an image file with frames,
    the amount of frame, and the desired speed of the animation, it will display the animation for you.
 */
public class Animation {

    protected BufferedImage animationImage;
    // the bounds of the current frame we want to draw
    private Rectangle sourceRectangle;
    private int time, speed;
    public Animation(String filePath, int frames, int speedInMilliseconds) {
        try {
            animationImage = ImageIO.read(new File("Assets/Sprites/" + filePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
        sourceRectangle = new Rectangle(0,0, animationImage.getWidth() / frames, animationImage.getHeight());
        // our game runs in 60 frames per second, so this converts the speedInMilliseconds into a time the game can read
        speed = speedInMilliseconds / (1000/60);
    }
    /**
        moves the rectangle to the next frame we want to draw
     */
    private void moveFrame() {
        // each frame is the same width, so to move to the next frame, just add the width to current x
        // the % animationImage.getWidth() is necessary to move the x position back to 0 when it reaches the end of the
        // image
        if (time++ % speed == 0)
            sourceRectangle.x = (sourceRectangle.x + sourceRectangle.width) % animationImage.getWidth();

    }
    public void draw(Graphics2D g, Rectangle bounds) {
        moveFrame();
        g.drawImage(animationImage, bounds.x, bounds.y, bounds.x + bounds.width, bounds.y + bounds.height,
                sourceRectangle.x, sourceRectangle.y, sourceRectangle.x + sourceRectangle.width, sourceRectangle.y + sourceRectangle.height, null);
    }
}
