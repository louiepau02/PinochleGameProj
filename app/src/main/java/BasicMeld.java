import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/*
    Concrete class for the Ace Run meld.
 */
public class BasicMeld implements MeldInterface{
    private final List<String> handToCheck;
    private final int score;

    public BasicMeld() {
        handToCheck = new ArrayList<>();
        score = 0;
    }

    @Override
    public int getScore() {
        return score;
    }

    @Override
    public List<String> getHandToCheck() {
        return handToCheck;
    }
}
