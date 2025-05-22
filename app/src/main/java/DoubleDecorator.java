import java.util.List;

public class DoubleDecorator extends MeldDecorator {
    public DoubleDecorator(MeldInterface decoratedMeld) {super(decoratedMeld);}

    @Override
    public int getScore() {
        int score = decoratedAceRun.getScore();
        // If the hand is an Ace Run.
        if(score == 150){
            score *= 10;
        }
        // If the hand is a Pinochle.
        else {
            score = 300;
        }
        return score;
    }

    @Override
    public List<String> getHandToCheck() {
        List<String> handToCheck = decoratedAceRun.getHandToCheck();
        handToCheck.addAll(handToCheck);
        return handToCheck;
    }
}

