package tomcat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NioClient {
    private Selector selector;
    private BufferedReader clientInput = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) throws IOException {
        NioClient client = new NioClient();
        client.init();
        client.start();
    }

    public void init() throws IOException {
        this.selector = Selector.open();//创建选择器
        SocketChannel channel = SocketChannel.open();//创建socketChannel
        channel.configureBlocking(false);//设置为非阻塞
        channel.connect(new InetSocketAddress("127.0.0.1", 8080));
        channel.register(selector, SelectionKey.OP_CONNECT);//注册connect事件

    }

    public void start() throws IOException {
        while (true) {
            selector.select();//此方法会阻塞，直到至少有一个已注册的事件发生。
            Iterator<SelectionKey> ite = this.selector.selectedKeys().iterator();
            //获取发生事件的SelectionKey对象集合
            while (ite.hasNext()) {
                SelectionKey key = ite.next();
                ite.remove();//从集合中移除即将处理的SelectionKey避免重复处理
                if (key.isConnectable()) {
                    connect(key);//连接事件
                } else if (key.isReadable()) {
                    read(key);
                }
            }

        }

    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);//创建读取的缓冲区
        channel.read(buffer);
        String response = new String(buffer.array()).trim();
        System.out.println("服务端响应:" + response);
        String nextRequest = clientInput.readLine();//读取客户端请求输入
        ByteBuffer outBuffer = ByteBuffer.wrap(nextRequest.getBytes());
        channel.write(outBuffer);//将请求发送到服务端
    }

    public void connect(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        if (channel.isConnectionPending()) {//如果正在链接
            if (channel.finishConnect()) { //完成链接
                channel.configureBlocking(false);//设置非阻塞
                channel.register(this.selector, SelectionKey.OP_READ);//注册读事件
                String request = clientInput.readLine(); //输入客户端请求
                channel.write(ByteBuffer.wrap(request.getBytes()));//发送到服务端
            } else {
                key.cancel();
            }
        }

    }
}
