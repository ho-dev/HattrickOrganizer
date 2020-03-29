package module.series.promotion;

import core.util.HOLogger;

import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Processes a queue of elements in a queue by calling a closure being executed
 * on this element.
 *
 * <p>Tasks are processed by multiple threads belonging to a pool whose size is
 * defined in <code>NUM_THREADS</code>.</p>
 *
 * <p>A typical usage of this class is to execute multiple web requests in parallel
 * for a given lists of resources to be retrieved (e.g. match details for a list match ids)</p>
 *
 * @param <T> Type of the elements to be processed in the queue.
 */
public class ProcessAsynchronousTask<T> {

    /**
     * Task to be executed asynchronously on the queue elements.
     * @param <T>
     */
    @FunctionalInterface
    interface ProcessTask<T> {
        void execute(T val);
    }

    // Keep number of threads conservative to not hammer HT server.
    private final static int NUM_THREADS = 3;
    private final ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);

    final Queue<T> queue = new LinkedBlockingQueue<>();

    private int errorCount = 0;

    /**
     * Adds an element to the queue for processing.
     * @param entry - Element to process.
     */
    public void addToQueue(T entry) {
        queue.add(entry);
    }

    /**
     * Executes <code>task</code> on each element of the queue in multiple parallel threads
     * until the queue is empty.
     *
     * @param task - Lambda defining the task to be performed on each queue element
     */
    public void execute(ProcessTask<T> task) {
        for (int i = 0; i < NUM_THREADS; i++) {
            executorService.submit(() -> {
                HOLogger.instance().debug(ProcessAsynchronousTask.class, "Execution service...");
                while (!queue.isEmpty()) {
                    T val = queue.poll();
                    task.execute(val);
                }

                executorService.shutdown();
            });
        }

        try {
            executorService.awaitTermination(30, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            HOLogger.instance().error(ProcessAsynchronousTask.class, "Error whilst waiting for tasks to complete:" + e.getMessage());
        }
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void incErrorCount() {
        errorCount++;
    }

    public void resetErrorCount() {
        errorCount = 0;
    }
}
