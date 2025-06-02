
import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.*;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("serial")
public class Pinochle extends CardGame {
    private final Map<String, String> trumpImages = new HashMap<>(Map.of(
            Suit.SPADES.getSuitShortHand(), "sprites/bigspade.gif",
            Suit.CLUBS.getSuitShortHand(), "sprites/bigclub.gif",
            Suit.DIAMONDS.getSuitShortHand(), "sprites/bigdiamond.gif",
            Suit.HEARTS.getSuitShortHand(), "sprites/bigheart.gif"));

    static public String trumpSuit = null;

    static public final int seed = 30008; // Original is 30008
    static final Random random = new Random(seed);
    private final Properties properties;
    private final StringBuilder logResult = new StringBuilder();
    private final List<List<String>> playerAutoMovements = new ArrayList<>();


    private final String version = "1.0";
    public final int nbPlayers = 2;
    public final int nbStartCards = 12;
    private final int handWidth = 400;
    private final int trickWidth = 40;
    private int currentBid = 0;
    private final Deck deck = new Deck(Suit.values(), Rank.values(), "cover");
    private final Location[] handLocations = {
            new Location(350, 625),
            new Location(350, 75),
    };

    private final Location[] scoreLocations = {
            new Location(575, 675),
            new Location(25, 25),
    };
    private final TextActor[] scoreActors = {null, null, null, null};

    Font bigFont = new Font("Arial", Font.BOLD, 36);
    Font smallFont = new Font("Arial", Font.BOLD, 18);

    /**
     * Bidding elements
     */

    //GUI related
    private final GGButton bidSelectionActor = new GGButton("sprites/bid_10.gif", false);
    private final GGButton bidConfirmActor = new GGButton("sprites/done30.gif", false);
    private final GGButton bidPassActor = new GGButton("sprites/bid_pass.gif", false);
    private TextActor playerBidActor = new TextActor("Bidding", Color.white, bgColor, smallFont);
    private TextActor currentBidActor = new TextActor("Current Bid: ", Color.white, bgColor, smallFont);
    private TextActor newBidActor = new TextActor("New Bid: ", Color.white, bgColor, smallFont);

    private final Location bidSelectionLocation = new Location(600, 100);
    private final Location bidConfirmLocation = new Location(660, 100);
    private final Location bidPassLocation = new Location(630, 150);
    private final Location playerBidLocation = new Location(550, 30);
    private final Location currentBidLocation = new Location(550, 50);
    private final Location newBidLocation = new Location(550, 75);

    private final int COMPUTER_PLAYER_INDEX = 0;
    private final int HUMAN_PLAYER_INDEX = 1;
    private int bidWinPlayerIndex = 0;

    /**
     * Trump Elements
     */
    private final TextActor trumpInstructionActor = new TextActor("Trump Selection", Color.white, bgColor, smallFont);
    private final GGButton clubTrumpActor = new GGButton("sprites/clubs_item.png", false);
    private final GGButton spadeTrumpActor = new GGButton("sprites/spades_item.png", false);
    private final GGButton diamondTrumpActor = new GGButton("sprites/diamonds_item.png", false);
    private final GGButton heartTrumpActor = new GGButton("sprites/hearts_item.png", false);

    private Actor trumpActor;

    private final Location trumpInstructionLocation = new Location(550, 80);
    private final Location clubTrumpLocation = new Location(580, 100);
    private final Location spadeTrumpLocation = new Location(610, 100);
    private final Location diamondTrumpLocation = new Location(640, 100);
    private final Location heartTrumpLocation = new Location(670, 100);
    private final Location trumpLocation = new Location(620, 120);

    private final Location[] trickHandLocations = {
            new Location(75, 350),
            new Location(625, 350)
    };


    private final Location playingLocation = new Location(350, 350);
    private final Location textLocation = new Location(350, 450);


    private int thinkingTime = 2000;

    private int delayTime = 600;

    private Hand[] hands;
    private Hand[] trickWinningHands;


    public void setStatus(String string) {
        setStatusText(string);
    }

    private int[] scores = new int[nbPlayers];

    private int[] autoIndexHands = new int[nbPlayers];
    private boolean isAuto = false;
    private Hand playingArea;
    private Hand topTwo;
    private Hand pack;

    /**
     * Score Section
     */

    private void initScore() {
        for (int i = 0; i < nbPlayers; i++) {
            // scores[i] = 0;
            String text = "[" + String.valueOf(scores[i]) + "]";
            scoreActors[i] = new TextActor(text, Color.WHITE, bgColor, bigFont);
            addActor(scoreActors[i], scoreLocations[i]);
        }
    }

