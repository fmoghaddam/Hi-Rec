package algorithms.fm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import it.unimi.dsi.fastutil.floats.FloatArrayList;
import model.DataModel;
import model.DataType;
import model.Rating;

/**
 * This class holds the input data for the recommendation process. It is used for training, test and validation data.
 */
public class Data {
	
	private static final Logger LOG = Logger.getLogger(Data.class.getCanonicalName());
    ///Next two parameters decide, if the data is to be transposed
    protected boolean has_xt;
    protected boolean has_x;	

    public Data(boolean has_x, boolean has_xt) { 
            this.data_t = null;
            this.data = null;
            this.has_x = has_x;
            this.has_xt = has_xt;
    }

    ///The actual data (one pointer for "normal" data, one for the transposed data. Only one of these is actually used at a time.)
    public LargeSparseMatrixFloat data_t;
    public LargeSparseMatrixFloat data;
    
    ///The ratings. This is only used if this object represents training or validation data.
    public DVectorFloat target = new DVectorFloat();

    ///Simple counters for how much data is stored inside the object
    public int num_feature;
    public int num_cases;

    ///Minimum and maximum values for the ratings
    public float min_target;
    public float max_target;

    /**
     * Does verbose debug output
     */
    public void debug(){
        if (has_x) {
                for (data.begin(); (!data.end()) && (data.getRowIndex() < 4); data.next() ) {
                        LOG.info(target.get(data.getRowIndex()));
                        for (int j = 0; j < data.getRow().size; j++) {
                        	LOG.info(" " + data.getRow().data[j].id + ":" + data.getRow().data[j].value);	
                        }
                }
        }
    }
    
    /**
     * Transposes the data stored inside the class. The data has to be loaded beforehand with one of the load methods. This method is only used for the MCMC and ALS algorithm.
     */
    public void create_data_t(){
        // for creating transpose data, the data has to be memory-data because we use random access
        DVectorSparse_rowFloat localData = ((LargeSparseMatrixMemoryFloat)this.data).data;

        data_t = new LargeSparseMatrixMemoryFloat();

        DVectorSparse_rowFloat localDataT = ((LargeSparseMatrixMemoryFloat)this.data_t).data;

        // make transpose copy of training data
        localDataT.setSize(num_feature);
        localDataT.init();
        
        // find dimensionality of matrix
        DVectorInt num_values_per_column = new DVectorInt();
        num_values_per_column.setSize(num_feature);
        num_values_per_column.init(0);
        
        long num_values = 0;
        for (int i = 0; i < localData.dim; i++) {
                for (int j = 0; j < localData.get(i).size; j++) {
                        num_values_per_column.set(localData.get(i).data[j].id,num_values_per_column.get(localData.get(i).data[j].id)+1);
                        num_values++;
                }
        }	


        ((LargeSparseMatrixMemoryFloat)this.data_t).num_cols = localData.dim;
        ((LargeSparseMatrixMemoryFloat)this.data_t).num_values = num_values;

        // create data structure for values			
        for (int i = 0; i < localDataT.dim; i++) {
                localDataT.get(i).data = new sparse_entryFloat[num_values_per_column.get(i)];
                localDataT.get(i).size = num_values_per_column.get(i);	
                for(int j=0;j<num_values_per_column.get(i);j++){
                    localDataT.get(i).data[j] = new sparse_entryFloat();
                }
        } 
        // write the data into the transpose matrix
        num_values_per_column.setSize(0);
        num_values_per_column.setSize(num_feature);
        num_values_per_column.init(0); // num_values per column now contains the pointer on the first empty field
        for (int i = 0; i < localData.dim; i++) {
                for (int j = 0; j < localData.get(i).size; j++) {
                        int f_id = localData.get(i).data[j].id;
                        int cntr = num_values_per_column.get(f_id);
                        localDataT.get(f_id).data[cntr].id = i;
                        localDataT.get(f_id).data[cntr].value = localData.get(i).data[j].value;
                        num_values_per_column.set(f_id, num_values_per_column.get(f_id)+1 );
                }
        }
        num_values_per_column.setSize(0);
    }
    
