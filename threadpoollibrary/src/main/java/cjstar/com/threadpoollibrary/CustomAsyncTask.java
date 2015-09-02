package cjstar.com.threadpoollibrary;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Custom AsyncTask , the same as AsyncTask, but it can be created in other</br>
 * Thread both UI Thread or not. And more , it use {@link CustomExecutor} to manage the thread executor.<br/>
 * The reason for doing this is to use this CustomAsyncTask to instead</br>
 * AsyncTask, because AsyncTask can not be used in other thread except UI thread,<br/>
 * as the {@link AsyncTask#onPreExecute()} will be execute in AsyncTask's create thread,<br/>
 * and if you create{@link AsyncTask} in a thread which is not main thread and execute it,<br/>
 * the application will throw {@link RuntimeException#initCause(Throwable)} what you update <br/>
 * UI not in main thread exception. Learn more: you can see {@link AsyncTask}.
 *
 * @param <Params>
 * @param <Progress>
 * @param <Result>
 * @author CJstar 2015-9-1
 */
public abstract class CustomAsyncTask<Params, Progress, Result>{

    private static String TAG = "CustomAsyncTask";

    /**
     * the thread run status
     */
    private Status mStatus = Status.PENDING;

    /**
     * if true , the thread had been canceled,else it is run normal
     */
    private AtomicBoolean mCancelled = new AtomicBoolean(false);

    /**
     * do the background task
     */
    private TaskThread mFuture;
    private Params[] mParams;
    /**
     * the task pool manager,the same as {@link AsyncTask#THREAD_POOL_EXECUTOR}
     */
    private static final CustomExecutor DEFAULT_THREAD_POOL_EXECUTOR = new CustomThreadPoolExecutor();

    /**
     * it will be called in a thread which is not UI thread
     *
     * @param params
     * @return the result will return to {@link #onPostExecute(Object)}
     */
    protected abstract Result doInBackground(Params... params);

    /**
     * Runs on the UI thread before {@link #doInBackground}.
     * But this method will not be executed when we call the {@link #execute()} or {@link #execute(Object...)}
     * or {@link #executeOnExecutor(CustomExecutor)} or {@link #executeOnExecutor(CustomExecutor, Object...).
     * It will be call when UI thread handled out. So, this method will not been called as soon as the execute called
     *
     * @see #onPostExecute
     * @see #doInBackground
     */
    protected void onPreExecute() {
    }

    /**
     * execute the task , the doInBackground will be call in a thread which is
     * not UI thread; and all the task will be managed in a thread pool, this
     * pool has a max size of 128 {@link #DEFAULT_THREAD_POOL_EXECUTOR} normally. you
     * can provide the executor at the same time.
     *
     * @param executor
     */
    public void executeOnExecutor(final CustomExecutor executor) {
        // Get a handler that can be used to post to the main thread
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                onPreExecute();
                mFuture = new TaskThread();
                try {
                    executor.execute(mFuture);
                    mStatus = Status.RUNNING;
                } catch (Throwable t) {
                    mStatus = Status.FINISHED;
                    t.printStackTrace();
                    Log.e(TAG, t.getMessage());
                }
            }
        };

        mainHandler.post(myRunnable);
    }

    /**
     * execute the task , the doInBackground will be call in a thread which is
     * not UI thread; and all the task will be managed in a thread pool, this
     * pool has a max size of 128 {@link #DEFAULT_THREAD_POOL_EXECUTOR} normally. you
     * can provide the executor at the same time.
     *
     * @param
     * @param executor
     */
    public void executeOnExecutor(final CustomExecutor executor, Params... params) {
        // Get a handler that can be used to post to the main thread
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                mStatus = Status.RUNNING;
                onPreExecute();
                mFuture = new TaskThread();
                try {
                    executor.execute(mFuture);
                } catch (Throwable t) {
                    mStatus = Status.FINISHED;
                    t.printStackTrace();
                    Log.e(TAG, t.getMessage());
                }
            }
        };

        this.mParams = params;

        mainHandler.post(myRunnable);
    }

    /**
     * execute the task with the follow params
     *
     * @param params
     */
    public void execute(Params... params) {
        // Get a handler that can be used to post to the main thread
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                mStatus = Status.RUNNING;
                onPreExecute();
                mFuture = new TaskThread();
                DEFAULT_THREAD_POOL_EXECUTOR.execute(mFuture);
            }
        };

        this.mParams = params;

        mainHandler.post(myRunnable);

    }

    /**
     * execute the task without params
     */
    public void execute() {
        // Get a handler that can be used to post to the main thread
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                mStatus = Status.RUNNING;
                onPreExecute();
                mFuture = new TaskThread();
                DEFAULT_THREAD_POOL_EXECUTOR.execute(mFuture);
            }
        };

        mainHandler.post(myRunnable);

    }

    /**
     * after the task executed, call the {@link #onPostExecute(Object)}.
     *
     * @param result the result got in background
     */
    private void doPostExecute(final Result result) {
        // Get a handler that can be used to post to the main thread
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                onPostExecute(result);
            }
        };
        mainHandler.post(myRunnable);
    }

    /**
     * it will be called after the doInBackground executed, and this method will
     * be called in UI thread.
     *
     * @param result
     */
    protected void onPostExecute(Result result) {
    }

    protected void onProgressUpdate(Progress... values) {
    }

    /**
     * if the task have been canceled ,will call this method.see
     * {@link #cancel(boolean)}
     *
     * @param result
     */
    protected void onCancelled(Result result) {
        onCancelled();
    }

    protected void onCancelled() {
    }

    /**
     * whether the task is canceled,see {@link #getStatus()}
     *
     * @return
     */
    public final boolean isCancelled() {
        return mCancelled.get();
    }

    /**
     * whether the task is running,see {@link #getStatus()}
     *
     * @return
     */
    public final boolean isRunning() {
        return mStatus == Status.RUNNING;
    }

    /**
     * get the finish status,see {@link #getStatus()}
     *
     * @return
     */
    public final boolean isFinished() {
        return mStatus == Status.FINISHED;
    }

    /**
     * get the status whether the task have been executed
     *
     * @return if the task have been execute return true, else return false. see
     * {@link #getStatus()}
     */
    public final boolean isExecuted() {
        return mStatus != Status.PENDING;
    }

    /**
     * get the task status at now
     *
     * @return an enum value will be got
     */
    public final Status getStatus() {
        return mStatus;
    }

    /**
     * if the Task is run , call this method to cancel the doInBackground
     * method, finish the task without any result for this task
     *
     * @param mayInterruptIfRunning if true the task will be finished, else do nothing
     * @return if the task had been executed, and not been interrupted ,it will
     * be canceled and call onCancelled method ,finally return true,
     * else do nothing and return false
     */
    @SuppressWarnings("static-access")
    public final boolean cancel(boolean mayInterruptIfRunning) {
        if (mayInterruptIfRunning) {
            mCancelled.set(true);
            onCancelled();

            if (mFuture != null ) {
                return true;
            } else {
                return false;
            }

        } else {
            return false;
        }
    }

    /**
     * the background thread handler</br>
     * use this handler to handler the result for the doBackground method
     *
     * @author xuchun
     */

    private  Handler mainHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mStatus == Status.FINISHED) {
                @SuppressWarnings("unchecked")
                Result mResult = (Result) msg.obj;
                doPostExecute(mResult);
            }
        }
    };

    /**
     * TaskThread extends Thread and we can use the thread to execute the
     * doBackground method
     *
     * @author xuchun
     */
    private class TaskThread implements Runnable {
        @Override
        public void run() {
            Result mResult = doInBackground(mParams);
            mStatus = Status.FINISHED;
            Message msg = new Message();
            msg.obj = mResult;
            mainHandler.handleMessage(msg);
        }
    }

    /**
     * Indicates the current status of the task. Each status will be set only
     * once
     * during the lifetime of a task.
     */
    public enum Status {
        /**
         * Indicates that the task has not been executed yet.
         */
        PENDING,
        /**
         * Indicates that the task is running.
         */
        RUNNING,
        /**
         * Indicates that {@link CustomAsyncTask#onPostExecute} has finished.
         */
        FINISHED,
    }

}