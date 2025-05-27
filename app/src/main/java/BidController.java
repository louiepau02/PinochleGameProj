import ch.aplu.jcardgame.Card;
import ch.aplu.jgamegrid.GGButton;
import ch.aplu.jgamegrid.GGButtonListener;
import ch.aplu.jgamegrid.Location;
import ch.aplu.jgamegrid.TextActor;

import java.awt.*;
import java.util.*;
import java.util.List;

public class BidController {
    private int currentBid = 0;
    Pinochle pinochle;
    Properties properties;

    //from pinochle class
    private boolean hasHumanBid = false;
    private int humanBid = 0;
    private final int BID_SELECTION_VALUE = 10;
    private final int MAX_SINGLE_BID = 20;
    private boolean hasComputerPassed = false;
    private boolean hasHumanPassed = false;
    private final List<Integer> computerAutoBids = new ArrayList<>();
    private final List<Integer> humanAutoBids = new ArrayList<>();
    private int computerAutoBidIndex = 0;
    private int humanAutoBidIndex = 0;
    public static final String RANDOM_BID = "random";
    public static final String COMPUTER_BID = "computer";
    public static final String HUMAN_BID = "human";

    private final int COMPUTER_PLAYER_INDEX = 0;
    private final int HUMAN_PLAYER_INDEX = 1;
    private int bidWinPlayerIndex = 0;

    private final int nbPlayers;

    private boolean isAuto = false;
    private int totalMeldScore = 0; //for computer



    public BidController(Pinochle game, Properties properties, int bid, int playerNum) {
        this.pinochle = game;
        this.properties = properties;
        this.currentBid = bid;
        this.nbPlayers = playerNum;
        //other attributes we need
    }

    private void initBids() {
        pinochle.addActor(pinochle.getBidSelectionActor(), pinochle.getBidSelectionLocation());
        pinochle.addActor(pinochle.getBidConfirmActor(), pinochle.getBidConfirmLocation());
        pinochle.addActor(pinochle.getBidPassActor(), pinochle.getBidPassLocation());

        pinochle.addActor(pinochle.getPlayerBidActor(), pinochle.getPlayerBidLocation());
        pinochle.addActor(pinochle.getCurrentBidActor(), pinochle.getCurrentBidLocation());
        pinochle.addActor(pinochle.getNewBidActor(), pinochle.getNewBidLocation());

        pinochle.setActorOnTop(pinochle.getBidSelectionActor());
        pinochle.setActorOnTop(pinochle.getBidConfirmActor());
        pinochle.setActorOnTop(pinochle.getBidPassActor());

        pinochle.getBidSelectionActor().setActEnabled(false);
        pinochle.getBidConfirmActor().setActEnabled(false);
        pinochle.getBidPassActor().setActEnabled(false);

        hasComputerPassed = false;

        System.out.println("init bids");
        pinochle.getBidSelectionActor().addButtonListener(new GGButtonListener() {
            @Override
            public void buttonPressed(GGButton ggButton) {
                hasHumanBid = false;

                if (humanBid >= MAX_SINGLE_BID) {
                    pinochle.getBidSelectionActor().setActEnabled(false);
                    pinochle.setStatus("Maximum amount of a single bid reached");
                } else {
                    humanBid += BID_SELECTION_VALUE;
                }
                updateBidText(HUMAN_PLAYER_INDEX, humanBid + currentBid);
            }

            @Override
            public void buttonReleased(GGButton ggButton) {
            }

            @Override
            public void buttonClicked(GGButton ggButton) {
            }
        });

        pinochle.getBidConfirmActor().addButtonListener(new GGButtonListener() {
            @Override
            public void buttonPressed(GGButton ggButton) {
                currentBid = currentBid + humanBid;
                hasHumanBid = true;
                humanBid = 0;
                updateBidText(HUMAN_PLAYER_INDEX, currentBid);
                pinochle.setStatus("");
            }

            @Override
            public void buttonReleased(GGButton ggButton) {
            }

            @Override
            public void buttonClicked(GGButton ggButton) {
            }
        });

        pinochle.getBidPassActor().addButtonListener(new GGButtonListener() {
            @Override
            public void buttonPressed(GGButton ggButton) {
                updateBidText(HUMAN_PLAYER_INDEX, 0);
                humanBid = 0;
                hasHumanPassed = true;
                pinochle.setStatus("");
            }

            @Override
            public void buttonReleased(GGButton ggButton) {
            }

            @Override
            public void buttonClicked(GGButton ggButton) {
            }
        });
    }

    private void removeBids() {
        pinochle.removeActor(pinochle.getBidSelectionActor());
        pinochle.removeActor(pinochle.getBidConfirmActor());
        pinochle.removeActor(pinochle.getBidPassActor());

        pinochle.removeActor(pinochle.getNewBidActor());
    }

    private void removeBidText() {
        pinochle.removeActor(pinochle.getCurrentBidActor());
        pinochle.removeActor(pinochle.getNewBidActor());
        pinochle.removeActor(pinochle.getPlayerBidActor());
    }

