package processador;

import java.util.concurrent.ThreadFactory;

public class FabricaThreads implements ThreadFactory {

    @Override
    public Thread newThread(Runnable r) {

        Thread thread = new Thread(r);
        thread.setUncaughtExceptionHandler(new Handler());
        thread.setDaemon(true);
        return thread;

    }
}
