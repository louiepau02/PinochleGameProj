import java.util.List;

public class KingDecorator extends MeldDecorator {
    private String kingSuit;

    public KingDecorator(MeldInterface decoratedMeld) {
        super(decoratedMeld);
        kingSuit = Pinochle.trumpSuit;
    }

    public KingDecorator(MeldInterface decoratedMeld, String suit){
        super(decoratedMeld);
        kingSuit = suit;
    }

    @Override
    public int getScore() {
        int score = decoratedMeld.getScore();
        if(kingSuit.equals(Pinochle.trumpSuit)){
            if ((score==150)||(score==190)) {
                // for AR + K or AR + RM
                score += 40;
            } else {
                // for RM
                score += 20;
            }
        }
        else{
            // half of common marriage score
            score += 10;
        }
        return score;
    }

    @Override
    public List<String> getHandToCheck() {
        List<String> handToCheck = decoratedMeld.getHandToCheck();
        handToCheck.add((Rank.KING.getRankCardValue() + kingSuit));
        // System.out.println("KING DECORATOR, SUIT" + kingSuit);

        return handToCheck;
    }

}