    private void updateScore(int player) {
        removeActor(scoreActors[player]);
        int displayScore = Math.max(scores[player], 0);
        String text = "P" + player + "[" + String.valueOf(displayScore) + "]";
        scoreActors[player] = new TextActor(text, Color.WHITE, bgColor, bigFont);
        addActor(scoreActors[player], scoreLocations[player]);
    }

    private void initScores() {
        Arrays.fill(scores, 0);
    }


    /**
     * Card Dealing
     *
     * @param list
     * @return
     */

    private Card selected;
    private Card topTwoSelected;

    private void initGame() {
        hands = new Hand[nbPlayers];
        trickWinningHands = new Hand[nbPlayers];
        for (int i = 0; i < nbPlayers; i++) {
            hands[i] = new Hand(deck);
            trickWinningHands[i] = new Hand(deck);
        }
        playingArea = new Hand(deck);
        topTwo = new Hand(deck);
        dealingOut(hands, nbPlayers, nbStartCards);
        playingArea.setView(this, new RowLayout(playingLocation, (playingArea.getNumberOfCards() + 3) * trickWidth));
        playingArea.draw();

        for (int i = 0; i < nbPlayers; i++) {
            hands[i].sort(Hand.SortType.SUITPRIORITY, false);
        }
        // Set up human player for interaction
        CardListener cardListener = new CardAdapter()  // Human Player plays card
        {
            public void leftDoubleClicked(Card card) {
                if (!checkValidTrick(card, hands[HUMAN_PLAYER_INDEX].getCardList(), playingArea.getCardList())) {
                    setStatus("Card is not valid. Player needs to choose higher card of the same suit or trump suit");
                    return;
                }
                selected = card;
                hands[HUMAN_PLAYER_INDEX].setTouchEnabled(false);
            }
        };
        hands[HUMAN_PLAYER_INDEX].addCardListener(cardListener);


        // Add top 2 cards from pack
        for (int i = 0; i < 2; i++) {
            Card tempCard = pack.getCard(i);

            tempCard.removeFromHand(false);
            topTwo.insert(tempCard, true);
        }

        System.out.println(hands[HUMAN_PLAYER_INDEX].getCardList());
        System.out.println(topTwo.getCardList());
        // Define listener for choosing a card from the pack
        CardListener packListener = new CardAdapter()  // Listener for dealing pack
        {
            public void leftDoubleClicked(Card card) {
                setStatus("Card is not valid. Player needs to choose one card from the two.");
                topTwoSelected = card;
                topTwo.setTouchEnabled(false);
            }
        };
        topTwo.addCardListener(packListener);

        // graphics
        RowLayout[] layouts = new RowLayout[nbPlayers];
        for (int i = 0; i < nbPlayers; i++) {
            layouts[i] = new RowLayout(handLocations[i], handWidth);
            layouts[i].setRotationAngle(180 * i);
            hands[i].setView(this, layouts[i]);
            hands[i].setTargetArea(new TargetArea(playingLocation));
            hands[i].draw();
        }

        topTwo.setView(this, new RowLayout(playingLocation, (topTwo.getNumberOfCards() + 3) * trickWidth));

        RowLayout[] trickHandLayouts = new RowLayout[nbPlayers];

        for (int i = 0; i < nbPlayers; i++) {
            trickHandLayouts[i] = new RowLayout(trickHandLocations[i], handWidth);
            trickHandLayouts[i].setRotationAngle(90 + 180 * i);
            trickWinningHands[i].setView(this, trickHandLayouts[i]);
            trickWinningHands[i].draw();
        }
    }


    // return random Card from ArrayList
    public static Card randomCard(ArrayList<Card> list) {
        int x = random.nextInt(list.size());
        return list.get(x);
    }

    private String getCardName(Card card) {
        Suit suit = (Suit) card.getSuit();
        Rank rank = (Rank) card.getRank();
        return rank.getRankCardValue() + suit.getSuitShortHand();
    }

    private boolean checkCardInList(List<Card> cardList, List<String> cardsToCheck) {
        ArrayList<String> cardsToRemove = new ArrayList<>(cardsToCheck);
        for (Card card : cardList) {
            String cardName = getCardName(card);
            cardsToRemove.remove(cardName);
        }
        return cardsToRemove.isEmpty();
    }

