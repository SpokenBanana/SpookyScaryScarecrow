package GameStates.ArcadeGames.Pong;

import GameStates.ArcadeGames.ArcadeGame;
import GameStates.GameStateManager;
import Handlers.KeyInput;
import Handlers.MouseInput;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Random;

/**
 * This is a clone of the classic Pong game that the player can play in the arcade
 */
public class PongGame extends ArcadeGame {
    private Rectangle playerPaddle, computerPaddle, ball;
    private int playerScore, computerScore, ballxVel, ballyVel, computerAIDelay;
    private Random random;

    // keep track if someone has scored, if so then we pause the ball until the player presses something
    private boolean gameRestart;

    public PongGame(GameStateManager manager, KeyInput keys, MouseInput mouse) {
        super(manager, keys, mouse);
        playerPaddle = new Rectangle(50, 250, 10,100);
        computerPaddle = new Rectangle(508, 250, 10,100);
        soundManager.addSound("pong_music", "Music/pong_theme.wav");
        soundManager.addSound("menu_music", "Music/arcade_menu.wav");
        soundManager.playSound("menu_music", true);

        ballyVel = 4;
        ballxVel = 4;
        random = new Random();
        gameRestart = true;

        // the delay is here so the AI, isn't unbeatable, he needs time to react like a human
        computerAIDelay = 30;

        ball = new Rectangle(GAME_WIDTH / 2, GAME_HEIGHT / 2, 10,10);
    }
    @Override
    public void update() {
        switch (state) {
            case Pause:
                if (keyInput.isPressed(KeyEvent.VK_SPACE) || keyInput.isPressed(KeyEvent.VK_ENTER)){
                    state = State.Playing;
                }
                if (keyInput.isPressed(KeyEvent.VK_Q))
                    parentManager.deleteCurrentGame();
                break;
            case GameOver:
            case Menu:
                if (keyInput.isPressed(KeyEvent.VK_SPACE) || keyInput.isPressed(KeyEvent.VK_ENTER)){
                    state = State.Playing;
                    computerScore = playerScore = 0;
                    soundManager.stopCurrentSound();
                    soundManager.playSound("pong_music", true);
                }
                if (keyInput.isPressed(KeyEvent.VK_Q))
                    parentManager.deleteCurrentGame();
                break;
            case Playing:
                if (keyInput.isHeld(KeyEvent.VK_S) || keyInput.isHeld(KeyEvent.VK_DOWN)) {
                    // we don't the paddle to go out of bounds so we move the player back if he tries
                    playerPaddle.y += (playerPaddle.y <  GAME_HEIGHT - playerPaddle.height) ? 5 : -5;
                    gameRestart = false;
                }
                else if (keyInput.isHeld(KeyEvent.VK_W) || keyInput.isHeld(KeyEvent.VK_UP)) {
                    playerPaddle.y += (playerPaddle.y > 0) ? -5 : 5;
                    gameRestart = false;
                }
                else if (keyInput.isPressed(KeyEvent.VK_ENTER))
                    state = State.Pause;

                // don't move the ball unless the player decides to start the game
                if (!gameRestart)
                    updateBall();

                checkBallCollision();
                checkIfBallOutOfBounds();
                updateComputerAI();

                if (playerScore == 5 || computerScore == 5){
                    soundManager.stopCurrentSound();
                    soundManager.playSound("menu_music", true);
                    state = State.GameOver;
                }

                break;
        }
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(new Color(20,20,40));
        g.fillRect(0,0, GAME_WIDTH, GAME_HEIGHT);
        g.setFont(new Font("Sans Serif", Font.BOLD, 25));
        g.setColor(Color.white);
        switch (state) {
            case Menu:
                g.drawString("Pong!", 100, 200);
                g.drawString("Press [SPACE] to play! ([Q] to quit)", 20, 300);
                g.drawString("Use up and down arrows to move!", 50, 400);
                g.drawString("First one to 5 points wins!", 50, 500);
                break;
            case Playing:
                g.drawString("[ENTER] to pause", 300, 600);
                g.drawString("Your Score: " + playerScore, 50,50);
                g.drawString("Opponent Score: " + computerScore, 300,50);
                g.fill(playerPaddle);
                g.fill(computerPaddle);
                g.fillOval(ball.x, ball.y, ball.width, ball.height);
                break;
            case Pause:
                g.drawString("| |", 300, 300);
                g.drawString("Press [ENTER] to resume", 250, 350);
                break;
            case GameOver:
                g.drawString("Press [SPACE] to play again! ([Q] to quit)", 20, 300);
                break;
        }
    }


