package util;

/**
 * This class is just a structure to keep two variables.
 *
 * @author Farshad Moghaddam
 */
public class Tuple<X, Y> {
    public final X x;
    public final Y y;

    /**
     * Constructor.
     *
     * @param x
     * @param y
     */
    public Tuple(
            X x,
            Y y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Gets the first variable.
     *
     * @return the first variable
     */
    public X getX() {
        return x;
    }

    /**
     * Gets the second variable.
     *
     * @return the second variable
     */
    public Y getY() {
        return y;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Tuple [x=" + x + ", y=" + y + "]";
    }

}