    private List<Card> removeCardFromList(List<Card> cardList, List<String> cardsToRemove) {
        List<Card> newCardList = new ArrayList<>();
        List<String> newCardsToRemove = new ArrayList<>(cardsToRemove);
        for (Card card : cardList) {
            String cardName = getCardName(card);
            if (newCardsToRemove.contains(cardName)) {
                newCardsToRemove.remove(cardName);
            } else {
                newCardList.add(card);
            }
        }
        return newCardList;
    }

    public Card getRandomCardForHand(Hand hand) {
        List<Card> existingCards = playingArea.getCardList();
        if (existingCards.isEmpty()) {
            int x = random.nextInt(hand.getCardList().size());
            return hand.getCardList().get(x);
        }

        delay(thinkingTime);
        Card existingCard = existingCards.get(0);
        Card higherCard = getHigherCardFromList(existingCard, hand.getCardList());
        if (higherCard != null) {
            return higherCard;
        }

        Card trumpCard = getTrumpCard(hand.getCardList());
        if (trumpCard != null) {
            return trumpCard;
        }

        int x = random.nextInt(hand.getCardList().size());
        return hand.getCardList().get(x);
    }

    private Rank getRankFromString(String cardName) {
        String rankString = cardName.substring(0, cardName.length() - 1);
        Integer rankValue = Integer.parseInt(rankString);

        for (Rank rank : Rank.values()) {
            if (rank.getShortHandValue() == rankValue) {
                return rank;
            }
        }

        return Rank.ACE;
    }

    private Suit getSuitFromString(String cardName) {
        String rankString = cardName.substring(0, cardName.length() - 1);
        String suitString = cardName.substring(cardName.length() - 1, cardName.length());
        Integer rankValue = Integer.parseInt(rankString);

        for (Suit suit : Suit.values()) {
            if (suit.getSuitShortHand().equals(suitString)) {
                return suit;
            }
        }
        return Suit.CLUBS;
    }


    private Card getCardFromList(List<Card> cards, String cardName) {
        Rank existingRank = getRankFromString(cardName);
        Suit existingSuit = getSuitFromString(cardName);
        for (Card card : cards) {
            Suit suit = (Suit) card.getSuit();
            Rank rank = (Rank) card.getRank();
            if (suit.getSuitShortHand().equals(existingSuit.getSuitShortHand())
                    && rank.getRankCardValue() == existingRank.getRankCardValue()) {
                return card;
            }
        }

        return null;
    }

    private Card applyAutoMovement(Hand hand, String nextMovement) {
        if (hand.isEmpty()) return null;
        String[] cardStrings = nextMovement.split("-");
        String cardDealtString = cardStrings[0];
        if (nextMovement.isEmpty()) {
            return null;
        }
        Card dealt = getCardFromList(hand.getCardList(), cardDealtString);
        if (dealt == null) {
            System.err.println("cannot draw card: " + cardDealtString + " - hand: " + hand.getCardList());
        }

        return dealt;
    }

    private void dealingOut(Hand[] hands, int nbPlayers, int nbCardsPerPlayer) {
        pack = deck.toHand(false);

        for (int i = 0; i < nbPlayers; i++) {
            String initialCardsKey = "players." + i + ".initialcards";
            String initialCardsValue = properties.getProperty(initialCardsKey);
            if (initialCardsValue == null) {
                continue;
            }
            String[] initialCards = initialCardsValue.split(",");
            for (String initialCard : initialCards) {
                if (initialCard.length() <= 1) {
                    continue;
                }
                Card card = getCardFromList(pack.getCardList(), initialCard);
                if (card != null) {
                    card.removeFromHand(false);
                    hands[i].insert(card, false);
                }
            }
        }

        for (int i = 0; i < nbPlayers; i++) {
            int cardsToDealt = nbCardsPerPlayer - hands[i].getNumberOfCards();
            for (int j = 0; j < cardsToDealt; j++) {
                if (pack.isEmpty()) return;
                Card dealt = randomCard(pack.getCardList());
                dealt.removeFromHand(false);
                hands[i].insert(dealt, false);
            }
        }
    }

    /**
     * Bid Section
     */


    private void updateTrumpActor() {
        String trumpImage = trumpImages.get(trumpSuit);
        trumpActor = new Actor(trumpImage);
        addActor(trumpActor, trumpLocation);
    }

