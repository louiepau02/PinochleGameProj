import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
    Class that contains all the possible melds.
    In charge of instantiating the different meld hands as Lists.
 */
public final class Meld {
    private static Meld instance;
    public List<MeldDecorator> melds = new ArrayList<>();

    private Meld(boolean additional) {
        populateMeld(additional);
    }

    public static Meld getInstance(boolean additional) {
        if (instance == null) {
            instance = new Meld(additional);
        }
        return instance;
    }

    private void populateMeld(boolean additional) {
        // Create all the melds - if not additional meld
        if(!additional){
            // Basic meld - without additional feature
            MeldDecorator base = new MeldDecorator(new BasicMeld());

            // Ace Run based melds.
            MeldDecorator aceRun = new AceRunDecorator(new BasicMeld());
            MeldDecorator aceRunK = new KingDecorator(new AceRunDecorator(new BasicMeld()));
            MeldDecorator aceRunQ = new QueenDecorator(new AceRunDecorator(new BasicMeld()));

            // Marriage based melds.
            MeldDecorator RM = new KingDecorator(new QueenDecorator(new BasicMeld()));

            // Add melds to list in the correct order based on score in decreasing order.
            assert melds != null;
            this.melds.addAll(Arrays.asList(
                    aceRunK, aceRunQ, aceRun, RM
                    /*common marriage*/
            ));
        } else {
            // Basic meld.
            MeldDecorator base = new MeldDecorator(new BasicMeld());

            // Ace Run based melds.
            MeldDecorator aceRun = new AceRunDecorator(base);
            MeldDecorator aceRunK = new KingDecorator(new AceRunDecorator(base));
            MeldDecorator aceRunQ = new QueenDecorator(new AceRunDecorator(base));
            MeldDecorator aceRunRM = new KingDecorator(new QueenDecorator(new AceRunDecorator((base))));
            MeldDecorator doubleAR = new DoubleDecorator(new AceRunDecorator(base));

            // Marriage based melds.
            MeldDecorator RM = new KingDecorator(new QueenDecorator(base));


            // Pinochle based melds.
            MeldDecorator pinochle = new PinochleDecorator(base);
            MeldDecorator doubleP = new DoubleDecorator(new PinochleDecorator(base));

            // Jacks Abound meld.
            MeldDecorator jacksAbound = new JacksAboundDecorator(base);

            // Aces Around meld.
            MeldDecorator acesAround = new AcesAroundDecorator(base);

            // Dix meld.
            MeldDecorator dix = new DixDecorator(base);


            // Add melds to list in the correct order based on score in decreasing order.
            assert melds != null;
            this.melds.addAll(Arrays.asList(
                    doubleAR, jacksAbound, doubleP, aceRunRM, aceRunK, aceRunQ, aceRun, acesAround,
                    RM, pinochle, dix
                    /*common marriage*/
            ));

            // add in the meld hands for common marriage
            // repeating shorthands so make an array
            List<Suit> mainSuits = Arrays.asList(Suit.SPADES, Suit.DIAMONDS, Suit.HEARTS, Suit.CLUBS);
            for (Suit suit: mainSuits){
                String currSuit = suit.getSuitShortHand();
                if (!currSuit.equals(Pinochle.trumpSuit)){
                    this.melds.add(new KingDecorator(new QueenDecorator(new BasicMeld(), currSuit), currSuit));
                }
            }
        }
    }

    public List<MeldDecorator> getMelds() {
        return melds;
    }

}