    private void updateBidText(int playerIndex, int newBid) {
        //System.out.println("updating bid text");
        String playerBidString = "";
        switch (playerIndex) {
            case -1:
                playerBidString = "Bid";
                break;
            case 0:
                playerBidString = "Computer Bid";
                break;
            case 1:
                playerBidString = "Human Bid";
                break;
        }

        removeBidText();
        pinochle.setCurrentBidActor(new TextActor("Current Bid: " + currentBid,
                Color.WHITE, pinochle.getBGcolor(), pinochle.getSmallFont()));
        pinochle.addActor(pinochle.getCurrentBidActor(), pinochle.getCurrentBidLocation());

        String newBidString = newBid == 0 ? "" : String.valueOf(newBid);
        pinochle.setNewBidActor(new TextActor("New Bid: " + newBidString,
                Color.WHITE, pinochle.getBGcolor(), pinochle.getSmallFont()));
        pinochle.addActor(pinochle.getNewBidActor(), pinochle.getNewBidLocation());

        pinochle.setPlayerBidActor(new TextActor(playerBidString, Color.WHITE,
                pinochle.getBGcolor(), pinochle.getSmallFont()));
        pinochle.addActor(pinochle.getPlayerBidActor(), pinochle.getPlayerBidLocation());

        pinochle.delay(pinochle.getDelayTime());
    }

    private void displayBidButtons(boolean isShown) {
        pinochle.getBidSelectionActor().setActEnabled(isShown);
        pinochle.getBidConfirmActor().setActEnabled(isShown);
        pinochle.getBidPassActor().setActEnabled(isShown);
    }

    private void askForBidForPlayerIndex(int playerIndex, boolean isFirst) {
        ArrayList<Card> hand = pinochle.getHands(playerIndex);
        Map<String, Integer> suitCount = new HashMap<>(); // dictionary
        boolean moreThanSix = false;

        if (playerIndex == COMPUTER_PLAYER_INDEX) {
            System.out.println("computer bidding now");
            int bidValue = 0;
            if (isAuto && computerAutoBids != null && computerAutoBidIndex < computerAutoBids.size()) {
                bidValue = computerAutoBids.get(computerAutoBidIndex);
                computerAutoBidIndex++;
            } else {
                // Populate the dictionary -> access to hand
                for (Card card : hand){
                    String suit = card.getSuit().toString();
                    // Merge normal suits and xxTWO into a single key
                    if (suit.contains("TWO")) {
                        // it contains TWO
                        suit = suit.replace("TWO", "");
                    }

                    if (suitCount.containsKey(suit)){
                        suitCount.put(suit, suitCount.get(suit) + 1);
                    } else {
                        suitCount.put(suit, 1);
                    }
                }

                countTempTrumpSuit(suitCount); // get the temp Trump suit - not being selected yet
                MeldScoringCalculator meldScoreCalculator = new MeldScoringCalculator();
                totalMeldScore = meldScoreCalculator.calculateScore(pinochle.getHands(COMPUTER_PLAYER_INDEX));
                System.out.println("meldscore for computer: " + totalMeldScore);

                if (isFirst){
                    // Computer has first bid
                    // Opening bid will be equal to the total meld score of its hand.
                    System.out.println("Computer is the first bidder");
                    currentBid += totalMeldScore;
                } else {
                    for (Map.Entry<String, Integer> entry : suitCount.entrySet()){
                        if (entry.getValue() >= 6) { // If hand has 6 or more cards in the same suit
                            // Raise the bid by 20
                            bidValue += 20;
                            moreThanSix = true;
                        }
                    }

                    if(!moreThanSix){
                        // Raise by 10
                        bidValue += 10;
                    }

                    int bidThreshold = bidThreshold(suitCount, hand, playerIndex);

                    if ((currentBid + bidValue) < bidThreshold){
                        updateBidText(playerIndex, currentBid + bidValue);
                    }else {
                        hasComputerPassed = true;
                    }
                }
            }



            pinochle.delay(pinochle.getThinkingTime());
            if (bidValue == 0) {
                hasComputerPassed = true;
                hasHumanBid = false;

                return;
            }

            currentBid += bidValue;
            updateBidText(playerIndex, 0);
            hasHumanBid = false;
        } else {
            System.out.println("human bidding now");
            displayBidButtons(true);
            updateBidText(playerIndex, 0);
            if (isAuto && humanAutoBids != null && humanAutoBidIndex < humanAutoBids.size()) {
                humanBid = humanAutoBids.get(humanAutoBidIndex);
                currentBid = currentBid + humanBid;
                humanAutoBidIndex++;
                if (humanBid == 0) {
                    hasHumanPassed = true;
                }
                updateBidText(HUMAN_PLAYER_INDEX, currentBid);
            } else {
                while (!hasHumanBid && !hasHumanPassed) pinochle.delay(pinochle.getDelayTime());
            }
            hasHumanBid = true;
        }
    }