    /**
        pretty much just moves the ball
     */
    protected void updateBall() {
        ball.x += ballxVel;
        ball.y += ballyVel;
    }

    /**
        Checks if the ball has collided with anything in the game
     */
    protected void checkBallCollision() {
        // make him bounce around the edges on top and bottom of screen
        if (ball.y < 0 || ball.y > GAME_HEIGHT - ball.height) {
            ballyVel *= - 1;
        }
        checkIfBallHit(playerPaddle);
        checkIfBallHit(computerPaddle);
    }
    /**
        Check if the ball collided with the rectangle area provided. If it has, it moves the ball in
         the other direction, and the AI will have to react to that, giving a delay in it's AI.
     */
    protected void checkIfBallHit(Rectangle entity) {
        if (entity.intersects(ball)){
            ballxVel *= -1;

            // we don't want his reaction time to be predictable so we make the time
            // randomly assigned with a number between 40 and 60
            computerAIDelay = random.nextInt(20) + 40;

            // if the paddle hits the ball vertically, it makes sense that it would send the ball in the
            // opposite y direction
            if (ball.y < entity.y || ball.y > entity.y + entity.height)
                ballyVel *= -1;

            // there was this glitch of the ball being caught inside the paddle, this makes it so if the ball hits the
            // paddle, he get moved to where he can't get hit by hit right after again
            if (ball.x > 300)
                ball.x = computerPaddle.x - ball.width;
            else
                ball.x = playerPaddle.x + ball.width;
            updateBall();
        }
    }
   /**
        checks if the ball reached outside the screen in the x-position. If so that means someone scored and we
        have to move the ball to the center
    */
    protected void checkIfBallOutOfBounds() {
        if (ball.x < 0) {
            computerScore++;
            resetGame();
        }
        else if (ball.x > GAME_WIDTH - ball.width){
            playerScore++;
            resetGame();
        }
    }
    /**
        The computer's logic will handled her. Basically, if the ball is higher than the middle of the paddle,
        it moves up, if it is lower, then it moves down.
     */
    protected void updateComputerAI() {
        if (computerAIDelay <= 0) {
            // adding half the paddle's height to its y value gives us it's center
            if (ball.y < computerPaddle.y + (computerPaddle.height / 2)) {
                computerPaddle.y += (computerPaddle.y > 0) ? -5 : 5;
            }
            else if (ball.y > computerPaddle.y + (computerPaddle.height / 2)) {
                computerPaddle.y += (computerPaddle.y <  GAME_HEIGHT - computerPaddle.height) ? 5 : -5;
            }
        }
        else
            computerAIDelay--;
    }
    /**
        We don't want the player to be able to predict which way the ball will move at the beginning of the
        round, so we use this to sort of randomize where it will go
     */
    protected void randomizeBallMovement() {
        if (random.nextInt(100) % 2 == 0)
            ballxVel *= -1;
        if (random.nextInt(100) % 2 == 0)
            ballyVel *= -1;
    }
    /**
        sets up the game for a fair start. Paddles are centered vertically and the ball is completely
        centered.
     */
    protected void resetGame() {
        playerPaddle.x = 50;
        computerPaddle.x = 508;
        computerPaddle.y = playerPaddle.y = 250;
        ball.x = ball.y = 300;
        randomizeBallMovement();
        gameRestart = true;

    }
}
