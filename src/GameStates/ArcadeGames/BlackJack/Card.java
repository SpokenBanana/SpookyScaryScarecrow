package GameStates.ArcadeGames.BlackJack;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Represents a card in Black jack. This is mostly to draw the card of the screen easier and make it look fancy.
 * It inherits from Rectangle just to make it easier to draw and keep track of its bounds
 */
public class Card extends Rectangle {

    String representation;
    BufferedImage spriteSuit;
    public Card(Rectangle bounds, String value) {
        super(bounds);
        representation = value;
        try{
            String relativePath = "Assets/Sprites/ArcadeGames/BlackJack/";
            switch (value.charAt(value.length()-1)) {
                case 'h':
                    spriteSuit = ImageIO.read(new File(relativePath + "heart.png"));
                    break;
                case 'c':
                    spriteSuit = ImageIO.read(new File(relativePath + "club.png"));
                    break;
                case 'v':
                    spriteSuit = ImageIO.read(new File(relativePath + "clover.png"));
                    break;
                case 'd':
                    spriteSuit = ImageIO.read(new File(relativePath + "diamond.png"));
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getValue() {
//                                                  the last char represents it's suit so exclude that
        return Integer.parseInt(representation.substring(0, representation.length()-1));
    }

    public void draw(Graphics2D g, BufferedImage cardSprite) {
        g.drawImage(cardSprite, x, y, null);
        g.setColor(Color.black);
        g.setFont(new Font("Droid Sans", Font.BOLD, 15));
        String value = representation.substring(0, representation.length()-1);
        String suit = representation.substring(representation.length()-1);
        if (value.equals("11"))
            g.drawString("A", x + 20, y + 20);
        else
            g.drawString(value, x + 20, y + 20);

        g.drawImage(spriteSuit, x + 8, y + 30, null);
    }

    /**
     * We want to be able to tell what the card is by it's representation as a string
     * @return String representation of the card
     */
    @Override
    public String toString(){
        return representation;
    }


}
