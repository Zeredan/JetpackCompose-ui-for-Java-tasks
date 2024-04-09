import kotlin.Pair;

public class Synchronizer {
    Businessable<String> object;
    private volatile boolean isReading = false;
    public synchronized void write(int index, Pair<String, Number> value) throws InterruptedException {
        while(isReading) this.wait();
        Thread.sleep(2000);
        object.set(index, value);
        isReading = true;
        this.notifyAll();
    }

    public synchronized Pair<String, Number> read(int index) throws InterruptedException {
        while(!isReading) this.wait();
        Thread.sleep(1000);
        isReading = false;
        this.notifyAll();
        return object.get(index);
    }

    public Synchronizer(Businessable<String> object)
    {
        this.object = object;
    }
}