    private void askForTrumpCard() {
        if (isAuto) {
            trumpSuit = properties.getProperty("players.trump", "C");
            updateTrumpActor();
            return;
        }

        addActor(trumpInstructionActor, trumpInstructionLocation);
        if (bidWinPlayerIndex == COMPUTER_PLAYER_INDEX) {
            //keep the one:)
            //Suit selectedTrumpSuit = Arrays.stream(Suit.values()).findAny().get();
            //trumpSuit = selectedTrumpSuit.getSuitShortHand();
        } else {
            trumpSuit = null;
            addActor(clubTrumpActor, clubTrumpLocation);
            addActor(spadeTrumpActor, spadeTrumpLocation);
            addActor(heartTrumpActor, heartTrumpLocation);
            addActor(diamondTrumpActor, diamondTrumpLocation);

            GGButtonListener buttonListener = new GGButtonListener() {
                @Override
                public void buttonPressed(GGButton ggButton) {
                    if (ggButton.equals(clubTrumpActor)) {
                        trumpSuit = Suit.CLUBS.getSuitShortHand();
                    } else if (ggButton.equals(spadeTrumpActor)) {
                        trumpSuit = Suit.SPADES.getSuitShortHand();
                    } else if (ggButton.equals(heartTrumpActor)) {
                        trumpSuit = Suit.HEARTS.getSuitShortHand();
                    } else if (ggButton.equals(diamondTrumpActor)) {
                        trumpSuit = Suit.DIAMONDS.getSuitShortHand();
                    }
                }

                @Override
                public void buttonReleased(GGButton ggButton) {
                }

                @Override
                public void buttonClicked(GGButton ggButton) {
                }
            };

            clubTrumpActor.addButtonListener(buttonListener);
            spadeTrumpActor.addButtonListener(buttonListener);
            heartTrumpActor.addButtonListener(buttonListener);
            diamondTrumpActor.addButtonListener(buttonListener);

            while (trumpSuit == null) delay(delayTime);
        }
        removeActor(clubTrumpActor);
        removeActor(spadeTrumpActor);
        removeActor(heartTrumpActor);
        removeActor(diamondTrumpActor);
        updateTrumpActor();
    }


    /**
     * Logging Logic
     *
     * @param player
     * @param card
     */

    private void addCardPlayedToLog(int player, Card card) {
        logResult.append("P" + player + "-");

        Rank cardRank = (Rank) card.getRank();
        Suit cardSuit = (Suit) card.getSuit();
        logResult.append(cardRank.getCardLog() + cardSuit.getSuitShortHand());

        logResult.append(",");
    }

    public void addBidInfoToLog() {
        logResult.append("Bid:" + bidWinPlayerIndex + "-" + currentBid + "\n");
    }

    private void addTrumpInfoToLog() {
        logResult.append("Trump: " + trumpSuit + "\n");
        logResult.append("Melding Scores: " + scores[0] + "-" + scores[1] + "\n");
    }

    private void addRoundInfoToLog(int roundNumber) {
        logResult.append("\n");
        logResult.append("Round" + roundNumber + ":");
    }

    private void addPlayerCardsToLog() {
        logResult.append("Initial Cards:");
        for (int i = 0; i < nbPlayers; i++) {
            logResult.append("P" + i + "-");
            logResult.append(convertCardListoString(hands[i]));
        }
    }

    private String convertCardListoString(Hand hand) {
        StringBuilder sb = new StringBuilder();
        sb.append(hand.getCardList().stream().map(card -> {
            Rank rank = (Rank) card.getRank();
            Suit suit = (Suit) card.getSuit();
            return rank.getCardLog() + suit.getSuitShortHand();
        }).collect(Collectors.joining(",")));
        sb.append("-");
        return sb.toString();
    }

    private void addEndOfGameToLog(List<Integer> winners) {
        logResult.append("\n");
        logResult.append("Trick Winning: ");
        for (int i = 0; i < nbPlayers; i++) {
            logResult.append("P" + i + ":");
            logResult.append(convertCardListoString(trickWinningHands[i]));
        }
        logResult.append("\n");
        logResult.append("Final Score: ");
        for (int i = 0; i < scores.length; i++) {
            logResult.append(scores[i] + ",");
        }
        logResult.append("\n");
        logResult.append("Winners: " + String.join(", ", winners.stream().map(String::valueOf).collect(Collectors.toList())));
    }

    /**
     * Check Trick Taking logic
     *
     * @param card1
     * @param card2
     * @return
     */

    private boolean isSameSuit(Card card1, Card card2) {
        Suit card1Suit = (Suit) card1.getSuit();
        Suit card2Suit = (Suit) card2.getSuit();
        return card1Suit.getSuitShortHand().equals(card2Suit.getSuitShortHand());
    }

