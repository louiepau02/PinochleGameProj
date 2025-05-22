import java.util.Arrays;
import java.util.List;

public class JacksAboundDecorator extends MeldDecorator {
    public JacksAboundDecorator(MeldInterface decoratedMeld) {super(decoratedMeld);}

    @Override
    public int getScore() {
        int score = decoratedMeld.getScore();
        score += 400;
        return score;
    }

    @Override
    public List<String> getHandToCheck() {
        List<String> handToCheck = decoratedMeld.getHandToCheck();

        handToCheck.addAll(Arrays.asList(
            Rank.JACK.getRankCardValue() + Suit.DIAMONDS.getSuitShortHand(),
            Rank.JACK.getRankCardValue() + Suit.DIAMONDS.getSuitShortHand(),
            Rank.JACK.getRankCardValue() + Suit.HEARTS.getSuitShortHand(),
            Rank.JACK.getRankCardValue() + Suit.HEARTS.getSuitShortHand(),
            Rank.JACK.getRankCardValue() + Suit.SPADES.getSuitShortHand(),
            Rank.JACK.getRankCardValue() + Suit.SPADES.getSuitShortHand(),
            Rank.JACK.getRankCardValue() + Suit.CLUBS.getSuitShortHand(),
            Rank.JACK.getRankCardValue() + Suit.CLUBS.getSuitShortHand()
        ));

        return handToCheck;
    }
}

