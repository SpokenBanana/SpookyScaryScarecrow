package Entity.Items;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class Item {
    BufferedImage sprite;
    BufferedImage icon;

    public abstract void action();
    public abstract void update();
    public abstract void draw(Graphics2D g);
}
