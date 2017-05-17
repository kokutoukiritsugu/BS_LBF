import java.io.*;
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
            // -2 for bom
            int fileSize = (int) file.length() - 2;

            // 1~3bit magic number
            // +2 for file end \0
            int in = (fileSize + 2) / 2;
            int out = 0;
            if (in / 8192 <= 0) {
                if (in / 64 <= 0) {
                    out = in;
                    bos.write((byte) (out & 0xff));
                } else {
                    out = in % 64 + 64;
                    bos.write((byte) (out & 0xff));

                    out = in / 64;
                    bos.write((byte) (out & 0xff));
                }
            } else {
                out = (in % 8192 + 8192) % 64 + 64;
                bos.write((byte) (out & 0xff));

                out = in % 8192 / 64 + 128;
                bos.write((byte) (out & 0xff));

                out = in / 8192;
                bos.write((byte) (out & 0xff));
            }

            // file
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);
            byte[] fileContentByte = new byte[fileSize];
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
