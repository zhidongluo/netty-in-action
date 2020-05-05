package nia.chapter4.example;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class OIOServer {

    public void serve(int port) throws IOException {
        final ServerSocket socket = new ServerSocket(port);

        try {
            while (true) {
                final Socket client = socket.accept();
                System.out.println("Accepted connection from " + client);

                new Thread(() -> {
                    OutputStream out;

                    try {
                        out = client.getOutputStream();
                        out.write("Hi\r\n".getBytes(StandardCharsets.UTF_8));
                        out.flush();
                        client.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally {
                        try {
                            client.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        OIOServer server = new OIOServer();
        server.serve(9999);
    }
}
