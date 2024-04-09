public class SingletonSync {
    public Integer a;
    public String b;
    private static Boolean isUsing = false;
    private static Integer lock = 2;
    public Integer currentAmount = 0;
    private static SingletonSync instance = null;

    public static synchronized SingletonSync getInstance() throws InterruptedException {
        if (instance == null){
            System.out.println("In null check true");
            instance = new SingletonSync(5, "alpaca");
        }
        return instance;
    }

    public static SingletonSync getInstance1() throws InterruptedException {
        synchronized (lock) {
            if (instance == null) {
                System.out.println("In null check true");
                Thread.sleep(5000);
                instance = new SingletonSync(5, "alpaca");
            }
        }
        System.out.println("returning");
        return instance;
    }

    /**
     * Synchronised allows to be only 1 static method from static methods of class exists
     * after method finishes it notifies all "simple" waiting methods and one of them becomes leader, other wait again
     * when method calls wait() it notifies again "simple" waiting methods only and being pushed to "induced" waiting list
     * element in "induced" list can be notified by notifyAll() and notify only!
     * (that means when we use another wait() or finish method the "induced" waiter will not be called)
     */
    public synchronized void timeCons() throws InterruptedException {
        System.out.println("time cons start");
        Thread.sleep(4000);
        System.out.println("time cons end");
    }
    public synchronized void add() throws InterruptedException {
        while(currentAmount > 4){
            System.out.println("cannot add, now: " + currentAmount);
            wait();
        }
        Thread.sleep(2000);
        currentAmount++;
        System.out.println("added, now: " + currentAmount);
        notifyAll();
    }

    public synchronized void remove() throws InterruptedException {
        while(currentAmount < 3){
            System.out.println("cannot remove, now: " + currentAmount);
            wait();
        }
        Thread.sleep(2000);
        currentAmount--;
        System.out.println("removed, now: " + currentAmount);
        notifyAll();
    }

    private SingletonSync(Integer a, String b)
    {
        this.a = a;
        this.b = b;
    }
}
