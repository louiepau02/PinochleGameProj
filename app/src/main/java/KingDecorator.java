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
        handToCheck.add((Rank.KING.getRankCardValue() + kingSuit));
        System.out.println("KING DECORATOR, SUIT" + kingSuit);

        return handToCheck;
    }

}

