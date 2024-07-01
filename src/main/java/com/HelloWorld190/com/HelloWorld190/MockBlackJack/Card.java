package com.HelloWorld190.MockBlackJack;
import java.util.ArrayList;

public class Card {

    // Enum for suits and ranks
    public enum Suit {
        HEARTS, DIAMONDS, CLUBS, SPADES
    }
    public enum Rank {
        ACE(1), TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7),
        EIGHT(8), NINE(9), TEN(10), JACK(10), QUEEN(10), KING(10);

        int value;

        private Rank(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
    }

    private Suit suit;
    private Rank rank;
    public boolean isFaceUp = true;

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }
    
    @Override
    public String toString() {
        if (!isFaceUp) return "Hidden Card";
        return rank + "("+ suit.toString().charAt(0) +")";
    }
    // Getters for card values

    public Suit getSuit() {
        return suit;
    }
    public Card.Rank getRank() {
        return rank;
    }

    public static ArrayList<Card> makeShoe() {
        ArrayList<Card> shoe = new ArrayList<Card>();
        for (int i = 0; i < 6; i++) {
            for (Card.Suit suit : Card.Suit.values()) {
                for (Card.Rank rank : Card.Rank.values()) {
                    shoe.add(new Card(suit, rank));
                }
            }
        }
        return shoe; 
    }

    public int getValue() {
        return rank.getValue();
    }
}