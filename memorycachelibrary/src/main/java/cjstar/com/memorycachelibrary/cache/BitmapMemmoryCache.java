package cjstar.com.memorycachelibrary.cache;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.text.TextUtils;

import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;

import cjstar.com.memorycachelibrary.MemoryCacheDefaultOptions;
import cjstar.com.memorycachelibrary.MemoryCacheOptions;

/**
 * Created by CJstar on 15/9/2.
 */
public class BitmapMemmoryCache extends MemmoryCache<Bitmap> {

    private MemoryCacheOptions options;

    private LruCache<String, Bitmap> mMemoryCache;

    private LinkedHashMap<String, SoftReference<Bitmap>> mSoftCache;


    /**
     * Create a instance by default config
     */
    public BitmapMemmoryCache() {
        options = new MemoryCacheOptions.Builder()
                .setmMaxCacheCount(MemoryCacheDefaultOptions.mMaxCacheCount)
                .setmMaxCacheSize(MemoryCacheDefaultOptions.mMaxCacheSize)
                .setmUseCache(MemoryCacheDefaultOptions.mUseCache)
                .build();
        initializeCache();
    }

    /**
     * Create a instance by cacheOptions
     *
     * @param cacheOptions not null, if it is null {@link NullPointerException} will be throw
     */
    public BitmapMemmoryCache(MemoryCacheOptions cacheOptions) {
        this.options = cacheOptions;
        initializeCache();
    }


    private void initializeCache() {

        if (options == null) {
            throw new NullPointerException("BitmapMemmoryCache#options is null");
        }

        if (options.getmMaxCacheSize() == 0) {
            throw new IllegalArgumentException("BitmapMemmoryCache#max cache size is o");
        }

        if (options.getmMaxCacheCount() == 0) {
            throw new IllegalArgumentException("BitmapMemmoryCache#max cache count is o");
        }

        mMemoryCache = new LruCache<String, Bitmap>(options.getmMaxCacheSize()) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
            }

            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                // TODO Auto-generated method stub
                super.entryRemoved(evicted, key, oldValue, newValue);
                if (oldValue != null) {
                    // the allocation is full, move bitmap to soft reference
                    mSoftCache.put(key, new SoftReference<Bitmap>(oldValue));
                }

            }
        };

        mSoftCache = new LinkedHashMap<String, SoftReference<Bitmap>>(
                options.getmMaxCacheCount(), 0.75f, true) {
            private static final long serialVersionUID = 6040103833179403725L;

            @Override
            protected boolean removeEldestEntry(
                    Entry<String, SoftReference<Bitmap>> eldest) {
                if (size() > options.getmMaxCacheCount()) {
                    return true;
                }
                return false;
            }
        };
    }

    @Override
    public Bitmap get(String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }

        Bitmap bitmap;

        // get bitmap from mMemoryCache
        synchronized (mMemoryCache) {
            bitmap = mMemoryCache.get(key);

            if (bitmap != null && !bitmap.isRecycled()) {
                // LRU : refresh this bitmap position
                mMemoryCache.remove(key);
                mMemoryCache.put(key, bitmap);
                return bitmap;
            }

        }

        // get bitmap from mSoftCache
        synchronized (mSoftCache) {
            SoftReference<Bitmap> bitmapReference = mSoftCache.get(key);
            if (bitmapReference != null) {
                bitmap = bitmapReference.get();

                if (bitmap != null && !bitmap.isRecycled()) {
                    // move bitmap to mSoftCache
                    mMemoryCache.put(key, bitmap);
                    mSoftCache.remove(key);
                    return bitmap;

                } else {
                    // is recycled
                    mSoftCache.remove(key);
                }
            }
        }

        return null;
    }

    @Override
    public boolean isExist(String key) {
        if (TextUtils.isEmpty(key)) {
            return false;
        }

        Bitmap bitmap;

        // get bitmap from mMemoryCache
        synchronized (mMemoryCache) {
            bitmap = mMemoryCache.get(key);
            if (bitmap != null && !bitmap.isRecycled()) {
                return true;
            }
        }

        // get bitmap from mSoftCache
        synchronized (mSoftCache) {
            SoftReference<Bitmap> bitmapReference = mSoftCache.get(key);
            if (bitmapReference != null) {
                bitmap = bitmapReference.get();
                if (bitmap != null && !bitmap.isRecycled()) {
                    return true;
                }
            }
        }


        return false;
    }

    @Override
    public Bitmap remove(String key) {
        // remove bitmap from mMemoryCache
        Bitmap bitmap;
        synchronized (mMemoryCache) {
            bitmap = mMemoryCache.remove(key);
        }

        // remove bitmap from mSoftCache
        synchronized (mSoftCache) {
            if (bitmap != null && !bitmap.isRecycled()) {
                mSoftCache.remove(key);

            } else {
                SoftReference<Bitmap> bitmapReference = mSoftCache.remove(key);
                if (bitmapReference != null) {
                    bitmap = bitmapReference.get();
                }
            }
        }

        return bitmap;
    }

    @Override
    public void put(Bitmap bitmap, String key) {
        if (bitmap == null || bitmap.isRecycled()) {
            return;
        }

        // thread synchronize
        synchronized (mMemoryCache) {
            mMemoryCache.put(key, bitmap);
        }
    }
}
