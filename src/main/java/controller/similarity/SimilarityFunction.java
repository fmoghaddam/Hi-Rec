package controller.similarity;

/**
 * All the possible similarity functions
 * @author FBM
 *
 */
public enum SimilarityFunction {
    COSINE("cosine"),
    PEARSON("pearson"),
    UNKNOWN("unknown");

    private String name;

    private SimilarityFunction(
            final String name)
    {
        this.name = name;
    }

    public static
            SimilarityFunction reolve(
                    final String name)
    {
        if (name == null) {
            throw new IllegalArgumentException("Name is null");
        }
        for (SimilarityFunction simfun: SimilarityFunction.values()) {
            if (name.equals(simfun.name)) {
                return simfun;
            }
        }
        return SimilarityFunction.UNKNOWN;
    }
}
