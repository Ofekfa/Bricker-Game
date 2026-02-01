package bricker.gameobjects;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.gui.ImageReader;
import danogl.gui.rendering.Renderable;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * UI component that displays remaining lives as hearts and a numeric counter.
 */
public class LivesUI {
    private static final float PADDING_X = 10f; // distance from left screen edge
    private static final float PADDING_Y = 10f; // distance from bottom screen edge
    private static final float HEART_WIDTH  = 24f;
    private static final float HEART_HEIGHT = 24f;
    private static final float HEART_GAP_X  = 6f; // gap between hearts
    private static final float NUM_TO_HEARTS_GAP_X = 20f; // gap between number and first heart

    private final GameObjectCollection collection;
    private final Renderable heartImg;
    private final Vector2 windowDimensions;
    private final int maxLives;

    private final GameObject[] hearts;
    private int shownHearts = 0;

    private final TextRenderable textRend = new TextRenderable("0");
    private final GameObject textObj;

    /**
     * Construct a LivesUI component and add it to the provided game object collection.
     *
     * @param collection game object collection to add UI elements to
     * @param imageReader image reader to load the heart image
     * @param windowDimensions current window dimensions
     * @param maxLives maximum number of lives displayable
     * @param initialLives initial displayed lives
     */
    public LivesUI(GameObjectCollection collection,
                   ImageReader imageReader,
                   Vector2 windowDimensions,
                   int maxLives,
                   int initialLives) {
        this.collection = collection;
        this.windowDimensions = windowDimensions;
        this.maxLives = maxLives;
        this.heartImg = imageReader.readImage("assets/heart.png", true);
        this.hearts = new GameObject[maxLives];

        Vector2 textPos = new Vector2(
                PADDING_X,
                windowDimensions.y() - PADDING_Y - HEART_HEIGHT
        );
        this.textObj = new GameObject(textPos, new Vector2(HEART_WIDTH, HEART_HEIGHT), textRend);
        this.textObj.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        collection.addGameObject(textObj, Layer.UI);

        updateLives(initialLives);
    }

    // ======== Helpers =======
    /**
     * Compute the vertical coordinate used to place hearts.
     *
     * @return y coordinate of the hearts baseline
     */
    private float baseY() { return windowDimensions.y() - PADDING_Y - HEART_HEIGHT; }

    /**
     * Compute the x-coordinate of the first heart (after the lives counter).
     *
     * @return x coordinate for the first heart
     */
    private float firstHeartX() { return PADDING_X + NUM_TO_HEARTS_GAP_X; }

    /**
     * Compute the position for a heart at a given index in the row.
     *
     * @param i index of heart, starting at 0
     * @return position where the heart GameObject should be placed
     */
    private Vector2 heartPosAtIndex(int i) {
        return new Vector2(firstHeartX() + i * (HEART_WIDTH + HEART_GAP_X), baseY());
    }

    /**
     * Update the visible number of lives shown by the UI.
     *
     * @param lives new lives count to display
     */
    public void updateLives(int lives) {
        if (lives < 0) lives = 0;
        if (lives > maxLives) lives = maxLives;

        // remove excess hearts from end to start
        while (shownHearts > lives) {
            int last = shownHearts - 1;
            if (hearts[last] != null) {
                collection.removeGameObject(hearts[last], Layer.UI);
                hearts[last] = null;
            }
            shownHearts--;
        }

        // add missing hearts - add at first empty []
        while (shownHearts < lives) {
            int idx = shownHearts;
            GameObject heart = new GameObject(
                    heartPosAtIndex(idx),
                    new Vector2(HEART_WIDTH, HEART_HEIGHT),
                    heartImg);
            heart.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
            collection.addGameObject(heart, Layer.UI);
            hearts[idx] = heart;
            shownHearts++;
        }

        textRend.setString(Integer.toString(lives));
        if (lives >= 3) textRend.setColor(Color.GREEN);
        else if (lives == 2) textRend.setColor(Color.YELLOW);
        else if (lives == 1) textRend.setColor(Color.RED);
    }

}
