import java.util.List;

public class QueenDecorator extends MeldDecorator {
    public QueenDecorator(MeldInterface decoratedMeld) {super(decoratedMeld);}

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
        handToCheck.add((Rank.QUEEN.getRankCardValue() + Pinochle.trumpSuit));

        return handToCheck;
    }
}

