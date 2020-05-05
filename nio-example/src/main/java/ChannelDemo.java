import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;

public class ChannelDemo {

    public static void main(String[] args) throws IOException {
        String relativelyPath = System.getProperty("user.dir");
        System.out.println(relativelyPath);
        FileInputStream inputStream = new FileInputStream(Paths.get(relativelyPath, "testin.txt")
                                                                   .toString());

        FileChannel inputStreamChannel = inputStream.getChannel();

        FileOutputStream outputStream = new FileOutputStream(Paths.get(relativelyPath, "testout.txt")
                                                                      .toString());
        FileChannel outputStreamChannel = outputStream.getChannel();

        copyData(inputStreamChannel, outputStreamChannel);
        inputStreamChannel.close();
        outputStreamChannel.close();
        System.out.println("copy data finished.");
    }

    private static void copyData(FileChannel inputStreamChannel,
                                 FileChannel outputStreamChannel) throws IOException {

        ByteBuffer buffer = ByteBuffer.allocate(20 * 1024);
        while (inputStreamChannel.read(buffer) != -1) {
            buffer.flip();
            while (buffer.hasRemaining()) {
                outputStreamChannel.write(buffer);
            }
            buffer.clear();
        }
    }
}
