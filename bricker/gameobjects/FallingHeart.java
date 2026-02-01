package bricker.gameobjects;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * A falling heart that can be collected by the original paddle to restore a life.
 * Falls at constant speed and only collides with the original paddle.
 */
public class FallingHeart extends GameObject {
    private static final float FALLING_HEART_SPEED = 100f;

    private final GameObjectCollection gameObjects;
    private final Vector2 windowDimensions;
    private final GameObject originalPaddle; // Reference to the original paddle (not ExtraPaddle)
    private final LifeRestoreCallback lifeRestoreCallback;

    /**
     * Callback interface for restoring a life when heart is collected.
     */
    public interface LifeRestoreCallback {
        /**
         Restore one life to the player, up to the maximum.
        */
        void restoreLife();
    }

    /**
     * Constructs a FallingHeart.
     *
     * @param topLeftCorner Position of the object.
     * @param dimensions Width and height in window coordinates.
     * @param renderable The renderable representing the object.
     * @param gameObjects Game objects collection for removal
     * @param windowDimensions Window dimensions for boundary checking
     * @param originalPaddle Reference to the original paddle (for collision detection)
     * @param lifeRestoreCallback Callback to restore a life when collected
     */
    public FallingHeart(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                        GameObjectCollection gameObjects, Vector2 windowDimensions,
                        GameObject originalPaddle, LifeRestoreCallback lifeRestoreCallback) {
        super(topLeftCorner, dimensions, renderable);
        this.gameObjects = gameObjects;
        this.windowDimensions = windowDimensions;
        this.originalPaddle = originalPaddle;
        this.lifeRestoreCallback = lifeRestoreCallback;

        // Set velocity to fall down at constant speed
        setVelocity(new Vector2(0, FALLING_HEART_SPEED));
    }

    /**
     * Override to only allow collision with the original paddle.
     *
     * @param other candidate object to collide with
     * @return true only if other equals the original paddle reference
     */
    @Override
    public boolean shouldCollideWith(GameObject other) {
        return other == originalPaddle;
    }

    /**
     * When colliding with the original paddle, restore a life via the callback and remove the heart.
     *
     * @param other the object collided with
     * @param collision collision data
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        // Restore a life and remove the heart
        lifeRestoreCallback.restoreLife();
        gameObjects.removeGameObject(this);
    }

    /**
     * Remove the heart if it falls below the bottom of the window.
     *
     * @param deltaTime time elapsed since last update
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        // Check if heart has exited the screen boundaries (below the screen)
        Vector2 center = getCenter();
        if (center.y() > windowDimensions.y()) {
            gameObjects.removeGameObject(this);
        }
    }
}
