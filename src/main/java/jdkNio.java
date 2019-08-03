import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

public class jdkNio {
    public static void main(String[]args){
        ServerSocketChannel ssc=null;
        Selector serverSelector=null;
        Selector clientSelector=null;
        try {
            ssc=ServerSocketChannel.open();
            serverSelector=Selector.open();
            clientSelector=Selector.open();
            ssc.bind(new InetSocketAddress(8001));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
