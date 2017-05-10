import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

/**
 * Created by kiritsugu on 2017-5-10.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        String lbfFileName = args.length > 0 ? args[0] : "Localizedjpn.lbf";
        String lbfFolderName = FilenameUtils.getBaseName(lbfFileName);

        FileInputStream fis = new FileInputStream(lbfFileName);
        BufferedInputStream bis = new BufferedInputStream(fis);

        while (true) {
            byte[] fileNameSizeByte = new byte[1];
            if (bis.read(fileNameSizeByte) == -1) {
                break;
            }
            int fileNameSize = (fileNameSizeByte[0] - 1) * 2;
            System.out.println(fileNameSize);

            byte[] fileNameByte = new byte[fileNameSize];
            bis.read(fileNameByte);
            String fileName = new String(fileNameByte, Charset.forName("utf-16le"));
            System.out.println(fileName);

            bis.skip(2);

            byte[] fileContentSizeByte = new byte[4];
            bis.read(fileContentSizeByte);
            ByteBuffer bb = ByteBuffer.wrap(fileContentSizeByte);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            int fileContentSize = bb.getInt();
            System.out.println(fileContentSize);

            byte[] fileContentByte = new byte[fileContentSize];
            bis.read(fileContentByte);
            String s = new String(fileContentByte, Charset.forName("utf-16le"));
            System.out.println(s);

            FileOutputStream fos = new FileOutputStream(lbfFolderName + "\\" + fileName);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            bos.write(fileContentByte);
            bos.close();

        }
    }
}
