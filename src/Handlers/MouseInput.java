package Handlers;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
    This class will help out any game state that needs to use the mouse, this may not be the most
    efficient way of doing this, but I think this works for this purpose.
 */
public class MouseInput implements MouseMotionListener, MouseListener{
    private Point mouseLocation;
    boolean clicked, oldClicked;

    public MouseInput() {
        mouseLocation = new Point(0,0);
        clicked = false;
    }
    public void update() {
        oldClicked = clicked;
    }
    public boolean isMouseOver(Rectangle position) {
        return position.contains(mouseLocation);
    }
    public boolean didMouseClickOn(Rectangle position) {
        return !oldClicked && clicked && position.contains(mouseLocation);
    }
    public Point getMouseLocation(){
        return mouseLocation;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseLocation.x = e.getX();
        mouseLocation.y = e.getY();
        clicked = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseLocation.x = e.getX();
        mouseLocation.y = e.getY();
        clicked = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseLocation.x = e.getX();
        mouseLocation.y = e.getY();
    }
}
