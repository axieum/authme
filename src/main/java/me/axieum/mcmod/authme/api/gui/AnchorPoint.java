package me.axieum.mcmod.authme.api.gui;

/**
 * An anchor point for a widget relative to the screen boundaries.
 */
public enum AnchorPoint
{
    TOP_LEFT(0f, 0f),
    TOP_CENTER(.5f, 0f),
    TOP_RIGHT(1f, 0f),
    MIDDLE_LEFT(0f, .5f),
    MIDDLE_CENTER(.5f, .5f),
    MIDDLE_RIGHT(1f, .5f),
    BOTTOM_LEFT(0f, 1f),
    BOTTOM_CENTER(.5f, 1f),
    BOTTOM_RIGHT(1f, 1f);

    /** The X-coordinate as a decimal percentage relative to the screen boundaries. */
    public final float x;
    /** The Y-coordinate as a decimal percentage relative to the screen boundaries. */
    public final float y;

    /**
     * Constructs a new anchor point enum.
     *
     * @param x X-coordinate as decimal percentage
     * @param y Y-coordinate as decimal percentage
     */
    AnchorPoint(final float x, final float y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns an appropriate anchor point for a given coordinate within screen bounds.
     *
     * @param x X-coordinate
     * @param y Y-coordinate
     * @param width screen width
     * @param height screen height
     * @return closest anchor point for the given coordinates
     */
    public static AnchorPoint getFromCoords(final int x, final int y, final int width, final int height)
    {
        // Transform the coordinates into a percentage relative to the provided screen bounds
        final float xp = x / (float) width;
        final float yp = y / (float) height;

        // Iterate over all defined anchor points and find the closest to the given coordinates
        AnchorPoint bestPoint = null;
        double bestDistSq = Double.MAX_VALUE;
        for (AnchorPoint point : AnchorPoint.values()) {
            double distSq = Math.pow(point.x - xp, 2) + Math.pow(point.y - yp, 2);
            if (distSq < bestDistSq) {
                bestPoint = point;
                bestDistSq = distSq;
            }
        }
        return bestPoint;
    }

    public int getOffsetX(final int x, final int width)
    {
        return (int) (x - this.x * width);
    }

    public int getOffsetY(final int y, final int height)
    {
        return (int) (y - this.y * height);
    }
}
