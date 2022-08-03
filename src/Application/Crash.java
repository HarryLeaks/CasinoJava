package Application;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.scene.shape.QuadCurve;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

class Crash extends Stage implements Commons{
	private SimpleIntegerProperty currentBet = new SimpleIntegerProperty();
	private SimpleDoubleProperty currentProfit = new SimpleDoubleProperty(0);
	private Label profit = new Label("Profit: " + currentProfit.getValue());
	private Label timeView = new Label("Profit: ");
	private SimpleIntegerProperty timeCount = new SimpleIntegerProperty(0);
	
	private final AudioClip sound = new AudioClip(this.getClass().getResource("/resources/sounds/win.mp3").toExternalForm());
	
	private Random randIndex = new Random();
	private int time;
	
	private Pane root = new Pane();
	private Thread time1;
	private PathTransition transition;
	
	private Image Image = new Image(this.getClass().getResource("/resources/img/f48.gif").toExternalForm(), 900.0, 600.0, false, true);
    private Image Explosion = new Image(this.getClass().getResource("/resources/img/explosion.gif").toExternalForm(), 900.0, 600.0, false, true);
	private Image Money = new Image(this.getClass().getResource("/resources/img/money.gif").toExternalForm(), 900.0, 600.0, false, true);
	private Image Crash = new Image(this.getClass().getResource("/resources/img/crash.gif").toExternalForm(), 900.0, 600.0, false, true);
    private ImageView img = new ImageView(Image);
    
    private Label autoCashOutLabel = new Label("Auto CashOut: ");
    private TextField autoCashOut = new TextField();
    
    private Double rot;
	private Button play, cashOut, resetBtn;
	private Slider quantity;
	private Boolean cashed = false, isFirst = true;
	private SimpleIntegerProperty mm = new SimpleIntegerProperty(PLAYER_START_MONEY);
	private Label money = new Label("Money: " + (int)mm.getValue());
	private Label selectedMoney, wrongInfo;
	
