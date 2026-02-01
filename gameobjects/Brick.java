package bricker.gameobjects;

import bricker.brick_strategies.CollisionStrategy;
import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * A Brick object placed in a grid. Each brick holds a {@link CollisionStrategy} which defines
 * what happens when it is hit (for example removal, spawning items, or chain reactions).
 * Bricks know their grid coordinates (row and column) so strategies such as exploding
 * bricks can locate neighbors.
 */
public class Brick extends GameObject {
    private final int row;
    private final int col;
    private final CollisionStrategy strategy;
    /**
     * Construct a Brick instance.
     *
     * @param topLeftCorner Position of the object.
     * @param renderable The renderable representing the object. Can be null.
     * @param row Row index in the brick grid
     * @param col Column index in the brick grid
     * @param strategy CollisionStrategy executed on hit
     */
    public Brick(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable, int row, int col,
                 CollisionStrategy strategy) {
        super(topLeftCorner, dimensions, renderable);
        this.row = row;
        this.col = col;
        this.strategy = strategy;
    }

    /**
     * Delegate collision handling to the configured {@link CollisionStrategy}.
     *
     * @param other the colliding object
     * @param collision collision data
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        strategy.onCollision(this, other);
    }

    /**
     * @return the row index of this brick in the grid
     */
    public int getRow() {
        return this.row;
    }

    /**
     * @return the column index of this brick in the grid
     */
    public int getCol() {
        return this.col;
    }
}
