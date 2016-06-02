package main;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.Timer;

public class InetAddressCache implements com.squarespace.AddressCache{

    // storing data in the LinkedBlockingDeque allows us to leverage a lot of necessary functionality.
    private LinkedBlockingDeque<AddressNode> cache;
    private int cachingTime;
    private TimerTask cleanUp;
    private Timer clean;

    public InetAddressCache(int expiryTimeInMilliseconds){
        cachingTime = expiryTimeInMilliseconds;
        cache = new LinkedBlockingDeque<AddressNode>();
        cleanUp = new CleanupTask();
        clean = new Timer();
        clean.scheduleAtFixedRate(cleanUp, 0, 5000);
    }

    //worst case O(n)
    public boolean offer(InetAddress address){
        Iterator<AddressNode> addressNodeIterator = cache.iterator();
        while(addressNodeIterator.hasNext()) {
            AddressNode current = addressNodeIterator.next();
            if (current.equals(new AddressNode(address))) {
                AddressNode newFirst = current;
                cache.remove(current);
                newFirst.resetTime();
                cache.offer(newFirst);
                return true;
            }
        }
        cache.offer(new AddressNode(address));
        return true;
    }

    //worst case O(n)
    public boolean contains(InetAddress address){
        return cache.contains(new AddressNode(address));
    }
    //worst case O(n)
    public boolean remove(InetAddress address){
        return cache.remove(new AddressNode(address));
    }
    //worst case O(1)
    public InetAddress peek(){
        InetAddress returnAddress;
        try{
             returnAddress = cache.peekLast().data;
        }
        catch(NullPointerException e){
            return null;
        }
        return returnAddress;
    }
    //worst case O(1)
    public InetAddress remove(){
        InetAddress returnAddress;
        try{
            returnAddress = cache.removeLast().data;
        }
        catch(NoSuchElementException e){
            return null;
        }
        return returnAddress;
    }

    // The deque.takeLast() method throws InterruptedException
    //worst case O(1)
    public InetAddress take() throws InterruptedException{
        return cache.takeLast().data;
    }

    //worst case O(1) -> cancels clean timer, sets cache to null, releasing deque for garbage collection
    public void close(){
        try {
            clean.cancel();
            cache = null;
        }
        catch(NullPointerException e){

        }
    }

    //worst case O(1) => LinkedBlockingDeque maintains a count of nodes
    public int size(){
        return cache.size();
    }

    //worst case O(1)
    public boolean isEmpty(){
        return cache.isEmpty();
    }

    //Testing purposes, prints InetAddress data in the cache
    public void printSelf(){
        Iterator<AddressNode> addressNodeIterator = cache.iterator();
        while(addressNodeIterator.hasNext()) {
            AddressNode current = addressNodeIterator.next();
            System.out.println(current.data.toString());
        }
    }

    //Stores InetAddress with an expiry time, for the cleanup task to look at
    private class AddressNode {
        public long expiryTime;
        public InetAddress data;
        AddressNode(InetAddress address){
            data = address;
            expiryTime = System.currentTimeMillis();
        }
        //used in offer() when moving an element to the back of a queue
        public void resetTime(){
            expiryTime = System.currentTimeMillis();
        }
        //overrides equals method so we can use the LinkedBlockingDeque contains method. -> we only care about comparing the InetAddress, not the expiry time
        @Override
        public boolean equals(Object o) {
            final AddressNode addressNode = (AddressNode) o;
            return (this.data == addressNode.data);
        }
    }

    // cleanup task runs ever 5 seconds.
    private class CleanupTask extends TimerTask{
        public void run(){
            cleanUp();
        }
        // Asymptotic complexity O(% of n that are expired)  Since we are dealing with the ends of the deque, best case is O(1) and worst case is O(n)
        // We can recursively move through expired nodes.  We can stop once one is not expired, since the eviction policy is FIFO
        private void cleanUp(){
            try{
                if(cache.peek() != null && (cache.peek().expiryTime <= System.currentTimeMillis() - cachingTime)){
                    cache.remove();
                    cleanUp();
                }
                else{
                    return;
                }
            }
            catch(NullPointerException e){
                System.out.println(e.getStackTrace().toString());
            }
        }
    }
}
