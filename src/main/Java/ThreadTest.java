import androidx.compose.runtime.MutableState;
import kotlin.Pair;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Random;


public class ThreadTest {
    private static String qwe = "";
    public static String testPrioritised(BookArray array, MutableState<Boolean> refresh, Integer writerPriority, Integer readerPriority) throws InterruptedException {
        StringWriter sw = new StringWriter();
        PrintWriter result = new PrintWriter(sw);

        Thread t1 = new Thread(() -> {
            int size = 0;
            for(Pair<String, Number> p : array) size++;
            for(int i = 0; i < size; ++i)
            {
                try {
                    Thread.sleep(1000);
                    System.out.println("Write to: " + i);
                    array.bookArray.set(i, new Pair<>("Book " + i, (int)(Math.random() * 1000) % 50));
                    refresh.setValue(!refresh.getValue());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Thread t2 = new Thread(() -> {
            int size = 0;
            for(Pair<String, Number> p : array) size++;
            for(int i = 0; i < size; ++i)
            {
                try {
                    Thread.sleep(1000);
                    System.out.println("Read from: " + i);
                    result.println(array.bookArray.get(i));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        System.out.println();
        t1.setPriority(writerPriority);
        t2.setPriority(readerPriority);
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        return sw.toString();
    }

    public static String synchronisedBlockTest(BookArray array, MutableState<Boolean> refresh, Integer writerPriority, Integer readerPriority) throws InterruptedException {
        StringWriter sw = new StringWriter();
        PrintWriter result = new PrintWriter(sw);

        final Boolean[] isCurReading = {false};

        Thread t1 = new Thread(() -> {
            int size = 0;
            for(Pair<String, Number> p : array) size++;
            for(int i = 0; i < size; ++i)
            {
                try {
                    synchronized (array)
                    {
                        while(isCurReading[0]) array.wait();
                        Thread.sleep(2000);
                        System.out.println("Write to: " + i);
                        array.bookArray.set(i, new Pair<>("Book " + i, (int)(Math.random() * 1000) % 50));
                        refresh.setValue(!refresh.getValue());
                        isCurReading[0] = true;
                        array.notifyAll();
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Thread t2 = new Thread(() -> {
            int size = 0;
            for(Pair<String, Number> p : array) size++;
            for(int i = 0; i < size; ++i)
            {
                try {
                    synchronized (array) {
                        if (!isCurReading[0]) array.wait();
                        Thread.sleep(1000);
                        System.out.println("Read from: " + i);
                        result.println(array.bookArray.get(i));
                        isCurReading[0] = false;
                        array.notifyAll();
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        System.out.println();
        t1.setPriority(writerPriority);
        t2.setPriority(readerPriority);
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        return sw.toString();
    }

    public static String synchronisedMethodTest(BookArray array, MutableState<Boolean> refresh, Integer writerPriority, Integer readerPriority) throws InterruptedException {
        Synchronizer synchronizer = new Synchronizer(array);

        StringWriter sw = new StringWriter();
        PrintWriter result = new PrintWriter(sw);

        final Boolean[] isCurWriting = {true};

        Thread t1 = new Thread(() -> {
            int size = 0;
            for(Pair<String, Number> p : array) size++;
            for(int i = 0; i < size; ++i)
            {
                try {
                    synchronizer.write(i, new Pair<>("Book " + i, (int)(Math.random() * 1000) % 50));
                    System.out.println("Write to: " + i);
                    refresh.setValue(!refresh.getValue());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Thread t2 = new Thread(() -> {
            int size = 0;
            for(Pair<String, Number> p : array) size++;
            for(int i = 0; i < size; ++i)
            {
                try {
                    result.println(synchronizer.read(i));
                    System.out.println("Read from: " + i);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        System.out.println();
        t1.setPriority(writerPriority);
        t2.setPriority(readerPriority);
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        return sw.toString();
    }
}
