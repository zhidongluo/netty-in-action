package nia.chapter1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by kerr.
 *
 * Listing 1.1 Blocking I/O example
 */
public class BlockingIoExample {

    /**
     * Listing 1.1 Blocking I/O example
     * */
    public void serve(int portNumber) throws IOException {
        // 服务端socket
        ServerSocket serverSocket = new ServerSocket(portNumber);
        // 阻塞监听客户端的连接
        Socket clientSocket = serverSocket.accept();

        System.out.println(clientSocket.getRemoteSocketAddress()
                                       .toString());

        // 读取流
        BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
        // 打印流
        PrintWriter out =
                new PrintWriter(clientSocket.getOutputStream(), true);
        String request, response;
        // 循环读取内容
        while ((request = in.readLine()) != null) {
            if ("Done".equals(request)) {
                break;
            }

            // 接收内容
            System.out.println(request);

            // 处理请求
            response = processRequest(request);
            // 输出内容
            out.println(response);
        }
    }

    private String processRequest(String request){
        return "Processed";
    }

    public static void main(String[] args) throws IOException {
        BlockingIoExample example = new BlockingIoExample();
        example.serve(8888);

    }
}
