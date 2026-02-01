package bricker.brick_strategies;

import bricker.gameobjects.Brick;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.rendering.Renderable;
import danogl.gui.UserInputListener;
import danogl.util.Counter;
import danogl.util.Vector2;

import java.util.Random;

/**
 * StrategyFactory constructs {@link CollisionStrategy} instances. supports creating
 * single special strategies (decorating a provided base) and creating a combined special strategy
 */
public class StrategyFactory {
    /**
     * Enumeration of the available strategy types. <p>
     * - BASIC: no special behavior (the base removal behavior).<br>
     * - ADDITIONAL_BALLS: spawn extra pucks/balls when hit.<br>
     * - EXTRA_PADDLE: spawn an extra temporary paddle when hit.<br>
     * - EXPLODING_BRICKS: explode and affect neighboring bricks when hit.<br>
     * - RETURN_LIFE: spawn a falling heart that restores a life when collected.<br>
     * - DOUBLE: special wrapper that composes two special behaviors (may nest once).
     */
    public enum StrategyType {
        /** No special behavior; use the provided base strategy as-is. */
        BASIC,
        /** Spawn additional pucks when the brick is hit. */
        ADDITIONAL_BALLS,
        /** Create an extra temporary paddle when the brick is hit. */
        EXTRA_PADDLE,
        /** Cause the brick to explode and affect neighbors. */
        EXPLODING_BRICKS,
        /** Spawn a falling heart that restores a life when collected. */
        RETURN_LIFE,
        /** A composite behavior that runs two other special behaviors when the brick is hit. */
        DOUBLE
    }

    /**
     * Static array listing the five "special" types (excluding BASIC). Used by the random-selection
     * logic to pick a special behavior uniformly. Keeping this array central makes the selection
     * logic explicit and easy to reason about.
     */
    private static final StrategyType[] SPECIAL_TYPES = new StrategyType[] {
            StrategyType.ADDITIONAL_BALLS,
            StrategyType.EXTRA_PADDLE,
            StrategyType.EXPLODING_BRICKS,
            StrategyType.RETURN_LIFE,
            StrategyType.DOUBLE
    };

    /**
     * Create a CollisionStrategy of the requested type.
     * The supplied {@code baseStrategy} will be used as the strategy's base. For decorated
     * strategies that should not remove the brick, pass a {@link NoOpCollisionStrategy} as base.
     *
     * @param type requested strategy type
     * @param baseStrategy base strategy for removal or passed-through
     * @param gameObjects game object collection for adding/removing objects
     * @param imageReader image reader for loading images
     * @param soundReader sound reader for loading sounds
     * @param windowDimensions current window dimensions
     * @param puckSize puck size
     * @param puckSpeed puck speed
     * @param paddleSize paddle dimensions
     * @param paddleRenderable renderable for paddles
     * @param inputListener user input listener
     * @param bricksLeft counter tracking remaining bricks
     * @param brickGrid grid of bricks
     * @param originalPaddle reference to the original paddle
     * @param lifeRestoreCallback callback invoked by falling hearts to restore lives
     * @return constructed CollisionStrategy instance
     */
    public static CollisionStrategy createStrategy(StrategyType type,
                                                   CollisionStrategy baseStrategy,
                                                   GameObjectCollection gameObjects,
                                                   ImageReader imageReader,
                                                   SoundReader soundReader,
                                                   Vector2 windowDimensions,
                                                   float puckSize,
                                                   float puckSpeed,
                                                   Vector2 paddleSize,
                                                   Renderable paddleRenderable,
                                                   UserInputListener inputListener,
                                                   Counter bricksLeft,
                                                   Brick[][] brickGrid,
                                                   GameObject originalPaddle,
                                                   bricker.gameobjects.FallingHeart.LifeRestoreCallback
                                                           lifeRestoreCallback) {
        switch (type) {
            case ADDITIONAL_BALLS:
                return new AdditionalBallsStrategy(baseStrategy, gameObjects, imageReader, soundReader,
                        windowDimensions, puckSize, puckSpeed);
            case EXTRA_PADDLE:
                return new ExtraPaddleStrategy(gameObjects, windowDimensions, paddleRenderable,
                        inputListener, paddleSize, bricksLeft);
            case EXPLODING_BRICKS:
                return new ExplodingBrickStrategy(baseStrategy, soundReader, brickGrid);
            case RETURN_LIFE:
                return new ReturnLifeStrategy(baseStrategy, gameObjects, imageReader, windowDimensions,
                        originalPaddle, lifeRestoreCallback);
            case BASIC:
            default:
                return baseStrategy;
        }
    }

