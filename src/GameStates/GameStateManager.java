package GameStates;

import java.awt.*;
import java.util.Stack;

/**
 * This manages the Game States we will deal with. You can think of GameStates as "screens"
 * such as menu, game over, etc. This will keep track of which "screen" we are currently are in
 * and switch between screens.
 */
public class GameStateManager {
    public Stack<GameState> gameStates;
    public GameStateManager() {

        gameStates = new Stack<GameState>();
    }
    public void update() {
        gameStates.peek().update();
    }
    public void draw(Graphics2D g) {
        gameStates.peek().draw(g);
    }
    /**
        This will push the game to the stack, making it the current screen. The previous screen will be
        in sort of a "pause" while this screen will be displayed. This will be useful for "pause" screens.
     */
    public void addGame(GameState game) {
        if (!gameStates.empty()) {
            gameStates.peek().leave();
        }
        game.enter();
        gameStates.push(game);
    }
    /**
        In contrast to the addGame(), this will completely throw away the previous screen and set the
        passed in game as the current screen.
     */
    public void setGame(GameState game) {
        if (!gameStates.empty()) {
            gameStates.peek().leave();
            gameStates.pop();
        }
        game.enter();
        gameStates.push(game);
    }
    /**
        This will remove the current game from the stack, meaning that the screen will no longer
        be displayed and will fall back to the next game in the stack. This would be useful for when
        the player resumes the game from the pause menu.
     */
    public void deleteCurrentGame() {
        if (!gameStates.empty()) {
            gameStates.peek().leave();
            gameStates.pop();
        }

        if (!gameStates.empty())
            gameStates.peek().enter();
    }
}
