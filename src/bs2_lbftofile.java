import org.apache.commons.io.FilenameUtils;

import java.io.*;
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
            // skip c style string end \0 ch
            int fileNameSize = (fileNameSizeByte[0] - 1) * 2;

            // filename
            byte[] fileNameByte = new byte[fileNameSize];
            bis.read(fileNameByte);
            String fileName = new String(fileNameByte, Charset.forName("utf-16le"));

            // skip c style string end \0 ch
            bis.skip(2);

            // 1~3 bit - file length
            byte[] fileContentSizeByte1 = new byte[1];
            bis.read(fileContentSizeByte1);

            int out = uByte(fileContentSizeByte1[0]);
            if (uByte(fileContentSizeByte1[0]) > 0x3f) {

                byte[] fileContentSizeByte2 = new byte[1];
                bis.read(fileContentSizeByte2);

                out += (uByte(fileContentSizeByte2[0]) - 1) * 64;
                if (uByte(fileContentSizeByte2[0]) > 0x7f) {

                    byte[] fileContentSizeByte3 = new byte[1];
                    bis.read(fileContentSizeByte3);

                    out += (uByte(fileContentSizeByte3[0]) - 1) * 8192;

                }
            }

            int fileContentSize = out * 2 - 2;

            // file
            byte[] fileContentByte = new byte[fileContentSize];
            bis.read(fileContentByte);

            bis.skip(2);

            FileOutputStream fos = new FileOutputStream(lbfFolderName + "\\" + fileName);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            // write BOM
            bos.write(new byte[]{(byte) 0xFF, (byte) 0xFE});
            bos.write(fileContentByte);
            bos.close();
        }
        bis.close();
    }

    private static int uByte(byte signedByte) {
        return ((int) signedByte) & 0xff;
    }
}
