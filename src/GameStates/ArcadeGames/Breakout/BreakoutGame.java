package GameStates.ArcadeGames.Breakout;

import GameStates.ArcadeGames.ArcadeGame;
import GameStates.GameStateManager;
import Handlers.KeyInput;
import Handlers.MouseInput;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

/*
    This arcade game is called Breakout! The goal is to break all the bricks in the game without
 */
public class BreakoutGame extends ArcadeGame {

    // we want bricks to act just like a rectangle except it has a color;
    private class Brick extends Rectangle {
        public Color color;
        public Brick(Rectangle bounds, Color c) {
            super(bounds);
            color = c;
        }
        public void draw(Graphics2D g) {
            g.setColor(color);
            g.fillRect(x, y, width, height);
        }
    }

    // a color for each row!
    private Color[] rowColors = {Color.red, Color.white, Color.orange, Color.cyan, Color.green, Color.pink,
                                 Color.yellow};

    private final short BRICK_WIDTH = 55, BRICK_HEIGHT = 25, BRICK_SPACING = 5;
    private boolean gameRestart;
    private Rectangle player, ball;
    private int ballxVel, ballyVel, score, lives;
    private ArrayList<Brick> bricks;

    public BreakoutGame(GameStateManager manager, KeyInput keys, MouseInput mouse) {
        super(manager, keys, mouse);
        initializeBricks();
        ball = new Rectangle(300,300,15,15);
        ballxVel = ballyVel = 4;
        player = new Rectangle(300,550,100,20);
        resetGame();
        soundManager.addSound("menu_music", "Music/arcade_menu.wav");
        soundManager.addSound("music", "Music/breakout_theme.wav");
        soundManager.playSound("menu_music", true);
    }

    @Override
    public void update() {
        switch (state) {
            case GameOver:
            case Menu:
                if (keyInput.isPressed(KeyEvent.VK_ENTER)){
                    state = State.Playing;
                    soundManager.stopCurrentSound();
                    soundManager.playSound("music");
                    resetGame();
                }
                else if (keyInput.isPressed(KeyEvent.VK_Q))
                    parentManager.deleteCurrentGame();
                break;
            case Playing:
                if (keyInput.isPressed(KeyEvent.VK_ENTER))
                    state = State.Pause;
                if (mouseInput.didMouseClickOn(new Rectangle(0,0, GAME_WIDTH, GAME_HEIGHT)))
                    gameRestart = false;

                // make the paddle follow the mouse
                player.x = (int) mouseInput.getMouseLocation().getX() - (player.width / 2);

                // if the ball got repositioned, wait until the player clicks to move again
                if (!gameRestart)
                    moveBall();

                checkBallCollision();
                checkBallAndBrickCollision();

                // ball went past the player, got to place the ball back in the middle
                if (ball.y > GAME_HEIGHT)
                    repositionGame();

                // either the player lost or he won, either one, the game ends
                if (lives == 0 || bricks.size() == 0) {
                    soundManager.stopCurrentSound();
                    soundManager.playSound("menu_music", true);
                    state = State.GameOver;
                }
                break;
            case Pause:
                if (keyInput.isPressed(KeyEvent.VK_ENTER))
                    state = State.Playing;
                else if (keyInput.isPressed(KeyEvent.VK_Q))
                    parentManager.deleteCurrentGame();
                break;
        }
    }

    @Override
    public void draw(Graphics2D g) {
        g.setFont(new Font(ARCADE_FONT, Font.BOLD, 15));
        g.setColor(new Color(20, 50, 100));
        g.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
        g.setColor(Color.white);
        g.drawString("Breakout", 620,50);
        g.drawString("Press [ENTER] to pause/resume", 50, 630);
        switch (state) {
            case Playing:
                // easy to read iterating through the collection and calling the update method on each "brick".
                bricks.forEach(bricks -> bricks.draw(g));
                g.setColor(Color.white);

                if (gameRestart)
                    g.drawString("Click to begin!", 300, 350);

                g.fill(player);
                g.fillOval(ball.x, ball.y, ball.width, ball.height);

                g.drawString("Score: " + score, 500, 630);
                g.drawString("Lives: " + lives, 100, 700);
                break;
            case Menu:
                g.drawString("Press [ENTER] to play!", 100, 100);
                g.drawString("Press [Q] to quit!", 100, 150);
                break;
            case GameOver:
                g.drawString("Press [ENTER] to play again!", 100, 100);
                g.drawString("Press [Q] to quit!", 100, 150);
                break;
            case Pause:
                g.drawString("| |", 300, 300);
                g.drawString("Press [Q] to quit", 250, 400);
                break;
        }
    }

    /**
     * This will start off the bricks in the position in sort of a board formation and give the bricks
     * colors for their rows
     */
    private void initializeBricks() {
        // the amount of colors we have will be amount of rows we have
        bricks = new ArrayList<Brick>();
        int x = BRICK_SPACING, y = BRICK_SPACING;
        for (Color rowColor : rowColors) {
            for (int j = 0; j < 10; j++) {
                bricks.add(new Brick(new Rectangle(x, y, BRICK_WIDTH, BRICK_HEIGHT), rowColor));
                x += BRICK_SPACING + BRICK_WIDTH;
            }
            x = BRICK_SPACING;
            y += BRICK_SPACING + BRICK_HEIGHT;
        }
    }

    /**
     * Will check if the ball has hit anything, if it has, then we have to change his
     * velocity appropriately.
     */
    private void checkBallCollision() {
        // check if the ball has hit the paddle or reached out of the screen
        if (player.intersects(ball)) {
            ballyVel *= -1;
            ball.y = player.y - ball.height;
            if (ball.x < player.x || ball.x > player.x + player.width - 4)
                ballxVel *= -1;
            moveBall();
        }
        // ball hit top of screen
        else if (ball.y < 0) {
            ballyVel *= -1;
            moveBall();
        }
        // ball hit left or right edge
        else if (ball.x < 0 || ball.x > GAME_WIDTH - ball.width) {
            ballxVel *= -1;
            moveBall();
            moveBall();
        }
    }

    /**
     * checks if the ball has hit any brick and removes any brick that has been hit
     */
    private void checkBallAndBrickCollision() {
        // save how many we had before
        int sizeBefore = bricks.size();

        // removes any bricks that has hit the ball
        bricks.removeIf((brick) -> {
            if (brick.intersects(ball)) {
                ballyVel *= -1;

                // if the ball hit it horizontally, we have to make it bounce of it horizontally
                if (ball.x < brick.x || ball.x > brick.x + BRICK_WIDTH)
                    ballxVel *= -1;
                // return true because it was hit so yes, we want to remove this
                return true;
            }
            return false;
        });

        // each brick hit is 100 points, so add to the score the amount we hit times 100
        score += (sizeBefore - bricks.size()) * 100;
    }

    /**
     * Resets the game. Sets up the bricks, resets score, and repositions ball
     */
    private void resetGame() {
        score = 0;
        lives = 3;
        initializeBricks();
        ball.setLocation(300,300);
        gameRestart = true;
    }
    /**
     * This should be called when the ball exits through the bottom of the game. It will restart the game giving the
     * player a chance to prepare.
     */
    private void repositionGame() {
        ball.setLocation(300,300);
        lives--;
        gameRestart = true;
    }

    /**
     * This moves the ball around the space
     */
    private void moveBall() {
        ball.x += ballxVel;
        ball.y += ballyVel;
    }
}