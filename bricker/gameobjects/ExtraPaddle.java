package bricker.gameobjects;


import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * A temporary extra paddle that behaves like the main paddle but has a limited number of hits.
 * When its hit counter reaches zero it removes itself from the game objects collection.
 *
 * Note: A static presence flag is used to ensure at most one ExtraPaddle exists at any time.
 */
public class ExtraPaddle extends Paddle {
    private final GameObjectCollection collection;
    private int hitsLeft = 4;

    // global presence flag: true if an ExtraPaddle instance has been created and not yet removed
    private static boolean present = false;

    /**
     * Return whether any ExtraPaddle is currently present in the game.
     *
     * @return true if an ExtraPaddle exists; false otherwise
     */
    public static boolean isPresent() { return present; }

    /**
     * Construct an ExtraPaddle and mark presence. Callers should check {@link #isPresent()}
     * before creating to avoid multiple ExtraPaddles.
     */
    public ExtraPaddle(Vector2 topLeftCorner,
                       Vector2 size,
                       Renderable renderable,
                       UserInputListener inputListener,
                       Vector2 windowDimensions,
                       GameObjectCollection collection) {
        super(topLeftCorner, size, renderable, inputListener, windowDimensions);
        this.collection = collection;
        present = true;
    }

    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        hitsLeft--;
        if (hitsLeft <= 0) {
            // clear presence before actual removal
            present = false;
            collection.removeGameObject(this);
        }
    }
}