    private boolean isHigherRank(Card card1, Card card2) {
        Rank card2Rank = (Rank) card2.getRank();
        Rank card1Rank = (Rank) card1.getRank();
        return card1Rank.getRankCardValue() > card2Rank.getRankCardValue();
    }

    private Card getHigherCardFromList(Card existingCard, List<Card> cards) {
        return cards.stream().filter(playerCard -> {
            return isSameSuit(existingCard, playerCard) && isHigherRank(playerCard, existingCard);
        }).findAny().orElse(null);
    }

    private Card getTrumpCard(List<Card> cards) {
        return cards.stream().filter(playerCard -> {
            Suit playerCardSuit = (Suit) playerCard.getSuit();
            return playerCardSuit.getSuitShortHand().equals(trumpSuit);
        }).findAny().orElse(null);
    }

    private boolean checkValidTrick(Card playingCard, List<Card> playerCards, List<Card> existingCards) {
        if (existingCards.isEmpty()) {
            return true;
        }

        Suit playingSuit = (Suit) playingCard.getSuit();
        Rank playingRank = (Rank) playingCard.getRank();
        Card existingCard = existingCards.get(0);
        Suit existingSuit = (Suit) existingCard.getSuit();
        Rank existingRank = (Rank) existingCard.getRank();

        if (playingSuit.getSuitShortHand().equals(existingSuit.getSuitShortHand()) && playingRank.getRankCardValue() > existingRank.getRankCardValue()) {
            return true;
        }

        Card higherCard = getHigherCardFromList(existingCard, playerCards);
        if (higherCard != null) {
            return false;
        }

        boolean isExistingTrump = existingSuit.getSuitShortHand().equals(trumpSuit);
        boolean isPlayingTrump = playingSuit.getSuitShortHand().equals(trumpSuit);

        if (isExistingTrump && isPlayingTrump) {
            return false;
        }

        if (isPlayingTrump) {
            return true;
        }

        Card trumpCard = getTrumpCard(playerCards);
        if (trumpCard != null) {
            return false;
        }
        return true;
    }

    private int checkWinner(int playerIndex) {
        assert (playingArea.getCardList().size() == 2);
        int previousPlayerIndex = Math.abs(playerIndex - 1) % 2;
        Card card1 = playingArea.getCardList().get(0);
        Card card2 = playingArea.getCardList().get(1);

        boolean isHigherRankSameSuit = isSameSuit(card1, card2) && isHigherRank(card2, card1);
        if (isHigherRankSameSuit) {
            return playerIndex;
        }

        Suit card1Suit = (Suit) card1.getSuit();
        if (card1Suit.getSuitShortHand().equals(trumpSuit)) {
            return previousPlayerIndex;
        }

        Suit card2Suit = (Suit) card2.getSuit();
        if (card2Suit.getSuitShortHand().equals(trumpSuit)) {
            return playerIndex;
        }

        return previousPlayerIndex;
    }

    private void transferCardsToWinner(int trickWinPlayerIndex) {
        for (Card card : playingArea.getCardList()) {
            trickWinningHands[trickWinPlayerIndex].insert(card, true);
        }
        playingArea.removeAll(true);
        RowLayout[] trickHandLayouts = new RowLayout[nbPlayers];
        delay(delayTime);
        for (int i = 0; i < nbPlayers; i++) {
            trickHandLayouts[i] = new RowLayout(trickHandLocations[i], handWidth);
            trickHandLayouts[i].setRotationAngle(90);
            trickWinningHands[i].setView(this, trickHandLayouts[i]);
            trickWinningHands[i].draw();
        }

        delay(delayTime);
    }


    private void updateTrickScore() {
        for (int i = 0; i < nbPlayers; i++) {
            List<Card> cards = trickWinningHands[i].getCardList();
            int score = 0;
            for (Card card : cards) {
                Rank rank = (Rank) card.getRank();
                Suit suit = (Suit) card.getSuit();
                boolean isNineCard = rank.getRankCardValue() == Rank.NINE.getRankCardValue();
                boolean isTrumpCard = suit.getSuitShortHand().equals(trumpSuit);
                if (isNineCard && isTrumpCard) {
                    score += Rank.NINE_TRUMP;
                } else {
                    score += rank.getScoreValue();
                }
            }

            scores[i] += score;
            if (i == bidWinPlayerIndex) {
                if (scores[i] < currentBid) {
                    scores[i] = 0;
                }
            }
        }
    }

