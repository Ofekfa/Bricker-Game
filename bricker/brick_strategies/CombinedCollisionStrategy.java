package bricker.brick_strategies;

import danogl.GameObject;

/**
 * A combined collision strategy that executes a base strategy once
 * and then executes an array of special strategies
 */
public class CombinedCollisionStrategy implements CollisionStrategy {
    private final CollisionStrategy base;
    private final CollisionStrategy[] specials;
    private final int specialsCount;

    /**
     * Construct a combined strategy.
     *
     * @param base The strategy to execute once.
     * @param specials Array of special strategies to execute after the base removal. Items beyond
     *                 {@code specialsCount} in the array are ignored.
     * @param specialsCount Number of entries in {@code specials} that are valid (0..specials.length).
     */
    public CombinedCollisionStrategy(CollisionStrategy base,
                                     CollisionStrategy[] specials,
                                     int specialsCount) {
        this.base = base;
        this.specials = specials;
        this.specialsCount = specialsCount;
    }

    /**
     * Execute the base strategy once and then each of the special strategies in order.
     *
     * @param objA The brick whose strategy is being executed.
     * @param objB The colliding object.
     */
    @Override
    public void onCollision(GameObject objA, GameObject objB) {
        if (base != null) base.onCollision(objA, objB);
        if (specials == null || specialsCount <= 0) return;
        for (int i = 0; i < specialsCount; i++) {
            CollisionStrategy s = specials[i];
            if (s != null) s.onCollision(objA, objB);
        }
    }
}
