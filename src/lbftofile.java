import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

public class lbftofile {
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

            // 4 bit - file length
            byte[] fileContentSizeByte = new byte[4];
            bis.read(fileContentSizeByte);
            int fileContentSize = ByteBuffer.wrap(fileContentSizeByte).order(ByteOrder.LITTLE_ENDIAN).getInt();

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
