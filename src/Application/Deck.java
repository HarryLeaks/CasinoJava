package Application;

import java.util.ArrayList;
import java.util.Collections;

import Application.Card.Rank;
import Application.Card.Suit;

public class Deck {
	private ArrayList<Card> cards = new ArrayList<Card>();

	private ArrayList<Card> taken = new ArrayList<>();

	public Deck() {
		for (Suit suit : Suit.values()) {
			for (Rank rank : Rank.values()) {
				cards.add(new Card(suit, rank));
			}
		}
	}

	public void reset() {
		cards.addAll(taken);
		taken.clear();
	}

	public void shuffle() {
		Collections.shuffle(cards);
	}

	public Card drawCard() {
		if (cards.isEmpty())
			return null;

		Card drawn = cards.remove(0);
		taken.add(drawn);

		return drawn;
	}
}