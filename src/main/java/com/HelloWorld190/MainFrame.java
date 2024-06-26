import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.io.File;

import javax.swing.KeyStroke;
import java.util.ArrayList;
import java.util.Collections;
import jaco.mp3.player.MP3Player;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicOptionPaneUI.ButtonAreaLayout;

public class MainFrame {
    static int FRAME_WIDTH = 940, FRAME_HEIGHT = 650;
    JFrame frame;
    Game game;
    ImprovedJTextField inputField;
    JPanel playerHand, dealerHand, southPanel, wagerPanel, alertPanel, moneyPanel;
    JLabel alert;
    JLabel hundredLabel, fiftyLabel, tenLabel, fiveHundredLabel, thousandLabel;
    ArrayList<JLabel> titleLabels = new ArrayList<>();

    GridBagConstraints c = new GridBagConstraints();

    private enum SouthPanelState {
        BETTING, PLAYING
    }

    SouthPanelState southPanelState = SouthPanelState.BETTING;

    public MainFrame(Game game) {
        frame = new JFrame("BlackJack");
        this.game = game;
    }

    public void createFrame() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        Color darkGreen = new Color(47, 104, 45);
        frame.getContentPane().setBackground(darkGreen);
        frame.setName("BlackJack");
        frame.setLayout(new BorderLayout(20,15));
        // frame.setResizable(false);

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(darkGreen);
        String title = "BlackJack";
        char[] titleChars = title.toCharArray();
        for (char c : titleChars) {
            JLabel titleLabel = new JLabel(c+"");
            titleLabel.setForeground(Color.yellow);
            titleLabel.setFont(new Font("SanSerif", Font.BOLD, 75));
            titleLabels.add(titleLabel);
            titlePanel.add(titleLabel);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Color[] colors = {Color.yellow, Color.white, Color.green, Color.red, Color.blue, Color.orange, Color.pink, Color.cyan, Color.lightGray};
                while (true) {
                    Color c1 = colors[(int)(Math.random()*colors.length)];
                    Color c2 = colors[(int)(Math.random()*colors.length)];
                    if (c1 == c2) continue;
                    for (int i = 0; i < titleLabels.size(); i++) {
                        titleLabels.get(i).setForeground(c1);
                        frame.setVisible(true);
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        titleLabels.get(i).setForeground(c2);
                        frame.setVisible(true);
                    }
                }
            }
        }).start();
        frame.add(titlePanel, BorderLayout.NORTH);

        JPanel mainAreaPanel = new JPanel();
        mainAreaPanel.setBackground(darkGreen);
        mainAreaPanel.setLayout(new BoxLayout(mainAreaPanel, BoxLayout.Y_AXIS));

        dealerHand = new JPanel();
        dealerHand.setBackground(darkGreen);
        setDealerTotal("0");
        mainAreaPanel.add(dealerHand, BorderLayout.NORTH);

        playerHand = new JPanel();
        playerHand.setBackground(darkGreen);
        setPlayerTotal("0");
        mainAreaPanel.add(playerHand, BorderLayout.SOUTH);

        frame.add(mainAreaPanel, BorderLayout.CENTER);

        southPanel = new JPanel();
        bettingPanelLoad();

        moneyPanel = new JPanel();
        moneyPanel.setBackground(darkGreen);
        setMoney("0");
        moneyPanel.setLayout(new GridLayout(5,2));

        JLabel tenChip = new JLabel(new ImageIcon(new ImageIcon("src/main/resources/images/Chips/TEN.png").getImage().getScaledInstance(70, 70, Image.SCALE_DEFAULT)));
        tenLabel = new JLabel("0", SwingConstants.CENTER);
        tenLabel.setForeground(Color.white);
        tenLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        moneyPanel.add(tenChip); moneyPanel.add(tenLabel);

        JLabel fiftyChip = new JLabel(new ImageIcon(new ImageIcon("src/main/resources/images/Chips/FIFTY.png").getImage().getScaledInstance(70, 70, Image.SCALE_DEFAULT)));
        fiftyLabel = new JLabel("0", SwingConstants.CENTER);
        fiftyLabel.setForeground(Color.white);
        fiftyLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        moneyPanel.add(fiftyChip); moneyPanel.add(fiftyLabel);

        JLabel hundredChip = new JLabel(new ImageIcon(new ImageIcon("src/main/resources/images/Chips/HUNDRED.png").getImage().getScaledInstance(70, 70, Image.SCALE_DEFAULT)));
        hundredLabel = new JLabel("0", SwingConstants.CENTER);
        hundredLabel.setForeground(Color.white);
        hundredLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        moneyPanel.add(hundredChip); moneyPanel.add(hundredLabel);

        JLabel fiveHundredChip = new JLabel(new ImageIcon(new ImageIcon("src/main/resources/images/Chips/FIVE_HUNDRED.png").getImage().getScaledInstance(70, 70, Image.SCALE_DEFAULT)));
        fiveHundredLabel = new JLabel("0", SwingConstants.CENTER);
        fiveHundredLabel.setForeground(Color.white);
        fiveHundredLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        moneyPanel.add(fiveHundredChip); moneyPanel.add(fiveHundredLabel);

        JLabel thousandChip = new JLabel(new ImageIcon(new ImageIcon("src/main/resources/images/Chips/THOUSAND.png").getImage().getScaledInstance(70, 70, Image.SCALE_DEFAULT)));
        thousandLabel = new JLabel("0", SwingConstants.CENTER);
        thousandLabel.setForeground(Color.white);
        thousandLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        moneyPanel.add(thousandChip); moneyPanel.add(thousandLabel);

        frame.add(moneyPanel, BorderLayout.WEST);

        JPanel statusPanel = new JPanel();
        statusPanel.setBackground(darkGreen);
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));

        wagerPanel = new JPanel();
        wagerPanel.setBackground(darkGreen);
        wagerPanel.setLayout(new GridLayout(5, 5));
        setWager("0");
        // for (int i = 0; i < 9; i++) {
        //     wagerPanel.add(new JLabel(/*new ImageIcon("com/ConsoleBlackJack/images/Chip.png")*/));
        // }
        statusPanel.add(wagerPanel, BorderLayout.NORTH);

        // JPanel alertPanel = new JPanel();
        // alertPanel.setBackground(darkGreen);
        alert = new JLabel("Welcome to BlackJack!");
        /*alertPanel*/alert.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2,2,2,2,new Color(50, 1, 19)),
                BorderFactory.createEmptyBorder(5,5,5,5)
            ),"Status:", TitledBorder.LEFT, TitledBorder.TOP, new Font("SanSerif", Font.BOLD, 20), Color.white));
        alert.setForeground(Color.yellow);
        alert.setFont(new Font("Arial", Font.PLAIN, 20));
        alert.setBackground(darkGreen);
        // alertPanel.add(alert);

        statusPanel.add(alert, BorderLayout.SOUTH);
        
        frame.add(statusPanel, BorderLayout.EAST);
    }

    public void addPlayerCard(Card card) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new MP3Player(new File("src/main/resources/audio/DealCard.mp3")).play();
        String filepath = "src/main/resources/images/" + card.getSuit().toString()+"/"+card.getRank().toString()+".png";
        JLabel cardLabel = new JLabel(new ImageIcon(filepath));
        playerHand.add(cardLabel);
        frame.setVisible(true);
    }
    public void addDealerCard(Card card) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new MP3Player(new File("src/main/resources/audio/DealCard.mp3")).play();
        if (card.isFaceUp) {
            String filepath = "src/main/resources/images/" + card.getSuit().toString()+"/"+card.getRank().toString()+".png";
            JLabel cardLabel = new JLabel(new ImageIcon(filepath));
            dealerHand.add(cardLabel);
        } else {
            String filepath = "src/main/resources/images/CardFaceDown.png";
            JLabel cardLabel = new JLabel(new ImageIcon(filepath));
            dealerHand.add(cardLabel);
        }
        frame.setVisible(true);
    }
    public void clearHands() {
        playerHand.removeAll();
        setPlayerTotal("0");
        dealerHand.removeAll();
        setDealerTotal("0");
        frame.setVisible(true);
    }
    public void switchSouthPanelState() {
        if (southPanelState == SouthPanelState.BETTING) {
            southPanelState = SouthPanelState.PLAYING;
            southPanel.removeAll();
            playingPanelLoad();                                                     
        } else {
            southPanelState = SouthPanelState.BETTING;
            southPanel.removeAll();
            bettingPanelLoad();
        }
        frame.setVisible(true);
    }

    private void playingPanelLoad() {
        southPanel.setBackground(Color.DARK_GRAY);
        southPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0),"User Actions:"));
        ((TitledBorder) southPanel.getBorder()).setTitleFont(new Font("SanSerif", Font.BOLD, 20));
        ((TitledBorder) southPanel.getBorder()).setTitleColor(Color.white);
        southPanel.setLayout(new ButtonAreaLayout(true, 20));
        
        JButton blankLeft = new JButton();
        blankLeft.setOpaque(true);
        blankLeft.setBorderPainted(false);
        blankLeft.setFocusPainted(false);
        blankLeft.setBackground(Color.gray);

        JButton hitButton = new JButton("HIT");
        hitButton.setOpaque(true);
        hitButton.setBorderPainted(false);
        hitButton.setFocusPainted(false);
        hitButton.setBackground(Color.green);
        hitButton.addActionListener(e -> {
            synchronized (Round.o) {
                alert.setText("    Player has Hit!    ");
                game.currentRound.selectedAction = Round.Actions.HIT;
                Round.o.notify();
            }
        });

        JButton standButton = new JButton("STAND");
        standButton.setOpaque(true);
        standButton.setBorderPainted(false);
        standButton.setFocusPainted(false);
        standButton.setBackground(Color.red);
        standButton.addActionListener(e -> {
            synchronized (Round.o) {
                alert.setText("    Player Stands!    ");
                game.currentRound.selectedAction = Round.Actions.STAND;
                Round.o.notify();
            }
        });

        JButton doubleButton = new JButton("DOUBLE-DOWN");
        doubleButton.setOpaque(true);
        doubleButton.setBorderPainted(false);
        doubleButton.setFocusPainted(false);
        doubleButton.setBackground(Color.yellow);
        doubleButton.addActionListener(e -> {
            synchronized (Round.o) {
                alert.setText(" Player Doubles Down! ");
                game.currentRound.selectedAction = Round.Actions.DOUBLE_DOWN;
                Round.o.notify();
            }
        });

        JButton splitButton = new JButton("SPLIT");
        splitButton.setOpaque(true);
        splitButton.setBorderPainted(false);
        splitButton.setFocusPainted(false);
        splitButton.setBackground(Color.blue);
        splitButton.addActionListener(e -> {
            synchronized (Round.o) {
                alert.setText("    Player Splits!    ");
                game.currentRound.selectedAction = Round.Actions.SPLIT;
                Round.o.notify();
            }
        });

        JButton insuranceButton = new JButton("INSURANCE");
        insuranceButton.setOpaque(true);
        insuranceButton.setBorderPainted(false);
        insuranceButton.setFocusPainted(false);
        insuranceButton.setBackground(Color.orange);
        insuranceButton.addActionListener(e -> {
            synchronized (Round.o) {
                alert.setText("Player Takes Insurance!");
                game.currentRound.selectedAction = Round.Actions.INSURANCE;
                Round.o.notify();
            }
        });

        JButton blankRight = new JButton();
        blankRight.setOpaque(true);
        blankRight.setBorderPainted(false);
        blankRight.setFocusPainted(false);
        blankRight.setBackground(Color.gray);

        southPanel.add(blankLeft);
        southPanel.add(hitButton);
        southPanel.add(standButton);
        southPanel.add(doubleButton);
        southPanel.add(splitButton);
        southPanel.add(insuranceButton);
        southPanel.add(blankRight);
        frame.add(southPanel, BorderLayout.SOUTH);
    }
    private void bettingPanelLoad() {
        southPanel.setBackground(Color.DARK_GRAY);
        southPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0),"Place a bet:"));
        ((TitledBorder) southPanel.getBorder()).setTitleFont(new Font("SanSerif", Font.BOLD, 20));
        ((TitledBorder) southPanel.getBorder()).setTitleColor(Color.white);
        southPanel.setLayout(new GridBagLayout());

        JLabel SouthWest = new JLabel(new ImageIcon());
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 0;
        southPanel.add(SouthWest, c);
        
        inputField = new ImprovedJTextField("Enter bet here...");
        inputField.setColumns(1);
        c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.weighty = 0.5;
        c.anchor = GridBagConstraints.LINE_END;
        c.gridx = 1;
        c.gridy = 0;
        southPanel.add(inputField, c);

        JButton button = new JButton("PLACE WAGER!");
        c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.LINE_START;
        c.weightx = 0.5;
        c.weighty = 0.5;
        c.gridx = 2;
        c.gridy = 0;
        button.addActionListener(e -> {
            synchronized (Round.o) {
                System.out.println("WINDOWS ACTIVATED");
                Round.o.notify();
            }
        });
        inputField.getInputMap().put(KeyStroke.getKeyStroke("ENTER"),"pressed");
        inputField.getActionMap().put("pressed", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                System.out.println("ENTER PRESSED");
                button.doClick();
            }
        });
        southPanel.add(button, c);

        JLabel SouthEast = new JLabel(new ImageIcon());
        c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 3;
        c.gridy = 0;
        southPanel.add(SouthEast, c);

        frame.add(southPanel, BorderLayout.SOUTH);
    }

    public void setActionsVisibility(boolean[] playerActions) {
        playerActions = playerActions == null ? new boolean[]{false, false, false, false, false} : playerActions;

        if (playerActions[2]) {
            southPanel.getComponent(3).setEnabled(true);
            southPanel.getComponent(3).setBackground(Color.yellow);
        } else {
            southPanel.getComponent(3).setEnabled(false);
            southPanel.getComponent(3).setBackground(Color.gray);
        }
        if (playerActions[3]) {
            southPanel.getComponent(4).setEnabled(true);
            southPanel.getComponent(4).setBackground(Color.blue);
        } else {
            southPanel.getComponent(4).setEnabled(false);
            southPanel.getComponent(4).setBackground(Color.gray);
        }
        if (playerActions[4]) {
            southPanel.getComponent(5).setEnabled(true);
            southPanel.getComponent(5).setBackground(Color.orange);
        } else {
            southPanel.getComponent(5).setEnabled(false);
            southPanel.getComponent(5).setBackground(Color.gray);
        }
        frame.setVisible(true);
    }

    public void setDealerTotal(String t) {
        dealerHand.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(144,238,144)),
                "Total: " + t , TitledBorder.RIGHT, TitledBorder.BOTTOM, new Font("SanSerif", Font.BOLD, 20), Color.white
                ), /*      ^^^ this is the main part :) */
                "Dealer's Hand", TitledBorder.LEFT, TitledBorder.TOP, new Font("SanSerif", Font.BOLD, 20), Color.red
        ));
    }
    public void setPlayerTotal(String t) {
        playerHand.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(144,238,144)),
                "Total: " + t , TitledBorder.RIGHT, TitledBorder.BOTTOM, new Font("SanSerif", Font.BOLD, 20), Color.white
                ), /*      ^^^ this is the main part :) */
                "Player's Hand", TitledBorder.LEFT, TitledBorder.TOP, new Font("SanSerif", Font.BOLD, 20), Color.blue
        ));
    }
    public void setWager(String t) {
        wagerPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(50,1,19)),
                "Total: $" + t , TitledBorder.RIGHT, TitledBorder.BOTTOM, new Font("SanSerif", Font.BOLD, 20), Color.white
                ), /*      ^^^ this is the main part :) */
                "Wager:", TitledBorder.LEFT, TitledBorder.TOP, new Font("SanSerif", Font.BOLD, 20), Color.white
        ));
    }
    public void setMoney(String t) {
        moneyPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(50,1,19)),
                "Total: $" + t , TitledBorder.RIGHT, TitledBorder.BOTTOM, new Font("SanSerif", Font.BOLD, 20), Color.white
                ), /*      ^^^ this is the main part :) */
                "Your Chips:", TitledBorder.LEFT, TitledBorder.TOP, new Font("SanSerif", Font.BOLD, 20), Color.white
        ));
    }
    public void setChips() {
        tenLabel.setText(Collections.frequency(game.chips, Chip.TEN) + "");
        fiftyLabel.setText(Collections.frequency(game.chips, Chip.FIFTY) + "");
        hundredLabel.setText(Collections.frequency(game.chips, Chip.HUNDRED) + "");
        fiveHundredLabel.setText(Collections.frequency(game.chips, Chip.FIVE_HUNDRED) + "");
        thousandLabel.setText(Collections.frequency(game.chips, Chip.THOUSAND) + "");
        setMoney(Chip.sumChipValue(game.chips) + "");
    }
    public void setWageredChips(ArrayList<Chip> chips) {
        wagerPanel.removeAll();
        for (Chip chip : chips) {
            wagerPanel.add(new JLabel(new ImageIcon(new ImageIcon("src/main/resources/images/Chips/"+chip.toString()+".png").getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT))));
        }
    }
}
