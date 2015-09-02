package cjstar.com.threadpoollibrary;

/**
 * Custom executor for Threads, it content executors for {@link Runnable} and {@link CustomAsyncTask}
 */
public abstract class CustomExecutor {
    /**
     * Execute the runnable
     * @param runnable this task content
     */
    public abstract void execute(Runnable runnable);

    /**
     * shout the thread pool
     */
    public abstract void shoutDown();
}
