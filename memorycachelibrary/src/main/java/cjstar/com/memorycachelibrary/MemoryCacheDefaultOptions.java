package cjstar.com.memorycachelibrary;

/**
 * Created by CJstar on 15/9/2.
 */
public class MemoryCacheDefaultOptions {
    /**
     * the max cache memory size(Byte)
     */
    public static int mMaxCacheSize = (int) Runtime.getRuntime().maxMemory() / 1024 / 6;

    /**
     * The max cache weak objects count
     */
    public static int mMaxCacheCount = 10;

    /**
     * Whether you can you memory cache, you can control by this switch, default it is true
     */
    public static boolean mUseCache = true;
}
