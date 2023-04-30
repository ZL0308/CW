package uk.ac.soton.comp1206.component;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.Pair;

/**
 * A Leaderboard to show the score of each player in the real time
 */
public class Leaderboard extends ScoresList {

  /**
   * To initialize the size of list
   */
  public void initializeList() {
    this.setPrefSize(150, 400);
  }

  /**
   * Display the real-time score in MultiplayerScene
   */
  @Override
  public void displayScore() {
    double delaySeconds = 0.1;  // initial delay second
    int a = 1;
    var timeline = new Timeline();
    this.getChildren().clear();  // clear whole the content and then refill the content


    for (Pair<String, Integer> p : scores) {
      a++;
      var stackPane = new StackPane();            // Add the crossing effect when a player is dead
      stackPane.setPrefSize(150, 20);
      var scoreDetail = new Text(p.getKey() + " " + p.getValue());
      stackPane.getChildren().add(scoreDetail);

      if (p.getKey().contains("DEAD")) {      //  Add the line cross the dead player
        var line = new Line(0, scoreDetail.getLayoutBounds().getHeight() / 2, scoreDetail.getLayoutBounds().getWidth() * 1.4, scoreDetail.getLayoutBounds().getHeight() / 2);
        line.setStroke(Color.GRAY);
        line.setStrokeWidth(2);
        line.setOpacity(0.8);
        stackPane.getChildren().add(line);
      }

      this.getChildren().add(stackPane);

      scoreDetail.getStyleClass().add("text1");
      stackPane.setVisible(false);                 // Initially hide the scoreDetail
      var keyFrame = new KeyFrame(Duration.seconds(delaySeconds * a), event -> {   // add delay second for each player
        stackPane.setVisible(true);
        this.reveal(scoreDetail);
      });
      timeline.getKeyFrames().add(keyFrame);
    }
    timeline.play();
  }
}
