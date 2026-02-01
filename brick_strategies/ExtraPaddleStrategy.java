package bricker.brick_strategies;

import bricker.gameobjects.ExtraPaddle;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;

/**
 * Strategy that spawns an extra paddle when a brick is broken.
 * The extra paddle is a limited-life paddle that can take hits and then be removed.
 * Only one ExtraPaddle is allowed in the entire game at any time.
 */
public class ExtraPaddleStrategy implements CollisionStrategy {
    private final GameObjectCollection collection;
    private final Vector2 windowDimensions;
    private final Renderable paddleRenderable;
    private final UserInputListener inputListener;
    private final Vector2 paddleSize;

    private final BasicCollisionStrategy basic;

    /**
     * Construct an ExtraPaddleStrategy.
     *
     * @param collection game object collection to add/remove the extra paddle
     * @param windowDimensions the current window dimensions
     * @param paddleRenderable renderable to use for the extra paddle
     * @param inputListener user input listener
     * @param paddleSize the size of the paddle
     * @param bricksLeft counter for remaining bricks
     */
    public ExtraPaddleStrategy(GameObjectCollection collection,
                               Vector2 windowDimensions,
                               Renderable paddleRenderable,
                               UserInputListener inputListener,
                               Vector2 paddleSize,
                               Counter bricksLeft) {
        this.collection = collection;
        this.windowDimensions = windowDimensions;
        this.paddleRenderable = paddleRenderable;
        this.inputListener = inputListener;
        this.paddleSize = paddleSize;
        this.basic = new BasicCollisionStrategy(collection, bricksLeft);
    }

    /**
     * Remove the brick via the basic strategy and then create/restore
     * an extra paddle if none exists in the game.
     * @param brick the brick that was hit
     * @param hitter the object that hit the brick
     */
    @Override
    public void onCollision(GameObject brick, GameObject hitter) {
        basic.onCollision(brick, hitter);

        if (ExtraPaddle.isPresent()) return;

        ExtraPaddle p = new ExtraPaddle(
                new danogl.util.Vector2(0,0),
                paddleSize,
                paddleRenderable,
                inputListener,
                windowDimensions,
                collection
        );
        p.setCenter(new danogl.util.Vector2(windowDimensions.x()/2f, windowDimensions.y()/2f));
        collection.addGameObject(p);
    }
}
