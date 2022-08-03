package Application;

import javafx.beans.property.SimpleIntegerProperty;

public class Player {
	private String name;

	private Hand hand;

	private SimpleIntegerProperty money = new SimpleIntegerProperty(0);

	private SimpleIntegerProperty currentBet = new SimpleIntegerProperty(0);

	public Player(String name, int startSum) {
		this.name = name;
		this.money.set(startSum);
	}

	public void resetHand() {
		hand.reset();
	}

	public String name() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SimpleIntegerProperty moneyProperty() {
		return money;
	}

	public int money() {
		return money.get();
	}

	public void setMoney(int sum) {
		money.set(sum);
	}

	public Hand hand() {
		return hand;
	}

	public void setHand(Hand hand) {
		this.hand = hand;
	}

	public SimpleIntegerProperty currentBetProperty() {
		return currentBet;
	}

	public int currentBet() {
		return currentBet.get();
	}

	public void setCurrentBet(int currentBet) {
		this.currentBet.set(currentBet);
	}
}