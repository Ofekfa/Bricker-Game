package bricker.gameobjects;

import danogl.GameObject;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.event.KeyEvent;

/**
 * A user-controlled paddle that moves left/right in response to keyboard input.
 * Ensures the paddle stays inside the window horizontal bounds.
 */
public class Paddle extends GameObject {

    private static final float MOVEMENT_SPEED = 350f;
    private final UserInputListener inputListener;
    private final Vector2 windowDimensions;

    /**
     * Construct a new Paddle.
     *
     * @param topLeftCorner initial top-left corner
     * @param dimensions width and height of the paddle
     * @param renderable visual representation
     * @param inputListener user input listener that handles left/right key presses
     * @param windowDimensions current window size for bounds checking
     */
    public Paddle(Vector2 topLeftCorner,
                  Vector2 dimensions,
                  Renderable renderable,
                  UserInputListener inputListener,
                  Vector2 windowDimensions) {
        super(topLeftCorner, dimensions, renderable);
        this.inputListener = inputListener;
        this.windowDimensions = windowDimensions;
    }

    /**
     * Update paddle position based on left/right key presses and clamp to window bounds.
     *
     * @param deltaTime elapsed time since last update
     */
    @Override
    public void update(float deltaTime) {
        Vector2 movementDir = Vector2.ZERO;
        if(inputListener.isKeyPressed(KeyEvent.VK_LEFT)) {
            movementDir = movementDir.add(Vector2.LEFT);
        }
        if(inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) {
            movementDir = movementDir.add(Vector2.RIGHT);
        }
        setVelocity(movementDir.mult(MOVEMENT_SPEED));
        super.update(deltaTime);

        Vector2 topLeft = getTopLeftCorner();
        float x = topLeft.x();
        float y = topLeft.y();
        float width = getDimensions().x();
        float maxX = windowDimensions.x() - width;

        if (x < 0f) {
            setTopLeftCorner(new Vector2(0f, y));
        }
        else if (x > maxX) {
            setTopLeftCorner(new Vector2(maxX, y));
        }
    }
}
