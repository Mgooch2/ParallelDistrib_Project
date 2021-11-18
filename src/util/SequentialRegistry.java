package util;
import java.util.*;

/**
 * An ADT which associates an object with a unique and sequential id
 * Each registered object receives a unique id number which is ascending from 0.
 */
public class SequentialRegistry<T> {
    private List<T> list = new Vector<T>();
    private int startIdx = 0;
    
    public SequentialRegistry() {}
    public SequentialRegistry(int startIdx) {
        this.startIdx = startIdx;
    }

    /**
     * Register an object with this registry
     * If the node is already registered, returns its ID.
     * @param t The InetAddress of the node to register.
     * @return The ID number assigned to the node. -1 if adding failed.
     */
    public int register(T t) {
        // Check that the address is not already registered.
        if (!contains(t)) {
            list.add(t);
        }
        return indexOf(t);
    }
    /** @return the index of the given object, or -1 if not registered */
    public int indexOf(T t) {
        int i = list.indexOf(t);
        if (i == -1) return i;
        return i + startIdx;
    }
    /** @return The object with the given ID, or null if not registered */
    public T get(int id) {
        if (!contains(id))
            return null;
        int i = id - startIdx;
        return list.get(i);
    }
    /** @return Whether a node with the given address is registered */
    public boolean contains(T t) {
        return indexOf(t) != -1;
    }
    /** @return Whether a node with the given ID exists in this registry */
    public boolean contains(int id) {
        int i = id - startIdx;
        return (i >= 0) && (i < list.size()) && (list.get(i) != null);
    }
    /**
     * 
     * @param id
     * @return Whether or not the node existed previously.
     */
    public boolean unregister(int id) {
        if (get(id) == null)
            return false;
        int i = id - startIdx;
        return list.set(i, null) != null;
    }
    /**
     * 
     * @param id
     * @return Whether or not the node existed previously.
     */
    public boolean unregister(T t) {
        if (indexOf(t) == -1)
            return false;
        return list.remove(t);
    }

    public static void main(String[] args) {
        
    }
}
