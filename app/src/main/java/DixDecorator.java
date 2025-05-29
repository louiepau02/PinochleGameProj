import java.util.List;

public class DixDecorator extends MeldDecorator {
    public DixDecorator(MeldInterface decoratedMeld) {super(decoratedMeld);}

    @Override
    public int getScore() {
        int score = decoratedMeld.getScore();
        score += 10;
        return score;
    }

    @Override
    public List<String> getHandToCheck() {
        List<String> handToCheck = decoratedMeld.getHandToCheck();
        handToCheck.add((Rank.NINE.getRankCardValue() + Pinochle.trumpSuit));

        return handToCheck;
    }
}

