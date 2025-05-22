import ch.aplu.jcardgame.Card;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
    Class that contains all the different melds.
    In charge of instantiating the different meld hands as Lists.
 */
public class Meld {

    private List<String> getTenToAceCards() {
        return Arrays.asList(
                Rank.ACE.getRankCardValue() + trumpSuit,
                Rank.JACK.getRankCardValue() + trumpSuit,
                Rank.QUEEN.getRankCardValue() + trumpSuit,
                Rank.KING.getRankCardValue() + trumpSuit,
                Rank.TEN.getRankCardValue() + trumpSuit);
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
        cardsToCheck.add(Rank.KING.getRankCardValue() + trumpSuit);
        if (checkCardInList(list, cardsToCheck)) {
            return cardsToCheck;
        }
        return null;
    }

    private List<String> checkAceRunExtraQueen(List<Card> list) {
        List<String> cardsToCheck = new ArrayList<>(getTenToAceCards());
        cardsToCheck.add(Rank.QUEEN.getRankCardValue() + trumpSuit);

        if (checkCardInList(list, cardsToCheck)) {
            return cardsToCheck;
        }
        return null;
    }

    private List<String> checkRoyalMarriage(List<Card> list) {
        List<String> cardsToCheck = Arrays.asList(
                Rank.QUEEN.getRankCardValue() + trumpSuit,
                Rank.KING.getRankCardValue() + trumpSuit);
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
