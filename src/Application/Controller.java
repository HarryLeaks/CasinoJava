package Application;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import Application.Card.Rank;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class Controller implements Commons, Initializable {

	private Deck deck;
	private Player player, dealer;

	@FXML
	private Pane root;
	
	@FXML
	private Region background;
	
	@FXML
	private BorderPane paneLeft, paneRight;
	
	@FXML
	private Rectangle rectangleLeftBG, rectangleRightBG;

	@FXML
	private HBox containerDealerCards, containerPlayerCards, containerLeftAndRightPane, containerBtnHitAndStand;
	
	@FXML
	private VBox containerPlayerHands, containerGameButtons, containerInitButtons;

	@FXML
	private Slider betSlider;
	
	@FXML
	private Label labelBetSlider, labelGameMessage, labelMoney, labelDealerScore, labelPlayerScore;
	
	@FXML
	private Button btnPlay, btnReset, btnHit, btnStand, btnDoubleDown;
	
	private SimpleBooleanProperty inGameOver, inGame, inAnimation, inTwoCardsHands; 

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		inGameOver = new SimpleBooleanProperty(false);
		inGame = new SimpleBooleanProperty(false);
		inAnimation = new SimpleBooleanProperty(false);
		inTwoCardsHands = new SimpleBooleanProperty(false);
		
		deck = new Deck();
		player = new Player(System.getProperty("user.name"), 150);
		dealer = new Player("Dealer", Integer.MAX_VALUE);

		player.setHand(new Hand(containerPlayerCards.getChildren()));
		dealer.setHand(new Hand(containerDealerCards.getChildren()));

		root.setPrefSize(APP_WIDTH, APP_HEIGHT);
		background.setPrefSize(APP_WIDTH, APP_HEIGHT);
		rectangleLeftBG.setWidth(APP_WIDTH * 0.7 - 1.5 * APP_SPACING);
		rectangleLeftBG.setHeight(APP_HEIGHT - 2 * APP_SPACING);
		rectangleRightBG.setWidth(APP_WIDTH * 0.3 - 1.5 * APP_SPACING);
		rectangleRightBG.setHeight(APP_HEIGHT - 2 * APP_SPACING);

		labelMoney.setText("CASH: " + player.money());
		labelGameMessage.setText("PRESS PLAY TO PLAY");
		labelBetSlider.setText("CURRENT BET: " + (int) betSlider.getValue());
		playerUpdateScoreMessage(dealer, labelDealerScore);
		playerUpdateScoreMessage(player, labelPlayerScore);
		betSlider.setMax(player.money());

		BooleanBinding notInGameInAnimationInGameOver = Bindings.or(Bindings.or(inGame.not(), inAnimation), inGameOver);
		BooleanBinding inGameInGameOver = Bindings.or(inGame, inGameOver);

		btnPlay.disableProperty().bind(inGameInGameOver);
		btnDoubleDown.disableProperty().bind(Bindings.or(inTwoCardsHands.not(), notInGameInAnimationInGameOver));
		btnHit.disableProperty().bind(notInGameInAnimationInGameOver);
		btnStand.disableProperty().bind(notInGameInAnimationInGameOver);
		labelBetSlider.disableProperty().bind(inGameInGameOver);
		betSlider.disableProperty().bind(inGameInGameOver);

		player.hand().valueProperty().addListener((obs, old, newValue) -> {
			playerUpdateScoreMessage(player, labelPlayerScore);
		});

		player.hand().sizeProperty().addListener((obs, old, newValue) -> {
			inTwoCardsHands.set(newValue.intValue() == 2 ? true : false);

			if (newValue.intValue() == PLAYER_HAND_SIZE_BLACK_JACK && player.hand().value() == PLAYER_HAND_MAX_VALUE) {
				endGame();
			}
		});

		player.currentBetProperty().addListener((obs, old, newValue) -> {
			labelBetSlider.setText("CURRENT BET: " + player.currentBet());
		});

		dealer.hand().valueProperty().addListener((obs, old, newValue) -> {
			playerUpdateScoreMessage(dealer, labelDealerScore);

			if (newValue.intValue() >= PLAYER_HAND_MAX_VALUE) {
				endGame();
			}
		});

		player.moneyProperty().addListener((obs, old, newValue) -> {
			betSlider.setMax(newValue.intValue());
			labelMoney.setText("CASH: " + newValue.intValue());
		});

		betSlider.valueProperty().addListener((obs, old, newValue) -> {
			player.setCurrentBet((int) betSlider.getValue());
		});
	}

	private void startNewGame() {	
		try {
			Except.VerifyNoMoney(player.currentBet());
		}catch(IllegalArgumentException e) {
			labelGameMessage.setText("YOU MUST BET WITH MONEY");
			return;
		}
			
			inGame.set(true);
			inAnimation.set(true);
			labelGameMessage.setText("");
			
			deck.reset();
			deck.shuffle();
			dealer.resetHand();
			player.resetHand();
	
			ArrayList<Card> playerDrawn = new ArrayList<Card>();
			ArrayList<Card> dealerDrawn = new ArrayList<Card>();
	
			SequentialTransition revealOneAtATime = new SequentialTransition();
			for (int i = 0; i < 4; i++) {
				Card drawn = deck.drawCard();
				Player giveCardTo;
				String drawSound;
				
				if(i % 2 == 0) {
					giveCardTo = dealer;
					drawSound = DEALER_DRAW_SOUND;
					dealerDrawn.add(drawn);
				}else {
					giveCardTo = player;
					drawSound = PLAYER_DRAW_SOUND;
					playerDrawn.add(drawn);
				}
				addCardToView(drawn, revealOneAtATime, giveCardTo, drawSound);
			}
	
			dealer.hand().hideFirstCard();
	
			revealOneAtATime.setOnFinished(event -> {
				player.hand().addAllCards(playerDrawn.toArray(new Card[playerDrawn.size()]));
				dealer.hand().addAllCards(dealerDrawn.toArray(new Card[dealerDrawn.size()]));
				inAnimation.set(false);
			});
	
			revealOneAtATime.play();
	}

	private void endGame() {
		inGame.set(false);

		dealer.hand().showFirstCard();
		playerUpdateScoreMessage(dealer, labelDealerScore);

		int dealerValue = dealer.hand().valueProperty().get();
		int playerValue = player.hand().valueProperty().get();
		Player winner = null;

		if (dealerValue == PLAYER_HAND_MAX_VALUE || playerValue > PLAYER_HAND_MAX_VALUE || dealerValue == playerValue
				|| (dealerValue < PLAYER_HAND_MAX_VALUE && dealerValue > playerValue)) {
			winner = dealer;
			player.setMoney(player.moneyProperty().get() - player.currentBetProperty().get());
		} else {
			winner = player;
			player.setMoney(player.moneyProperty().get() + player.currentBetProperty().get());
		}

		labelGameMessage.setText(winner.name() + " WON");

		try{
			Except.VerifyNoMoney((player.moneyProperty().get()));
		}catch(IllegalArgumentException e) {
			gameOver();
		}
	}

	private void gameOver() {
		inGameOver.set(true);
		labelGameMessage.setText("GAME OVER");
		btnPlay.setVisible(false);
		btnReset.setVisible(true);
	}

	private void resetGame() {
		player.setMoney(PLAYER_START_MONEY);

		labelGameMessage.setText("PRESS PLAY TO PLAY");

		btnReset.setVisible(false);
		btnPlay.setVisible(true);
		inGameOver.set(false);
	}

	private void playerUpdateScoreMessage(Player player, Label scoreLabel) {
		scoreLabel.setText(player.name() + ": " + player.hand().visibleValue());
	}
	
	private void addCardToView(Card drawn, SequentialTransition revealOneAtATime, Player player, String drawSound) {
		PauseTransition ptSound = new PauseTransition(Duration.millis(100));
		ptSound.setOnFinished(ptSoundEvent -> {
			Sound.playSound(drawSound);
		});

		FadeTransition ft = new FadeTransition(Duration.millis(200), drawn);
		ft.setFromValue(0);
		ft.setToValue(1.0);

		PauseTransition pt = new PauseTransition(Duration.millis(300));

		revealOneAtATime.getChildren().addAll(ptSound, ft, pt);

		player.hand().addCardToView(drawn);
	}

	private void playerStand() {
		ArrayList<Card> dealerDrawn = new ArrayList<Card>();
		SequentialTransition revealOneAtATime = new SequentialTransition();

		inAnimation.set(true);

		PauseTransition showFirstCard = new PauseTransition(Duration.millis(100));
		showFirstCard.setOnFinished((ptEvent -> {
			dealer.hand().showFirstCard();
			playerUpdateScoreMessage(dealer, labelDealerScore);
		}));
		revealOneAtATime.getChildren().addAll(showFirstCard, new PauseTransition(Duration.millis(300)));

		int cnt = dealer.hand().valueProperty().get();
		int numAces = dealer.hand().aces();

		while (cnt < DEALER_HAND_STOP) {
			Card drawn = deck.drawCard();
			dealerDrawn.add(drawn);

			addCardToView(drawn, revealOneAtATime, dealer, DEALER_DRAW_SOUND);

			cnt += drawn.value();
			if (drawn.rank() == Rank.ACE)
				numAces++;

			if (cnt > PLAYER_HAND_MAX_VALUE && numAces > 0) {
				cnt -= 10;
				numAces--;
			}
		}

		final int sum = cnt;

		revealOneAtATime.setOnFinished(revealOneAtATimeEvent -> {
			dealer.hand().addAllCards(dealerDrawn.toArray(new Card[dealerDrawn.size()]));
			inAnimation.set(false);

			if (sum < PLAYER_HAND_MAX_VALUE) {
				endGame();
			}
		});

		revealOneAtATime.play();
	}

	private void playerHit() {
		inAnimation.set(true);

		Card drawn = deck.drawCard();
		ArrayList<Card> card = new ArrayList<Card>();
		card.add(drawn);
		
		SequentialTransition revealOneAtATime = new SequentialTransition();
		addCardToView(drawn, revealOneAtATime, player, PLAYER_DRAW_SOUND);

		revealOneAtATime.setOnFinished(revealOneAtATimeEvent -> {
			player.hand().addAllCards(card.toArray(new Card[card.size()]));
			inAnimation.set(false);

			if (player.hand().valueProperty().get() > PLAYER_HAND_MAX_VALUE) {
				endGame();
			} else if (player.hand().valueProperty().get() == PLAYER_HAND_MAX_VALUE) {
				playerStand();
			}
		});

		revealOneAtATime.play();
	}

	private void playerDoubleDown() {
		inAnimation.set(true);

		int newBet = Math.min(player.currentBetProperty().get() * 2, player.money());
		player.setCurrentBet(newBet);
		betSlider.setValue(newBet);

		Card drawn = deck.drawCard();
		ArrayList<Card> card = new ArrayList<Card>();
		card.add(drawn);
		
		SequentialTransition revealOneAtATime = new SequentialTransition();
		addCardToView(drawn, revealOneAtATime, player, PLAYER_DRAW_SOUND);

		revealOneAtATime.setOnFinished(revealOneAtATimeEvent -> {
			player.hand().addAllCards(card.toArray(new Card[card.size()]));
			inAnimation.set(false);

			if (player.hand().valueProperty().get() > PLAYER_HAND_MAX_VALUE) {
				endGame();
			} else {
				playerStand();
			}
		});

		revealOneAtATime.play();
	}

	@FXML
	private void btnDoubleDownEvent(ActionEvent click) {
		playerDoubleDown();
	}

	@FXML
	private void btnHitEvent(ActionEvent click) {
		playerHit();
	}

	@FXML
	private void btnStandEvent(ActionEvent click) {
		playerStand();
	}

	@FXML
	private void btnStartEvent(ActionEvent click) {
		startNewGame();
	}

	@FXML
	private void btnResetEvent(ActionEvent click) {
		resetGame();
	}
}