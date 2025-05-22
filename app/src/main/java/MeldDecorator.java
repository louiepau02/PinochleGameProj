import java.util.List;

public class MeldDecorator implements MeldInterface {
    protected MeldInterface decoratedAceRun;

    public MeldDecorator(MeldInterface decoratedAceRun){
        this.decoratedAceRun = decoratedAceRun;
    }

    @Override
    public int getScore() {
        return decoratedAceRun.getScore();
    }

    @Override
    public List<String> getHandToCheck() {
        return decoratedAceRun.getHandToCheck();
    }
}
