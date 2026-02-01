package bricker.brick_strategies;

import danogl.GameObject;

/**
 * A collision strategy that performs no action. Use as a placeholder base strategy when
 * composing special behaviors so that the composed behaviors do not attempt to remove the brick.
 */
public class NoOpCollisionStrategy implements CollisionStrategy {
    /**
     * No operation.
     *
     * @param objA unused
     * @param objB unused
     */
    @Override
    public void onCollision(GameObject objA, GameObject objB) {
        // intentionally no-op
    }
}
