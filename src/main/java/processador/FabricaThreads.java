package processador;

import java.util.concurrent.ThreadFactory;

public class FabricaThreads implements ThreadFactory {

    @Override
    public Thread newThread(Runnable r) {

        Thread thread = newThread(r);
        thread.setUncaughtExceptionHandler(new Handler());
        return thread;

    }
}
