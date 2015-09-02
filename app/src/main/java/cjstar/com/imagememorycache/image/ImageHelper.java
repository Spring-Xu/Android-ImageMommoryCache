package cjstar.com.imagememorycache.image;

import android.widget.ImageView;

import cjstar.com.imagememorycache.download.DownloadTask;
import cjstar.com.threadpoollibrary.CustomThreadPoolExecutor;

/**
 * Created by CJstar on 15/8/24.
 */
public class ImageHelper {
    private static ImageHelper mImageHelper;

    public static ImageHelper getInstance() {
        if (mImageHelper == null) {
            synchronized (ImageHelper.class) {
                if (mImageHelper == null) {
                    mImageHelper = new ImageHelper();
                }
            }
        }

        return mImageHelper;
    }

    CustomThreadPoolExecutor executor;
    public void showImage(ImageView imgView, String url, int defaultRes) {
        DownloadTask task = new DownloadTask(imgView, defaultRes, url);

        if(executor==null){
            executor = new CustomThreadPoolExecutor();
        }

        executor.execute(task);
    }
}