    private void playGame() {
        //call the controller then the function
        BidController bidController = new BidController(this, properties, currentBid, nbPlayers);
        bidController.askForBid();
        System.out.println("the trump suit now" + trumpSuit);
        bidWinPlayerIndex = bidController.getBidWinPlayerIndex();
        askForTrumpCard();
        distributePack();

        for (int i = 0; i < nbPlayers; i++) {
            //or just call newScoringCalculator here
            MeldScoringCalculator calculator = new MeldScoringCalculator();
            scores[i] = calculator.calculateScore(hands[i].getCardList());
            System.out.println("checking player" + i + "score" + scores[i]);
            System.out.println();
            updateScore(i);
            delay(delayTime);
        }
        addTrumpInfoToLog();

        discardCards();

        int nextPlayer = bidWinPlayerIndex;
        int numberOfCards = hands[COMPUTER_PLAYER_INDEX].getNumberOfCards();
        addPlayerCardsToLog();
        for (int i = 0; i < numberOfCards; i++) {
            addRoundInfoToLog(i);
            for (int j = 0; j < nbPlayers; j++) {
                if (isAuto) {
                    int nextPlayerAutoIndex = autoIndexHands[nextPlayer];
                    List<String> nextPlayerMovement = playerAutoMovements.get(nextPlayer);
                    String nextMovement = "";

                    if (nextPlayerMovement.size() > nextPlayerAutoIndex && !nextPlayerMovement.equals("")) {
                        nextMovement = nextPlayerMovement.get(nextPlayerAutoIndex);
                        nextPlayerAutoIndex++;

                        autoIndexHands[nextPlayer] = nextPlayerAutoIndex;
                        Hand nextHand = hands[nextPlayer];

                        // Apply movement for player
                        selected = applyAutoMovement(nextHand, nextMovement);
                        delay(delayTime);
                        if (selected != null) {
                            selected.removeFromHand(true);
                        } else {
                            selected = getRandomCardForHand(hands[nextPlayer]);
                            selected.removeFromHand(true);
                        }
                    } else {
                        selected = getRandomCardForHand(hands[nextPlayer]);
                        selected.removeFromHand(true);
                    }
                }

                if (!isAuto) {
                    if (HUMAN_PLAYER_INDEX == nextPlayer) {
                        hands[HUMAN_PLAYER_INDEX].setTouchEnabled(true);

                        setStatus("Player " + nextPlayer + " is playing. Please double click on a card to discard");
                        selected = null;
                        while (null == selected) delay(delayTime);
                        selected.removeFromHand(true);
                    } else {
                        setStatusText("Player " + nextPlayer + " thinking...");
                        selected = getRandomCardForHand(hands[nextPlayer]);
                        selected.removeFromHand(true);
                    }
                }

                addCardPlayedToLog(nextPlayer, selected);
                playingArea.insert(selected, true);

                playingArea.setView(this, new RowLayout(playingLocation, (playingArea.getNumberOfCards() + 2) * trickWidth));
                playingArea.draw();

                if (playingArea.getCardList().size() == 2) {
                    delay(delayTime);
                    int trickWinPlayerIndex = checkWinner(nextPlayer);
                    transferCardsToWinner(trickWinPlayerIndex);

                    nextPlayer = trickWinPlayerIndex;
                } else {
                    nextPlayer = (nextPlayer + 1) % nbPlayers;
                }
            }
        }

        updateTrickScore();
    }

    private void setupPlayerAutoMovements() {
        String player0AutoMovement = properties.getProperty("players.0.cardsPlayed");
        String player1AutoMovement = properties.getProperty("players.1.cardsPlayed");

        String[] playerMovements = new String[]{"", ""};
        if (player0AutoMovement != null) {
            playerMovements[0] = player0AutoMovement;
        }

        if (player1AutoMovement != null) {
            playerMovements[1] = player1AutoMovement;
        }

        for (int i = 0; i < playerMovements.length; i++) {
            String movementString = playerMovements[i];
            List<String> movements = Arrays.asList(movementString.split(","));
            playerAutoMovements.add(movements);
        }
    }

