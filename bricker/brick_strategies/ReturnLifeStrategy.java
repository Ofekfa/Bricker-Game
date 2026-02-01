package bricker.brick_strategies;

import bricker.gameobjects.FallingHeart;
import bricker.gameobjects.Brick;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * Strategy that creates a falling heart when a brick is broken.
 * The heart can be collected by the original paddle to restore a life.
 */
public class ReturnLifeStrategy implements CollisionStrategy {
    private static final float HEART_SIZE = 24f;

    private final CollisionStrategy baseStrategy;
    private final GameObjectCollection gameObjects;
    private final Renderable heartImage;
    private final Vector2 windowDimensions;
    private final GameObject originalPaddle;
    private final FallingHeart.LifeRestoreCallback lifeRestoreCallback;

    /**
     * Constructs a ReturnLifeStrategy.
     *
     * @param baseStrategy The base strategy to execute (removes brick and decrements counter)
     * @param gameObjects The game objects collection for adding the falling heart
     * @param imageReader Image reader for loading the heart image
     * @param windowDimensions Window dimensions for boundary checking
     * @param originalPaddle Reference to the original paddle
     * @param lifeRestoreCallback Callback to restore a life when heart is collected
     */
    public ReturnLifeStrategy(CollisionStrategy baseStrategy,
                             GameObjectCollection gameObjects,
                             ImageReader imageReader,
                             Vector2 windowDimensions,
                             GameObject originalPaddle,
                             FallingHeart.LifeRestoreCallback lifeRestoreCallback) {
        this.baseStrategy = baseStrategy;
        this.gameObjects = gameObjects;
        this.heartImage = imageReader.readImage("assets/heart.png", true);
        this.windowDimensions = windowDimensions;
        this.originalPaddle = originalPaddle;
        this.lifeRestoreCallback = lifeRestoreCallback;
    }

    /**
     * Execute the base removal strategy and then spawn a falling heart at the brick's center.
     *
     * @param objA the brick which was hit
     * @param objB the colliding object
     */
    @Override
    public void onCollision(GameObject objA, GameObject objB) {
        // First, execute the base strategy (removes brick and decrements counter)
        baseStrategy.onCollision(objA, objB);

        // Only spawn a heart if a Brick was hit
        if (!(objA instanceof Brick)) return;

        // Create falling heart at the brick's center
        Vector2 brickCenter = objA.getCenter();
        FallingHeart fallingHeart = new FallingHeart(
                brickCenter,
                new Vector2(HEART_SIZE, HEART_SIZE),
                heartImage,
                gameObjects,
                windowDimensions,
                originalPaddle,
                lifeRestoreCallback
        );
        fallingHeart.setCenter(brickCenter);
        gameObjects.addGameObject(fallingHeart);
    }
}
