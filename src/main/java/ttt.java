
import org.junit.Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public class ttt {
    public static void main(String[]args) throws IOException {
        FileInputStream fis=null;
        BufferedInputStream bis=null;
        try {
            fis=new FileInputStream("D:\\没有\\1.txt");
            bis=new BufferedInputStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        byte[]b=new byte[10];
        int a=0;
        while ((a=bis.read(b))!=-1){
            String s=new String(b,0,a, Charset.defaultCharset());
            System.out.print(s);
        }
/*        RandomAccessFile file=null;
        try {
            file=new RandomAccessFile("D:\\没有\\1.txt","rw");
            file.seek(file.length()-1);

            file.write("shs 傻逼\r\n".getBytes());
        }catch (IOException e){
            e.printStackTrace();
        }finally{
            file.close();
        }*/

    }
    @Test
    public void test() throws IOException {
        FileInputStream fis=null;
        FileOutputStream fos=null;
        FileChannel input = null;
        FileChannel output=null;
        ByteBuffer bf=null;
        long start=System.currentTimeMillis();
        try{
            fis=new FileInputStream("D:\\没有\\1.txt");
            fos=new FileOutputStream(new File("D:\\没有\\2.txt"));
            input=fis.getChannel();
            output=fos.getChannel();
            long size=input.size();
            bf=ByteBuffer.allocate((int) size);
            input.read(bf);
            output.write(bf);
            //input.transferTo(0,size,output);

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            System.out.print(System.currentTimeMillis()-start);
            input.close();
            output.close();
            fis.close();
            fos.close();
        }
    }
    @Test
    public void test2(){
        FileInputStream fis=null;
        FileOutputStream fos=null;
        byte[] buffer=new byte[32];
        long start=System.currentTimeMillis();
        try {
            fis=new FileInputStream("D:\\没有\\1.txt");
            fos=new FileOutputStream("D:\\没有\\3.txt");
            int num=0;
            while ((num=fis.read(buffer))!=-1){
                fos.write(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            System.out.print(System.currentTimeMillis()-start);
        }
    }
}

