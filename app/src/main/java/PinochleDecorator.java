import java.util.Arrays;
import java.util.List;

public class PinochleDecorator extends MeldDecorator {
    public PinochleDecorator(MeldInterface decoratedMeld) {super(decoratedMeld);}

    @Override
    public int getScore() {
        int score = decoratedAceRun.getScore();
        score += 40;
        return score;
    }

    @Override
    public List<String> getHandToCheck() {
        List<String> handToCheck = decoratedAceRun.getHandToCheck();
        handToCheck.addAll(Arrays.asList(
            Rank.JACK.getRankCardValue() + Suit.DIAMONDS.getSuitShortHand(),
            Rank.QUEEN.getRankCardValue() + Suit.SPADES.getSuitShortHand()
        ));

        return handToCheck;
    }
}

