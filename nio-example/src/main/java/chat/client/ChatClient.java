package chat.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ChatClient {

    private static final String        HOST = "127.0.0.1";
    private static final int           PORT = 9999;
    private static       SocketChannel sc;

    private static Object lock = new Object();

    private static ChatClient chatClient;

    public static ChatClient getInstance() {
        synchronized (lock) {
            if (chatClient == null) {
                try {
                    chatClient = new ChatClient();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return chatClient;
        }
    }

    private ChatClient() throws IOException {
        sc = SocketChannel.open();
        sc.configureBlocking(false);
        sc.connect(new InetSocketAddress(HOST, PORT));
    }

    public void sendMsg(String msg) {
        try {
            while (!sc.finishConnect()) {
            }
            sc.write(ByteBuffer.wrap(msg.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String receiveMsg() {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.clear();
        StringBuffer sb    = new StringBuffer();
        int          count = 0;
        String       msg   = null;
        try {
            while ((count = sc.read(buffer)) > 0) {
                sb.append(new String(buffer.array(), 0, count));
            }
            if (sb.length() > 0) {
                msg = sb.toString();
                if ("close".equals(sb.toString())) {
                    msg = null;
                    sc.close();
                    sc.socket()
                      .close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return msg;
    }

}
