import java.util.List;

public class QueenDecorator extends MeldDecorator {
    private String queenSuit;

    public QueenDecorator(MeldInterface decoratedMeld) {
        super(decoratedMeld);
        queenSuit = Pinochle.trumpSuit;
    }

    public QueenDecorator(MeldInterface decoratedMeld, String suit) {
        super(decoratedMeld);
        queenSuit = suit;
    }

    @Override
    public int getScore() {
        int score = decoratedMeld.getScore();
        if(score==190){
            score += 40;
        }
        if(score==0){
            score += 20;
        }
        return score;
    }

    @Override
    public List<String> getHandToCheck() {
        List<String> handToCheck = decoratedMeld.getHandToCheck();
        handToCheck.add((Rank.QUEEN.getRankCardValue() + queenSuit));

        return handToCheck;
    }

}

