/**
 * 
 */
package run;

import interfaces.Recommender;
import model.Globals;

/**
 * Running Configuration
 * 
 * @author FBM
 *
 */
public final class Configuration {

    private final int id;
    private final Recommender algorithm;
    private final boolean useLowLevel;
    private final boolean useGenre;
    private final boolean useTag;
    private final boolean useRating;

    /**
     * @param algorithm 
     * @param useLowLevel
     * @param useGenre
     * @param useTag
     * @param useRating
     */
    public Configuration(
            int id, Recommender algorithm, boolean useLowLevel,
            boolean useGenre, boolean useTag,
            boolean useRating)
    {
        if (algorithm == null) {
            throw new IllegalArgumentException("Algorithm is null");
        }
        this.id = id;
        this.algorithm = algorithm;
        this.useLowLevel = useLowLevel;
        this.useGenre = useGenre;
        this.useTag = useTag;
        this.useRating = useRating;
        if (algorithm.isSimilairtyNeeded()) {
            this.evaluate();
        }else{
            this.basicEvaluate();
        }
    }

    /**
     * Do the basic evaluation which is needed for all the algorithms
     */
    private
            void basicEvaluate() {
        if(!this.useLowLevel && !this.useGenre && !this.useTag && !this.useRating){
            throw new IllegalArgumentException(
                    "At least one of the lowlevel, genre, tag or rating should be true in config file");
        }
    }

    /**
     * Evaluate this configuration
     */
    private
            void evaluate() {
        if (this.useLowLevel && (Globals.LOW_LEVEL_FILE_PATH == null
                || Globals.LOW_LEVEL_FILE_PATH.isEmpty()))
        {
            throw new IllegalArgumentException(
                    "You want to use LowLevel feature but have not set LowLevel file path");
        }
        if (this.useGenre && (Globals.GENRE_FILE_PATH == null
                || Globals.GENRE_FILE_PATH.isEmpty()))
        {
            throw new IllegalArgumentException(
                    "You want to use genre but have not set genre file path");
        }
        if (this.useTag && (Globals.TAG_FILE_PATH == null
                || Globals.TAG_FILE_PATH.isEmpty()))
        {
            throw new IllegalArgumentException(
                    "You want to use tag but have not set tag file path");
        }
        if ((this.useLowLevel && this.useTag) || (this.useGenre && this.useTag)
                || (this.useLowLevel && this.useRating)
                || ((this.useGenre && this.useRating))
                || (this.useTag && this.useRating))
        {
            throw new IllegalArgumentException(
                    "Only one of the lowlevel, genre,tag and rating can be true in config file. The only exception is lowLevel and Genre could be true");
        }
        if (!(this.useLowLevel || this.useGenre || this.useTag
                || this.useRating))
        {
            throw new IllegalArgumentException(
                    "At least one of the lowlevel, genre, tag or rating should be true in config file");
        }
    }

    /**
     * @return the algorithm
     */
    public final
            Recommender getAlgorithm() {
        return algorithm;
    }

    /**
     * @return the useLowLevel
     */
    public final
            boolean isUseLowLevel() {
        return useLowLevel;
    }

    /**
     * @return the useGenre
     */
    public final
            boolean isUseGenre() {
        return useGenre;
    }

    /**
     * @return the useTag
     */
    public final
            boolean isUseTag() {
        return useTag;
    }

    /**
     * @return the useRating
     */
    public final
            boolean isUseRating() {
        return useRating;
    }

    /**
     * @return the id
     */
    public final
            int getId() {
        return id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public
            int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((algorithm == null) ? 0 : algorithm.hashCode());
        result = prime * result + id;
        result = prime * result + (useGenre ? 1231 : 1237);
        result = prime * result + (useLowLevel ? 1231 : 1237);
        result = prime * result + (useRating ? 1231 : 1237);
        result = prime * result + (useTag ? 1231 : 1237);
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public
            boolean equals(
                    Object obj)
    {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Configuration other = (Configuration)obj;
        if (algorithm == null) {
            if (other.algorithm != null) {
                return false;
            }
        } else if (!algorithm.equals(other.algorithm)) {
            return false;
        }
        if (id != other.id) {
            return false;
        }
        if (useGenre != other.useGenre) {
            return false;
        }
        if (useLowLevel != other.useLowLevel) {
            return false;
        }
        if (useRating != other.useRating) {
            return false;
        }
        if (useTag != other.useTag) {
            return false;
        }
        return true;
    }

    /*
     * @see java.lang.Object#toString()
     */
    @Override
    public
            String toString() {
        return "Configuration [id=" + id + ", algorithm=" + algorithm
                + ", useLowLevel=" + useLowLevel + ", useGenre="
                + useGenre + ", useTag=" + useTag + ", useRating=" + useRating
                + "]";
    }

}
