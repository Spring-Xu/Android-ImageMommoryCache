package cjstar.com.memorycachelibrary;

import cjstar.com.memorycachelibrary.cache.BitmapMemoryCache;

/**
 * The {@link BitmapMemoryCache} will be config by this options.
 * Created by xuchun on 15/9/2.
 */
public class MemoryCacheOptions {

    /**
     * the max cache memory size(Byte)
     */
    private int mMaxCacheSize;

    /**
     * The max cache weak objects count
     */
    private int mMaxCacheCount;

    /**
     * Whether you can you memory cache, you can control by this switch, default it is true
     */
    private boolean mUseCache = true;

    private MemoryCacheOptions(Builder builder) {
        this.mMaxCacheSize = builder.mMaxCacheSize;
        this.mMaxCacheCount = builder.mMaxCacheCount;
        this.mUseCache = builder.mUseCache;
    }

    /**
     * Create a instance which is config by default config
     */
    public MemoryCacheOptions() {
        //TODO default config
    }

    public void setmMaxCacheCount(int mMaxCacheCount) {
        this.mMaxCacheCount = mMaxCacheCount;
    }

    public void setmMaxCacheSize(int mMaxCacheSize) {
        this.mMaxCacheSize = mMaxCacheSize;
    }

    public void setmUseCache(boolean mUseCache) {
        this.mUseCache = mUseCache;
    }

    public int getmMaxCacheCount() {
        return mMaxCacheCount;
    }

    public boolean ismUseCache() {
        return mUseCache;
    }

    public int getmMaxCacheSize() {
        return mMaxCacheSize;
    }

    public static class Builder {
        /**
         * the max cache memory size(Byte)
         */
        private int mMaxCacheSize;

        /**
         * The max weak cache objects count
         */
        private int mMaxCacheCount;

        /**
         * Whether you can you memory cache, you can control by this switch, default it is true
         */
        private boolean mUseCache = true;

        /**
         * Set the max cache weak objects' count
         *
         * @param mMaxCacheCount
         * @return
         */
        public Builder setmMaxCacheCount(int mMaxCacheCount) {
            this.mMaxCacheCount = mMaxCacheCount;
            return this;
        }

        /**
         * Set the max cache size in memory(Byte)
         *
         * @param mMaxCacheSize
         * @return
         */
        public Builder setmMaxCacheSize(int mMaxCacheSize) {
            this.mMaxCacheSize = mMaxCacheSize;
            return this;
        }

        /**
         * The cache Switch
         *
         * @param mUseCache
         * @return
         */
        public Builder setmUseCache(boolean mUseCache) {
            this.mUseCache = mUseCache;
            return this;
        }

        public Builder() {
        }

        /**
         * Get MemoryCacheOptions by this Builder's config
         *
         * @return
         */
        public MemoryCacheOptions build() {
            return new MemoryCacheOptions(this);
        }
    }
}
