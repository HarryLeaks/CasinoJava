package Application;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

public class Card extends Parent implements Commons {
	private final static String BG_PATH = "/resources/img/backside.png";
	private final static ImagePattern BG_PATTERN = new ImagePattern(new Image(Card.class.getResource(BG_PATH).toExternalForm()));

	enum Suit {
		HEARTS, DIAMONDS, CLUBS, SPADES
	};

	enum Rank {
		TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8), NINE(9), TEN(10), JACK(10), QUEEN(10), KING(10), ACE(11);

		final int value;

		private Rank(int value) {
			this.value = value;
		}
	};

	private final String IMG_PATH;
	private final ImagePattern IMG_PATTERN;

	private final Suit suit;

	private final Rank rank;

	private Rectangle card;

	private SimpleBooleanProperty showCard = new SimpleBooleanProperty(true);

	public Card(Suit suit, Rank rank) {
		this.suit = suit;
		this.rank = rank;
		IMG_PATH = "/resources/img/" + rank.toString().toLowerCase() + "_of_" + suit.toString().toLowerCase() + ".png";
		IMG_PATTERN = new ImagePattern(new Image(Card.class.getResource(IMG_PATH).toExternalForm()));

		card = new Rectangle(CARD_WIDTH, CARD_HEIGHT);
		card.setArcWidth(15);
		card.setArcHeight(15);
		card.setFill(IMG_PATTERN);
		getChildren().add(card);

		showCard.addListener((obs, old, newValue) -> {
			if (newValue.booleanValue()) {
				card.setFill(IMG_PATTERN);
			} else {
				card.setFill(BG_PATTERN);
			}
		});
	}

	public void showCard() {
		showCard.set(true);
	}

	public void hideCard() {
		showCard.set(false);
	}

	public boolean isHidden() {
		return !showCard.get();
	}

	public Suit suit() {
		return suit;
	}

	public Rank rank() {
		return rank;
	}

	public int value() {
		return rank.value;
	}
}