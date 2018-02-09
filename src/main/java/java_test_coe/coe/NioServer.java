package java_test_coe.coe;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NioServer implements Runnable {

    private Integer port1 = 8099;
    
    private ServerSocketChannel serversocket1;
    
    private SocketChannel clientchannel1;
    
    private ExecutorService fixedThreadPool;

    private Selector selector;
    
    private ByteBuffer buf = ByteBuffer.allocate(512);
    
    public NioServer() {
        init();
    }

    public void init() {
        try {
            this.selector = SelectorProvider.provider().openSelector();

            this.serversocket1 = ServerSocketChannel.open();
            this.serversocket1.configureBlocking(false);
            this.serversocket1.socket().bind(new InetSocketAddress("localhost", this.port1));
            this.serversocket1.register(this.selector, SelectionKey.OP_ACCEPT);
            
            this.fixedThreadPool = Executors.newFixedThreadPool(5);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void accept(SelectionKey key) throws IOException {
        ServerSocketChannel server = (ServerSocketChannel) key.channel();
        clientchannel1 = server.accept();
        clientchannel1.configureBlocking(false);  
        clientchannel1.register(this.selector, SelectionKey.OP_READ);
    }

    public void read(SelectionKey key) throws IOException {

        this.buf.clear(); 
        SocketChannel channel = (SocketChannel) key.channel();
        int count = channel.read(this.buf);

        if (count == -1) {
            key.channel().close();
            key.cancel();
            return;
        }
        
        final String input = new String(this.buf.array()).trim();
    	this.fixedThreadPool.execute(new Runnable() {
    		public void run() {
    			//具体用jdbc实现数据库查询
				System.out.println("您的输入为：" + input);
				//传回服务端
    		}
    	});
    }

    public void run() {
        while (true) {
            try {
                System.out.println("running ... ");  
                this.selector.select();
                
                Iterator selectorKeys = this.selector.selectedKeys().iterator();
                while (selectorKeys.hasNext()) {
                    System.out.println("running2 ... ");
                    SelectionKey key = (SelectionKey) selectorKeys.next();
                    selectorKeys.remove();
                    if (!key.isValid()) {
                        continue;
                    }
                    if (key.isAcceptable()) {
                        this.accept(key);
                    } else if (key.isReadable()) {
                        this.read(key);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        NioServer server = new NioServer();
        Thread thread = new Thread(server);
        thread.start();
    }
}