    //computer player's hand??
    private void countTempTrumpSuit(Map<String, Integer> suitCount){
        List<String> tempTrumpSuit = new ArrayList<>();
        int maxCount = -1;
        System.out.println("finding the trump suit - in bid controller");
        /* Pick the suit with most cards of the same suit in hand*/
        for(Map.Entry<String, Integer> entry : suitCount.entrySet()){
            String suit = entry.getKey();
            int count = entry.getValue();

            if (count > maxCount) {
                maxCount = count;
                tempTrumpSuit.clear();
                tempTrumpSuit.add(suit);
            } else if (count == maxCount) {
                tempTrumpSuit.add(suit);
            }
        }

        // choose randomly if there is more than one suit with same count
        if (tempTrumpSuit.size() == 1) {
            pinochle.setTrumpSuit(tempTrumpSuit.get(0));
        } else {
            Random rand = new Random();
            pinochle.setTrumpSuit(tempTrumpSuit.get(rand.nextInt(tempTrumpSuit.size())));
        }

        System.out.println("the temp trump suit:"+ pinochle.trumpSuit);
    }

    /*
        Return the maximum between:
        The total card value of the majority suit (the suit that has the most cards), or
        The total card value of the suit that contains the most Aces, 10s, and Kings.
     */
    private int bidThreshold(Map<String, Integer> suitCount, ArrayList<Card> hand, int playerIndex){

        int maxSuitValue = Collections.max(suitCount.values());


        int largestNum = 0;
        for (String key : suitCount.keySet()) { // diamonds, hearts, spades, club
            int tempcount = 0;

            for (Card card : hand){
                if (card.getRank()== Rank.ACE){tempcount += 11;} // Aces
                if (card.getRank()== Rank.TEN){tempcount += 10;} // 10s
                if (card.getRank()== Rank.KING){tempcount += 4;} // Kings
                if (tempcount > largestNum){largestNum = tempcount;}
            }
        }

        return (Math.max(maxSuitValue, largestNum) + pinochle.getScores(playerIndex)); // need to find this;
    }

    public void askForBid() {
        initBids();
        displayBidButtons(false);
        String bidOrder = properties.getProperty("players.bid_first", "random"); //human
        String player0Bids = properties.getProperty("players.0.bids", ""); //10,20,10,20,0
        String player1Bids = properties.getProperty("players.1.bids", ""); // 0,20,10,20,0

        if (player0Bids != null) {
            if (!player0Bids.isEmpty()) {
                java.util.List<String> bidStrings = Arrays.asList(player0Bids.split(","));
                computerAutoBids.addAll(bidStrings.stream().map(Integer::parseInt).toList());
            }
        }

        if (player1Bids != null) {
            if (!player1Bids.isEmpty()) {
                List<String> bidStrings = Arrays.asList(player1Bids.split(","));
                humanAutoBids.addAll(bidStrings.stream().map(Integer::parseInt).toList());
            }
        }

        boolean isContinueBidding = true;
        updateBidText(-1, 0);
        Random rand = new Random(1);
        int playerIndex = switch (bidOrder) {
            case RANDOM_BID -> rand.nextInt(nbPlayers);
            case COMPUTER_BID -> COMPUTER_PLAYER_INDEX;
            case HUMAN_BID -> HUMAN_PLAYER_INDEX;
            default -> COMPUTER_PLAYER_INDEX;
        };

        playerIndex = 0;

        // flag
        boolean isFirst = true;
        do {
            for (int i = 0; i < nbPlayers; i++) {
                System.out.println("it's player" + i + "turn");
                askForBidForPlayerIndex(playerIndex, isFirst);
                isFirst = false;
                playerIndex = (playerIndex + 1) % nbPlayers;
                isContinueBidding = !hasHumanPassed && !hasComputerPassed;
                if (!isContinueBidding) {
                    bidWinPlayerIndex = playerIndex;
                    break;
                }
            }
        } while (isContinueBidding);

        removeBids();
        updateBidResult();
        pinochle.addBidInfoToLog();
    }

    private void updateBidResult() {
        pinochle.removeActor(pinochle.getPlayerBidActor());
        pinochle.removeActor(pinochle.getCurrentBidActor());

        pinochle.setCurrentBidActor(new TextActor("Current Bid: " + currentBid,
                Color.WHITE, pinochle.getBGcolor(), pinochle.getSmallFont()));
        pinochle.addActor(pinochle.getCurrentBidActor(), pinochle.getCurrentBidLocation());

        String playerBidString = bidWinPlayerIndex == COMPUTER_PLAYER_INDEX ? "Computer Win" : "Human Win";
        pinochle.setPlayerBidActor(new TextActor(playerBidString, Color.WHITE,
                pinochle.getBGcolor(), pinochle.getSmallFont()));
        pinochle.addActor(pinochle.getPlayerBidActor(), pinochle.getPlayerBidLocation());
    }

    public int getBidWinPlayerIndex(){
        return bidWinPlayerIndex;
    }

}