    Crash(){				
		        final Image bgImage = new Image(this.getClass().getResource("/resources/img/bg.jpg").toExternalForm(), 900.0, 600.0, false, true);
		        final ImageView imgV = new ImageView(bgImage);
				img.setFitHeight(100);
				img.setFitWidth(100);
				img.setTranslateY(430);
				rot = img.getRotate();
			
				money.setTranslateX(670);
				money.setTranslateY(10);
				
				play = new Button("Bet");
				play.setTranslateX(690);
				play.setTranslateY(400);
				
				resetBtn = new Button("Reset");
				resetBtn.setTranslateX(670);
				resetBtn.setTranslateY(50);
				resetBtn.setVisible(false);
				
				quantity = new Slider();
				quantity.setMax((int)mm.getValue());
				quantity.setTranslateX(650);
				quantity.setTranslateY(380);
				
				selectedMoney = new Label("Money: " + (int)quantity.getValue());
				selectedMoney.setTranslateX(670);
				selectedMoney.setTranslateY(330);
				
				profit = new Label();
				profit.setTranslateX(10);
				profit.setTranslateY(10);
				timeView = new Label();
				timeView.setTranslateX(10);
				timeView.setTranslateY(60);
				autoCashOutLabel.setTranslateX(615);
				autoCashOutLabel.setTranslateY(240);
				autoCashOut.setTranslateX(760+autoCashOutLabel.getPrefWidth());
				autoCashOut.setTranslateY(240);
				autoCashOut.setPrefSize(35, 35);
				autoCashOut.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
				
				changeMaxValue();
				
				cashOut = new Button("Cash Out");
				cashOut.setDisable(true);
				cashOut.setTranslateX(670);
				cashOut.setTranslateY(450);
				
			    QuadCurve quadTo = new QuadCurve();
			    quadTo.setControlX(600.0f);
			    quadTo.setControlY(500.0f);
			    quadTo.setStartX(20);
			    quadTo.setStartY(460);
			    quadTo.setEndX(600.0f);
			    quadTo.setEndY(40.0f);

				transition = new PathTransition();
				transition.setNode(img);
				transition.setDuration(Duration.seconds(21));
				transition.setPath(quadTo);
				transition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
				
				wrongInfo = new Label("INCORRECT PARAMETERS");
				wrongInfo.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
				wrongInfo.setTranslateX(300);
				wrongInfo.setTranslateY(200);
				wrongInfo.setVisible(false);
				
				play.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent e) throws NumberFormatException {
						try{
							Except.VerifyNoMoney((int)mm.getValue());
							Except.VerifyNoMoney((int)quantity.getValue());
							Except.VerifyNumber(autoCashOut.getText());
							cashOut.setDisable(false);
							quantity.setDisable(true);
							play.setDisable(true);
							autoCashOut.setDisable(true);
							img.setImage(Image);
							img.setVisible(true);
							transition.play();
							currentProfit.setValue(currentBet.getValue());
							profit.setText("Profit: " + currentProfit.getValue());
							timeView.setText("Time: " + (int)timeCount.getValue());
							mm.setValue(mm.getValue() - currentBet.getValue());
							money.setText("Money: " + (int)mm.getValue());
							
							time = randIndex.nextInt(20) + 1;
							
							System.out.println(time);
							
							time1 = new Thread(){
								public void run() {
									try {
										timeCounter(time);
										Except.VerifyNoMoney((int)mm.getValue());
									} catch (IllegalArgumentException | IllegalStateException e) {
										changeAllStatus(true);
									}
									resetBtn();
									try {
										time1.join(0);
									} catch (InterruptedException e1) {
										e1.printStackTrace();
									}
								}
							};
							if(isFirst) {
								time1.start();
							}else {
								time1.run();
							}	
						}catch(IllegalArgumentException | NullPointerException e1) {
							changeAllStatus(true);
							wrongInfo.setVisible(true);;
						}
					}
				});
				
				resetBtn.setOnAction(new EventHandler<ActionEvent>(){
					@Override
					public void handle(ActionEvent arg0) {
						mm.setValue(150);
						money.setText("Money: " + mm.getValue());
						quantity.setMax((int)mm.getValue());
						changeMaxValue();
						changeAllStatus(false);
						wrongInfo.setVisible(false);
					}					
				});
				
				root.getChildren().addAll(imgV, img, money, quantity, selectedMoney, play, cashOut, profit, resetBtn, timeView, autoCashOutLabel, autoCashOut, wrongInfo);				

				Scene scene = new Scene(root, APP_WIDTH, APP_HEIGHT);
				scene.getStylesheets().add(getClass().getResource("crash.css").toExternalForm());
				this.setScene(scene);
				this.sizeToScene();
				this.setResizable(false);
				this.setTitle("Crash Game");
				this.show();
	}
    
    public void changeAllStatus(Boolean noMoney) {
    	if(noMoney) {
        	play.setDisable(true);
        	quantity.setDisable(true);
        	resetBtn.setVisible(true);
        	autoCashOut.setDisable(true);
    	}else {
        	play.setDisable(false);
        	quantity.setDisable(false);
        	resetBtn.setVisible(false);
        	autoCashOut.setDisable(false);
    	}
    }
	
	public void timeCounter(int seconds){
		long endTime = System.currentTimeMillis() + (seconds * 1000);
		long moneyUpdate = System.currentTimeMillis() + 1000;
		currentProfit.setValue(currentBet.getValue());
		
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					profit.setText("Porfit: " + currentProfit.getValue());
				}catch(IllegalStateException e) {
					e.printStackTrace();
				}
			}
		});
		
		double sum = currentBet.getValue();
		timeCount.setValue(1);;
		BigDecimal bd;
		
		while (System.currentTimeMillis() < endTime) {
			cashOut.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {	
					sound.play();
					cashed = true;					
					mm.setValue(mm.getValue() + currentProfit.getValue());
					money.setText("Money: " + (int)mm.getValue());
					selectedMoney.setText("Money: " + (int)quantity.getValue());
				}
			});
			if(cashed == true) break;
			
			if(System.currentTimeMillis() == moneyUpdate) {
				currentProfit.setValue(currentProfit.getValue() + (sum * 0.2));
				bd = new BigDecimal(currentProfit.getValue()).setScale(2, RoundingMode.HALF_UP);
				currentProfit.setValue(bd.doubleValue());
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						try {
							profit.setText("Profit: " + currentProfit.getValue());
							timeView.setText("Time: " + timeCount.getValue());
							
							if(timeCount.getValue() == Integer.parseInt(autoCashOut.getText())){
								sound.play();
								cashed = true;					
								mm.setValue(mm.getValue() + currentProfit.getValue());
								money.setText("Money: " + (int)mm.getValue());
								selectedMoney.setText("Money: " + (int)quantity.getValue());
							}
						}catch(IllegalStateException e) {
							e.printStackTrace();
						}
					}
				});
				timeCount.setValue((int)timeCount.getValue() + 1);
				moneyUpdate = System.currentTimeMillis() + 1000;
			}
		}
		transition.stop();
		cashOut.setDisable(true);
		changeAnimation(cashed); 
		
		if((int)mm.getValue() != 0) {
			quantity.setMax(mm.getValue());
			changeMaxValue();
		}
	}
	
	public void changeMaxValue() throws IllegalStateException{
		quantity.valueProperty().addListener((obs, old, newValue) -> {
			currentBet.setValue((int) quantity.getValue());
			selectedMoney.setText("Money: " + (int)quantity.getValue());
		});
	}
	
	public void changeAnimation(Boolean cashed) {
		if(!cashed) {
			img.setImage(Explosion);
			try {
				Thread.sleep(800);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			img.setImage(Crash);
			try {
				Thread.sleep(1000);
			}catch(InterruptedException e) {
				e.printStackTrace();
			}
		}else {
			img.setImage(Money);
			try {
				Thread.sleep(1000);
			}catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
		img.setVisible(false);
		img.setX(20);
		img.setY(460);
		img.setRotate(rot);
	}
	
	public void resetBtn() {
		quantity.setDisable(false);
		play.setDisable(false);
		autoCashOut.setDisable(false);
		cashed = false;
	}
}