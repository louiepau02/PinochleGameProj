import java.util.List;

public class QueenDecorator extends MeldDecorator {
    public QueenDecorator(MeldInterface decoratedMeld) {super(decoratedMeld);}

    @Override
    public int getScore() {
        int score = decoratedAceRun.getScore();
        score += 40;
        return score;
    }

    @Override
    public List<String> getHandToCheck() {
        List<String> handToCheck = decoratedAceRun.getHandToCheck();
        handToCheck.add((Rank.QUEEN.getRankCardValue() + Pinochle.trumpSuit));

        return handToCheck;
    }
}

