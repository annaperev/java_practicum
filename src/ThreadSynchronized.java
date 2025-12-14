import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.LongStream;

class Accounts {

    private final long[] accounts;

    public Accounts(int size) {
        this.accounts = LongStream.range(0, size).map(it -> 1_000).toArray();
    }

    // без synchronized код не потокобезопасный, так как разные потоки могут одновременно обращаться в одним счетам
    // и перезатереть результат перевода
    public synchronized void transfer(int from, int to, long amount) {
        accounts[from] -= amount;
        accounts[to] += amount;
    }

    public void printState() {
        System.out.println(Arrays.toString(accounts));
    }

}

class TransferThread extends Thread {

    private final Accounts accounts;
    private final int from;
    private final int to;

    public TransferThread(Accounts accounts, int from, int to) {
        this.accounts = accounts;
        this.from = from;
        this.to = to;
    }

    @Override
    public void run() {
        for (int i = 0; i < 100_000; i++) {
            accounts.transfer(from, to, 1);
        }
    }
}

public class ThreadSynchronized {

    public static void main(String[] args) throws InterruptedException {
        final int size = 10;
        Accounts accounts = new Accounts(size);

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Thread t = new TransferThread(accounts, i, i + 1 == size ? 0 : i + 1);
            t.start();
            threads.add(t);
        }

        for (Thread t : threads) {
            t.join();
        }

        accounts.printState();
    }

}