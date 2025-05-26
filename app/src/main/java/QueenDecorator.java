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
        if (queenSuit.equals(Pinochle.trumpSuit)){
            if (score==150) {
                // for AR + Q or AR + RM
                score += 40;
            }
            else {
                // for RM score
                score += 20;
            }
        }
        else{
            //for common marriage score
            score += 10;
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

