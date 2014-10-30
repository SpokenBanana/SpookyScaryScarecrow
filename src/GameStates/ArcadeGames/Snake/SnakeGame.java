package GameStates.ArcadeGames.Snake;

import Entity.Entity;
import GameStates.ArcadeGames.ArcadeGame;
import GameStates.GameStateManager;
import Handlers.KeyInput;
import Handlers.MouseInput;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

/*
    This is one of the games the player can play in the arcade room! The classic snake game!
    The player is this "snake" that starts of with 3 "parts" and eats food to gain another part. When the
    player touches himself, he loses!
 */
public class SnakeGame extends ArcadeGame {

    // not much to this class so let's just make it an inner class
    private class Node {
        Rectangle data;
        // we'll make it a little funner and have each snake part a different color
        Color color;
        Node next;
        public Node(Point location, Color c) {
            data = new Rectangle(location.x, location.y, CELL_SIZE, CELL_SIZE);
            color = c;
            next = null;
        }
    }
    private int score, delay;
    private Node snake;
    private Point food;
    private BufferedImage nodeSprite;

    // width and height of the snake game screen
    private final int WIDTH = 400, HEIGHT = 400;
    // cell_size = size of each "node",
    final int CELL_SIZE = 10;

    Entity.Direction snakeDirection;

    // colors to choose from, we random pick a number between 0 and the size of this array, and use the color at that
    // index. We want to randomly color the nodes
    final Color[] colors = {Color.red, Color.pink, Color.yellow, Color.cyan, Color.green, Color.ORANGE};
    Random random = new Random();

