import io.netty.buffer.ByteBufUtil;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;

public class ScatterGatherIO {

    public static void main(String[] args) {
        String data = "Scattering and Gathering example shown in yiibai.com";
        // gatherBytes(data);
        scatterBytes();//原文出自【易百教程】，商业转载请联系作者获得授权，非商业请保留原文链接：https://www.yiibai.com/java_nio/scatter-gather-or-vectored-input-output.html#article-start
    }


    // 聚集
    public static void gatherBytes(String data){

        String relativePath = System.getProperty("user.dir");

        ByteBuffer buffer1 = ByteBuffer.allocate(8);

        ByteBuffer buffer2 = ByteBuffer.allocate(400);

        buffer1.asIntBuffer()
               .put(420);

        buffer2.asCharBuffer()
               .put(data);

        FileChannel channelInstance = createChannelInstance(Paths.get(relativePath, "testout.txt").toString(), true);

        try {
            channelInstance.write(new ByteBuffer[]{buffer1, buffer2});
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    // 分散
    public static void scatterBytes() {

        String relativePath = System.getProperty("user.dir");

        ByteBuffer b1 = ByteBuffer.allocate(8);
        ByteBuffer b2 = ByteBuffer.allocate(400);
        FileChannel channelInstance = createChannelInstance(Paths.get(relativePath, "testout.txt")
                                                                 .toString(), false);
        try {
            channelInstance.read(new ByteBuffer[]{b1, b2});
        } catch (IOException e) {
            e.printStackTrace();
        }

        // b1.rewind();
        //
        // b2.rewind();

        b1.flip();
        b2.flip();

        int buffOne = b1.asIntBuffer()
                  .get();

        String buffTwo = b2.asCharBuffer()
                     .toString();

        System.out.println(buffOne);
        System.out.println(buffTwo);

    }

    public static FileChannel createChannelInstance(String file,
                                                    boolean isOutput) {
        FileChannel fileChannel = null;

        try {
            if (isOutput) {
                fileChannel = new FileOutputStream(file).getChannel();
            }else{
                fileChannel = new FileInputStream(file).getChannel();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return fileChannel;
    }

}
