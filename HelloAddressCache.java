import main.InetAddressCache;
import java.net.InetAddress;
import java.net.UnknownHostException;

// Quick program to demonstrate functionality of cleanup task
public class HelloAddressCache {
    public static void main(String args[]){

        InetAddressCache test = new InetAddressCache(5000); // cache initialized with expiry time = 5000ms. runs first cleanup task
        for (int i = 1; i <= 5; i++) {
            addAddress(test, "27.0.0." + i);
        }

        test.printSelf(); // between first and second run of cleanup task

        waitFor(6000);  // second cleanup task happens in here
        System.out.println("Printing self");  // after 5 second cleanup task, cache has been cleaned
        test.printSelf();// cache is empty, prints nothing

        addAddress(test, "squarespace.com");

        waitFor(3000);  // this makes the total time waited be ~9 seconds.
        System.out.println("Printing self"); //we are approx. 1 second away from third cleaning task
        test.printSelf();//prints an address

        waitFor(4000);  //third cleanup task gets run but address has been in cache for < 5 seconds (expiry time) so cleanup task leaves address
        System.out.println("Printing self 3 seconds since last cleanup task"); // approximately 2 seconds from next cleanup task.  Addresses are expired.
        test.printSelf(); // prints an address

        waitFor(1000);
        System.out.println("Printing self 4 seconds since last cleanup task"); // approx. 1 second away from cleanup task execution
        test.printSelf(); // prints an address

        waitFor(1000);
        System.out.println("Printing self 5 seconds since last cleanup task"); //cleanup task has been run, expired address nodes removed.
        test.printSelf(); // prints nothing,

        test.close();
    }
    //cleanly adds an InetAddress to the cache based on a string (makes program more readable)
    public static void addAddress(InetAddressCache cache, String addressStr){
        try{
            cache.offer(InetAddress.getByName(addressStr));
        }
        catch(UnknownHostException e){
            System.out.println(e);
        }
    }
    // catches exceptions, makes program more readable
    public static void waitFor(int time){
        try{
            Thread.sleep(time);
        }
        catch(InterruptedException ex){
            System.out.println(ex);
        }

    }

}
