package uk.ac.soton.comp1206.component;

import javafx.animation.AnimationTimer;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.Canvas;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.event.LastBlockListener;


/**
 * The Visual User Interface component representing a single block in the grid.
 * <p>
 * Extends Canvas and is responsible for drawing itself.
 * <p>
 * Displays an empty square (when the value is 0) or a coloured square depending on value.
 * <p>
 * The GameBlock value should be bound to a corresponding block in the Grid model.
 */
public class GameBlock extends Canvas {

  /**
   * A logger to log messages and events that occur during the execution of an application.
   */
  private static final Logger logger = LogManager.getLogger(GameBlock.class);

  /**
   * A boolean variable to determine if we need to place the indicator
   */
  private boolean ifSet = false;
  private boolean ifLastHoveredBlock;

  /**
   * The set of colours for different pieces
   */
  public static final Color[] COLOURS = {
          Color.TRANSPARENT,
          Color.DEEPPINK,
          Color.RED,
          Color.ORANGE,
          Color.YELLOW,
          Color.YELLOWGREEN,
          Color.LIME,
          Color.GREEN,
          Color.DARKGREEN,
          Color.DARKTURQUOISE,
          Color.DEEPSKYBLUE,
          Color.AQUA,
          Color.AQUAMARINE,
          Color.BLUE,
          Color.MEDIUMPURPLE,
          Color.PURPLE
  };

  /**
   * A board this block belongs to.
   */
  private final GameBoard gameBoard;

  /**
   * The width of the canvas to render
   */
  private final double width;

  /**
   * The height of the canvas to render
   */
  private final double height;

  /**
   * The column this block exists as in the grid
   */
  private final int x;

  /**
   * The row this block exists as in the grid
   */
  private final int y;

  /**
   * The value of this block (0 = empty, otherwise specifies the colour to render as)
   */
  private final IntegerProperty value = new SimpleIntegerProperty(0);

  /**
   * The number of the col which is need to add the hover effect
   */
  private int hoveredCol;

  /**
   * The number of the row which is need to add the hover effect
   */
  private int hoveredRow;

  /**
   * A last block listener
   */
  protected LastBlockListener lastBlockListener;


  /**
   * Create a new single Game Block
   *
   * @param gameBoard the board this block belongs to
   * @param x         the column the block exists in
   * @param y         the row the block exists in
   * @param width     the width of the canvas to render
   * @param height    the height of the canvas to render
   */
  public GameBlock(GameBoard gameBoard, int x, int y, double width, double height) {
    this.gameBoard = gameBoard;
    this.width = width;
    this.height = height;
    this.x = x;
    this.y = y;

    //A canvas needs a fixed width and height
    setWidth(width);
    setHeight(height);

    //Do an initial paint
    paint();

    //When the value property is updated, call the internal updateValue method
    value.addListener(this::updateValue);

  }

  /**
   * When the value of this block is updated,
   *
   * @param observable what was updated
   * @param oldValue   the old value
   * @param newValue   the new value
   */
  private void updateValue(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
    paint();
  }

  /**
   * Handle painting of the block canvas
   */
  public void paint() {

    //If the block is empty, paint as empty
    if (value.get() == 0) {
      paintEmpty();
    } else {
      //If the block is not empty, paint with the colour represented by the value
      paintColor(COLOURS[value.get()]);
    }

    if (ifSet) {
      addIndicator();
    }
  }

  /**
   * Paint this canvas empty
   */
  private void paintEmpty() {

    var gc = getGraphicsContext2D();
    var gradient = new LinearGradient(    // use gradient to create the dark transparent effect
            0, 0, 1, 1, true, CycleMethod.NO_CYCLE,          // the coordinates are proportional, from 0,0 to 1,1 for each block
            new Stop(0, Color.BLACK.deriveColor(0, 1, 1, 0.3)),
            new Stop(0.5, Color.BLACK.deriveColor(0, 1, 1, 0.5)),
            new Stop(1, Color.BLACK.deriveColor(0, 1, 1, 0.3))
            /* The gradient has three color stops, starting and ending with semi-transparent
            black and having a more opaque black color at the center.*/
    );

    //Clear
    gc.setEffect(null);
    gc.clearRect(0, 0, width, height);

    //Fill
    gc.setFill(gradient);
    gc.fillRect(0, 0, width, height);

    //Border
    gc.setStroke(Color.WHITE);
    gc.strokeRect(0, 0, width, height);
  }

  /**
   * Paint this canvas with the given colour
   *
   * @param colour the colour to paint
   */
  private void paintColor(Paint colour) {
    var gc = getGraphicsContext2D();

    var shadow = new DropShadow();   // Add some shadow to make the block looks more stereoscopic.
    shadow.setOffsetX(-3);      // add the offset to make the shadow
    shadow.setOffsetY(-3);
    shadow.setColor(Color.BLACK.deriveColor(0, 1, 1, 0.8));
    shadow.setRadius(4);

    //Clear
    gc.clearRect(0, 0, width, height);
    gc.setEffect(null);
    //Colour fill
    gc.setFill(colour);
    gc.fillRect(0, 0, width, height);
    gc.setEffect(shadow);

    //Border
    gc.setStroke(Color.BLACK);
    gc.strokeRect(0, 0, width, height);
  }

