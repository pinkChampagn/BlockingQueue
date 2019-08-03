import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.atomic.AtomicInteger;

public class eee {
    public static void main(String[]args) throws FileNotFoundException {
        RandomAccessFile raf=new RandomAccessFile("D:\\test.txt","rw");
        AtomicInteger wait=new AtomicInteger(20);
        try {

            for(int i=0;i<20;i++){
                new Thread(()->{
                    StringBuffer sb=new StringBuffer();
                    for(int j=0;j<5000;j++){
                        sb.append(System.currentTimeMillis()+Thread.currentThread().getName()+" ");
                    }
                    try {
                        raf.write((sb.toString()+"\r\n").getBytes());

                    }catch (IOException e) {
                        e.printStackTrace();
                    }finally {
                        wait.decrementAndGet();
                    }

                }).start();
            }
            while (wait.get()!=0){

            }
        }finally {
            try {
                raf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
