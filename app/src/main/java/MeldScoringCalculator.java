import ch.aplu.jcardgame.*;

import java.util.*;
import java.util.List;

public class MeldScoringCalculator {
    public int getScore(ArrayList<Card> cardList){
        return this.calculateMeldingScore(cardList);
    }


    private int calculateMeldingScore(List<Card> list){
        return 0;
    }

    /*
    private List<String> checkTenToAceRun(List<Card> list) {
        List<String> cardsToCheck = getTenToAceCards();
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

 */

}
