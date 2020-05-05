package chat.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatServer implements Runnable {

    private Selector selector;

    private SelectionKey serverKey;

    private boolean isRun;

    private CopyOnWriteArrayList<String> unames;

    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-Mm-dd HH:mm:ss");

    public ChatServer(int port) {
        isRun = true;
        unames = new CopyOnWriteArrayList<>();
        init(port);
    }

    private void init(int port) {

        try {
            selector = Selector.open();

            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

            serverSocketChannel.socket()
                               .bind(new InetSocketAddress(port));

            serverSocketChannel.configureBlocking(false);

            serverKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("server starting...");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        try {
            while (isRun) {
                int n = selector.select();
                if (n > 0) {
                    Iterator<SelectionKey> iterator = selector.selectedKeys()
                                                              .iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        if (key.isAcceptable()) {
                            iterator.remove();

                            ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
                            SocketChannel       clientChannel = serverChannel.accept();
                            if (clientChannel == null) {
                                continue;
                            }
                            clientChannel.configureBlocking(false);
                            clientChannel.register(selector, SelectionKey.OP_READ);
                        }

                        if (key.isReadable()) {
                            readMsg(key);
                        }

                        if (key.isWritable()) {
                            writeMsg(key);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeMsg(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        Object        obj     = key.attachment();
        key.attach("");
        if (obj.toString()
               .equals("close")) {
            key.channel();
            channel.socket()
                   .close();
            channel.close();
            return;
        } else {
            channel.write(ByteBuffer.wrap(obj.toString()
                                             .getBytes()));
        }
        key.interestOps(SelectionKey.OP_READ);
    }

    private void readMsg(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();

        ByteBuffer   byteBuffer = ByteBuffer.allocate(1024);
        StringBuffer sb         = new StringBuffer();

        int count = channel.read(byteBuffer);

        if (count > 0) {
            byteBuffer.flip();
            sb.append(new String(byteBuffer.array(), 0, count));
        }

        String str = sb.toString();

        if (str.indexOf("open_") != -1) {
            String name = str.substring(5);
            printInfo(name + " online");
            unames.add(name);

            Iterator<SelectionKey> iterator = selector.selectedKeys()
                                                      .iterator();
            while (iterator.hasNext()) {
                SelectionKey selKey = iterator.next();

                if (key != serverKey) {
                    selKey.attach(unames);
                    selKey.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
                }
            }
        } else if (str.indexOf("exit_") != -1) {
            String name = str.substring(5);
            unames.remove(name);

            key.attach("close");

            key.interestOps(SelectionKey.OP_WRITE);

            Iterator<SelectionKey> iterator = key.selector()
                                                 .selectedKeys()
                                                 .iterator();

            while (iterator.hasNext()) {
                SelectionKey selKey = iterator.next();
                if (selKey != serverKey && selKey != key) {
                    selKey.attach(unames);
                    selKey.interestOps(selKey.interestOps() | SelectionKey.OP_WRITE);
                }
            }
            printInfo(name + " offline");

        } else {

            String name = str.substring(0, str.indexOf("^"));
            String msg  = str.substring(str.indexOf("^") + 1);
            printInfo("(" + name + "）说:" + msg);
            String smsg = name + " " + LocalDateTime.now()
                                                    .format(dateTimeFormatter) + "\n " + msg + "\n";
            Iterator<SelectionKey> iterator = selector.selectedKeys()
                                                      .iterator();
            while (iterator.hasNext()) {
                SelectionKey selKey = iterator.next();
                if (selKey != serverKey) {
                    selKey.attach(smsg);
                    selKey.interestOps(selKey.interestOps() | SelectionKey.OP_WRITE);
                }
            }

        }
    }

    private void printInfo(String str) {
        System.out.println("[" + LocalDateTime.now()
                                              .format(dateTimeFormatter) + "] -> " + str);
    }

    public static void main(String[] args) {
        ChatServer server = new ChatServer(9999);
        new Thread(server).start();
    }
}
