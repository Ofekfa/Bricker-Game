package bricker.brick_strategies;

import bricker.gameobjects.Brick;
import danogl.GameObject;
import danogl.gui.Sound;
import danogl.gui.SoundReader;

/**
 * Strategy that causes a brick to explode and destroy adjacent bricks (up, down, left, right).
 * The explosion can trigger chain reactions if adjacent bricks also have special behaviors.
 * Uses a 2D array to efficiently look up adjacent bricks by their row and column indices.
 */
public class ExplodingBrickStrategy implements CollisionStrategy {
    private static final String EXPLOSION_SOUND_PATH = "assets/explosion.wav";

    private final CollisionStrategy baseStrategy;
    private final Sound explosionSound;
    private final Brick[][] brickGrid;

    /**
     * Constructs an ExplodingBrickStrategy.
     *
     * @param baseStrategy The base strategy to execute (removes brick and decrements counter)
     * @param soundReader Sound reader for playing explosion sound
     * @param brickGrid 2D array storing all bricks by their grid coordinates [row][col]
     */
    public ExplodingBrickStrategy(CollisionStrategy baseStrategy,
                                  SoundReader soundReader,
                                  Brick[][] brickGrid) {
        this.baseStrategy = baseStrategy;
        this.explosionSound = soundReader.readSound(EXPLOSION_SOUND_PATH);
        this.brickGrid = brickGrid;
    }

    /**
     * Execute explosion: play sound, remove the current brick and attempt to explode adjacent ones.
     *
     * @param objA the brick that was hit
     * @param objB the colliding object
     */
    @Override
    public void onCollision(GameObject objA, GameObject objB) {
        if (explosionSound != null) explosionSound.play();

        // First, execute the base strategy - removes brick and decrements counter
        baseStrategy.onCollision(objA, objB);

        // Check if objA is a Brick and explode adjacent bricks
        if (!(objA instanceof Brick)) return;
        Brick brick = (Brick) objA;
        int row = brick.getRow();
        int col = brick.getCol();

        // Clear this brick from the grid to avoid processing it again
        if (row >= 0 && row < brickGrid.length &&
            col >= 0 && col < brickGrid[row].length) {
            brickGrid[row][col] = null;
        }

        explodeAdjacentBrick(row - 1, col, objB);
        explodeAdjacentBrick(row + 1, col, objB);
        explodeAdjacentBrick(row, col - 1, objB);
        explodeAdjacentBrick(row, col + 1, objB);
    }

    /**
     * Explodes an adjacent brick at the given row and column.
     * Looks up the brick in the 2D array and triggers its strategy.
     *
     * @param row Row index of the adjacent brick
     * @param col Column index of the adjacent brick
     * @param collidingObject The object that collided with the original brick
     */
    private void explodeAdjacentBrick(int row, int col, GameObject collidingObject) {
        // Check bounds to avoid array index out of bounds
        if (row < 0 || row >= brickGrid.length) {
            return;
        }
        if (col < 0 || brickGrid[row] == null || col >= brickGrid[row].length) {
            return;
        }

        Brick adjacentBrick = brickGrid[row][col];
        if (adjacentBrick != null) {
            brickGrid[row][col] = null;
            adjacentBrick.onCollisionEnter(collidingObject, null);
        }
    }
}
