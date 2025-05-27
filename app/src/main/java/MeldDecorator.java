import java.util.List;

public class MeldDecorator implements MeldInterface {
    protected MeldInterface decoratedMeld;
    protected String trumpSuit;

    public MeldDecorator(MeldInterface decoratedMeld){
        this.decoratedMeld = decoratedMeld;
    }

    @Override
    public int getScore() {
        return decoratedMeld.getScore();
    }

    @Override
    public List<String> getHandToCheck() {
        return decoratedMeld.getHandToCheck();
    }

}
