package model;

/**
 * Rating class
 *
 * @author FBM
 */
public class Rating {

    private final int userId;
    private final int itemId;
    private final float rating;

    public Rating(int userId, int itemId, float rating) {
        super();
        this.userId = userId;
        this.itemId = itemId;
        this.rating = rating;
    }

    public int getUserId() {
        return userId;
    }

    public int getItemId() {
        return itemId;
    }

    public float getRating() {
        return rating;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Rating [userId=" + userId + ", itemId=" + itemId + ", rating=" + rating + "]";
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + itemId;
        result = prime * result + Float.floatToIntBits(rating);
        result = prime * result + userId;
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Rating other = (Rating) obj;
        if (itemId != other.itemId) {
            return false;
        }
        if (Float.floatToIntBits(rating) != Float.floatToIntBits(other.rating)) {
            return false;
        }
        if (userId != other.userId) {
            return false;
        }
        return true;
    }

}