    /**
     * Create a combined CollisionStrategy by randomly selecting special behaviors
     * The constructed returned strategy will:
     *  - execute {@code realBase} once (removing the brick and decrementing {@code bricksLeft}), and
     *  - execute each of the randomly-chosen special strategies.
     * Inner special strategies are created with a {@link NoOpCollisionStrategy} base so they do not
     * attempt to remove the brick again.
     *
     * @param realBase the base strategy to execute
     * @param gameObjects game object collection
     * @param imageReader image reader
     * @param soundReader sound reader
     * @param windowDimensions window size
     * @param puckSize puck size
     * @param puckSpeed puck speed
     * @param paddleSize paddle size
     * @param paddleRenderable paddle renderable
     * @param inputListener user input listener
     * @param bricksLeft bricks-left counter
     * @param brickGrid brick grid for exploding behavior
     * @param originalPaddle original paddle reference
     * @param lifeRestoreCallback life restore callback
     * @param rand source of randomness
     * @return a CollisionStrategy executing base once then the randomly chosen specials
     */
    public static CollisionStrategy createRandomSpecialStrategy(CollisionStrategy realBase,
                                                                 GameObjectCollection gameObjects,
                                                                 ImageReader imageReader,
                                                                 SoundReader soundReader,
                                                                 Vector2 windowDimensions,
                                                                 float puckSize,
                                                                 float puckSpeed,
                                                                 Vector2 paddleSize,
                                                                 Renderable paddleRenderable,
                                                                 UserInputListener inputListener,
                                                                 Counter bricksLeft,
                                                                 Brick[][] brickGrid,
                                                                 GameObject originalPaddle,
                                                                bricker.gameobjects.
                                                                        FallingHeart.
                                                                        LifeRestoreCallback
                                                                        lifeRestoreCallback,
                                                                 Random rand) {
        // draw two outer picks, expanding DOUBLE at most one level; up to 3 specials
        StrategyType[] drawn = new StrategyType[3];
        int drawnCount = 0;

        for (int outer = 0; outer < 2 && drawnCount < 3; outer++) {
            StrategyType pick = SPECIAL_TYPES[rand.nextInt(SPECIAL_TYPES.length)];

            if (pick != StrategyType.DOUBLE) {
                drawn[drawnCount++] = pick;
            } else {
                // expand DOUBLE into two inner picksxs
                for (int inner = 0; inner < 2 && drawnCount < 3; inner++) {
                    StrategyType innerPick = SPECIAL_TYPES[rand.nextInt(SPECIAL_TYPES.length)];
                    while (innerPick == StrategyType.DOUBLE) {
                        innerPick = SPECIAL_TYPES[rand.nextInt(SPECIAL_TYPES.length)];
                    }
                    drawn[drawnCount++] = innerPick;
                }
            }
        }

        // Build special strategy instances using a NoOp base so they won't remove the brick.
        NoOpCollisionStrategy noop = new NoOpCollisionStrategy();
        CollisionStrategy[] specials = new CollisionStrategy[3];
        int specialsCount = 0;
        for (int i = 0; i < drawnCount; i++) {
            StrategyType t = drawn[i];
            CollisionStrategy s = createStrategy(t, noop, gameObjects, imageReader, soundReader,
                    windowDimensions, puckSize, puckSpeed, paddleSize, paddleRenderable, inputListener,
                    bricksLeft, brickGrid, originalPaddle, lifeRestoreCallback);
            specials[specialsCount++] = s;
        }

        if (specialsCount == 0) return realBase;
        // Return a CombinedCollisionStrategy (base once and then specials)
        return new CombinedCollisionStrategy(realBase, specials, specialsCount);
    }
}
