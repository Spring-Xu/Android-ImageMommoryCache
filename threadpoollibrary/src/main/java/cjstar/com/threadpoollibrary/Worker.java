package cjstar.com.threadpoollibrary;

/**
 * Created by CJstar on 15/8/31.
 */
public interface Worker {
    public void work();

    public boolean hasNext();

    public void preWork();

    public void afterWork();
}
