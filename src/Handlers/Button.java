package Handlers;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
    This class is used to represent a button in the game. Yeah, I could have just used Java's JButton
    but that would require each game state class to be a JPanel and I don't think that would be ideal since we are
    probably going to switch from state to state a lot, constructing a new JPanel each time would really be hard
    on performance and memory. Even though I've already used a lot of slow algorithms, this is where I draw the line!
 */
public class Button extends Rectangle {
    protected BufferedImage backgroundSprite;
    protected String buttonText;
    protected final int CHARACTER_WIDTH = 5;
    public boolean isHovered;
    protected final Font buttonFont = new Font("Sans Serif", Font.PLAIN, 15);
    protected final Color hoveredColor = new Color(20,100,185), regularColor = Color.black;

    public Button(Rectangle position, String text) {
        super(position);
        try {
            backgroundSprite = ImageIO.read(new File("Assets/UI/button.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        buttonText = text;
        isHovered = false;
    }

    public void changeText(String text) {
        buttonText = text;
    }
    public String getButtonText() {
        return buttonText;
    }

    public void draw(Graphics2D g) {
        g.setFont(buttonFont);
        if (isHovered)
            g.drawImage(backgroundSprite, x, y, width, height, hoveredColor, null);
        else
            g.drawImage(backgroundSprite, x, y, width, height, regularColor, null);
        g.setColor(Color.WHITE);
                                                    // trying to center the text.
        g.drawString(buttonText, x + ((width - (buttonText.length() * CHARACTER_WIDTH)) / 2),
                y + (height / 2) + CHARACTER_WIDTH );
    }


}