    public String runApp() {
        setTitle("Pinochle  (V" + version + ") Constructed for UofM SWEN30006 with JGameGrid (www.aplu.ch)");
        setStatusText("Initializing...");
        initScores();
        initScore();
        setupPlayerAutoMovements();
        initGame();
        playGame();

        for (int i = 0; i < nbPlayers; i++) updateScore(i);
        int maxScore = 0;
        for (int i = 0; i < nbPlayers; i++) if (scores[i] > maxScore) maxScore = scores[i];
        List<Integer> winners = new ArrayList<Integer>();
        for (int i = 0; i < nbPlayers; i++) if (scores[i] == maxScore) winners.add(i);
        String winText;
        if (winners.size() == 1) {
            winText = "Game over. Winner is player: " +
                    winners.iterator().next();
        } else {
            winText = "Game Over. Drawn winners are players: " +
                    String.join(", ", winners.stream().map(String::valueOf).collect(Collectors.toList()));
        }
        addActor(new Actor("sprites/gameover.gif"), textLocation);
        setStatusText(winText);
        refresh();
        addEndOfGameToLog(winners);

        return logResult.toString();
    }

    public Pinochle(Properties properties) {
        super(700, 700, 30);
        this.properties = properties;
        isAuto = Boolean.parseBoolean(properties.getProperty("isAuto"));
        thinkingTime = Integer.parseInt(properties.getProperty("thinkingTime", "200"));
        delayTime = Integer.parseInt(properties.getProperty("delayTime", "50"));
    }

    //Setter methods
    public static void setTrumpSuit(String trumpSuit) {
        Pinochle.trumpSuit = trumpSuit;
    }

    public void setCurrentBidActor(TextActor textActor) {
        currentBidActor = textActor;
    }

    public void setNewBidActor(TextActor textActor) {
        newBidActor = textActor;
    }

    public void setPlayerBidActor(TextActor textActor) {
        playerBidActor = textActor;
    }

    //Getter methods
    public GGButton getBidSelectionActor() {
        return bidSelectionActor;
    }

    public GGButton getBidConfirmActor() {
        return bidConfirmActor;
    }

    public GGButton getBidPassActor() {
        return bidPassActor;
    }

    public TextActor getPlayerBidActor() {
        return playerBidActor;
    }

    public TextActor getCurrentBidActor() {
        return currentBidActor;
    }

    public TextActor getNewBidActor() {
        return newBidActor;
    }

    public Location getBidSelectionLocation() {
        return bidSelectionLocation;
    }

    public Location getBidConfirmLocation() {
        return bidConfirmLocation;
    }

    public Location getBidPassLocation() {
        return bidPassLocation;
    }

    public Location getPlayerBidLocation() {
        return playerBidLocation;
    }

    public Location getCurrentBidLocation() {
        return currentBidLocation;
    }

    public Location getNewBidLocation() {
        return newBidLocation;
    }

    public Color getBGcolor() {
        return bgColor;
    }

    public Font getSmallFont() {
        return smallFont;
    }

    public int getDelayTime() {
        return delayTime;
    }

    public ArrayList getHands(int playerIndex) {
        return hands[playerIndex].getCardList();
    }

    public int getThinkingTime() {
        return thinkingTime;
    }

    public int getScores(int playerIndex) {
        return scores[playerIndex];
    }

    public void distributePack() {
        // Display top 2 cards from pack

        topTwo.setView(this, new RowLayout(playingLocation, (topTwo.getNumberOfCards() + 2) * trickWidth));
        topTwo.draw();

        for (Card card : topTwo.getCardList()) {
            System.out.println("Card = " + card);
        }

        if (!isAuto) {
            // Bid winner selects card
            if (bidWinPlayerIndex == COMPUTER_PLAYER_INDEX) {
                // Computer won, automatic pick
                setStatusText("Player " + bidWinPlayerIndex + " thinking...");
                topTwoSelected = getRandomCardForHand(topTwo);
                topTwoSelected.removeFromHand(true);
            } else {
                // Player won
                topTwo.setTouchEnabled(true);
                setStatus("Player " + bidWinPlayerIndex + " is playing. Please double click on a card to add to hand.");
                topTwoSelected = null;

                while (null == topTwoSelected) delay(delayTime);
                System.out.println("Picked card: " + topTwoSelected);
                topTwoSelected.removeFromHand(true);
            }
        }

        // Add selected to bid winner's hand
        hands[bidWinPlayerIndex].insert(topTwoSelected, true);

        // Add other to other player
        Card remaining = topTwo.get(0);
        if (bidWinPlayerIndex == COMPUTER_PLAYER_INDEX) {
            hands[HUMAN_PLAYER_INDEX].insert(remaining, true);
        } else {
            hands[COMPUTER_PLAYER_INDEX].insert(remaining, true);
        }

        // Alternate between cards until pack is fully given out
        ArrayList<Card> restOfPack = pack.getCardList();
        int tempIndex = 0;
        for (Card card : restOfPack) {
            if (bidWinPlayerIndex == COMPUTER_PLAYER_INDEX) {
                if (tempIndex % 2 == 0) {
                    hands[COMPUTER_PLAYER_INDEX].insert(card, true);
                } else {
                    hands[HUMAN_PLAYER_INDEX].insert(card, true);
                }
            } else {
                if (tempIndex % 2 == 0) {
                    hands[HUMAN_PLAYER_INDEX].insert(card, true);
                } else {
                    hands[COMPUTER_PLAYER_INDEX].insert(card, true);
                }
            }
            tempIndex++;
        }


        // Drawing both hands
        for (int i = 0; i < nbPlayers; i++) {
            //hands[i].setView(this, layouts[i]);
            hands[i].draw();
        }
    }

