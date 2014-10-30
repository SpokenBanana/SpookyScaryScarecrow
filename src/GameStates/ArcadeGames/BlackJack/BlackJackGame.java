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
    private int playerScore, computerScore;

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
                if (keyInput.isPressed(KeyEvent.VK_ENTER))
                    state = State.Pause;
                turnIn.isHovered = mouseInput.isMouseOver(turnIn);
                hit.isHovered = mouseInput.isMouseOver(hit);

                if (mouseInput.didMouseClickOn(hit)) {
                    // deal cards
                    addRandomCardTo(playerHand);
                    makeComputerDecision();

                    // check if the player has won or lost already
                    if (getTotalValue(playerHand) > 21) {
                        endDecision = EndDecision.Lost;
                        state = State.GameOver;
                        repositionComputerHand();
                    }
                    // if the player gets 21, he wins right there, unless the computer also has 21, then it's a tie
                    else if (getTotalValue(playerHand) == 21 && getTotalValue(computerHand) != 21) {
                        endDecision = EndDecision.Win;
                        state = State.GameOver;
                        repositionComputerHand();
                    }

                    // update scores
                    playerScore = getTotalValue(playerHand);
                    computerScore = getTotalValue(computerHand);
                }
                // game is done, check winner and update scores!
                if (mouseInput.didMouseClickOn(turnIn)) {
                    state = State.GameOver;

                    determineWinner();
                    repositionComputerHand();

                    playerScore = getTotalValue(playerHand);
                    computerScore = getTotalValue(computerHand);
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
        g.drawImage(bkg, 0,0, null);
        g.setFont(new Font("Droid Sans", Font.BOLD, 20));
        g.setColor(Color.white);
        g.drawString("Press [ENTER] to play/pause", 50, 660);
        switch (state) {
            case Menu:
                g.drawString("Press [ENTER] to play!", 200, 50);
                g.drawString("Press [Q] to quit!", 200, 100);

                g.drawString("Rules", 300, 260);
                g.drawString("The goal is to get as close to 21 as possible!", 10, 300);
                g.drawString("You start with 2 cards, and so does the computer!", 10, 330);

                g.drawString("You can decide to get another card, or use you current cards!", 10, 380);
                g.drawString("The number on the card is the score, the sum of your score", 10, 410);
                g.drawString("is the total!", 10, 430);
                g.drawString("The player with a total score as close to 21 without", 10, 470);
                g.drawString("going over wins!!", 10, 500);
                break;
            case Playing:
                turnIn.draw(g);
                hit.draw(g);
                playerHand.forEach(hand -> hand.draw(g, cardSprite));
                break;
            case Pause:
                g.drawString("| |", 250,300);
                g.drawString("Press [Q] to quit", 200,340);
                break;
            case GameOver:
                g.drawString("Press [ENTER] to play!", 200, 50);
                g.drawString("Press [Q] to quit!", 200, 100);

                playerHand.forEach(hand -> hand.draw(g, cardSprite));
                g.setColor(Color.white);
                g.drawString("Score: " + playerScore, 100, 450);

                computerHand.forEach(hand -> hand.draw(g, cardSprite));
                g.setColor(Color.white);
                g.drawString("Opponent score: " + computerScore, 444, 30);

                g.setColor(Color.white);
                switch (endDecision) {
                    case Win:
                        g.drawString("YOU WON!!!", 20, 630);
                        if (playerScore == 21)
                            g.drawString("Perfect 21!", 120, 630);
                        break;
                    case Lost:
                        g.drawString("YOU LOST!!!", 20, 630);
                        if (playerScore > 21)
                            g.drawString("Awww! Busted!", 120, 630);
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
            // we are done once we see they are in neither hand
        } while(inComputerHand && inPlayerHand);

        hand.add(randomCard);
    }


    protected int getTotalValue(ArrayList<Card> hand) {
        int total = 0;
        int aces = 0;
        // aces are tricky, so we'll just see what the result is without them first
        for (Card card : hand)
            if (card.getValue() == 11)
                 aces++;
            else
                total += card.getValue();
        // only do this logic if he still has a chance to win and if he has aces
        if (total < 21 && aces > 0) {
            do {
                // we are going to try to count aces as 11 until it will cause the player to loose
                // after it will cause the player to loose, we count it as 1
                if (total + 11 > 21)
                    total++;
                else
                    total += 11;
                aces--;
            } while (aces > 0);
        }

        return total;
    }

}