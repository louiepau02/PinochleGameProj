import java.util.Arrays;
import java.util.List;


public class AceRunDecorator extends MeldDecorator {
    public AceRunDecorator(MeldInterface decoratedMeld) {super(decoratedMeld);}

    @Override
    public int getScore() {
        int score = decoratedMeld.getScore();
        score += 150;
        return score;
    }

    @Override
    public List<String> getHandToCheck() {
        List<String> handToCheck = decoratedMeld.getHandToCheck();
        handToCheck.addAll(Arrays.asList(
            Rank.ACE.getRankCardValue() + Pinochle.trumpSuit,
            Rank.JACK.getRankCardValue() + Pinochle.trumpSuit,
            Rank.QUEEN.getRankCardValue() + Pinochle.trumpSuit,
            Rank.KING.getRankCardValue() + Pinochle.trumpSuit,
            Rank.TEN.getRankCardValue() + Pinochle.trumpSuit
        ));

        return handToCheck;
    }
}