    public SnakeGame(GameStateManager manager, KeyInput keys, MouseInput mouse) {
        super(manager, keys, mouse);
        keyInput = keys;
        parentManager = manager;
        soundManager.addSound("menu_music", "Music/arcade_menu.wav");
        soundManager.addSound("snake_music", "Music/snake_theme.wav");
        soundManager.playSound("menu_music", true);
        food = new Point(random.nextInt(WIDTH - CELL_SIZE),random.nextInt(HEIGHT - CELL_SIZE));
        setUpSnake();
        try {
            nodeSprite = ImageIO.read(new File("Assets/universal.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void setUpSnake() {
        snake = new Node(new Point(60, 60), Color.white);
        snake.next = new Node(new Point(snake.data.x - CELL_SIZE, snake.data.y), Color.green);
        snakeDirection = Entity.Direction.Right;
        for (int i = 0; i < 3; i++) {
            addNodeToSnake(colors[random.nextInt(colors.length)]);
        }
    }
    private void addNodeToSnake(Color color) {
        // we don't actually want to change snake, just travel through him
        Node copy = snake;

        // we can tell where to place the new node by checking the difference in
        // the locations.
        int oldx = snake.data.x, oldy = snake.data.y;

        // this makes it so we make it to the last node snake has
        while (copy.next != null) {
            oldx = copy.data.x;
            oldy = copy.data.y;
            copy = copy.next;
        }

        // the difference in the positions can tell us where to place the next node!
        int diffx = copy.data.x - oldx;
        int diffy = copy.data.y - oldy;
        Node toAdd = new Node(new Point(copy.data.x + diffx, copy.data.y + diffy), color);

        // now append the node to add at the end of the snake!
        copy.next = toAdd;
    }
        // we are using the head to check for collision, so when traveling through it, we skip it
    private boolean checkIfSnakeDied() {
        Node copy = snake;
        copy = copy.next;
        while (copy != null) {
            if (copy.data.intersects(snake.data))
                return true;
            copy = copy.next;
        }
        return false;
    }

    // the regualar mod (%) doesn't work so well with negative numbers, so we can fix that with this!
    private int mod(int number) {
        return ((( number % WIDTH) + WIDTH) % WIDTH);
    }
    /*
        We move the snake in a certain direction, diffX and diffY means difference in their respective
        coordinates, we add it the current coordinates of snake, and then move the rest of snake in the
        "snake" like pattern
     */
    private void moveSnake(int diffX, int diffY) {
        int oldx = snake.data.x, oldy = snake.data.y;

        // we mod it so in case the player goes off the screen, he just appears on the other side!
        int newx = mod(snake.data.x + diffX), newy = mod(snake.data.y + diffY);

        Node copy = snake;
        do {
            // move the current node to desired position
            copy.data.x = newx;
            copy.data.y = newy;

            // the next one will now take the location the current on was previously at
            newx = oldx;
            newy = oldy;

            // now the one after the next will know what space to take up
            oldx = copy.next.data.x;
            oldy = copy.next.data.y;

            // on to the next one!
            copy = copy.next;
        } while (copy.next != null);

        // we left one behind, so catch him up!
        copy.data.x = newx;
        copy.data.y = newy;
    }
    private void updateSnake() {
        switch (snakeDirection) {
            case Down:
                moveSnake(0, 10);
                break;
            case Up:
                moveSnake(0, -10);
                break;
            case Left:
                moveSnake(-10, 0);
                break;
            case Right:
                moveSnake(10, 0);
                break;
        }
    }
    protected void drawSnake(Graphics2D g) {
        Node copy = snake;
        while (copy != null) {
            g.drawImage(nodeSprite, copy.data.x, copy.data.y, CELL_SIZE, CELL_SIZE, copy.color, null);
            copy = copy.next;
        }
    }
    private void checkIfSnakeAteFood() {
        if (snake.data.intersects(new Rectangle(food.x, food.y, CELL_SIZE, CELL_SIZE))) {
            food.x = random.nextInt(WIDTH - CELL_SIZE);
            food.y = random.nextInt(HEIGHT - CELL_SIZE);
            addNodeToSnake(colors[random.nextInt(colors.length)]);
            score += 100;
        }
    }

    @Override
    public void update() {
        // since it's a small game, we don't need to make a class for each state, just run different logic
        switch (state) {
            case Menu:
                if (keyInput.isPressed(KeyEvent.VK_SPACE)){
                    soundManager.stopCurrentSound();
                    soundManager.playSound("snake_music");
                    state = State.Playing;
                }
                else if (keyInput.isPressed(KeyEvent.VK_Q))
                    parentManager.deleteCurrentGame();
                break;
            case Playing:
                if (keyInput.isPressed(KeyEvent.VK_ENTER)) {
                    state = State.Pause;
                }
                else if (keyInput.isPressed(KeyEvent.VK_W) || keyInput.isPressed(KeyEvent.VK_UP)) {
                    if (snakeDirection != Entity.Direction.Down)
                        snakeDirection = Entity.Direction.Up;
                }
                else if (keyInput.isPressed(KeyEvent.VK_S) || keyInput.isPressed(KeyEvent.VK_DOWN)) {
                    if (snakeDirection != Entity.Direction.Up)
                        snakeDirection = Entity.Direction.Down;
                }
                else if (keyInput.isPressed(KeyEvent.VK_D) || keyInput.isPressed(KeyEvent.VK_RIGHT)) {
                    if (snakeDirection != Entity.Direction.Left)
                        snakeDirection = Entity.Direction.Right;
                }
                else if (keyInput.isPressed(KeyEvent.VK_A) || keyInput.isPressed(KeyEvent.VK_LEFT)) {
                    if (snakeDirection != Entity.Direction.Right)
                        snakeDirection = Entity.Direction.Left;
                }


                // we have this delay so we can have that "chopy" effect
                if (delay <= 0) {
                    updateSnake();
                    delay = 5;
                }
                else
                    delay--;
                if (checkIfSnakeDied()){
                    soundManager.stopCurrentSound();
                    soundManager.playSound("menu_music");
                    state = State.GameOver;
                }
                checkIfSnakeAteFood();
                break;
            case GameOver:
                if (keyInput.isPressed(KeyEvent.VK_SPACE)){
                    setUpSnake();
                    soundManager.stopCurrentSound();
                    soundManager.playSound("snake_music");
                    score = 0;
                    state = State.Playing;
                }
                else if (keyInput.isPressed(KeyEvent.VK_Q))
                    parentManager.deleteCurrentGame();
                break;
            case Pause:
                if (keyInput.isPressed(KeyEvent.VK_Q))
                    parentManager.deleteCurrentGame();
                else if (keyInput.isPressed(KeyEvent.VK_ENTER))
                    state = State.Playing;
                break;
        }
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(new Color(9));
        g.fillRect(0,0, GAME_WIDTH, GAME_HEIGHT);
        g.setColor(new Color(50,70,160));
        g.fillRect(0,0, WIDTH, HEIGHT);

        g.setFont(new Font("Sans Serif", Font.BOLD, 15));
        g.setColor(Color.white);
        g.drawString("SNAKE", 450,50);
        g.drawString("Press [ENTER] to pause/resume", 50, 440);
        g.drawString("Score: " + score, 430,200);

        switch (state) {
            case Menu:
                g.drawString("Press [SPACE] to play! Press [Q] to quit :(", 10, 150);
                g.drawString("You can move with Arrow Keys or WSAD!", 20,200);
                break;
            case Playing:
                drawSnake(g);
                g.drawImage(nodeSprite, food.x, food.y, CELL_SIZE, CELL_SIZE, Color.white, null);
                break;
            case GameOver:
                g.drawString("GAME OVER", 200,200);
                g.drawString("Press [SPACE] to play again! Press [Q] to quit :(", 10,300);
                break;
            case Pause:
                g.drawString("Press [Q] to quit", 120,250);
                g.drawString("| |", 160,200);
                break;
        }
    }
}
