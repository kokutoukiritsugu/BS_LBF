import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

public class bs2_filetolbf {
    public static void main(String[] args) throws IOException {
        String lbfFolderName = args.length > 0 ? args[0] : "Localizedjpn";
        File f = new File(lbfFolderName);

        FileOutputStream fos = new FileOutputStream(lbfFolderName + ".lbf");
        BufferedOutputStream bos = new BufferedOutputStream(fos);

        for (String fileName : f.list()) {
            // 1 byte - filename size
            byte[] fileNameByte = fileName.getBytes(Charset.forName("utf-16le"));
            int fileNameSize = fileNameByte.length / 2 + 1;
            bos.write(fileNameSize & 0xff);

            // filename
            bos.write(fileNameByte);

            // c style string end \0 ch
            bos.write(0x00);
            bos.write(0x00);

            // 4 byte - file size
            File file = new File(lbfFolderName + "\\" + fileName);
            // -2 for bom , -2 for end \0
            long fileSize = file.length() - 2 - 2;
            byte[] fileSizeBytes = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt((int) fileSize).array();
            bos.write(fileSizeBytes);

            // file
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);
            byte[] fileContentByte = new byte[(int) fileSize];
            // skip BOM
            bis.skip(2);
            bis.read(fileContentByte);
            bos.write(fileContentByte);
            // wirte end \0
            bos.write(new byte[]{(byte) 0x00, (byte) 0x00});
            bis.close();
        }
        bos.close();
    }
}
