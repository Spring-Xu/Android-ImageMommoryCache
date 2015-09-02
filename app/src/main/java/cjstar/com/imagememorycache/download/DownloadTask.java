package cjstar.com.imagememorycache.download;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import cjstar.com.imagefilecache.cache.LRUFileCache;
import cjstar.com.imagememorycache.image.ImageLoaderUtils;
import cjstar.com.memorycachelibrary.cache.BitmapMemoryCache;

/**
 * @author  Created by CJstar on 15/8/24.
 * usually, subclasses of AsyncTask are declared inside the activity class.
 * that way, you can easily modify the UI thread from here
 */
public class DownloadTask implements Runnable {

    WeakReference<ImageView> weakReference;
    String url;
    public DownloadTask(ImageView imageView,int defaultId,String url) {
        weakReference = new WeakReference(imageView);
        imageView.setTag(url);
        this.url = url;
    }

    @Override
    public void run() {
        Message msg = new Message();
        Bitmap bitmap = null;
        bitmap = BitmapMemoryCache.getInstance().get(url);
        if(bitmap!=null&&!bitmap.isRecycled()){
            msg.obj = bitmap;
            handler.sendMessage(msg);
            return ;
        }

        if(LRUFileCache.getInstance().getDiskFile(url)!=null){
            bitmap = ImageLoaderUtils.loadHugeBitmapFromSDCard(LRUFileCache.getInstance().getFilePathByKey(url), 0, 0);
            BitmapMemoryCache.getInstance().put(url,bitmap);
            msg.obj = bitmap;
            handler.sendMessage(msg);
            return ;
        }

        InputStream input = null;
        HttpURLConnection connection = null;
        try {
            URL mUrl = new URL(url);
            connection = (HttpURLConnection) mUrl.openConnection();
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                msg.obj = bitmap;
                handler.sendMessage(msg);
                return ;
            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();

            // download the file
            input = connection.getInputStream();
            LRUFileCache.getInstance().addDiskFile(url.toString(), input);
            bitmap = ImageLoaderUtils.loadHugeBitmapFromSDCard(LRUFileCache.getInstance().getFilePathByKey(url.toString()),0,0);
        }catch (Throwable t){
            t.printStackTrace();
        }

        if(bitmap!=null&&!bitmap.isRecycled()){
            BitmapMemoryCache.getInstance().put(url,bitmap);
        }

        msg.obj = bitmap;
        handler.sendMessage(msg);
    }

    Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d("","handleMessage");
            Bitmap bitmap= (Bitmap)msg.obj;
            ImageView imageView = weakReference.get();

            if(imageView!=null&&url.equals(imageView.getTag().toString())&&bitmap!=null&&!bitmap.isRecycled()){
                Log.d("","handleMessage set image bitmap");
                imageView.setImageBitmap(bitmap);

            }else {
                Log.d("","handleMessage:"+url+" view:"+imageView+" bitmap:"+bitmap);
            }
        }
    };
}