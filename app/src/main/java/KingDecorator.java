import java.util.List;

public class KingDecorator extends MeldDecorator {
    public KingDecorator(MeldInterface decoratedMeld) {super(decoratedMeld);}

    @Override
    public int getScore() {
        int score = decoratedMeld.getScore();
        if(score==190){
            score += 40;
        }
        if(score==20){
            score += 20;
        }
        return score;
    }

    @Override
    public List<String> getHandToCheck() {
        List<String> handToCheck = decoratedMeld.getHandToCheck();
        handToCheck.add((Rank.KING.getRankCardValue() + Pinochle.trumpSuit));

        return handToCheck;
    }
}

