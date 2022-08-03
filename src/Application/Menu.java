package Application;

import javafx.geometry.Pos;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.beans.binding.Bindings;
import javafx.scene.shape.Shape;
import javafx.animation.FillTransition;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Color;
import javafx.scene.media.AudioClip;
import javafx.scene.shape.Rectangle;

import java.net.URL;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;
import javafx.animation.FadeTransition;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.InvalidationListener;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.Node;
import javafx.scene.media.Media;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.media.MediaPlayer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.media.MediaView;
import javafx.application.Application;

class Blackjack extends Stage implements Commons{
	private void addMedia() {
		Sound.addSound(DEALER_DRAW_SOUND, new AudioClip(this.getClass().getResource("/resources/sounds/cardPlace1.wav").toExternalForm()), 0);
		Sound.addSound(PLAYER_DRAW_SOUND, new AudioClip(this.getClass().getResource("/resources/sounds/cardPlace1.wav").toExternalForm()), 1);
	}
	
	Blackjack() throws Exception{
				addMedia();

				FXMLLoader loader = new FXMLLoader(getClass().getResource("GUI.fxml"));
				Pane root = loader.load();
				
				Scene scene = new Scene(root, APP_WIDTH, APP_HEIGHT);
				scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				this.setScene(scene);
				this.sizeToScene();
				this.setResizable(false);
				this.setTitle("Black Jack");
				this.show();
	}
}



public class Menu extends Application
{
    @FXML
    private MediaView mv;
    private MediaPlayer rep;
    private Slider volume;
    private Pane root;
    private Button skip;
    
    public Menu() {
        this.volume = new Slider(0.0, 1.0, 0.5);
        this.skip = new Button("Skip");
    }
    
    public void start(final Stage stage) throws Exception {
        stage.setScene(new Scene(this.createContent()));
        stage.sizeToScene();
		stage.setResizable(false);
        stage.setOnCloseRequest(e->e.consume());
        stage.show();
    }
    
    private Parent createContent() {
        (this.root = new Pane()).setPrefSize(800.0, 500.0);
        final URL localiz = this.getClass().getResource("/resources/sounds/online_casino.mp4");
        final String Path = localiz.toExternalForm();
        final Media video = new Media(Path);
        (this.rep = new MediaPlayer(video)).setAutoPlay(true);
        (this.mv = new MediaView(this.rep)).setFitHeight(600.0);
        this.mv.setFitWidth(890.0);
        this.volume.setMajorTickUnit(0.1);
        this.volume.setShowTickLabels(true);
        final HBox cx = new HBox(0.0, new Node[] { (Node)this.volume });
        final HBox cx2 = new HBox(0.0, new Node[] {(Node)this.skip });
        HBox.setMargin((Node)this.volume, new Insets(460.0, 10.0, 10.0, 10.0));
        cx2.setTranslateX(750);
        cx2.setTranslateY(460);
        
        this.volume.valueProperty().addListener((InvalidationListener) new InvalidationListener() {
            public void invalidated(final Observable ov) {
                if (Menu.this.volume.isValueChanging()) {
                    Menu.this.rep.setVolume(Menu.this.volume.getValue());
                }
            }
        });
        
        this.skip.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent arg0) {
				Menu.this.rep.seek(rep.getStopTime());
			}
        });
        
        final VBox box = new VBox(10.0, new Node[] { 
        	(Node)new MenuItem(" BLACKJACK", () -> {
				try {
					new Blackjack();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}), (Node)new MenuItem(" CRASH", () -> {
				try {
					new Crash();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}), (Node)new MenuItem(" QUIT", () -> { 
				Platform.exit(); 
				System.exit(0);  
			})
        });
        
        box.setTranslateX(50.0);
        box.setTranslateY(250.0);
        box.setVisible(false);
        final Image bgImage = new Image(this.getClass().getResource("/resources/img/background.jpg").toExternalForm(), 900.0, 600.0, false, true);
        final ImageView imgV = new ImageView(bgImage);
        final FadeTransition ft = new FadeTransition();
        final FadeTransition ft2 = new FadeTransition();
        this.rep.setOnEndOfMedia((Runnable)new Runnable() {
            @Override
            public void run() {
                ft.setDuration(Duration.millis(1000.0));
                ft.setNode((Node)Menu.this.root);
                ft.setFromValue(1.0);
                ft.setToValue(0.0);
                ft.play();
            }
        });
        ft.setOnFinished(new EventHandler<ActionEvent>() {
            public void handle(final ActionEvent actionEvent) {
                Menu.this.mv.setVisible(false);
                cx.setVisible(false);
                imgV.setVisible(true);
                box.setVisible(true);
                ft2.setDuration(Duration.millis(1000.0));
                ft2.setNode((Node)Menu.this.root);
                ft2.setFromValue(0.0);
                ft2.setToValue(1.0);
                ft2.play();
            }
        });
        imgV.setVisible(false);
        this.root.getChildren().addAll(mv, cx, cx2, imgV, box);
        return (Parent)this.root;
    }
    
    public static void pressed(final Rectangle bg, final AudioClip sound) {
        bg.setFill((Paint)Color.LIGHTBLUE);
        sound.play();
    }
    
    private static class MenuItem extends StackPane
    {
        MenuItem(final String name, final Runnable action) {
            final LinearGradient gradient = new LinearGradient(0.0, 0.5, 1.0, 0.5, true, CycleMethod.NO_CYCLE, new Stop[] { new Stop(0.1, Color.web("black", 0.85)), new Stop(1.0, Color.web("black", 0.15)) });
            final Rectangle bg0 = new Rectangle(150.0, 30.0, (Paint)gradient);
            final Rectangle bg2 = new Rectangle(150.0, 30.0, (Paint)Color.web("black", 0.2));
            final FillTransition ft = new FillTransition(Duration.seconds(0.6), (Shape)bg2, Color.web("black", 0.2), Color.web("white", 0.3));
            ft.setAutoReverse(true);
            ft.setCycleCount(Integer.MAX_VALUE);
            final AudioClip sound = new AudioClip(this.getClass().getResource("/resources/sounds/mouse_hover.wav").toExternalForm());
            final AudioClip sound2 = new AudioClip(this.getClass().getResource("/resources/sounds/mouse_click.wav").toExternalForm());
            this.hoverProperty().addListener((o, oldValue, isHovering) -> {
                if (isHovering) {
                    ft.playFromStart();
                    sound.play();
                }
                else {
                    ft.stop();
                    bg2.setFill((Paint)Color.web("black", 0.2));
                }
            });
            final Rectangle line = new Rectangle(5.0, 20.0);
            line.widthProperty().bind(Bindings.when(this.hoverProperty()).then(6).otherwise(4));
            line.fillProperty().bind(Bindings.when(this.hoverProperty()).then(Color.RED).otherwise(Color.GRAY));
            final Text text = new Text(name);
            text.setFont(Font.font(17.0));
            text.fillProperty().bind(Bindings.when(this.hoverProperty()).then(Color.WHITE).otherwise(Color.GRAY));
            this.setOnMouseClicked(e -> action.run());
            this.setOnMousePressed(e -> Menu.pressed(bg0, sound2));
            this.setOnMouseReleased(e -> bg0.setFill((Paint)gradient));
            this.setAlignment(Pos.CENTER_LEFT);
            final HBox box = new HBox(10.0, line, text);
            box.setAlignment(Pos.CENTER_LEFT);
            this.getChildren().addAll(bg0, bg2, box);
        }
    }
    
    public static void main(String[] args) {
    	launch(args);
    }
}
