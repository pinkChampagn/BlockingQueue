import org.junit.Test;
import sun.misc.Unsafe;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Objects;

public class test1 {
    private static final Unsafe unsafe=getUnsafe();
    private static long offset=0;

    static {
        try {
            offset=unsafe.objectFieldOffset(newTest.class.getDeclaredField("Size"));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    ArrayList<Integer> list=new ArrayList<>(10);
    private String string;
    private volatile int Size=10;

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


    public static void main(String[]args){
        try {

            RandomAccessFile raf=new RandomAccessFile("D:\\1.txt","rw");
            String s=raf.readLine();
            System.out.print(Long.valueOf(s));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void supplier(int i){
        list.add(i);
        {
            int ss=Size;
            while (!unsafe.compareAndSwapInt(this,offset,ss,ss+1)){
                ss=Size;
            }
        }

    }
    @Test
    public void test(){
        for(int i=0;i<5;i++){
            new Thread(()->{supplier(1);}).start();
        }
        while (true){

        }
    }
    @Test
    public void newTest() throws FileNotFoundException {
        RandomAccessFile raf=new RandomAccessFile("D:\\test.txt","rw");
        try {

            for(int i=0;i<10;i++){
                new Thread(()->{
                    for(int j=0;j<100;j++){
                        try {
                            raf.write((System.currentTimeMillis()+"\r\n").getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
            Thread.currentThread().join();
        }  catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            try {
                raf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