    /**
     * The main method to load input data into the class, when used together with R101. A data model containing the input has to be provided as well as a "mapping" structure, that is used to map the R101-ids (e.g. user id) to libfm-ids. The reasoning for this is, that to libfm-ids have to be consecutive beginning at 0, which is not the case for the R101-ids. When instantiating new features because of a previously unknown id (e.g. new item) this mehtod uses the idOffset parameter, which contains the next free id. These parameters have to be given to this mehtod, because there are more than one Data instances, so the central storage of the idOffset and mappings is in the FactorizationMachine object.
     * @param inputData A data model containing the input data.
     * @param idMap This structure represents a mapping from all R101-ids to the corresponding libfm-ids. The List-part represents the feature columns (e.g. users = 0; items = 1; first_context_feature = 2; ...). The HashMAp inside this list maps one R101-id to its libfm-counterpart. If no match is found, the next idOffset is assigned as the new libfm-id and the mapping is added to the appropriate HashMap.
     * @param idOffset As described in the idMap section, this "global counter" is used if a R101-id does not yet have a libfm counterpart. The idOffset is then used and stored inside the mapping. Afterwards the idOffset is incremented.
     * @param contextEnabled If this switch is set to false, context information will be disregarded even if it is available.
     * @param dataType 
     * @param uSE_PERSONAITY 
     * @return The new idOffset, after potential inserts of unknown R101 features.
     * @throws IOException
     */
    public int loadFromDataModel(DataModel inputData, List<HashMap<Integer, Integer>> idMap, int idOffset, boolean contextEnabled, DataType dataType) throws IOException{
        ///The following conversion mechanism is basically a blunt copy of the algorithm from Rendles "tripleformatconverter"
    	///This converter is used to convert a Movielens file to a libfm file
    	///The algorithm was modified to do the conversion in memory and from R101-input instead
    	
    	///The DataLine class represents one line that is later to be added to this classes data structures.
    	///DataLine is only used temporarily though (for counting features and instantiating low lewel data structures with the appropriate size
    	///Maybe the DataLine approach could be skipped as it can be considered a legacy of the tripleformatconverter. But to data it still stand as it was tested for correctness.
        List<DataLine> conversionList  = new ArrayList<>(inputData.getRatings().size());
        int id_cntr = idOffset;
        List<HashMap<Integer, Integer>> id = idMap;
        for(Rating rating : inputData.getRatings()){
        	///Build a temporary DataLine for each R101-Triple
            DataLine dataLine = new DataLine();
            dataLine.Rating =  rating.getRating();
            ///search if there is already a unique identifier for the USER
            if (!id.get(0).containsKey(rating.getUserId())) {
                    id.get(0).put(rating.getUserId(), id_cntr);
                    id_cntr++;
            }
            ///Add the feature-id for the user to the DataLine object
            dataLine.LibFmId1 = id.get(0).get(rating.getUserId());
            
            ///search if there is already a unique identifier for the ITEM
            if (!id.get(1).containsKey(rating.getItemId())) {
                    id.get(1).put(rating.getItemId(), id_cntr);
                    id_cntr++;
            }
            ///Add the feature-id for the item to the DataLine object
            dataLine.LibFmId2 = id.get(1).get(rating.getItemId());

            ///Check for extraInformation (= context)
            FloatArrayList o = null;
            
            if(contextEnabled){
            	switch (dataType) {
				case Personality:
					o = inputData.getUser(rating.getUserId()).getPersonalityValues();
					break;
				case LowLevelFeature:
					o = inputData.getItem(rating.getItemId()).getLowLevelFeature();
					break;
				default:
					break;
				}
            }
            ///Is there context and is the usage wanted?
            if(o!=null&&contextEnabled){
                float[] context = o.toFloatArray();
                ///Allocate more feature-ids for the context
                dataLine.ContextIds = new int[context.length];
                for(int i = 0; i<context.length;i++){
                	///If necessary: Add more HashMaps for the context. These are used in the same way as the ones for users and items: R101-id -> libfm-id
                    if(id.size()<=i+2)
                        id.add(new HashMap<Integer, Integer>());
                    ///search if there is already a unique identifier for the context-feature
                    HashMap<Integer, Integer> hashMap = id.get(i+2);
					if (!hashMap.containsKey((int)context[i])) {
                            hashMap.put((int)context[i], id_cntr);
                            id_cntr++;
                    }
                    ///Add the feature-id for the context-feature to the DataLine object
                    Integer integer = hashMap.get((int)context[i]);
					dataLine.ContextIds[i] = integer;
                }
            }else{
                dataLine.ContextIds = new int[0];
            }
            
            conversionList.add(dataLine);
        }
        
	    //Logging.log("has x = " +has_x );
		//Logging.log("has xt = " + has_xt);
        if(!(has_x||has_xt))
            throw new IllegalArgumentException();

        ///Here the original had an option for binary loading which is omitted, because we load from R101 and not libfm files.
		
        data = new LargeSparseMatrixMemoryFloat();

        ///The following variable was called data in the original, which hides a field of this class. It was renamed.
        DVectorSparse_rowFloat dataLocal = ((LargeSparseMatrixMemoryFloat)this.data).data;

        ///From here on out sizes for data structures and static values (e.g. min target) are calculated.
        int num_rows = 0;
        long num_values = 0;
        num_feature = 0;
        min_target = Float.MAX_VALUE;
        max_target = -Float.MIN_VALUE;
        
        float _value;
        for(int i = 0; i < conversionList.size(); i++){
            DataLine currentLine = conversionList.get(i);
            
            _value = currentLine.Rating;
            min_target = Math.min(_value, min_target);
            max_target = Math.max(_value, max_target);
            
            num_feature = Math.max(currentLine.LibFmId1, num_feature);
            num_values++;
            
            num_feature = Math.max(currentLine.LibFmId2, num_feature);
            num_values++;
            
            for(int j = 0;j < currentLine.ContextIds.length;j++){
                num_feature = Math.max(currentLine.ContextIds[j], num_feature);
                num_values++;
            }
            
            num_rows++;
        }
        num_feature++; // number of feature is bigger (by one) than the largest value
         
        dataLocal.setSize(num_rows);
        dataLocal.init();
        
        ///Not in original but necessary for avoiding nullpointer exceptions
        target.setSize(num_rows);
        target.init(0.0f);

        ((LargeSparseMatrixMemoryFloat)data).num_cols = num_feature;
        ((LargeSparseMatrixMemoryFloat)data).num_values = num_values;        
        
        ///From this point on the data, that was stored in the temporary DataLine objects, is transferred to their final destination inside the "low level" data structures, on which all the calculations are made.
        int row_id = 0;
        for(int i = 0; i < conversionList.size(); i++){
            DataLine currentLine = conversionList.get(i);

            target.set(row_id, (float)currentLine.Rating);
            sparse_rowFloat row = dataLocal.get(row_id);
            row.data = new sparse_entryFloat[2 + currentLine.ContextIds.length];
            row.size = 2 + currentLine.ContextIds.length;

            row.data[0] = new sparse_entryFloat();
            row.data[0].id = currentLine.LibFmId1;
            row.data[0].value = 1;
            
            row.data[1] = new sparse_entryFloat();
            row.data[1].id = currentLine.LibFmId2;
            row.data[1].value = 1;
            
            for(int j = 0;j < currentLine.ContextIds.length;j++){
                row.data[j+2] = new sparse_entryFloat();
                row.data[j+2].id = currentLine.ContextIds[j];
                row.data[j+2].value = 1;
            }
            
            row_id++;
        }
        
        num_cases = target.dim;

        ///The bool has_xt is automatically set before this method is called. It triggers the call of the create_data_t method, which transposes all of the data. This is necessary for the MCMC and ALS algorithm.
        if (has_xt) {create_data_t();}
        ///After new items are added the idOffset for new features has changed. So we return the new offset here.
        return id_cntr;
    }
}
