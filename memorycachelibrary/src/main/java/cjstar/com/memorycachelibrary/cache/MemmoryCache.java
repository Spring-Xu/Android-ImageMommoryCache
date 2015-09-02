package cjstar.com.memorycachelibrary.cache;

/**
 * This is used for cache
 * @author  Created by CJstar on 15/9/2.
 */
public abstract class MemmoryCache<T> {
    /**
     * Get the object by key from cache. If key is null or empty, throw {@link NullPointerException}.
     * @param key
     * @return
     */
    public abstract T get(String key);

    /**
     * Add a object to cache, and if the t is null or key is null, {@link NullPointerException} will<br/>
     * be throw
     * @param t
     * @param key
     */
    public abstract  void put(T t, String key);

    /**
     * Remove a cached object from cache, and the {@link NullPointerException} will be throw if key is null.
     * @param key
     * @return
     */
    public abstract T remove(String key);

    /**
     * Try to find a cached object in cache, if found return true, else return false,and the<br/>
     * {@link NullPointerException} will be throw if key is null.
     * @param key
     * @return
     */
    public abstract boolean isExist(String key);
}
