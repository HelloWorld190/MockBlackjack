package com.HelloWorld190.MockBlackJack;
import java.util.ArrayList;
import java.util.Arrays;

public enum Chip {
    TEN(10), FIFTY(50), HUNDRED(100), FIVE_HUNDRED(500), THOUSAND(1000);
    
    private int value;
    
    private Chip(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
    public static Chip getChip(int value) {
        switch (value) {
            case 1000:
                return Chip.THOUSAND;
            case 500:
                return Chip.FIVE_HUNDRED;
            case 100:
                return Chip.HUNDRED;
            case 50:
                return Chip.FIFTY;
            case 10:
                return Chip.TEN;
        }
        return null;
    }
    public static ArrayList<Chip> breakdown(Chip chip) {
        switch (chip) {
            case THOUSAND:
                return new ArrayList<Chip>(
                    Arrays.asList(Chip.FIVE_HUNDRED, Chip.FIVE_HUNDRED)
                );
            case FIVE_HUNDRED:
                return new ArrayList<Chip>(
                    Arrays.asList(Chip.HUNDRED, Chip.HUNDRED, Chip.HUNDRED, Chip.HUNDRED, Chip.HUNDRED)
                );
            case HUNDRED:
                return new ArrayList<Chip>(
                    Arrays.asList(Chip.FIFTY, Chip.FIFTY)
                );
            case FIFTY:
                return new ArrayList<Chip>(
                    Arrays.asList(Chip.TEN, Chip.TEN, Chip.TEN, Chip.TEN, Chip.TEN)
                );
            case TEN:
                throw new IllegalArgumentException("Cannot breakdown a ten chip");
        }
        return null;
    }
    public static int sumChipValue(ArrayList<Chip> chips) {
        int total = 0;
        for (Chip chip : chips) {
            total += chip.getValue();
        }
        return total;
    }
}
