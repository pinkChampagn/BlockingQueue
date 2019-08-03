package test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


public class syncTest {
    //ArrayBlockingQueue bq=new ArrayBlockingQueue(10);
    //原子操作替代CountDownLatch
    //private AtomicInteger allThread =new AtomicInteger(10);
    public static void main(String[]args) throws FileNotFoundException {
        //CompletableFuture cf=new CompletableFuture();

        //生产者消费者线程未全部执行结束 前 阻塞 main线程
        CountDownLatch cdl=new CountDownLatch(11);
        //逆序输出线程未执行结束前阻塞main线程
        CountDownLatch cc=new CountDownLatch(1);
        //阻塞队列
        ArrayBlockingQueue bq=new ArrayBlockingQueue(10);
        //消费者输出到文件使用的流
        //FileOutputStream fos=new FileOutputStream("D:\\time.txt");
        //FileInputStream fis=new FileInputStream("D:\\time.txt");
        //消费者输出到文件使用的流
        RandomAccessFile raf=new RandomAccessFile("D:\\time.txt","rw");
        //排序输出线程读取源文件的流
        RandomAccessFile raf1=new RandomAccessFile("D:\\time.txt","rw");
        // 输出到新文件的流
        RandomAccessFile OutRaf=new RandomAccessFile("D:\\time-order.txt","rw");
        ExecutorService threadpool = Executors.newFixedThreadPool(11);
        //线程池版
/*        for (int i=0;i<5;i++){
            threadpool.execute(new provider(bq,cdl));
        }
        for(int i=0;i<6;i++){
            threadpool.execute(new consumer2(raf,cdl,bq));
        }*/
        /*--------------------------------*/
        //CompletableFuture.runAsync(new provider(bq, cdl), threadpool).thenRunAsync(new consumer2(raf, cdl, bq), threadpool);

        for (int i=0;i<5;i++){
            new Thread(new provider(bq,cdl)).start();
        }
        for(int i=0;i<6;i++){
            //new Thread(new consumer(bq,fos,cdl)).start();
            new Thread(new consumer2(raf,cdl,bq)).start();
        }

        try {
            cdl.await();
            System.out.println("生产消费过程结束--------------");
            //new Thread(new reverse_order(OutRaf,raf1,cc)).start();
            //threadpool.execute(new reverse_order(OutRaf,raf,cc));
            //cc.await();
            System.out.println("排序输出结束------------------");
            //Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            try {
                raf1.close();
                raf.close();
                OutRaf.close();
                threadpool.shutdown();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
class provider implements Runnable{
    BlockingQueue bq=null;
    //CyclicBarrier cb=null;
    CountDownLatch cdl=null;
    long start=0;
    public provider(ArrayBlockingQueue abq,CountDownLatch cdl) {
        this.cdl=cdl;
        //this.cb=cb;
        this.bq=abq;
        start=System.currentTimeMillis();
    }
    @Override
    public void run() {
        //时间戳计算生产者执行时间
        while ((System.currentTimeMillis()-start)<2*1000) {

            try {
                bq.put(System.currentTimeMillis() + "");
                //cb.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } /*catch (BrokenBarrierException e) {
            e.printStackTrace();
        }*/
        }
        cdl.countDown();
    }
}
class consumer implements Runnable{
    BlockingQueue bq=null;
    OutputStream outputStream=null;
    //CyclicBarrier cb=null;
    CountDownLatch cdl=null;
    //RandomAccessFile raf=null;
    private byte[] lock=new byte[1];

    public consumer(BlockingQueue bq, OutputStream outputStream, CountDownLatch cdl) {
        //this.cb=cb;
        this.cdl=cdl;
        this.bq = bq;
        this.outputStream = outputStream;
    }

    @Override
    public void run() {
        while (cdl.getCount()>5) {
            try {
                String take=null;
                if(!bq.isEmpty()) {
                    take = (String) bq.take();
                    synchronized (lock) {
                        outputStream.write((take + "\r\n").getBytes());
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        cdl.countDown();

    }
}
class consumer2 implements Runnable{
    RandomAccessFile raf=null;
    CountDownLatch cdl=null;
    BlockingQueue bq=null;
    byte[] lock=new byte[1];
    public consumer2(RandomAccessFile raf, CountDownLatch cdl,BlockingQueue bq) {
        this.raf = raf;
        this.cdl = cdl;
        this.bq=bq;
    }
    @Override
    public void run() {int i=0;
        //@param cdl CountDownLatch实例,生产者线程每结束一个  countdown一次 初始化为10
        while (cdl.getCount()>6){

            //System.out.println(Thread.currentThread().getName()+(i++));
            //@param ba ArrayBlockingQueue
            if (!bq.isEmpty()) {
                //synchronized (lock) {
                    //if(bq.isEmpty())
                        //continue;
                    String take = null;
                    try {
                        take = (String) bq.take();
                        //synchronized (lock){
                        //raf.write((take + "\r\n").getBytes());
                        //}
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } /*catch (IOException e) {
                        e.printStackTrace();
                    }*/

                //}
            }
        }
        cdl.countDown();
    }
}
class reverse_order implements Runnable{
    //InputStream is=null;
    RandomAccessFile out=null;
    RandomAccessFile in=null;
    //ByteBuffer by=null;
    List<Long> list=new ArrayList();
    CountDownLatch cc=null;
    public reverse_order(RandomAccessFile out, RandomAccessFile in,CountDownLatch cc) {
        this.out = out;
        this.in = in;
        this.cc=cc;
    }

    @Override
    public void run() {
        try {
            String s=null;
            while((s = in.readLine())!=null)
                list.add(Long.valueOf(s));
            list.sort((Long e,Long e1)->{
                return e1.compareTo(e);
            });
            //Collections.sort(list);
            //Collections.reverse(list);
            /*list.sort((l1,l2)->{
                return l1>l2?l1:l2;
            });*/
            int index=0;
            while (index<list.size()){
                out.write((list.get(index)+"\r\n").getBytes());
                ++index;
            }
            cc.countDown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


