import ch.aplu.jcardgame.Card;
import ch.aplu.jgamegrid.GGButton;
import ch.aplu.jgamegrid.GGButtonListener;
import ch.aplu.jgamegrid.Location;
import ch.aplu.jgamegrid.TextActor;

import java.awt.*;
import java.util.*;
import java.util.List;

public class SuitAnalyzer {
    private final List<Card> cards;

    private final Map<String, Integer> suitCount = new HashMap<>();
    private final Map<Suit, Integer> highValueCount = new HashMap<>();

    public SuitAnalyzer(List<Card> cards) {
        this.cards = cards;
        analyze();
    }

    /*
        To get suitCount and highestValueCounts done:)
        might be useful for bidding as well - reference from there
     */
    public void analyze(){
        //the dictionary
        for(Card card : cards){
            String suit = Suit.valueOf(card.getSuit().toString()).getSuitShortHand();
            // Merge normal suits and xxTWO into a single key
            if (suit.contains("TWO")) {
                // it contains TWO
                suit = suit.replace("TWO", "");
            }

            if (suitCount.containsKey(suit)){
                suitCount.put(suit, suitCount.get(suit) + 1);
            } else {
                suitCount.put(suit, 1);
            }
        }
    }

    public String findSmallestSuit(String trumpSuit){
        List<String> candidates = new ArrayList<>();
        int min = Integer.MAX_VALUE;

        //find the smallest suit in hand:)
        for (Map.Entry<String, Integer> entry : suitCount.entrySet()){
            String suit = entry.getKey();
            if (suit.equals(trumpSuit)) continue;
            int count = entry.getValue();
            if (count < min) {
                min = count;
                candidates.clear();
                candidates.add(suit);
            } else if (count == min) {
                candidates.add(suit);
            }
        }
        return candidates.isEmpty() ? null : candidates.get(new Random().nextInt(candidates.size()));
    }

    public Map<String, Integer> getSuitCount() {
        return suitCount;
    }
}




/*
private int bidThreshold(Map<String, Integer> suitCount, ArrayList<Card> hand, int playerIndex){

        int maxSuitValue = Collections.max(suitCount.values());


        int largestNum = 0;
        for (String key : suitCount.keySet()) { // diamonds, hearts, spades, club
            int tempcount = 0;

            for (Card card : hand){
                if (card.getRank()== Rank.ACE){tempcount += 11;} // Aces
                if (card.getRank()== Rank.TEN){tempcount += 10;} // 10s
                if (card.getRank()== Rank.KING){tempcount += 4;} // Kings
                if (tempcount > largestNum){largestNum = tempcount;}
            }
        }

        return (Math.max(maxSuitValue, largestNum) + pinochle.getScores(playerIndex)); // need to find this;
    }

 */
