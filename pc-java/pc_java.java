import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.ArrayList;

/**
 * Producer Class.
 */
class Producer implements Runnable {

    private List<Integer> sharedQueue;
    private int maxSize=100; //maximum number of products which sharedQueue can hold at a time.
    int productionSize=10000; //Total no of items to be produced by each producer
    int producerNo;
    AtomicInteger atomicInteger;

    public Producer(List<Integer> sharedQueue, int producerNo, AtomicInteger atomicInteger) {
        this.sharedQueue = sharedQueue;
        this.producerNo = producerNo;
        this.atomicInteger = atomicInteger;
    }

    @Override
    public void run() {
        for (int i = 1; i <= productionSize; i++) { //produce products.
            try {
                produce(i);
            } catch (InterruptedException e) {  e.printStackTrace(); }
        }
        atomicInteger.getAndDecrement();
    }

    private void produce(int i) throws InterruptedException {

        synchronized (sharedQueue) {
            //if sharedQuey is full wait until consumer consumes.
            while (sharedQueue.size() == maxSize) {
                //System.out.println(Thread.currentThread().getName()+", Queue is full, producerThread is waiting for "
                        //+ "consumerThread to consume, sharedQueue's size= "+maxSize);
                sharedQueue.wait();
            }

            //Bcz each producer must produce unique product
            //Ex= producer0 will produce 1-5  and producer1 will produce 6-10 in random order
            int producedItem = (productionSize*producerNo)+ i;

            //System.out.println(Thread.currentThread().getName() +" Produced : " + producedItem);
            sharedQueue.add(producedItem);
            //Thread.sleep((long)(Math.random() * 1000));
            sharedQueue.notify();
        }
    }
}

/**
 * Consumer Class.
 */
class Consumer implements Runnable {
    private List<Integer> sharedQueue;
    AtomicInteger atomicInteger;
    int consumptionSize=10000;
    public Consumer(List<Integer> sharedQueue, AtomicInteger atomicInteger) {
        this.sharedQueue = sharedQueue;
        this.atomicInteger = atomicInteger;
    }

    @Override
    public void run() {
        for (int i = 1; i <= consumptionSize; i++) { //consume products.
            try {
                consume();
            } catch (InterruptedException e) {  e.printStackTrace(); }
        }
        atomicInteger.getAndDecrement();
    }

    private void consume() throws InterruptedException {

        synchronized (sharedQueue) {
            //if sharedQuey is empty wait until producer produces.
            while (sharedQueue.size() == 0) {
                //System.out.println(Thread.currentThread().getName()+", Queue is empty, consumerThread is waiting for "
                //        + "producerThread to produce, sharedQueue's size= 0");
                sharedQueue.wait();
            }

            //Thread.sleep((long)(Math.random() * 2000));
            sharedQueue.remove(0);
            sharedQueue.notify();
        }
    }

}


public class pc_java {

    public static void waitForAllThreadsToComplete(List<Thread> threads) {
        for(Thread thread: threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String args[]) throws InterruptedException {
        List<Integer> sharedQueue = new LinkedList<Integer>(); //Creating shared object
        List<Thread> threads = new ArrayList<Thread>();
        AtomicInteger atomicInteger = new AtomicInteger();
        AtomicInteger consumeInteger = new AtomicInteger();

        int producerCount = 1000;
        for(int i = 0; i < producerCount; i++) {
            atomicInteger.getAndIncrement();
            Producer producer=new Producer(sharedQueue, 0, atomicInteger);
            Thread producerThread = new Thread(producer, "ProducerThread"+i);
            threads.add(producerThread);
            producerThread.start();
            consumeInteger.getAndIncrement();
            Consumer consumer=new Consumer(sharedQueue, consumeInteger);
            Thread consumerThread = new Thread(consumer, "ConsumerThread"+i);
            threads.add(producerThread);
            consumerThread.start();
        }

        while(true){

            if (atomicInteger.get() == 0 && consumeInteger.get() == 0) {
                for(Thread thread: threads) {
                        thread.stop();
                }
                break;
            }
            Thread.sleep((long)(100));
        }
    }
}
