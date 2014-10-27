package Entity;

import java.awt.*;

/**
 * These are the "walls" in the game. I created this in mind that I may want to have some "Walls" behave
 * differently
 */
public class Block extends Rectangle {
    public boolean isDestroyable;
    public boolean isDoor;
    public Block(int x, int y, int width, int height){
        super(x, y, width, height);
    }
    public Block(Rectangle rectangle) {
        super(rectangle);
    }
}
