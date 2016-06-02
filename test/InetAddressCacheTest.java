package test;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import main.InetAddressCache;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;


public class InetAddressCacheTest {
    InetAddressCache cache;
    ArrayList<InetAddress> addressArrayList;

    @Before
    public void setUp(){
        cache = new InetAddressCache(5000);
        addressArrayList = new ArrayList<InetAddress>();
        int size = 10;
        for(int i = 1; i <= size; i++){
            try{
                addressArrayList.add(InetAddress.getByName("27.0.0." + i));
            }
            catch(UnknownHostException e) {
                System.out.println(e);
            }
        }
    }

    @After
    public void tearDown(){
        cache.close();
    }

    @Test
    public void offerChangesSizeOfCache(){
        int sizeOne = cache.size();
        cache.offer(addressArrayList.get(0));
        assertEquals(sizeOne + 1, cache.size());
    }

    @Test
    public void offerReturnsTrueOnSuccess(){
        assertTrue(cache.offer(addressArrayList.get(2)));
    }

    @Test
    public void containsReturnsTrueIfAnAddressIsPresent(){
        assertFalse(cache.contains(addressArrayList.get(1)));
        cache.offer(addressArrayList.get(1));
        assertTrue(cache.contains(addressArrayList.get(1)));
    }

    @Test
    public void containsReturnsFalseIfAnAddressIsNotPresent(){
        cache.offer(addressArrayList.get(1));
        assertFalse(cache.contains(addressArrayList.get(0)));
    }

    @Test
    public void removeWithAnAddressRemovesThatElement(){
        populateCache();
        InetAddress element1 = addressArrayList.get(1);
        assertTrue(cache.contains(element1));
        cache.remove(element1);
        assertFalse(cache.contains(element1));
    }

    @Test
    public void removeWithAnAddressReturnsTrueIfElementIsRemoved(){
        populateCache();
        InetAddress element1 = addressArrayList.get(1);
        assertTrue(cache.remove(element1));
    }
    @Test
    public void removeWithAnAddressReturnsFalseIfElementIsNotRemoved(){
        cache.offer(addressArrayList.get(0));
        assertFalse(cache.remove(addressArrayList.get(1)));
    }

    @Test public void removeWithAnAddressReducesTheSizeOfCacheBy1(){
        populateCache();
        int initialSize = cache.size();
        cache.remove(addressArrayList.get(0));
        assertEquals(initialSize -1, cache.size());
    }

    @Test
    public void peekReturnsNullIfCacheIsEmpty(){
        assertTrue(cache.size() == 0);
        assertNull(cache.peek());
    }

    @Test
    public void peekReturnsMostRecentAddressAdded(){
        populateCache();
        assertEquals(addressArrayList.get(addressArrayList.size() - 1), cache.peek());
    }

    @Test
    public void removeWithNoParamsReturnsNullIfCacheIsEmpty(){
        assertTrue(cache.size() == 0);
        assertNull(cache.remove());
    }

    @Test
    public void removeWithNoParamsReturnsMostRecentAddressAdded(){
        populateCache();
        assertEquals(addressArrayList.get(addressArrayList.size() - 1), cache.remove());
    }

    @Test
    public void removeWithNoParamsReducesTheSizeOfCacheBy1(){
        populateCache();
        int initialSize = cache.size();
        cache.remove();
        assertEquals(initialSize - 1, cache.size());
    }

    @Test
    public void takeReturnsMostRecentAddressAdded(){
        populateCache();
        try{
            assertEquals(addressArrayList.get(addressArrayList.size() - 1), cache.take());
        }
        catch(InterruptedException e){
            System.out.println(e);
            System.out.println("Should not have thrown here.");
        }
    }

    @Test(expected=InterruptedException.class)
    public void takeThrowsInterruptedExceptionWhenInterrupted() throws InterruptedException{
        Thread.currentThread().interrupt();
        assertEquals(addressArrayList.get(addressArrayList.size() - 1), cache.take());
    }



    @Test(expected=NullPointerException.class)
    public void closeMakesCacheThrowNullPointerExceptions(){
        cache.close();
        cache.offer(addressArrayList.get(0));
    }

    @Test
    public void sizeReturns0ForNewCache(){
        assertEquals(0, cache.size());
    }

    @Test
    public void sizeReturnsNumberOfElementsInCache(){
        populateCache();
        assertEquals(addressArrayList.size(), cache.size());
    }

    @Test
    public void isEmptyReturnsFalseIfCacheIsNotEmpty(){
        populateCache();
        assertFalse(cache.isEmpty());
    }

    @Test
    public void isEmptyReturnsTrueIfCacheIsEmpty(){
        assertEquals(0, cache.size());
        assertTrue(cache.isEmpty());
    }

    private void populateCache(){
        try {
            for (int i = 0; i < addressArrayList.size(); i++) {
                cache.offer(addressArrayList.get(i));
            }
            if (cache.size() != addressArrayList.size()) {
                Exception badPopulation = new Exception();
                throw badPopulation;
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
}
