package GameStates.ArcadeGames.MouseFritz;

import GameStates.ArcadeGames.ArcadeGame;
import GameStates.GameStateManager;
import Handlers.KeyInput;
import Handlers.MouseInput;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

/**
 * This is a game inspired by a small game a developer named Jonathon Blow created (at least I think he did).
 * It is a direct clone, since he only showed a demo, so it's as close as I thought the game was.
 *
 * The goal is to use your mouse to match up balls with the same color as much as you can. At set intervals, the balls
 * will "go on a frenzy" and all will be dangerous to touch, doing so reduces your score. If you match up two balls of
 * different colors, that is also a penalty. The player is to round up as much points as he can.
 */
public class MouseFritzGame extends ArcadeGame{

    private ArrayList<Ball> balls;
    private Cursor player;
    private int score, delay, timeUntilFritz, scorePenaltyDelay, rounds;

    public MouseFritzGame(GameStateManager manager, KeyInput keys, MouseInput mouse) {
        super(manager, keys, mouse);
        delay = score = 0;
        // there will be 10 rounds and then the game ends
        rounds = 10;
        player = new Cursor(new Rectangle(300,300,20,20));
        initializeBalls();
        timeUntilFritz = 200;
        scorePenaltyDelay = 100;
        // there are two states, when the balls are on the fritz, and when they are not. We keep track of that with this
    }

    @Override
    public void update() {
        switch (state) {
            case GameOver:
            case Menu:
                if (mouseInput.didMouseClickOn(new Rectangle(0,0, GAME_WIDTH, GAME_HEIGHT))){
                    score = 0;
                    rounds = 10;
                    state = State.Playing;
                }
                if (keyInput.isPressed(KeyEvent.VK_Q))
                    parentManager.deleteCurrentGame();

                break;
            case Playing:
                if (keyInput.isPressed(KeyEvent.VK_ENTER))
                    state = State.Pause;

                player.move(mouseInput.getMouseLocation());
                if (player.x > GAME_WIDTH || player.y > GAME_HEIGHT) {
                    // there's a delay so the score doesn't go down insanely fast
                    if (scorePenaltyDelay <= 0) {
                        scorePenaltyDelay = 40;
                        score--;
                    }
                    else
                        scorePenaltyDelay--;
                }
                balls.forEach(ball -> ball.update(GAME_WIDTH, GAME_HEIGHT));

                if (--timeUntilFritz <= 0) {
                    if (timeUntilFritz == 0)
                        balls.forEach(ball -> ball.onTheFritz = true);
                    // after a certain amount of time in this state, we move to the other
                    if (timeUntilFritz <= -400){
                        balls.forEach(ball -> ball.onTheFritz = false);
                        rounds--;
                        if (rounds == 0)
                            state = State.GameOver;
                        timeUntilFritz = 400;
                    }

                    if (delay <= 0) {
                        if (balls.stream().anyMatch(brick -> brick.intersects(player))) {
                            score--;
                            delay = 20;
                        }
                    }
                    else
                        delay--;
                }
                else {
                    if (delay <= 0)
                        checkCursorAndBallCollision();
                    else
                        delay--;
                }
                break;
            case Pause:
                if (keyInput.isPressed(KeyEvent.VK_ENTER))
                    state = State.Playing;
                if (keyInput.isPressed(KeyEvent.VK_Q))
                    parentManager.deleteCurrentGame();
                break;
        }

    }

    @Override
    public void draw(Graphics2D g) {
        g.setFont(new Font("Droid Sans", Font.BOLD, 20));
        g.setColor(new Color(20,90,140));
        g.fillRect(0,0, GAME_WIDTH, GAME_HEIGHT);
        g.setColor(Color.white);
        g.drawString("Mouse Fritz!", 555,700);
        g.drawString("Press [ENTER] to pause/resume", 50, 630);
        switch (state) {
            case Menu:
                g.drawString("Click to play!", 200,30);
                g.drawString("Press [Q] to quit", 250,60);

                g.drawString("With your mouse, try to match any two balls of the same color", 5, 100);
                g.drawString("Do so by touching one ball, then another of the same color!", 10, 130);
                g.drawString("Careful! Do not touch any balls when they all go red!", 10, 160);
                break;
            case Playing:
                if (timeUntilFritz <= 0)
                    g.drawString("ON THE FRITZ!! DON'T TOUCH THE BALLS", 100, 50);
                else if (timeUntilFritz <= 50)
                    g.drawString("ALMOST TIME!!!", 100, 50);
                if (player.x > GAME_WIDTH || player.y > GAME_HEIGHT) {
                    g.drawString("WHAT ARE YOU DOING OUT HERE!?", 10, 670);
                    g.drawString("GET IN THAT GAME OR I WILL KILL YOUR SCORE!", 10, 690);
                }
                g.drawString("Score: " + score, 400, 630);
                g.drawString("Rounds: " + rounds, 400, 660);
                balls.forEach(ball -> ball.draw(g));

                player.draw(g);
                break;
            case Pause:
                g.drawString("| |", 250,250);
                g.drawString("Press [Q] to quit", 200,300);
                break;
            case GameOver:
                g.drawString("GAME OVER", 200, 100);
                g.drawString("Click to play again!", 200,300);
                g.drawString("Press [Q] to quit", 250,250);
                g.drawString("Score was " + score, 200, 350);
                break;
        }
    }

    /**
     * This sets up the balls in the game
     */
    protected void initializeBalls() {
        Color[] colors = {Color.red, Color.blue, Color.yellow, Color.green, Color.orange, Color.MAGENTA, Color.pink};
        Random random = new Random();
        balls = new ArrayList<>();
        for (int i = 0; i < colors.length; i++) {
            // two of each color just to make sure there is always a pair
            balls.add(new Ball(new Rectangle(random.nextInt(GAME_WIDTH - 50), random.nextInt(GAME_HEIGHT - 50), 40,40), colors[i]));
            balls.add(new Ball(new Rectangle(random.nextInt(GAME_WIDTH - 50), random.nextInt(GAME_HEIGHT - 50), 40,40), colors[i]));
        }
    }

    /**
     * Checks if the player has hit any balls. If it has, it checks if the player is following the rules of the game,
     * that is, matching the balls with the same color.
     */
    protected void checkCursorAndBallCollision() {
        for (Ball ball : balls) {
            // player hit a ball
            if (ball.intersects(player)) {
                // check if he is trying to match a ball or not
                if (player.ballChosen == null) {
                    // first ball hit, it's the color he has to match now
                    player.ballChosen = ball;
                    ball.selected = true;
                }
                else {
                    // already has a ball he has to match, see if he matched the right ball
                    if (player.ballChosen.color == ball.color && player.ballChosen != ball) {
                        // he did! reward with a point
                        score += 10;
                        player.ballChosen.selected = false;
                        player.ballChosen = null;
                        delay = 40;
                    }
                    else if (player.ballChosen.color != ball.color && player.ballChosen != ball){
                        // he failed, penalize score and reset his picks
                        score--;
                        player.ballChosen.selected = false;
                        player.ballChosen = null;

                        // without this delay, the ball he last touch immediately becomes his chosen ball, which
                        // would be annoying.
                        delay = 40;
                    }
                }
            }
        }
    }
}