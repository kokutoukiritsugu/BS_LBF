import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

public class bs2_lbftofile {
    public static void main(String[] args) throws IOException {
        String lbfFileName = args.length > 0 ? args[0] : "Localizedjpn.lbf";
        String lbfFolderName = FilenameUtils.getBaseName(lbfFileName);
        File f = new File(lbfFolderName);
        f.mkdirs();

        FileInputStream fis = new FileInputStream(lbfFileName);
        BufferedInputStream bis = new BufferedInputStream(fis);
        while (true) {
            // 1 bit - filename length
            byte[] fileNameSizeByte = new byte[1];
            if (bis.read(fileNameSizeByte) == -1) break;
            System.out.println(fileNameSizeByte[0]);
            // skip c style string end \0 ch
            int fileNameSize = (fileNameSizeByte[0] - 1) * 2;
            System.out.println(fileNameSize);

            // filename
            byte[] fileNameByte = new byte[fileNameSize];
            bis.read(fileNameByte);
            String fileName = new String(fileNameByte, Charset.forName("utf-16le"));
            System.out.println(fileName);

            // skip c style string end \0 ch
            bis.skip(2);

            // 1 bit - file length
            byte[] fileContentSizeByte1 = new byte[1];
            bis.read(fileContentSizeByte1);
            int fileContentSizeByte1_int = ((int) fileContentSizeByte1[0]) & 0xff;

            int out = fileContentSizeByte1_int;
            if (fileContentSizeByte1_int > 0x3f) {

                byte[] fileContentSizeByte2 = new byte[1];
                bis.read(fileContentSizeByte2);
                int fileContentSizeByte2_int = ((int) fileContentSizeByte2[0]) & 0xff;

                out += (fileContentSizeByte2_int - 1) * 64;
                if (fileContentSizeByte2_int > 0x7f) {

                    byte[] fileContentSizeByte3 = new byte[1];
                    bis.read(fileContentSizeByte3);
                    int fileContentSizeByte3_int = ((int) fileContentSizeByte3[0]) & 0xff;

                    out += (fileContentSizeByte3_int - 1) * 8192;

                }
            }
            int fileContentSize = out * 2;
            System.out.println("fileContentSize " + fileContentSize * 2);

            // file
            byte[] fileContentByte = new byte[fileContentSize];
            bis.read(fileContentByte);

            FileOutputStream fos = new FileOutputStream(lbfFolderName + "\\" + fileName);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            bos.write(fileContentByte);
            bos.close();
        }
        bis.close();
    }
}
