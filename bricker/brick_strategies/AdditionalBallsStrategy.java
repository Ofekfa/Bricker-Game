package bricker.brick_strategies;

import bricker.gameobjects.Puck;
import bricker.gameobjects.Brick;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.Sound;
import danogl.gui.SoundReader;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;


/**
 * Strategy that spawns additional pucks when a brick is hit.
 * This strategy decorates a base {@link CollisionStrategy} so the base removal behavior is
 * still executed, and then new {@link Puck} instances are created and added to the
 * {@link GameObjectCollection}.
 */
public class AdditionalBallsStrategy implements CollisionStrategy {
    private static final int NUM_PUCKS = 2;

    private final CollisionStrategy baseStrategy;
    private final GameObjectCollection gameObjects;
    private final Renderable puckImage;
    private final Sound collisionSound;
    private final Vector2 windowDimensions;
    private final float puckSize;
    private final float puckSpeed;

    /**
     * Construct an AdditionalBallsStrategy.
     *
     * @param baseStrategy the base strategy to execute
     * @param gameObjects the game object collection to add pucks to
     * @param imageReader image reader to load puck images
     * @param soundReader sound reader to load collision sound
     * @param windowDimensions current window dimensions
     * @param puckSize size of each spawned puck
     * @param puckSpeed initial speed for spawned pucks
     */
    public AdditionalBallsStrategy(CollisionStrategy baseStrategy,
                                   GameObjectCollection gameObjects,
                                   ImageReader imageReader,
                                   SoundReader soundReader,
                                   Vector2 windowDimensions,
                                   float puckSize,
                                   float puckSpeed) {
        this.baseStrategy = baseStrategy;
        this.gameObjects = gameObjects;
        this.puckImage = imageReader.readImage("assets/mockBall.png", true);
        this.collisionSound = soundReader.readSound("assets/blop.wav");
        this.windowDimensions = windowDimensions;
        this.puckSize = puckSize;
        this.puckSpeed = puckSpeed;
    }

    /**
     * Execute the base strategy and then spawn pucks centered at the brick.
     * Only executes the additional-ball logic when {@code objA} is a {@link Brick}.
     *
     * @param objA the brick being hit
     * @param objB the object that hit the brick
     */
    @Override
    public void onCollision(GameObject objA, GameObject objB) {
        baseStrategy.onCollision(objA, objB);

        // Only spawn additional pucks when a Brick was hit.
        if (!(objA instanceof Brick)) return;

        Vector2 brickCenter = objA.getCenter();

        for (int i = 0; i < NUM_PUCKS; i++) {
            Puck puck = new Puck(
                    brickCenter,
                    new Vector2(puckSize, puckSize),
                    puckImage,
                    collisionSound,
                    gameObjects,
                    windowDimensions,
                    puckSpeed
            );
            puck.setCenter(brickCenter);
            gameObjects.addGameObject(puck);
        }
    }
}
