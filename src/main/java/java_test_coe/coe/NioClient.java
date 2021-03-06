package java_test_coe.coe;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.net.InetAddress;

public class NioClient {

    private ByteBuffer buffer = ByteBuffer.allocate(512);

    public void query(String host, int port) throws IOException {
        InetSocketAddress address = new InetSocketAddress(InetAddress.getByName(host), port);
        SocketChannel socket = null;
        byte[] bytes = new byte[512];
        System.out.println("请输入查询用户的id,模拟10次请求:");
        while (true) {
            try {
                System.in.read(bytes);
                socket = SocketChannel.open();
                socket.connect(address);
            	for (int i = 1; i < 11; i++) {
                    buffer.clear();
                    buffer.put(bytes);
                    buffer.flip();
                    socket.write(buffer);
                    buffer.clear();
				}
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (socket != null) {
                    socket.close();
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new NioClient().query("localhost", 8099);

    }
}
