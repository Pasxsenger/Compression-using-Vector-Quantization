import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MyRGBImage {
    int width;
    int height;
    byte[] bytes;
    BufferedImage img;

    MyRGBImage(String imgName, int width, int height){
        this.width = width;
        this.height = height;
        this.img = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        try{
            File imgFile = new File(imgName);
            InputStream imgFileInputStream = new FileInputStream(imgFile);

            long len = imgFile.length();
            bytes = new byte[(int)len];
            int offset = 0, bytesLen = bytes.length;
            int numRead = imgFileInputStream.read(bytes, offset, bytesLen-offset);
            while(offset < bytesLen && numRead >= 0)
                offset+=numRead;

            imgFileInputStream.close();
        } catch (IOException e){
            e.printStackTrace();
        }
        drawImg();
    }
    public void drawImg(){
        img.getRaster().setDataElements(0, 0, width, height, bytes);
    }

}