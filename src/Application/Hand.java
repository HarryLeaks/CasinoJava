package Application;

import Application.Card.Rank;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;

public class Hand implements Commons {
	private ObservableList<Node> cards;

	private SimpleIntegerProperty value = new SimpleIntegerProperty(0);

	private SimpleIntegerProperty size = new SimpleIntegerProperty(0);

	private int aces = 0;

	public Hand(ObservableList<Node> cards) {
		this.cards = cards;
	}

	public void reset() {
		cards.clear();
		value.set(0);
		aces = 0;
		size.set(0);
	}

	public void showFirstCard() {
		if (cards.isEmpty()) return;
		((Card) cards.get(0)).showCard();
	}

	public void hideFirstCard() {
		if (cards.isEmpty()) return;
		((Card) cards.get(0)).hideCard();
	}

	public void addAllCards(Card[] cardsToAdd) {
		try {
			VerifyArray(0, cardsToAdd);
		}catch(IndexOutOfBoundsException e) {
			return;
		}
	}
	
	public void VerifyArray(int pos, Card[] cardsToAdd) {
		Card card;
		card = cardsToAdd[pos];
		addCard(card);
		VerifyArray(pos+1, cardsToAdd);		
	}

	public void addCardToView(Card card) {
		cards.add(card);
	}

	public void addCard(Card card) {
		if (card.rank() == Rank.ACE)
			aces++;

		if (value.get() + card.value() > PLAYER_HAND_MAX_VALUE && aces > 0) {
			value.set(value.get() + card.value() - 10);
			aces--;
		} else {
			value.set(value.get() + card.value());
		}

		size.set(size.get() + 1);
	}

	public int visibleValue() {
		if (cards.size() == 0) return 0;
		
		Card first = (Card) cards.get(0);
		return first.isHidden() ? value.get() - first.value() : value.get();
	}

	public int aces() {
		return aces;
	}

	public SimpleIntegerProperty valueProperty() {
		return value;
	}

	public int value() {
		return value.get();
	}

	public SimpleIntegerProperty sizeProperty() {
		return size;
	}

	public int size() {
		return size.get();
	}
}