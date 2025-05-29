import ch.aplu.jcardgame.*;

import java.util.*;
import java.util.List;

public class MeldScoringCalculator {
    private final List<MeldDecorator> meldsToCheck;
    public MeldScoringCalculator() {
        // grab the pre-built, priority-ordered decorator chains
        this.meldsToCheck = Meld.getInstance().getMelds();
    }

    public int calculateScore(ArrayList<Card> cardList){
        return this.calculateMeldingScore(cardList);
    }

    /*
       calculate the player's score based off their cards in hand
       by comparing
    */
    private int calculateMeldingScore(List<Card> list){
        int totalScore = 0;


        //testing - print statement
        System.out.println("Initial hand cards: ");
        for (Card c : list) {
            System.out.print(getCardName(c) + " ");
        }
        System.out.println(" ");

        for(MeldDecorator meld : meldsToCheck){
            List<String> cardsToRemove = meld.getHandToCheck();

            //testing - print statement
            System.out.println("Checking meld: " + meld.getClass().getSimpleName());
            //testing - print statement
            System.out.println("Meld requires cards: " + cardsToRemove);
            //for each meld we call getHandToCheck to create the list(using card_to_remove logic)

            //actual code
            if(checkCardInMeld(list, cardsToRemove)){
                //System.out.println("score to be added" + meld.getScore());
                totalScore += meld.getScore();
                list = removeCardsFromHand(list, cardsToRemove);
            }
        }

        return totalScore;
    }

    private boolean checkCardInMeld(List<Card> cardInHand, List<String> cardsToCheck) {
        ArrayList<String> cardsToRemove = new ArrayList<>(cardsToCheck);
        for (Card card : cardInHand) {
            String cardName = getCardName(card);
            cardsToRemove.remove(cardName);
        }
        return cardsToRemove.isEmpty();
    }

    private String getCardName(Card card) {
        Suit suit = (Suit) card.getSuit();
        Rank rank = (Rank) card.getRank();
        return rank.getRankCardValue() + suit.getSuitShortHand();
    }

    private List<Card> removeCardsFromHand(List<Card> hand, List<String> cardsToRemove){
        List<Card> newCardList = new ArrayList<>();
        List<String> newCardsToRemove = new ArrayList<>(cardsToRemove);
        for (Card card : hand) {
            String cardName = getCardName(card);
            if (newCardsToRemove.contains(cardName)) {
                newCardsToRemove.remove(cardName);
            } else {
                newCardList.add(card);
            }
        }
        return newCardList;
    }

}
