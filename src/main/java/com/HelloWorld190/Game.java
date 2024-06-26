import jaco.mp3.player.MP3Player;

import java.awt.event.WindowEvent;
import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Game {
    private int TEN_CHIP = 0, FIFTY_CHIPS = 0, 
        HUNDRED_CHIPS = 0, FIVE_HUNDRED_CHIPS = 0, THOUSAND_CHIPS = 0;

    public ArrayList<Chip> chips;
    public ArrayList<Card> shoe;
    public ArrayList<Card> dealerHand, playerHand;
    public int bet;
    public int turn = 0;
    int dealerTotal = 0, playerTotal = 0;
    boolean dealerSoft = false, playerSoft = false;

    MainFrame frame;
    Round currentRound;

    public enum Results {
        BLACKJACK, DEALER_BLACKJACK, PLAYER_BUST, DEALER_BUST, PLAYER_WIN, DEALER_WIN, TIE, DEALER_TURN, SPLIT_CONTINUE
    }

    Results result;

    public Game(int ten, int fifty, int hundred, int fiveHundred, int thousand) {
        TEN_CHIP = ten; FIFTY_CHIPS = fifty; HUNDRED_CHIPS = hundred; 
        FIVE_HUNDRED_CHIPS = fiveHundred; THOUSAND_CHIPS = thousand;
        // initialize and set chips
        chips = new ArrayList<Chip>();
        for (int i = 0; i < TEN_CHIP; i++) {
            chips.add(Chip.TEN);
        }
        for (int i = 0; i < FIFTY_CHIPS; i++) {
            chips.add(Chip.FIFTY);
        }
        for (int i = 0; i < HUNDRED_CHIPS; i++) {
            chips.add(Chip.HUNDRED);
        }
        for (int i = 0; i < FIVE_HUNDRED_CHIPS; i++) {
            chips.add(Chip.FIVE_HUNDRED);
        }
        for (int i = 0; i < THOUSAND_CHIPS; i++) {
            chips.add(Chip.THOUSAND);
        }

        //initialize shoe
        shoe = Card.makeShoe();
        Collections.shuffle(shoe);

        //Create JFrame Application
        frame = new MainFrame(this);
        frame.createFrame();

        //Start music
        new MP3Player(new File("src/main/resources/audio/CasinoMusic.mp3")).play();
    }

    public ArrayList<Chip> bet(int amount, ArrayList<Chip> usedChips) {
        usedChips = usedChips == null ? new ArrayList<Chip>() : usedChips;
        // System.out.println("Bet: " + amount);
        if (amount > Chip.sumChipValue(chips) || amount < 10 || amount % 10 != 0) {
            return null;
        }

        for (int i = 0; i < chips.size(); i++) {
            if (amount >= chips.get(i).getValue()) {
                usedChips.add(chips.get(i));
                amount -= chips.get(i).getValue();
                chips.remove(i);
            }
        }
        
        if (amount == 0) {
            return usedChips;
        } else {
            if (chips.contains(Chip.FIFTY) && amount<50) {breakdown(50);} 
            else if (chips.contains(Chip.HUNDRED) && amount<100) {breakdown(100);}
            else if (chips.contains(Chip.FIVE_HUNDRED) && amount<500) {breakdown(500);}
            else if (chips.contains(Chip.THOUSAND)) {breakdown(1000);}
            bet(amount, usedChips);
        }
        return usedChips;
    }
    public void breakdown(int value) {
        ArrayList<Chip> breakdown = Chip.breakdown(Chip.getChip(value));
        chips.remove(Chip.getChip(value));
        chips.addAll(breakdown);
    }

    public void start() {
        frame.switchSouthPanelState();
        frame.frame.setVisible(true);
        while (Chip.sumChipValue(chips) > 0) {
        
            frame.wagerPanel.removeAll();
            concatChips();
            frame.dealerHand.removeAll(); frame.playerHand.removeAll();
            frame.setPlayerTotal("0"); frame.setDealerTotal("0");
            frame.setWager("0");
            frame.switchSouthPanelState();

            //Enable this to allow testing
            // ArrayList<Card> testHandPlayer = new ArrayList<Card>();
            // testHandPlayer.add(new Card(Card.Suit.HEARTS, Card.Rank.KING));
            // testHandPlayer.add(new Card(Card.Suit.HEARTS, Card.Rank.ACE));
            // ArrayList<Card> testHandDealer = new ArrayList<Card>();
            // testHandDealer.add(new Card(Card.Suit.HEARTS, Card.Rank.KING));
            // testHandDealer.add(new Card(Card.Suit.HEARTS, Card.Rank.KING));
            // currentRound = new Round(this, testHandDealer, testHandPlayer);

            //Disable this to allow testing
            currentRound = new Round(this);
            switch(currentRound.playerTurn()) {
                case BLACKJACK:
                    frame.alert.setText("      BlackJack!      ");
                    handleWaitError(1500);
                    break;
                case PLAYER_BUST:
                    frame.alert.setText("     Player Bust...     ");
                    handleWaitError(1500);
                    continue;
                case DEALER_TURN:
                    handleWaitError(1500);
                    frame.alert.setText("     Dealer's Turn     ");
                    handleWaitError(1500);
                    break;
                case TIE:
                case DEALER_BLACKJACK:
                case DEALER_BUST:
                case PLAYER_WIN:
                case DEALER_WIN:
                    throw new IllegalArgumentException("Invalid state");
                case SPLIT_CONTINUE:
                    continue;
            }
            switch (currentRound.dealerTurn()) {
                case DEALER_BLACKJACK:
                    frame.alert.setText("    Dealer BlackJack!    ");
                    if (!currentRound.insurance.isEmpty()) {
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        frame.alert.setText("Insurance paid out!");
                        chips.addAll(currentRound.insurance); chips.addAll(currentRound.insurance);
                    }
                    break;
                case DEALER_BUST:
                    frame.alert.setText("     Dealer Bust!     ");
                    chips.addAll(currentRound.usedChips); chips.addAll(currentRound.usedChips);
                    break;
                case PLAYER_WIN:
                    frame.alert.setText("     Player Wins!     ");
                    chips.addAll(currentRound.usedChips); chips.addAll(currentRound.usedChips);
                    break;
                case DEALER_WIN:
                    frame.alert.setText("     Dealer Wins     ");
                    break;
                case TIE:
                    frame.alert.setText("           Push           ");
                    chips.addAll(currentRound.usedChips);
                    break;
                case BLACKJACK:
                    chips.addAll(currentRound.usedChips); chips.addAll(currentRound.usedChips);
                    ArrayList<Chip> blackjackHalf = bet(Math.round((currentRound.wager/2)/10)*10, null);
                    chips.addAll(blackjackHalf); chips.addAll(blackjackHalf);
                    break;
                case PLAYER_BUST:
                case DEALER_TURN:
                case SPLIT_CONTINUE:
                    throw new IllegalArgumentException("Invalid state");
            }
            handleWaitError(2500);
        }
        frame.alert.setText("  Game Over...  ");
        handleWaitError(2000);
        frame.frame.dispatchEvent(new WindowEvent(frame.frame, WindowEvent.WINDOW_CLOSING));
    }

    private void concatChips() {
        Collections.sort(chips, Comparator.comparing(Chip::getValue));
        while (Collections.frequency(chips, Chip.TEN) >= 5) {
            for (int i = 0; i < 5; i++) {
                chips.remove(Chip.TEN);
            }
            chips.add(Chip.FIFTY);
        }
        while (Collections.frequency(chips, Chip.FIFTY) >= 2) {
            for (int i = 0; i < 2; i++) {
                chips.remove(Chip.FIFTY);
            }
            chips.add(Chip.HUNDRED);
        }
        while (Collections.frequency(chips, Chip.HUNDRED) >= 5) {
            for (int i = 0; i < 5; i++) {
                chips.remove(Chip.HUNDRED);
            }
            chips.add(Chip.FIVE_HUNDRED);
        }
        while (Collections.frequency(chips, Chip.FIVE_HUNDRED) >= 2) {
            for (int i = 0; i < 2; i++) {
                chips.remove(Chip.FIVE_HUNDRED);
            }
            chips.add(Chip.THOUSAND);
        }
    }

    public void startConsole() {
        while (Chip.sumChipValue(chips) >= 0) {
            currentRound = new Round(this);
            switch(currentRound.playerTurn()) {
                case BLACKJACK:
                    System.out.println("\nBlackjack!");
                    chips.addAll(currentRound.usedChips); chips.addAll(currentRound.usedChips);
                    handleWaitError(1500);
                    frame.switchSouthPanelState();
                    continue;
                case PLAYER_BUST:
                    System.out.println("\nPlayer Bust!");
                    handleWaitError(1500);
                    frame.switchSouthPanelState();
                    continue;
                case DEALER_TURN:
                    System.out.println("\nDealer's Turn");
                    handleWaitError(2500);
                    break;
                case TIE:
                case DEALER_BLACKJACK:
                case DEALER_BUST:
                case PLAYER_WIN:
                case DEALER_WIN:
                case SPLIT_CONTINUE:
                    throw new IllegalArgumentException("Invalid state");
            }
            switch (currentRound.dealerTurn()) {
                case DEALER_BLACKJACK:
                    System.out.println("\nDealer Blackjack!");
                    
                    break;
                case DEALER_BUST:
                    System.out.println("\nDealer Bust!");
                    chips.addAll(currentRound.usedChips); chips.addAll(currentRound.usedChips);
                    break;
                case PLAYER_WIN:
                    System.out.println("\nPlayer Wins!");
                    chips.addAll(currentRound.usedChips); chips.addAll(currentRound.usedChips);
                    break;
                case DEALER_WIN:
                    System.out.println("\nDealer Wins!");
                    break;
                case TIE:
                    System.out.println("\nTie!");
                    chips.addAll(currentRound.usedChips);
                    break;
                case BLACKJACK:
                case PLAYER_BUST:
                case DEALER_TURN:
                case SPLIT_CONTINUE:
                    throw new IllegalArgumentException("Invalid state");
            }
            handleWaitError(4500);
            frame.switchSouthPanelState();
        }
    }
    public void handleWaitError(int wait) {
        try {
            Thread.sleep(wait);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
