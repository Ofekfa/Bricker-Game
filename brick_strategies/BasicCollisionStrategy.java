package bricker.brick_strategies;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.util.Counter;

/**
 * Basic collision strategy that removes the brick from the game and decrements the
 * provided bricks counter.
 */
public class BasicCollisionStrategy implements CollisionStrategy {
    private final GameObjectCollection collection;
    private final Counter bricksLeft;

    /**
     * Construct a BasicCollisionStrategy.
     *
     * @param collection the game object collection to remove bricks from
     * @param bricksLeft counter tracking remaining bricks
     */
    public BasicCollisionStrategy(GameObjectCollection collection, Counter bricksLeft) {
        this.collection = collection;
        this.bricksLeft = bricksLeft;
    }

    /**
     * Remove the brick from the static objects layer and decrement {@code bricksLeft}
     * if the removal was successful.
     *
     * @param objA the brick to remove
     * @param objB the object that hit the brick
     */
    @Override
    public void onCollision(GameObject objA, GameObject objB) {
        boolean removed = collection.removeGameObject(objA, Layer.STATIC_OBJECTS);
        if (removed) {
            this.bricksLeft.decrement();
        }
    }
}
