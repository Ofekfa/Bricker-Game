package bricker.gameobjects;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.Sound;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * A simple bouncing ball that reflects its velocity upon collision and plays a collision sound.
 */
public class Ball extends GameObject {

    private final Sound collisionSound;

    /**
     * Construct a Ball.
     *
     * @param topLeftCorner initial top-left position
     * @param dimensions width and height of the ball
     * @param renderable renderable used to draw the ball
     * @param collisionSound sound to play on collision
     */
    public Ball(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                        Sound collisionSound) {
        super(topLeftCorner, dimensions, renderable);
        this.collisionSound = collisionSound;
    }

    /**
     * Reflect the ball velocity along the collision normal and play the collision sound.
     *
     * @param other the object this ball collided with
     * @param collision collision data
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        Vector2 newVel = getVelocity().flipped(collision.getNormal());
        setVelocity(newVel);
        this.collisionSound.play();
    }
}
