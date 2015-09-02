package cjstar.com.threadpoollibrary;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by CJstar on 15/8/31.
 */
public class CustomThreadPoolExecutor extends CustomExecutor {

    private static String TAG = "CustomThreadPoolExecutor";

    private BlockingQueue<Runnable> mQueue;

    private List<Worker> workers;

    private Lock locker = new ReentrantLock();

    /**
     * max pool size
     */
    private int maxPoolSize;
    /**
     * max execute threads count at the same time
     */
    private int maxExecutingSize;
    /**
     * the thread max interval time at each of them (mills)
     */
    private int maxInterval;

    /**
     * pool shout down tag
     */
    private boolean isShoutDown = false;

    private final boolean ISDEBUG = false;

    /**
     * Get the instance which has the default config, you can see {@link DefaultConfig}
     */
    public CustomThreadPoolExecutor() {
        this.maxExecutingSize = DefaultConfig.DEFAULT_MAX_EXECUTINGSIZE;
        this.maxPoolSize = DefaultConfig.DEFAULT_MAX_POOL_SIZE;
        this.maxInterval = DefaultConfig.DEFAULT_MAX_INTERVAL;
        init();
    }

    /**
     * Create a pool with the pool size and interval time
     *
     * @param maxPoolSize      max pool size
     * @param maxExecutingSize max execute threads count at the same time
     * @param maxInterval      the thread max interval time at each of them
     */
    private CustomThreadPoolExecutor(int maxPoolSize, int maxExecutingSize, int maxInterval) {
        this.maxExecutingSize = maxExecutingSize;
        this.maxInterval = maxInterval;
        this.maxPoolSize = maxPoolSize;
        init();
    }

    /**
     * initialize the thread pool and params
     */
    private void init() {
        if (this.maxPoolSize <= 0) {
            throw new IllegalArgumentException("CustomThreadPoolExecutor maxPoolSize is illegal:" + this.maxPoolSize);
        }

        if (this.maxInterval <= 0) {
            throw new IllegalArgumentException("CustomThreadPoolExecutor maxInterval is illegal:" + this.maxInterval);
        }

        if (this.maxExecutingSize <= 0) {
            throw new IllegalArgumentException("CustomThreadPoolExecutor maxExecutingSize is illegal:" + this.maxExecutingSize);
        }

        mQueue = new LinkedBlockingQueue<Runnable>(maxPoolSize);

        workers = Collections.synchronizedList(new ArrayList<Worker>());
    }

    /**
     * Execute the runnable
     *
     * @param runnable this task content
     */
    @Override
    public void execute(Runnable runnable) {
        offerRunnable(runnable);
    }

    private void offerRunnable(Runnable runnable) {
        if (runnable == null) {
            throw new NullPointerException("CustomThreadPoolExecutor # execute runnable is null");
        }

        if (isShoutDown) {
            throw new PoolFullException("CustomThreadPoolExecutor pool is shout downed");
        }

        locker.lock();

        if (isPoolFull()) {
            throw new PoolFullException();
        }

        if (isExcutingPoolFull()) {
            mQueue.offer(runnable);
        } else {
            Worker worker = new TaskWorker(runnable);
            workers.add(worker);
            worker.work();
        }

        locker.unlock();
    }

    /**
     * Get the excuting pool size, if it is larger than {@link #maxExecutingSize} return true, else return false
     *
     * @return
     */
    public boolean isExcutingPoolFull() {
        d("isExcutingPoolFull workers size:" + workers.size() + " maxExecutingSize:" + maxExecutingSize);
        return workers.size() >= maxExecutingSize;
    }


    public boolean isPoolFull() {
        d("isPoolFull mQueue size:" + mQueue.size() + " maxPoolSize:" + maxPoolSize);
        return mQueue.size() >= maxPoolSize;
    }

    @Override
    public void shoutDown() {
        locker.lock();
        isShoutDown = true;
        mQueue.clear();
        workers.clear();
        locker.unlock();
    }

    /**
     * Build ths Execute pool by set the pool size , executing size and max interval
     */
    public static class Builder {
        private int maxPoolSize;
        private int maxExecutingSize;
        private int maxInterval;

        public Builder() {
        }

        /**
         * Set thread pool size
         *
         * @param maxPoolSize
         * @return
         */
        public Builder setMaxPoolSize(int maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
            return this;
        }

        /**
         * set the executing size at the same time
         *
         * @param maxExecutingSize
         * @return
         */
        public Builder setMaxExecutingSize(int maxExecutingSize) {
            this.maxExecutingSize = maxExecutingSize;
            return this;
        }

        /**
         * max interval time
         *
         * @param maxInterval mills
         * @return
         */
        public Builder setMaxInterval(int maxInterval) {
            this.maxInterval = maxInterval;
            return this;
        }

        public CustomThreadPoolExecutor build() {
            return new CustomThreadPoolExecutor(maxPoolSize, maxExecutingSize, maxInterval);
        }
    }

    /**
     * worker for runnable or task
     */
    class TaskWorker implements Worker {

        private Runnable runnable;
        private HandlerThread handlerThread;
        private Handler currentHandler;

        public TaskWorker(Runnable run) {
            runnable = run;
        }

        @Override
        public void afterWork() {
            locker.lock();
            d("TaskWorker afterWork");
            if (hasNext()) {
                try {
                    Runnable run = mQueue.take();
                    workFor(run);
                    d("TaskWorker worker for next:" + mQueue.size());
                } catch (InterruptedException i) {
                    i.printStackTrace();

                } finally {
                    locker.unlock();
                }

            } else {
                // recycle worker
                handlerThread.quit();
                workers.remove(this);
                locker.unlock();
                d("TaskWorker finish");
            }
        }

        @Override
        public void work() {
            if (runnable == null) {
                throw new NullPointerException("runnable is null");
            }

            preWork();
            handlerThread = new HandlerThread("TaskWorker#" + System.currentTimeMillis());
            handlerThread.start();
            currentHandler = new Handler(handlerThread.getLooper());
            workFor(runnable);
        }

        private void workFor(final Runnable run) {
            d("workFor");
            currentHandler.post(new Runnable() {
                @Override
                public void run() {
                    run.run();
                    afterWork();
                }
            });
        }

        @Override
        public void preWork() {
            d("TaskWorker preWork");
        }

        @Override
        public boolean hasNext() {
            return !mQueue.isEmpty();
        }
    }

    private void d(String str) {
        if (ISDEBUG) {
            Log.d(TAG,str);
        }
    }
}
