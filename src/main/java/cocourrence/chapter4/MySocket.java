package cocourrence.chapter4;

import java.io.*;
import java.net.Socket;

public class MySocket {

    public static void main(String[] args) throws IOException {
        //如果需要进行https的请求只需要换成如下一句（https的默认端口为443，http默认端口为80）
        //Socket socket = SSLSocketFactory.getDefault().createSocket("xxx", 443);
        Socket socket = new Socket("localhost", 8080);

        //获取输入流，即从服务器获取的数据
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        //获取输出流，即我们写出给服务器的数据
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        //使用一个线程来进行读取服务器的响应
        new Thread(() -> {
            while (true) {
                String line = null;
                try {
                    while ((line = bufferedReader.readLine()) != null) {
                        System.out.println("recv : " + line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        bufferedWriter.flush();

    }

}

