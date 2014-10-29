package GameStates.ArcadeGames.BlackJack;

import GameStates.ArcadeGames.ArcadeGame;
import GameStates.GameStateManager;
import Handlers.Button;
import Handlers.KeyInput;
import Handlers.MouseInput;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

/**
 * A clone of a BlackJack game for the arcade! The player is given two cards, then decides whether or not to receive
 * more, once he decides not to get any more cards or if the sum of his cards is over 21, the game is over. The
 * goal is to get as close to 21 as he can, or at least closer than the dealer. If he gets 21, he wins.
 */
public class BlackJackGame extends ArcadeGame {

    // the result of the game
    private enum EndDecision {
        Win,
        Lost,
        Tie
    }

    private ArrayList<Card> playerHand, computerHand;
    private EndDecision endDecision;
    private BufferedImage bkg, cardSprite;
    private Handlers.Button hit, turnIn;

    public BlackJackGame(GameStateManager manager, KeyInput keys, MouseInput mouse) {
        super(manager, keys, mouse);
        playerHand = new ArrayList<>();
        computerHand = new ArrayList<>();
        endDecision = null;
        try {
            bkg = ImageIO.read(new File("Assets/Sprites/ArcadeGames/BlackJack/blackjackbkg.png"));
            cardSprite = ImageIO.read(new File("Assets/Sprites/ArcadeGames/BlackJack/card.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        hit = new Button(new Rectangle(100,400, 100,30),"Hit me!");
        turnIn = new Button(new Rectangle(250,400, 100,30),"Turn in!");
    }

    @Override
    public void update() {
        switch (state) {
            case GameOver:
            case Menu:
                if (keyInput.isPressed(KeyEvent.VK_ENTER)) {
                    startGame();
                    state = State.Playing;
                }
                if (keyInput.isPressed(KeyEvent.VK_Q))
                    parentManager.deleteCurrentGame();
                break;
            case Playing:
                turnIn.isHovered = mouseInput.isMouseOver(turnIn);
                hit.isHovered = mouseInput.isMouseOver(hit);
                if (mouseInput.didMouseClickOn(hit)) {
                    addRandomCardTo(playerHand);
                    makeComputerDecision();
                    if (getTotalValue(playerHand) > 21) {
                        endDecision = EndDecision.Lost;
                        state = State.GameOver;
                        repositionComputerHand();
                    }
                    else if (getTotalValue(playerHand) == 21) {
                        endDecision = EndDecision.Win;
                        state = State.GameOver;
                        repositionComputerHand();
                    }
                }
                if (mouseInput.didMouseClickOn(turnIn)) {
                    state = State.GameOver;
                    determineWinner();
                    repositionComputerHand();
                }
                break;
            case Pause:
                break;
        }
    }

    @Override
    public void draw(Graphics2D g) {
        g.drawImage(bkg, 0,0, null);
        g.setFont(new Font("Droid Sans", Font.BOLD, 20));
        g.setColor(Color.white);
        switch (state) {
            case Menu:
                g.drawString("Press [ENTER] to play!", 200, 50);
                g.drawString("Press [Q] to quit!", 200, 100);
                break;
            case Playing:

                turnIn.draw(g);
                hit.draw(g);
                playerHand.forEach(hand -> hand.draw(g, cardSprite));
                break;
            case Pause:
                break;
            case GameOver:
                g.drawString("Press [ENTER] to play!", 200, 50);
                g.drawString("Press [Q] to quit!", 200, 100);
                playerHand.forEach(hand -> hand.draw(g, cardSprite));
                computerHand.forEach(hand -> hand.draw(g, cardSprite));
                g.setColor(Color.white);
                switch (endDecision) {
                    case Win:
                        g.drawString("YOU WON!!!", 20, 630);
                        break;
                    case Lost:
                        g.drawString("YOU LOST!!!", 20, 630);
                        break;
                    case Tie:
                        g.drawString("You tied...", 20, 630);
                        break;
                }
                break;
        }
    }

    /**
     * The computer also gets a choice on how he wants to play, to keep things simple, it's just going to be a
     * little stupid AI
     */
    private void makeComputerDecision() {
        int total = getTotalValue(computerHand);
        int remaining = 21 - total;
        Random random = new Random();
        // depending on how much he has left until 21, that is the percentage of chance of whether he gets another card
        // or not. Example: total = 15, remaining = 6, 6 * 10 = 60, 60% chance of picking a card or not
        if (random.nextInt(100) < remaining * 10 && remaining > 0) {
            addRandomCardTo(computerHand);
        }
    }
    /**
     * Gets the score of both players and decides who wins
     */
    private void determineWinner() {
        int playerScore = getTotalValue(playerHand);
        int computerScore = getTotalValue(computerHand);

        if (playerScore == computerScore) {
            endDecision = EndDecision.Tie;
        }
        else if (playerScore == 21 || computerScore > 21) {
            endDecision = EndDecision.Win;
        }
        else if (playerScore > 21 || playerScore < computerScore) {
            endDecision = EndDecision.Lost;
        }
        else if (playerScore > computerScore)
            endDecision = EndDecision.Win;
    }

    /**
     * When finally showing the computer's hand, we want to have it look neat and not overlap
     * the player's hand
     */
    private void repositionComputerHand() {
        int x = 444, y = 40;
        for (Card aComputerHand : computerHand) {
            aComputerHand.x = x;
            aComputerHand.y = y;
            y += 50;
        }
    }

    /**
     * This is called to start off the game. It set's up both the computer's hand and they player's
     */
    private void startGame() {
        playerHand.clear();
        addRandomCardTo(playerHand);
        addRandomCardTo(playerHand);

        computerHand.clear();
        addRandomCardTo(computerHand);
        addRandomCardTo(computerHand);
    }

    private void addRandomCardTo(ArrayList<Card> hand) {
        Card randomCard;
        boolean inPlayerHand = false, inComputerHand = false;

        // h - [h]earts, d - [d]iamond, c - [c]lubs, v - clo[v]er
        char[] suits = {'h', 'd', 'c', 'v'};

        Random random = new Random();
        int x = 50, y = 608 - 130;

        if (hand.size() != 0) {
            // we want this next card to be next to the last card placed
            Card lastCard = hand.get(hand.size() -1);
            x = lastCard.x + 100;
            y = lastCard.y;
        }

        do {
            char suit = suits[random.nextInt(suits.length)];

            // Math checks out so the value can only be between 2 and 11;
            String value = Integer.toString(random.nextInt(10) + 2);

            randomCard = new Card(new Rectangle(x, y, 150, 130), value + suit);

            // go through and check if the card exists in the players hand
            for (Card card : playerHand) {
                if (card.toString().equals(randomCard.toString()))
                    inPlayerHand = true;
            }

            // go through and check if the card exists in the computers hand
            for (Card card : computerHand) {
                if (card.toString().equals(randomCard.toString()))
                    inComputerHand = true;
            }
        } while(inComputerHand && inPlayerHand);
        hand.add(randomCard);
    }


    protected int getTotalValue(ArrayList<Card> hand) {
        int total = 0;
        for (Card card : hand) {
            // Aces can be 1 or 11, so in the case of an Ace causing an overflow as 11, we make it into a 1 for the player.
            if (card.getValue() == 11 && total + 11 > 21)
                total += 1;
            else
                total += card.getValue();
        }
        return total;
    }

}