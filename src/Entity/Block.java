package Entity;

import java.awt.*;

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
