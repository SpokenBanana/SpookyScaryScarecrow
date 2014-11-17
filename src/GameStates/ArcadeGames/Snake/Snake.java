package GameStates.ArcadeGames.Snake;

import Entity.Entity;

import java.awt.*;
import java.util.Random;

/**
 * This class will represent the Snake in the Snake game. It will do all the logic in moving, drawing and updating.
 */
public class Snake {

    // This node class is really simple so we'll just have it as an inner class
    private class Node extends Rectangle {
        Color color;
        Node next;
        public Node(int x, int y, Color c) {
            super(x, y, NODE_SIZE, NODE_SIZE);
            color = c;
        }
        public void draw(Graphics2D g) {
            g.setColor(color);
            g.fill(this);
        }
    }

    // the snake will be represented as a linked-list for simplicity (assuming they are simple)
    private Node head;
    private final int NODE_SIZE = 10;

    // the random colors the snake "nodes" can be
    private final Color[] colors = {Color.red, Color.pink, Color.yellow, Color.cyan, Color.green, Color.ORANGE, Color.blue};
    private Random random;
    public Entity.Direction direction;

    // the snake will initially has 5 "nodes"
    public Snake() {
        random = new Random();
        resetSnake();
    }

    public void update() {
        switch (direction) {
            case Right:
                move(NODE_SIZE, 0);
                break;
            case Left:
                move(-NODE_SIZE, 0);
                break;
            case Down:
                move(0, NODE_SIZE);
                break;
            case Up:
                move(0, -NODE_SIZE);
                break;
        }
    }
    public void resetSnake() {
        direction = Entity.Direction.Right;
        head = new Node(200,200, Color.blue);
        for (int i = 0; i < 4; i++)
            addNode();
        move(NODE_SIZE, 0);
    }
    public void move(int diffX, int diffY) {
        int oldx = head.x, oldy = head.y;

        // we mod it so in case the player goes off the screen, he just appears on the other side!
        int newx = mod(head.x + diffX), newy = mod(head.y + diffY);

        Node copy = head;
        do {
            // move the current node to desired position
            copy.x = newx;
            copy.y = newy;

            // the next one will now take the location the current on was previously at
            newx = oldx;
            newy = oldy;

            // now the one after the next will know what space to take up
            oldx = copy.next.x;
            oldy = copy.next.y;

            // on to the next one!
            copy = copy.next;
        } while (copy.next != null);

        // we left one behind, so catch him up!
        copy.x = newx;
        copy.y = newy;
    }
    public boolean intersects(Rectangle rectangle) {
        return head.intersects(rectangle);
    }
    public void addNode() {
        // we don't actually want to change snake, just travel through him
        Node copy = head;

        // we can tell where to place the new node by checking the difference in the locations.
        int oldx = head.x, oldy = head.y;

        // this makes it so we make it to the last node snake has
        while (copy.next != null) {
            oldx = copy.x;
            oldy = copy.y;
            copy = copy.next;
        }

        // the difference in the positions can tell us where to place the next node!
        int diffx = copy.x - oldx;
        int diffy = copy.y - oldy;

        Color color = colors[random.nextInt(colors.length)];

        // now append the node to add at the end of the snake!
        copy.next = new Node(copy.x + diffx, copy.y + diffy, color);
    }
    public boolean didDie() {
        Node copy = head;
        copy = copy.next;
        while (copy != null) {
            if (copy.intersects(head))
                return true;
            copy = copy.next;
        }
        return false;
    }
    public void draw(Graphics2D g) {
        Node copy = head;
        while (copy != null) {
            copy.draw(g);
            copy = copy.next;
        }
    }
    // the regular mod (%) doesn't work so well with negative numbers, so we can fix that with this!
    private int mod(int number) {
        return ((( number % 400) + 400) % 400);
    }

}
