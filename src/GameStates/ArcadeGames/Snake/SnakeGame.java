package GameStates.ArcadeGames.Snake;

import Entity.Entity;
import GameStates.ArcadeGames.ArcadeGame;
import GameStates.GameStateManager;
import Handlers.KeyInput;
import Handlers.MouseInput;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Random;

/**
    This is one of the games the player can play in the arcade room! The classic snake game!
    The player is this "snake" that starts of with 3 "parts" and eats food to gain another part. When the
    player touches himself, he loses!
 */
public class SnakeGame extends ArcadeGame {
    private int score, delay;
    private Snake snake;
    private Point food;

    // width and height of the snake game screen
    private final int WIDTH = 400, HEIGHT = 400;

    // cell_size = size of each "node",
    private final int CELL_SIZE = 10;

    private Random random = new Random();

    public SnakeGame(GameStateManager manager, KeyInput keys, MouseInput mouse) {
        super(manager, keys, mouse);
        keyInput = keys;
        parentManager = manager;
        soundManager.addSound("menu_music", "Music/arcade_menu.wav");
        soundManager.addSound("snake_music", "Music/snake_theme.wav");
        soundManager.playSound("menu_music", true);
        food = new Point(random.nextInt(WIDTH - CELL_SIZE), random.nextInt(HEIGHT - CELL_SIZE));
        snake = new Snake();
    }

    @Override
    public void update() {
        // since it's a small game, we don't need to make a class for each state, just run different logic
        switch (state) {
            case Menu:
                if (keyInput.isPressed(KeyEvent.VK_SPACE)) {
                    soundManager.stopCurrentSound();
                    soundManager.playSound("snake_music");
                    state = State.Playing;
                } else if (keyInput.isPressed(KeyEvent.VK_Q))
                    parentManager.deleteCurrentGame();
                break;
            case Playing:
                if (keyInput.isPressed(KeyEvent.VK_ENTER)) {
                    state = State.Pause;
                } else if (keyInput.isPressed(KeyEvent.VK_W) || keyInput.isPressed(KeyEvent.VK_UP)) {
                    if (snake.direction != Entity.Direction.Down)
                        snake.direction = Entity.Direction.Up;
                } else if (keyInput.isPressed(KeyEvent.VK_S) || keyInput.isPressed(KeyEvent.VK_DOWN)) {
                    if (snake.direction != Entity.Direction.Up)
                        snake.direction = Entity.Direction.Down;
                } else if (keyInput.isPressed(KeyEvent.VK_D) || keyInput.isPressed(KeyEvent.VK_RIGHT)) {
                    if (snake.direction != Entity.Direction.Left)
                        snake.direction = Entity.Direction.Right;
                } else if (keyInput.isPressed(KeyEvent.VK_A) || keyInput.isPressed(KeyEvent.VK_LEFT)) {
                    if (snake.direction != Entity.Direction.Right)
                        snake.direction = Entity.Direction.Left;
                }


                // we have this delay so we can have that "choppy" effect
                if (delay <= 0) {
                    snake.update();
                    delay = 5;
                } else
                    delay--;
                if (snake.didDie()) {
                    soundManager.stopCurrentSound();
                    soundManager.playSound("menu_music");
                    state = State.GameOver;
                }
                checkSnakeEating();
                break;
            case GameOver:
                if (keyInput.isPressed(KeyEvent.VK_SPACE)) {
                    snake.resetSnake();
                    soundManager.stopCurrentSound();
                    soundManager.playSound("snake_music");
                    score = 0;
                    state = State.Playing;
                } else if (keyInput.isPressed(KeyEvent.VK_Q))
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
        g.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
        g.setColor(new Color(50, 70, 160));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        g.setFont(new Font(ARCADE_FONT, Font.BOLD, 15));
        g.setColor(Color.white);
        g.drawString("SNAKE", 450, 50);
        g.drawString("Press [ENTER] to pause/resume", 50, 440);
        g.drawString("Score: " + score, 430, 200);

        switch (state) {
            case Menu:
                g.drawString("Press [SPACE] to play! Press [Q] to quit :(", 10, 150);
                g.drawString("You can move with Arrow Keys or WSAD!", 20, 200);
                break;
            case Playing:
                snake.draw(g);
                g.setColor(Color.white);
                g.fillRect(food.x, food.y, CELL_SIZE, CELL_SIZE);
                break;
            case GameOver:
                g.drawString("GAME OVER", 200, 200);
                g.drawString("Press [SPACE] to play again! Press [Q] to quit :(", 10, 300);
                break;
            case Pause:
                g.drawString("Press [Q] to quit", 120, 250);
                g.drawString("| |", 160, 200);
                break;
        }
    }

    private void checkSnakeEating() {
        if (snake.intersects(new Rectangle(food.x, food.y, CELL_SIZE, CELL_SIZE))) {
            food.x = random.nextInt(WIDTH - CELL_SIZE);
            food.y = random.nextInt(HEIGHT - CELL_SIZE);
            score += 10;
            snake.addNode();
        }
    }
}

