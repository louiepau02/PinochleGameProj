import java.util.Arrays;
import java.util.List;

public class AcesAroundDecorator extends MeldDecorator {
    public AcesAroundDecorator(MeldInterface decoratedMeld) {super(decoratedMeld);}

    @Override
    public int getScore() {
        int score = decoratedAceRun.getScore();
        score += 100;
        return score;
    }

    @Override
    public List<String> getHandToCheck() {
        List<String> handToCheck = decoratedAceRun.getHandToCheck();

        handToCheck.addAll(Arrays.asList(
                Rank.ACE.getRankCardValue() + Suit.DIAMONDS.getSuitShortHand(),
                Rank.ACE.getRankCardValue() + Suit.HEARTS.getSuitShortHand(),
                Rank.ACE.getRankCardValue() + Suit.SPADES.getSuitShortHand(),
                Rank.ACE.getRankCardValue() + Suit.CLUBS.getSuitShortHand()
        ));

        return handToCheck;
    }
}