  /**
   * Get the column of this block
   *
   * @return column number
   */
  public int getX() {
    return x;
  }

  /**
   * Get the row of this block
   *
   * @return row number
   */
  public int getY() {
    return y;
  }

  /**
   * Get the current value held by this block, representing it's colour
   *
   * @return value
   */
  public int getValue() {
    return this.value.get();
  }

  /**
   * Bind the value of this block to another property. Used to link the visual block to a corresponding block in the Grid.
   *
   * @param input property to bind the value to
   */
  public void bind(ObservableValue<? extends Number> input) {
    value.bind(input);
  }

  /**
   * Add the indicator.
   */
  public void addIndicator() {
    double circleRadius = Math.min(width, height) / 4;   // the radius of the circle
    double circleCenterX = 1 + width / 2;  // determine the x coordinate
    double circleCenterY = 1 + height / 2; // determine the y coordinate
    var gc = getGraphicsContext2D();
    gc.setFill(Color.WHITE.deriveColor(0, 1, 1, 0.3));    // set the color
    // fill the circle
    gc.fillOval(
            circleCenterX - circleRadius,  // The x-coordinate of the upper-left corner of the bounding rectangle
            circleCenterY - circleRadius,     // The y-coordinate of the upper-left corner of the bounding rectangle
            circleRadius * 2,                 // The width of the bounding rectangle
            circleRadius * 2);                // The height of the bounding rectangle
  }

  /**
   * Set the boolean ifSet to true.
   */
  public void setIfSet() {
    ifSet = true;
  }

  /**
   * Add the hover when the mouse moving on.
   *
   * @param event mouse event
   */
  private void handleMouseMove(MouseEvent event) {
    lastBlockListener.clearLastBlock();          // clear the previous hover effect added by the keyboard when the mouse moving on, avoid the conflict between mouse and keyboard
    ifLastHoveredBlock = true;                   // set it is true when the mouse move into this block, which can avoid the conflict
    hoveredCol = (int) (event.getX() / width);   // get the x coordinate of the canvas and transfer it into the block coordinate
    hoveredRow = (int) (event.getY() / height);

    var gc = getGraphicsContext2D();
    gc.clearRect(0, 0, width, height);     // clear whole the previous effect

    addHover(hoveredRow, hoveredCol);            // add the hover
  }

  /**
   * Add the hover.
   *
   * @param row the row the block to be added
   * @param col teh column the block to be added
   */
  private void addHover(int row, int col) {
    var gc = getGraphicsContext2D();
    gc.setFill(Color.rgb(255, 255, 255, 0.5));
    gc.fillRect(col * width, row * height, width, height);  // transfer the block coordinate into the canvas coordinate and paint it
  }

  /**
   * Add handle method to handle the event
   */
  public void setIfHover() {
    setOnMouseMoved(this::handleMouseMove);
    setOnMouseExited(event -> {
      ifLastHoveredBlock = false;      // when the mouse moving away, set the boolean false
      paint();                         // repaint the block
    });
  }

  /**
   * Add the fade out.
   */
  public void fadeOut() {
    new AnimationTimer() {
      double currentOpacity = 0.5;      // the initial opacity

      /**
       * A method to create the animation during the time
       * @param opacity the opacity
       */
      @Override
      public void handle(long opacity) {
        var gc = getGraphicsContext2D();
        paintEmpty();              // paint the block with original color
        currentOpacity -= 0.01;    // decrease the opacity
        if (currentOpacity < 0.3) {
          stop();                  // if opacity < 0.3, stop
        } else {
          gc.setFill(Color.rgb(255, 0, 0, currentOpacity));   // set the color of the fade effect
          gc.fillRect(x, y, width, height);      // repaint the block
        }
      }
    }.start();     // start the animation
  }

  /**
   * Add the hover effect when there is a keyboard movement
   */
  public void handleKeyBoardMove() {

    int hoveredCol = (int) (x / width);      // get the block coordinate
    int hoveredRow = (int) (y / height);

    var gc = getGraphicsContext2D();
    gc.clearRect(0, 0, width, height);

    addHover(hoveredRow, hoveredCol);
  }

  /**
   * Get the boolean
   *
   * @return the boolean
   */
  public boolean getABoolean() {
    return ifLastHoveredBlock;
  }

  /**
   * Set the boolean
   *
   * @param b the boolean
   */
  public void setABoolean(Boolean b) {
    ifLastHoveredBlock = b;
  }

  /**
   * Initialize a listener to set the last block
   *
   * @param listener a listener
   */
  public void setLastBlockListener(LastBlockListener listener) {
    lastBlockListener = listener;
  }
}


