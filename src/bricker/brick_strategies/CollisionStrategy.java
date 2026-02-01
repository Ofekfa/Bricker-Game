package bricker.brick_strategies;

import danogl.GameObject;

/**
 * Interface representing a collision behavior for a Brick.
 */
public interface CollisionStrategy {
    /**
     * Called when a collision with a Brick occurs.
     * Implementations should perform their specific behavior - remove the brick,
     * spawn items, trigger explosions, or restore a life.
     *
     * @param objA The brick.
     * @param objB The colliding object (ball or puck).
     */
    void onCollision(GameObject objA, GameObject objB);
}
