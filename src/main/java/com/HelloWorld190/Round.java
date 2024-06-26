

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;


public class Round {
    int turn = 0;
    Integer wager; ArrayList<Chip> usedChips;
    Game game;
    ArrayList<Card> dealerHand, playerHand;
    int dealerTotal = 0, playerTotal = 0;
    boolean dealerSoft = false, playerSoft = false;
    int indicator = 0;
    boolean[] playerActions;
    ArrayList<Chip> insurance;
    public enum Actions {
        HIT, STAND, DOUBLE_DOWN, SPLIT, INSURANCE
    }
    Actions selectedAction;
    public static final Object o = new Object();


    public Round (Game game) {
        this.game = game;

        validateShoe(); //Initialize shoe if empty or null
        // consoleBoard();
        game.frame.setChips();
        JFrameBoard(FULL_RELOAD);

        // Ask for bet
        // usedChips = getConsoleBet();
        while (true) {
            synchronized (o) {
                try {
                    o.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            usedChips = processBet(game.frame.inputField.getText());
            if (usedChips != null) break;
        }

        game.frame.setWageredChips(usedChips);
        game.frame.setWager(""+wager);
        game.frame.setChips();
        game.frame.switchSouthPanelState();

        // Burn card
        burnCard();

        // Deal cards
        dealerHand = new ArrayList<Card>(); playerHand = new ArrayList<Card>();
        dealerHand.add(game.shoe.remove(0)); playerHand.add(game.shoe.remove(0)); 
        Card hidden = game.shoe.remove(0); hidden.isFaceUp = false;
        dealerHand.add(hidden); playerHand.add(game.shoe.remove(0));        

        // Player's Actions
        updatePlayerActions();

        JFrameBoard(FULL_RELOAD);
    }

    //WARNING: Only use this constructor for SPLIT
    public Round(Game game, ArrayList<Card> dealerHand, Card playerCard) {
        this.game = game;
        this.dealerHand = dealerHand;
        playerHand = new ArrayList<Card>();
        playerHand.add(playerCard);
        playerHand.add(game.shoe.remove(0));
        
        validateShoe(); //Initialize shoe if empty or null

        game.frame.setChips();
        game.frame.setActionsVisibility(null);
        JFrameBoard(SPLIT_INIT);
    }
    
    //TESTING ONLY
    public Round(Game game, ArrayList<Card> dealerHand, ArrayList<Card> playerHand) {
        this.game = game;

        validateShoe(); //Initialize shoe if empty or null
        // consoleBoard();
        game.frame.setChips();
        JFrameBoard(FULL_RELOAD);

        // Ask for bet
        // usedChips = getConsoleBet();
        while (true) {
            synchronized (o) {
                try {
                    o.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            usedChips = processBet(game.frame.inputField.getText());
            if (usedChips != null) break;
        }

        game.frame.setWageredChips(usedChips);
        game.frame.setWager(""+wager);
        game.frame.setChips();
        game.frame.switchSouthPanelState();

        // Burn card
        burnCard();

        // Deal cards
        this.dealerHand = dealerHand; this.playerHand = playerHand;   

        // Player's Actions
        updatePlayerActions();

        JFrameBoard(FULL_RELOAD);
    }

    private void validateShoe() {
        if (game.shoe.isEmpty() || game.shoe == null) {
            game.shoe = Card.makeShoe(); Collections.shuffle(game.shoe);
        }
    }

    private void burnCard() {
        Card burn = game.shoe.remove(0);
        game.frame.alert.setText("Burned card: " + burn);
    }

    private void updatePlayerActions() {
        playerActions = new boolean[]{true, true, false, false, false};
        if (wager <= Chip.sumChipValue(game.chips)) playerActions[2] = true;
        if (playerHand.get(0).getRank() == playerHand.get(1).getRank() && wager <= Chip.sumChipValue(game.chips)) playerActions[3] = true;
        if (dealerHand.get(0).getRank() == Card.Rank.ACE) playerActions[4] = true;
        
        game.frame.setActionsVisibility(playerActions);
    }

    @SuppressWarnings("unused")
    private ArrayList<Chip> getConsoleBet() {
        @SuppressWarnings("resource")
        Scanner scanner = new Scanner(System.in);
        while (true) {
            wager = 0;
            System.out.print("\nEnter your bet: ");
            try {wager = scanner.nextInt();}
            catch (Exception e) {System.out.println("Invalid bet"); continue;}
            ArrayList<Chip> a = game.bet(wager, usedChips);
            if (a.equals(null)) {System.out.println("Invalid bet"); continue;}
            return a;
        }
    }

    public ArrayList<Chip> processBet(String i) {
        try {
            wager = Integer.parseInt(i);
        } catch (Exception e) {
            game.frame.alert.setText("      Invalid bet      ");
        }
        ArrayList<Chip> a = game.bet(wager, null);
        if (a==null) {game.frame.alert.setText("      Invalid bet      "); return null;}
        return a;
    }

    public Game.Results playerTurn() {
        while (true) {
            if (selectedAction != Actions.SPLIT) game.frame.alert.setText("     Player's Turn     ");
            validateShoe(); //Initialize shoe if empty or null
            // consoleBoard();
            if (playerTotal == 21) return Game.Results.BLACKJACK;
            if (playerTotal == 11 && playerSoft) return Game.Results.BLACKJACK;
            if (playerTotal > 21) return Game.Results.PLAYER_BUST;

            // Get user input
            // @SuppressWarnings("resource")
            // String userInput = new Scanner(System.in).nextLine();

            // if (userInput.equals(">")) {
            //     indicator++;
            //     if (indicator > 4) indicator = 0;
            //     while (!playerActions[indicator]) {
            //         indicator++;
            //         if (indicator > 4) indicator = 0;
            //     }
            // } else if (userInput.equals("<")) {
            //     indicator--;
            //     if (indicator < 0) indicator = 4;
            //     while (!playerActions[indicator]) {
            //         indicator--;
            //         if (indicator < 0) indicator = 4;
            //     }
            // } else {
            //     turn++;
            //     switch (Actions.values()[indicator]) {
            //         case HIT:
            //             playerHand.add(game.shoe.remove(0));
            //             break;
            //         case STAND:
            //             return Game.Results.DEALER_TURN;
            //         case DOUBLE_DOWN:
            //         case SPLIT:
            //         case INSURANCE:
            //             System.out.println("Not implemented yet, sry");
            //             break;
            //     }
            // }
            synchronized (o) {
                try {
                    o.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            switch (selectedAction) {
                case HIT:
                    game.frame.alert.setText("     Player Hits!     ");
                    playerHand.add(game.shoe.remove(0));
                    JFrameBoard(PLAYER_CARD);
                    break;
                case STAND:
                    game.frame.alert.setText("     Player Stands!     ");
                    return Game.Results.DEALER_TURN;
                case DOUBLE_DOWN:
                    game.frame.alert.setText(" Player Doubles Down! ");
                    wager *= 2;
                    game.chips.addAll(usedChips);
                    usedChips = game.bet(wager, null);

                    game.frame.setWageredChips(usedChips);
                    game.frame.setWager(""+wager);
                    game.frame.setChips();

                    playerHand.add(game.shoe.remove(0));
                    JFrameBoard(PLAYER_CARD);

                    if (playerTotal == 21 || (playerTotal == 11 && playerSoft)) return Game.Results.BLACKJACK;
                    if (playerTotal > 21) return Game.Results.PLAYER_BUST;
                    return Game.Results.DEALER_TURN;  
                case SPLIT:
                    game.frame.alert.setText("    Player Splits!    ");
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }   
                    int playerTotalHand1 = 0, playerTotalHand2 = 0; 
                    game.bet(wager, null);
                    game.currentRound = new Round(game, dealerHand, playerHand.get(0));
                    game.frame.alert.setText("     Hand 1:    ");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Game.Results resultHandOne = game.currentRound.playerTurn();
                    playerTotalHand1 = game.currentRound.playerTotal;
                    if (game.currentRound.playerSoft && playerTotalHand1 <= 11) playerTotalHand1 += 10;
                    playerTotal = playerTotalHand1;
                    String resultFormat = "";
                    if (resultHandOne == Game.Results.PLAYER_BUST) game.frame.alert.setText((resultFormat = "Hand 1: BUST | Hand 2: "));
                    else if (resultHandOne == Game.Results.BLACKJACK) game.frame.alert.setText((resultFormat = "Hand 1: BLACKJACK | Hand 2: "));
                    else game.frame.alert.setText((resultFormat = "Hand 1: " + ((playerSoft)?determineSoft(playerTotal):playerTotal) + " | Hand 2: "));
                   
                    game.currentRound = new Round(game, dealerHand, playerHand.get(1));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Game.Results resultHandTwo = game.currentRound.playerTurn();
                    playerTotalHand2 = game.currentRound.playerTotal;
                    if (game.currentRound.playerSoft && playerTotalHand2 <= 11) playerTotalHand2 += 10;
                    playerTotal = playerTotalHand2;

                    if (resultHandTwo == Game.Results.PLAYER_BUST) game.frame.alert.setText(resultFormat + "BUST");
                    else if (resultHandTwo == Game.Results.BLACKJACK) game.frame.alert.setText(resultFormat + "BLACKJACK");
                    else game.frame.alert.setText(resultFormat + ((playerSoft)?determineSoft(playerTotal):playerTotal));

                    Game.Results dealerResult = game.currentRound.dealerTurn();
                    if (dealerResult == Game.Results.DEALER_BLACKJACK) {
                        game.frame.alert.setText("     Dealer Blackjack!     ");
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (resultHandOne == Game.Results.BLACKJACK) game.chips.addAll(usedChips);
                        if (resultHandTwo == Game.Results.BLACKJACK) game.chips.addAll(usedChips);
                        game.frame.alert.setText("Hand 1: " + ((resultHandOne==Game.Results.BLACKJACK)?"Push":"Dealer Wins!") + " | Hand 2: " + ((resultHandTwo==Game.Results.BLACKJACK)?"Push":"Dealer Wins!"));
                    } else if (dealerResult == Game.Results.DEALER_BUST) {
                        game.frame.alert.setText("     Dealer Bust!     ");
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (resultHandOne != Game.Results.PLAYER_BUST) {game.chips.addAll(usedChips); game.chips.addAll(usedChips);}
                        if (resultHandTwo != Game.Results.PLAYER_BUST) {game.chips.addAll(usedChips); game.chips.addAll(usedChips);}
                        game.frame.alert.setText("Hand 1: " + ((resultHandOne==Game.Results.PLAYER_BUST)?"Dealer Wins!":"Player Wins!") + " | Hand 2: " + ((resultHandTwo==Game.Results.PLAYER_BUST)?"Dealer Wins!":"Player Wins!"));
                    } else {
                        resultFormat = "Hand 1: ";
                        if (playerTotalHand1 <= 21){
                            if (dealerTotal == playerTotalHand1) {game.chips.addAll(usedChips); resultFormat+="Push";}
                            else if (dealerTotal > playerTotalHand1) resultFormat+="Dealer Wins!";
                            else {game.chips.addAll(usedChips); game.chips.addAll(usedChips); resultFormat+="Player Wins!";}
                        } else resultFormat+="BUST";

                        resultFormat += " | Hand 2: ";
                        if (playerTotalHand2 <= 21){
                            if (dealerTotal == playerTotalHand2) {game.chips.addAll(usedChips); resultFormat+="Push";}
                            else if (dealerTotal > playerTotalHand2) resultFormat+="Dealer Wins!";
                            else {game.chips.addAll(usedChips); game.chips.addAll(usedChips); resultFormat+="Player Wins!";}
                        } else resultFormat+="BUST";
                        game.frame.alert.setText(resultFormat);
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return Game.Results.SPLIT_CONTINUE;
                    }
                case INSURANCE:
                    game.frame.alert.setText("Player Takes Insurance!");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    game.frame.alert.setText("Place insurance bet! (Up to $" + Math.round((wager/2)/10)*10 + ")");
                    game.frame.switchSouthPanelState();
                    while (true) {
                        synchronized (o) {
                            try {
                                o.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        if (Integer.parseInt(game.frame.inputField.getText()) > Math.round((wager/2)/10)*10) {
                            game.frame.alert.setText("Invalid bet");
                            continue;
                        }
                        insurance = processBet(game.frame.inputField.getText());
                        if (insurance != null) break;
                    }
                    game.frame.alert.setText("Insurance bet placed! ($"+Chip.sumChipValue(insurance)+")");
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    game.frame.switchSouthPanelState();
                    break;
            }
            game.frame.setActionsVisibility(null);
        }
    }

    public Game.Results dealerTurn() {

        // consoleBoard();
        JFrameBoard(DEALER_INIT);
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (dealerTotal < 17) {
            dealerHand.add(game.shoe.remove(0));
            // consoleBoard();
            JFrameBoard(DEALER_CARD);
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (dealerTotal == 11 && dealerSoft) break;
        }
        if (dealerSoft && dealerTotal <= 11) dealerTotal += 10;
        if (playerSoft && playerTotal <= 11) playerTotal += 10;
        if (dealerTotal == 21 && playerTotal == 21) return Game.Results.TIE;
        if (dealerTotal == 21) return Game.Results.DEALER_BLACKJACK;
        if (playerTotal == 21) return Game.Results.BLACKJACK;
        if (dealerTotal > 21) return Game.Results.DEALER_BUST;
        if (dealerTotal > playerTotal) return Game.Results.DEALER_WIN;
        if (dealerTotal < playerTotal) return Game.Results.PLAYER_WIN;
        return Game.Results.TIE;
    } 

    final int FULL_RELOAD = 0, PLAYER_CARD = 1, DEALER_INIT = 2, DEALER_CARD = 3, SPLIT_INIT =4;
    private void JFrameBoard(int status) {
        dealerHand = dealerHand == null ? new ArrayList<Card>() : dealerHand;
        playerHand = playerHand == null ? new ArrayList<Card>() : playerHand;
        usedChips = usedChips == null ? new ArrayList<Chip>() : usedChips;
        playerActions = playerActions == null ? new boolean[5] : playerActions;
        if (status == FULL_RELOAD) {

            String alertString = "Dealing Cards";

            dealerTotal = 0; playerTotal = 0;
            for (Card card : dealerHand) {
                alertString += ".";
                game.frame.alert.setText(alertString);
                game.frame.addDealerCard(card);
                if (card.isFaceUp) {
                    dealerTotal += card.getValue();
                    if (card.getRank() == Card.Rank.ACE) dealerSoft = true;
                }
                if (dealerSoft && dealerTotal == 11) game.frame.setDealerTotal("21");
                else if (dealerSoft) game.frame.setDealerTotal(determineSoft(dealerTotal));
                else game.frame.setDealerTotal(dealerTotal+"");
            }
            for (Card card : playerHand) {
                alertString += ".";
                game.frame.alert.setText(alertString);
                game.frame.addPlayerCard(card);
                playerTotal += card.getValue();
                if (card.getRank() == Card.Rank.ACE) playerSoft = true;
                if (playerTotal == 21) game.frame.setPlayerTotal("21");
                else if (playerSoft) game.frame.setPlayerTotal(determineSoft(playerTotal));
                else game.frame.setPlayerTotal(playerTotal+"");
            }
        } else if (status == PLAYER_CARD || status == SPLIT_INIT) {
            if (status == SPLIT_INIT) {
                game.frame.playerHand.removeAll();
                game.frame.addPlayerCard(playerHand.get(0));
                playerTotal = playerHand.get(0).getValue();
                if (playerHand.get(0).getRank() == Card.Rank.ACE) playerSoft = true;
                if (dealerHand.get(dealerHand.size()-1).getRank() == Card.Rank.ACE) dealerSoft = true;

                dealerTotal = dealerHand.get(0).getValue();
                if (dealerSoft) game.frame.setDealerTotal(determineSoft(dealerTotal));
                else game.frame.setDealerTotal(dealerTotal+"");
            } else if (playerHand.get(playerHand.size()-1).getRank() == Card.Rank.ACE) playerSoft = true;
            game.frame.addPlayerCard(playerHand.get(playerHand.size()-1));
            playerTotal += playerHand.get(playerHand.size()-1).getValue();
            if (playerTotal == 21 || (playerTotal == 11 && playerSoft)) game.frame.setPlayerTotal("21");
            else if (playerSoft) game.frame.setPlayerTotal(determineSoft(playerTotal));
            else game.frame.setPlayerTotal(playerTotal+"");
        } else if (status == DEALER_INIT) {
            if (dealerHand.get(dealerHand.size()-1).getRank() == Card.Rank.ACE) dealerSoft = true;
            game.frame.dealerHand.remove(1);
            dealerHand.get(1).isFaceUp = true;
            game.frame.addDealerCard(dealerHand.get(1));
            dealerTotal += dealerHand.get(1).getValue();
            if (dealerTotal == 21 || (dealerTotal == 11 && dealerSoft)) game.frame.setDealerTotal("21");
            else if (dealerSoft) game.frame.setDealerTotal(determineSoft(dealerTotal));
            else game.frame.setDealerTotal(dealerTotal+"");
        } else if (status == DEALER_CARD) {
            if (dealerHand.get(dealerHand.size()-1).getRank() == Card.Rank.ACE) dealerSoft = true;
            game.frame.addDealerCard(dealerHand.get(dealerHand.size()-1));
            dealerTotal += dealerHand.get(dealerHand.size()-1).getValue();
            if (dealerTotal == 21 || (dealerTotal == 11 && dealerSoft)) game.frame.setDealerTotal("21");
            else if (dealerSoft) game.frame.setDealerTotal(determineSoft(dealerTotal));
            else game.frame.setDealerTotal(dealerTotal+"");
        } else {
            throw new IllegalArgumentException("Invalid status");
        }
    }
    
    @SuppressWarnings("unused")
    private void consoleBoard() {
        dealerHand = dealerHand == null ? new ArrayList<Card>() : dealerHand;
        playerHand = playerHand == null ? new ArrayList<Card>() : playerHand;
        usedChips = usedChips == null ? new ArrayList<Chip>() : usedChips;
        playerActions = playerActions == null ? new boolean[5] : playerActions;
        dealerTotal = 0; playerTotal = 0;

        // System.out.print("\033[H\033[2J");
        // System.out.flush();

        System.out.println("\nDealer's Hand: ");
        game.frame.playerHand.removeAll(); game.frame.dealerHand.removeAll();
        for (Card card : dealerHand) {
            System.out.print(card + " ");
            if (card.isFaceUp) {
                dealerTotal += card.getValue();
                if (card.getRank() == Card.Rank.ACE) dealerSoft = true;
            }
            // game.frame.addDealerCard(card);
        }
        if (dealerSoft) System.out.println(" | Dealer's Total: " + determineSoft(dealerTotal) + "\n");
        else System.out.println(" | Dealer's Total: " + dealerTotal + "\n");
        System.out.println("Player's Hand: ");
        for (Card card : playerHand) {
            System.out.print(card + " ");
            playerTotal += card.getValue();
            if (card.getRank() == Card.Rank.ACE) playerSoft = true;
        }
        if (playerSoft) System.out.println(" | Player's Total: " + determineSoft(playerTotal) + "\n");
        else game.frame.setPlayerTotal(playerTotal+"");
        System.out.print("\nCurrent wager: "); usedChips.forEach((Chip c) -> {System.out.print(c+" ");});
        System.out.print("($" + Chip.sumChipValue(usedChips) + ")");
        System.out.print("\n\nPlayer Actions: ");
        System.out.print("\n\n[" +
            ((playerActions[0])? ((indicator==0)?"*":" ") : "/") + "] HIT | [" +
            ((playerActions[1])? ((indicator==1)?"*":" ") : "/") + "] STAND | [" +
            ((playerActions[2])? ((indicator==2)?"*":" ") : "/") + "] DOUBLE DOWN | [" +
            ((playerActions[3])? ((indicator==3)?"*":" ") : "/") + "] SPLIT | [" + 
            ((playerActions[4])? ((indicator==4)?"*":" ") : "/") + "] INSURANCE");
        System.out.print("\n\nPlayer's Chips: THOUSAND ["+Collections.frequency(game.chips, Chip.THOUSAND)+"] | FIVE HUNDRED ["+
            Collections.frequency(game.chips, Chip.FIVE_HUNDRED)+"] | HUNDRED ["+Collections.frequency(game.chips, Chip.HUNDRED)+"] | FIFTY ["+
            Collections.frequency(game.chips, Chip.FIFTY)+"] | TEN ["+Collections.frequency(game.chips, Chip.TEN)+"] ");
        System.out.print("||| Total Money: $" + Chip.sumChipValue(game.chips));
        if (wager != null) System.out.print("\n\nEnter > to move right, < to move left, or any other key to select.\n");
    }

    private String determineSoft(int total) {
        if (total + 10 <= 21) return (String) (total + " or " + (total + 10));
        else return (String) (total+"");
    }
} 
