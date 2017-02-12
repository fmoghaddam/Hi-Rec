package algorithms.fm;

import java.util.ArrayList;
import java.util.List;

/**
 * A generic vector implementation. It was largly replaced by typed vectors, but for very large classes autoboxing is a negligible speed detriment.
 */
public final class DVector<T> {
    ///The actual data. An array (e.g. T[] _arr) is not possible, as Java does not allow arrays of generic type parameters.
    private List<T> _list;
    public int dim;
    
    public DVector(){
        _list = new ArrayList<>();
        dim = 0;
    }
    
    public DVector(int p_dim) {
        setSize(p_dim);
    }
    
    /**
     * Retrieve a specific value from the vector
     * @param idx The index of the element
     * @return The value of the element
     */
    public T get(int idx){
        return _list.get(idx);
    }
    
    ///
    /**
     * Fills the whole vector with the same value. Current values are lost. Size has to be set beforehand.
     * It is IMPERATIVE that this method is only used for primitive types.
     * For all other types all values point to the same object
     * @param v
     */
    public void init(T v) {
            for (int i = 0; i < dim; i++) {
                    _list.add(v);
            }	
    }
    
    /**
     * Overwrite the whole vector with the values from another vector instance
     * @param v The vector to be copied
     */
    public void assign(DVector<T> v) {
        if (v.dim != dim)
            setSize(v.dim);
        for (int i = 0; i < dim; i++) {
                _list.set(i, v.get(i));
        }
    }
    
    /**
     * Overwrite the whole vector with the values from an array
     * @param v The array which is to be copied into the vector
     */
    public void assign(T[] v) {
        if (v.length != dim)
            setSize(v.length);
        for (int i = 0; i < dim; i++) {
                _list.set(i, v[i]);
        }	
    }

    /**
     * Sets the size of the vector
     * @param p_dim The size to be set
     */
    public void setSize(int p_dim) {
        if (p_dim == dim) 
            return; 
        dim = p_dim;
        _list = new ArrayList<>(p_dim);
    }

    /**
     * Sets a specific element in the vector
     * @param i The index of the element to be set
     * @param elem The value for the element to be set
     */
    public void set(int i, T elem) {
        _list.set(i, elem);
    }

    /**
     * Adds a value to the end of the vector.
     * @param elem The element to be added to the vector
     */
    public void add(T elem) {
        _list.add(elem);
    }
}
