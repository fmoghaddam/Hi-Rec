package algorithms.fm;

/**
 * This class is used to store the input data temporarily, when it is transferred from R101s DataModel to libfms Data class. Its usage might be omitted later on, but to date it is the best option, because its usage mimics Rendles Movielens-To-LibFm-Conversion-Script.
 */
public class DataLine {
    public float Rating;
    ///This feature-id is typically used for the unique id assigned to the user.
    public int LibFmId1;
    ///This feature-id is typically used for the unique id assigned to the item.
    public int LibFmId2;
 	///These feature-ids can be used for any additional features (e.g. context)
    int[] ContextIds;
}
