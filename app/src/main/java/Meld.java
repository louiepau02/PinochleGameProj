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

    private Meld() {
        populateMeld();
    }

    public static Meld getInstance() {
        if (instance == null) {
            instance = new Meld();
        }
        return instance;
    }

    private void populateMeld() {
        // Create all the melds

        // Basic meld.
        MeldDecorator base = new MeldDecorator(new BasicMeld());

        // Ace Run based melds.
        MeldDecorator aceRun = new AceRunDecorator(new BasicMeld());
        MeldDecorator aceRunK = new KingDecorator(new AceRunDecorator(new BasicMeld()));
        MeldDecorator aceRunQ = new QueenDecorator(new AceRunDecorator(new BasicMeld()));
        MeldDecorator aceRunRM = new KingDecorator(new QueenDecorator(new AceRunDecorator(new BasicMeld())));
        MeldDecorator doubleAR = new DoubleDecorator(new AceRunDecorator(new BasicMeld()));

        // Marriage based melds.
        MeldDecorator RM = new KingDecorator(new QueenDecorator(new BasicMeld()));

        // initialise all possible melds for CM
//        MeldDecorator heartCM = new MeldDecorator(new BasicMeld());
//        MeldDecorator spadeCM = new MeldDecorator(new BasicMeld());
//        MeldDecorator clubsCM = new MeldDecorator(new BasicMeld());
//        MeldDecorator diamondCM = new MeldDecorator(new BasicMeld());
//
//        for (Suit suit: Suit.values()){
//            if (suit.getSuitShortHand()!=Pinochle.trumpSuit){
//                String cardSuit = suit.getSuitShortHand();
//                switch (cardSuit) {
//                    case "H":
//                        heartCM = new KingDecorator(new QueenDecorator(new BasicMeld(Suit.HEARTS)));
//                        break;
//                    case "S":
//                        spadeCM = new KingDecorator(new QueenDecorator(new BasicMeld(Suit.SPADES)));
//                        break;
//                    case "C":
//                        clubsCM = new KingDecorator(new QueenDecorator(new BasicMeld(Suit.CLUBS)));
//                        break;
//                    case "D":
//                        diamondCM = new KingDecorator(new QueenDecorator(new BasicMeld(Suit.DIAMONDS)));
//                        break;
//                }
//            }
//        }


        // Pinochle based melds.
        MeldDecorator pinochle = new PinochleDecorator(new BasicMeld());
        MeldDecorator doubleP = new DoubleDecorator(new PinochleDecorator(new BasicMeld()));

        // Jacks Abound meld.
        MeldDecorator jacksAbound = new JacksAboundDecorator(new BasicMeld());

        // Aces Around meld.
        MeldDecorator acesAround = new AcesAroundDecorator(new BasicMeld());

        // Dix meld.
        MeldDecorator dix = new DixDecorator(new BasicMeld());


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

    public List<MeldDecorator> getMelds() {
        return melds;
    }

}