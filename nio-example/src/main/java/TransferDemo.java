import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class TransferDemo {


    public static void main(String[] args) throws IOException {
        String relativePath = System.getProperty("user.dir");

        String[] ifs = new String[]{relativePath + "/input1.txt", relativePath + "/input2.txt", relativePath + "/input3.txt", relativePath + "/input4.txt"};

        String of = relativePath + "/output.txt";

        FileOutputStream outputStream = new FileOutputStream(new File(of));

        FileChannel outputStreamChannel = outputStream.getChannel();

        for (String s : ifs) {
            FileInputStream fileInputStream    = new FileInputStream(s);
            FileChannel     inputStreamChannel = fileInputStream.getChannel();

            inputStreamChannel.transferTo(0, inputStreamChannel.size(), outputStreamChannel);

            inputStreamChannel.close();
            fileInputStream.close();
        }

        outputStreamChannel.close();
        outputStream.close();
        System.out.println("all jobs done...");
    }

}
