import ch.aplu.jcardgame.Card;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
    Class that contains all the different melds.
    In charge of instantiating the different meld hands as Lists.
 */
public class Meld {
    // Baic meld.
    MeldInterface base = new BasicMeld();
    
    // Ace Run based melds.
    MeldInterface aceRun = new AceRunDecorator(new BasicMeld());
    MeldInterface aceRunK = new KingDecorator(new AceRunDecorator(new BasicMeld()));
    MeldInterface aceRunQ = new QueenDecorator(new AceRunDecorator(new BasicMeld()));
    MeldInterface aceRunRM = new KingDecorator(new QueenDecorator(new AceRunDecorator(new BasicMeld())));
    MeldInterface doubleAR = new DoubleDecorator(new AceRunDecorator(new BasicMeld()));

    // Marriage based melds.


    // Pinochle based melds.
    MeldInterface pinochle = new PinochleDecorator(new BasicMeld());
    MeldInterface doubleP = new DoubleDecorator(new PinochleDecorator(new BasicMeld()));

    // Jacks Abound meld.
    MeldInterface jacksAbound = new JacksAboundDecorator(new BasicMeld());

    // Aces Around meld.
    MeldInterface acesAround = new AcesAroundDecorator(new BasicMeld());

    // Dix meld.
    MeldInterface dix = new DixDecorator(new BasicMeld());



    /*
        Create Ace Run Concrete object.
     */
    private List<String> getTenToAceCards() {
        return Arrays.asList(
                Rank.ACE.getRankCardValue() + Pinochle.trumpSuit,
                Rank.JACK.getRankCardValue() + Pinochle.trumpSuit,
                Rank.QUEEN.getRankCardValue() + Pinochle.trumpSuit,
                Rank.KING.getRankCardValue() + Pinochle.trumpSuit,
                Rank.TEN.getRankCardValue() + Pinochle.trumpSuit);
    }

    private List<String> checkTenToAceRun(List<Card> list) {
        List<String> cardsToCheck = getTenToAceCards();
        if (checkCardInList(list, cardsToCheck)) {
            return cardsToCheck;
        }
        return null;
    }

    private List<String> checkAceRunExtraKing(List<Card> list) {
        List<String> cardsToCheck = new ArrayList<>(getTenToAceCards());
        cardsToCheck.add(Rank.KING.getRankCardValue() + Pinochle.trumpSuit);
        if (checkCardInList(list, cardsToCheck)) {
            return cardsToCheck;
        }
        return null;
    }

    private List<String> checkAceRunExtraQueen(List<Card> list) {
        List<String> cardsToCheck = new ArrayList<>(getTenToAceCards());
        cardsToCheck.add(Rank.QUEEN.getRankCardValue() + Pinochle.trumpSuit);

        if (checkCardInList(list, cardsToCheck)) {
            return cardsToCheck;
        }
        return null;
    }

    private List<String> checkRoyalMarriage(List<Card> list) {
        List<String> cardsToCheck = Arrays.asList(
                Rank.QUEEN.getRankCardValue() + Pinochle.trumpSuit,
                Rank.KING.getRankCardValue() + Pinochle.trumpSuit);
        if (checkCardInList(list, cardsToCheck)) {
            return cardsToCheck;
        }
        return null;
    }

    private int calculateMeldingScore(List<Card> list) {
        int score = 0;
        List<String> cardsToRemove = checkAceRunExtraKing(list);
        if (cardsToRemove != null) {
            score += 190;
            list = removeCardFromList(list, cardsToRemove);
        }
        cardsToRemove = checkAceRunExtraQueen(list);
        if (cardsToRemove != null) {
            score += 190;
            list = removeCardFromList(list, cardsToRemove);
        }

        cardsToRemove = checkTenToAceRun(list);
        if (cardsToRemove != null) {
            score += 150;
            list = removeCardFromList(list, cardsToRemove);
        }

        cardsToRemove = checkRoyalMarriage(list);
        if (cardsToRemove != null) {
            score += 40;
            list = removeCardFromList(list, cardsToRemove);
        }
        return score;
    }
}
