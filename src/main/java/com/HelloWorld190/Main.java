// import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        Game game = new Game(0,1,1,1,0);
        // MainFrame frame = new MainFrame(game);
        // frame.createFrame();
        // frame.addPlayerCard(new Card(Card.Suit.CLUBS, Card.Rank.ACE));
        // frame.addDealerCard(new Card(Card.Suit.CLUBS, Card.Rank.ACE));
        // frame.addDealerCard(new Card(Card.Suit.CLUBS, Card.Rank.ACE));
        // Card card = new Card(Card.Suit.CLUBS, Card.Rank.ACE);
        // card.isFaceUp=false;
        // frame.addDealerCard(card);

        // while (true) {
        //     System.out.println("H: " + game.frame.frame.getSize().getHeight() + " W: " + game.frame.frame.getSize().getWidth());
        //     try {
        //         Thread.sleep(1000);
        //     } catch (InterruptedException e) {
        //         e.printStackTrace();
        //     }
        // }
        game.start();
    }
}