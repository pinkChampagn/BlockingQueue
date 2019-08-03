import sun.misc.Unsafe;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.util.ArrayList;

import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 错误的例子，无法实现BlockingQueue的功能
 */
public class newTest {
    private static final Unsafe unsafe=getUnsafe();
    /**
     * main线程等待其他线程结束条件
     */
    private static AtomicInteger providers=new AtomicInteger(10);
    //ArrayList<Long> product=new ArrayList<>(10);
    private static long offset=0;

    static {
        try {
            offset=unsafe.objectFieldOffset(newTest.class.getDeclaredField("Size"));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    SynchronousQueue<Long> transfer=new SynchronousQueue<>();
    /**
     * cas自旋volatile变量+SynchronousQueue 代替 ArrayBlockingQueue
     */
    private volatile int Size=10;

    public static void main(String[]args) throws FileNotFoundException {
        ExecutorService threadpool = Executors.newCachedThreadPool();
        newTest t=new newTest();
        //最后读取使用
        RandomAccessFile raf=new RandomAccessFile("D:\\time.txt","rw");
        //consume输出
        RandomAccessFile raf2=new RandomAccessFile("E:\\time.txt","rw");
        //排序输出线程读取源文件的流
        RandomAccessFile raf1=new RandomAccessFile("D:\\time.txt","rw");
        for(int i=0;i<5;i++){
            threadpool.execute(()->{t.consume(raf2);});
        }

        for(int i=0;i<5;i++){
            threadpool.execute(()->{t.supplier();});
        }

        while (providers.get()!=0){

        }

    }
    public static Unsafe getUnsafe()  {
        Field theUnsafe = null;
        try {
            theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            return (Unsafe) theUnsafe.get(null);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }

    }

    public void supplier(){
        long start =System.currentTimeMillis();
        while ((System.currentTimeMillis()-start)<2*1000) {
            try {
                //product.add(System.currentTimeMillis());
                transfer.put(System.currentTimeMillis());
                {
                    int s=Size;
                    while (!unsafe.compareAndSwapInt(this,offset,s,s+1)){
                        s=Size;
                    }
                }

                //cb.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        providers.decrementAndGet();
    }
    public void consume(RandomAccessFile output){
        while (providers.get()>0){
            if (Size>0){
                Long take=null;
                try {
                    take= transfer.take();
                    /*synchronized (lock){
                        raf.write((take + "\r\n").getBytes());
                    }*/
                    output.write((take+"\r\n").getBytes());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
