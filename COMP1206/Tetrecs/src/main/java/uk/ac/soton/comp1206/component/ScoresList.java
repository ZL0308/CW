package uk.ac.soton.comp1206.component;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.Pair;

/**
 * A Scores List extends VBox, which displays the score
 */
public class ScoresList extends VBox {

  /**
   * A simple list property which contains the score and name of each record.
   */
  protected SimpleListProperty<Pair<String, Integer>> scores;

  /**
   * Initialize the score list.
   */
  public ScoresList() {
    scores = new SimpleListProperty<>();
    initializeList();
  }

  /**
   * The method to initialize the score list. Set the width and height.
   */
  public void initializeList() {
    setPrefWidth(100);
    setPrefHeight(300);
  }

  /**
   * A method to get the list property.
   *
   * @return return the list property
   */
  public SimpleListProperty<Pair<String, Integer>> scoresProperty() {
    return scores;
  }

  /**
   * Add the fade transition of each pair of record
   *
   * @param text The text content to be added
   */
  public void reveal(Text text) {
    var fadeTransition = new FadeTransition(Duration.millis(500), text);
    fadeTransition.setFromValue(0);      // initial opacity
    fadeTransition.setToValue(1);        // final opacity
    fadeTransition.play();
  }

  /**
   * Display each pair of record one by one
   */
  public void displayScore() {
    Platform.runLater(() -> {
      int a = 1;                        // introduce an int as a multiplier
      double delaySeconds = 0.2;        // add the delay of each line
      var timeline = new Timeline();
      this.getChildren().clear();
      for (Pair<String, Integer> p : scores) {
        a++;
        var scoreDetail = new Text(p.getKey() + " : " + p.getValue());
        this.getChildren().add(scoreDetail);    // add each text into this VBox
        scoreDetail.getStyleClass().add("heading");
        scoreDetail.setVisible(false);       // Initially hide the scoreDetail
        var keyFrame = new KeyFrame(Duration.seconds(delaySeconds * a), event -> {        // use the keyframe to display the score one by one
          scoreDetail.setVisible(true);      // set the score visible
          this.reveal(scoreDetail);          // call reveal to implement the fade effect
        });
        timeline.getKeyFrames().add(keyFrame);
      }
      timeline.play();                  // start playing the timeline
    });
  }
}