    public void discardCards() {
        // pick 12 cards to get rid of
        int nextPlayer = bidWinPlayerIndex;
        for (int i = 0; i < 24; i++) {
            if (!isAuto) {
                if (nextPlayer == HUMAN_PLAYER_INDEX) {
                    // player discards one card
                    hands[HUMAN_PLAYER_INDEX].setTouchEnabled(true);
                    setStatus("Player " + bidWinPlayerIndex + " is playing. Please double click on a card to discard from hand.");
                    selected = null;
                    while (null == selected) delay(delayTime);
                    selected.removeFromHand(true);
                } else {
                    // computer discards one card
                    setStatusText("Player " + nextPlayer + " thinking...");
                    /*
                    HELLO THIS LOGIC FOR HOW THE COMPUTER PLAYER DECIDES ON DISCARDING STILL NEEDS WORK !!
                     */
//                    Map<Suit, Integer> handSuitCount = createDictOfComputerHand();
//                    Suit smallestSuit = findSmallestSuit(handSuitCount); // get the smallest suit
//                    List<Card> toRemove = (hands[COMPUTER_PLAYER_INDEX]).getCardsWithSuit(smallestSuit);
//                    System.out.println("Cards to remove: " + toRemove);
                    selected = getRandomCardForHand(hands[COMPUTER_PLAYER_INDEX]);
                    selected.removeFromHand(true);
                }
                nextPlayer = (nextPlayer + 1) % nbPlayers;
            }
        }
    }

    // function to find the suit with the least amount of entries in the dictionary SuitDict
    public Suit findSmallestSuit(Map<Suit, Integer> suitDict) {
        List<Suit> tempSmallestSuit = new ArrayList<>();
        int minCount = 24;
        // System.out.println("The dictionary is size: " + minCount);
        /* Pick the suit with least cards of the same suit in hand*/
        for (Map.Entry<Suit, Integer> entry : suitDict.entrySet()) {
            Suit suit = entry.getKey();
            int count = entry.getValue();
            if (suit.getSuitShortHand().equals(Pinochle.trumpSuit)) {
                System.out.println("the suit is equal to the trump suit : " + Pinochle.trumpSuit);
                continue;
            } else {
                if (count < minCount) {
                    // get suit with the least cards
                    minCount = count;
                    tempSmallestSuit.clear();
                    tempSmallestSuit.add(suit);

                } else if (count == minCount) {
                    tempSmallestSuit.add(suit);
                }
            }
        }
        return tempSmallestSuit.get(0);
    }

    // function to create a dictionary
    public Map<Suit, Integer> createDictOfComputerHand(){
        // create a dictionary of Computer Player's cards
        Map<Suit, Integer> handSuitCount = new HashMap<>(); // dictionary
        ArrayList<Card> tempCardList = hands[COMPUTER_PLAYER_INDEX].getCardList();
        for (Card card : tempCardList) {
            Suit currSuit = (Suit) card.getSuit();
            // Merge normal suits and xxTWO into a single key
            if (currSuit.toString().contains("TWO")) {
                // it contains TWO
                String mergerName = currSuit.name().replace("TWO", "");
                currSuit = Suit.valueOf(mergerName);
            }

            if (handSuitCount.containsKey(currSuit)) {
                handSuitCount.put(currSuit, handSuitCount.get(currSuit) + 1);
            } else {
                handSuitCount.put(currSuit, 1);
            }
            System.out.println("the suit added: " + currSuit);
        }

        return handSuitCount;
    }



}
