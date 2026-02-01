package bricker.gameobjects;


import danogl.collisions.GameObjectCollection;
import danogl.gui.Sound;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.util.Random;

/**
 * A puck is a ball variant created by special bricks; it is smaller and moves upward
 * with a random initial angle. Pucks are removed when they fall below the bottom of the window.
 */
public class Puck extends Ball {
    private final GameObjectCollection gameObjects;
    private final Vector2 windowDimensions;
    private final float puckSpeed;

    /**
     * Construct a Puck.
     *
     * @param topLeftCorner initial position
     * @param dimensions size of the puck
     * @param renderable renderable used to draw the puck
     * @param collisionSound sound to play on collision
     * @param gameObjects collection used to remove the puck when out of bounds
     * @param windowDimensions window size for bounds
     * @param puckSpeed speed magnitude for puck
     */
    public Puck(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                Sound collisionSound, GameObjectCollection gameObjects, Vector2 windowDimensions,
                float puckSpeed) {
        super(topLeftCorner, dimensions, renderable, collisionSound);
        this.gameObjects = gameObjects;
        this.windowDimensions = windowDimensions;
        this.puckSpeed = puckSpeed;
        setRandomVelocityOnUpperHalf();
    }

    /**
     * Initialize a random velocity pointing to the upper half of the screen.
     */
    private void setRandomVelocityOnUpperHalf() {
        Random random = new Random();
        double angle = random.nextDouble() * Math.PI;
        float velX = (float) (puckSpeed * Math.cos(angle));
        float velY = (float) (puckSpeed * Math.sin(angle));
        // Make Y negative to go upward
        setVelocity(new Vector2(velX, -Math.abs(velY)));
    }

    /**
     * Remove the puck if it falls below the bottom of the window.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        // Check if puck has exited below the screen
        Vector2 center = getCenter();
        if (center.y() > windowDimensions.y()) {
            gameObjects.removeGameObject(this);
        }
    }
}
