package cjstar.com.threadpoollibrary;

/**
 * Created by CJstar on 15/8/31.
 */
public class DefaultConfig {
    /**
     * max execute threads count at the same time
     */
    public static final int DEFAULT_MAX_EXECUTINGSIZE =  Runtime.getRuntime().availableProcessors();
    /**
     * the thread max interval time at each of them (mills)
     */
    public static final int DEFAULT_MAX_INTERVAL = 1000;
    /**
     * max pool size
     */
    public static final int DEFAULT_MAX_POOL_SIZE = 128;
}
