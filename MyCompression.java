import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;


public class MyCompression {
    ArrayList<MyVector> vectors;
    ArrayList <MyCodeWord> codewords;

    MyCompression(){
        this.vectors = new ArrayList<>();
        this.codewords = new ArrayList<>();
    }

    private void byte2vec(byte[] bytes){
        int len = bytes.length;
        for(int i = 0; i < len; i+=2){
            MyVector vector = new MyVector((int)bytes[i] & 0x000000FF, (int)bytes[i+1] & 0x000000FF);
            vectors.add(vector);
        }
    }

    private void initCodeWords(int M, int N){
        int logMN = (int)(Math.log(N) / Math.log(M));
        int stepSize = (int)Math.floor(256.0 / logMN);
        if(M == 2 && N == 2){
            MyCodeWord cw1 = new MyCodeWord(127,127);
            MyCodeWord cw2 = new MyCodeWord(191,191);
            codewords.add(cw1);
            codewords.add(cw2);
        }
        else{
            int cwCnt = 0, base = ((stepSize / M) - 1);
            for(int i = 0; cwCnt < N; i+=stepSize){
                for(int j = 0; j <= 255 && cwCnt < N; j+=stepSize){
                    MyCodeWord cw = new MyCodeWord(i+base,j+base);
                    codewords.add(cw);
                    cwCnt++;
                }
            }
        }
    }

    private MyCodeWord findBMU(MyVector vector){
        double distance, minDistance = Double.MAX_VALUE;
        MyCodeWord cwBMU = null;

        for(MyCodeWord cw: codewords){
            distance = Math.abs(Math.pow((vector.x - cw.x),2) + Math.pow((vector.y - cw.y),2));
            if(minDistance > distance){
                minDistance = distance;
                cwBMU = cw;
            }
        }
        return cwBMU;
    }

    private void findCodeWords(int M, int N){
        boolean converged = false;
        while(!converged){
            for(MyVector vector: vectors){
                MyCodeWord cwBMU = findBMU(vector);
                cwBMU.errVec.x += vector.x;
                cwBMU.errVec.y += vector.y;
                cwBMU.vecCnt++;
            }

            Collections.sort(codewords);

            int cwCnt = 0;
            converged = true;
            for(int i = 0; i < N; i++){
                MyCodeWord cwi = codewords.get(i);
                int diff = cwi.diffCalculate();
                if(diff == -1){
                    codewords.remove(cwi);
                    MyCodeWord cwj = codewords.get(cwCnt);
                    MyCodeWord cw = new MyCodeWord(cwj.x + M*M,cwj.y + M*M);
                    codewords.add(cw);
                    cwCnt++;
                }
                else if(diff != 0){
                    converged = false;
                }
            }
        }
    }

    private void quantizeBytes(byte[] bytes){
        MyCodeWord cwBMU;
        int errorx = 0, errory = 0;
        for(int i = 0; i < bytes.length; i+=2){
            MyVector vector = new MyVector((int)bytes[i] & 0x000000FF, (int)bytes[i+1] & 0x000000FF);
            cwBMU = findBMU(vector);

            bytes[i] = (byte)cwBMU.x;
            bytes[i+1] = (byte)cwBMU.y;
            errorx += Math.pow(vector.x - cwBMU.x, 2);
            errory += Math.pow(vector.y - cwBMU.y, 2);

        }

        float meanXErr = (float)errorx/vectors.size();
        float meanYErr = (float)errory/vectors.size();
        float meanErr = (float)((meanXErr + meanYErr)/2.0);

        System.out.println("CodeWords: " + codewords.size());

        for(MyCodeWord code: codewords)
            System.out.println("x: " + code.x  + " y: " + code.y);
        System.out.println("Mean Squared Error (MSE): " + meanErr);
    }

    private void checkMN(int M, int N){
        double index = Math.log(N) / Math.log(M);
        if (M != 2) {
            double root = Math.sqrt(M);
            if (root != Math.floor(root)) {
                System.out.println("M = " + M + " is neither 2 nor a square number.");
                System.exit(0);
            }
        }
        if(!(N != 1 && index == Math.floor(index))){
            System.out.println("N = " + N + " is not a power of M = " + M + ".");
            System.exit(0);
        }
    }
    private void showImg(MyRGBImage originalImg, MyRGBImage newImg){
        newImg.drawImg();

        JFrame frame = new JFrame();
        GridBagLayout gLayout = new GridBagLayout();
        frame.getContentPane().setLayout(gLayout);

        JLabel lbText1 = new JLabel("Original image (Left)");
        lbText1.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel lbText2 = new JLabel("Image after compression (Right)");
        lbText2.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel lbIm1 = new JLabel(new ImageIcon(originalImg.img));
        JLabel lbIm2 = new JLabel(new ImageIcon(newImg.img));
        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 0;
        frame.getContentPane().add(lbText1, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = 0;
        frame.getContentPane().add(lbText2, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        frame.getContentPane().add(lbIm1, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 1;
        frame.getContentPane().add(lbIm2, c);

        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args){
        if(args.length != 3){
            System.out.println("Please use command in the format like this: java MyCompression image1-onechannel.rgb 2 16");
            System.exit(0);
        }

        String imgName = args[0];
        int M = Integer.parseInt(args[1]);
        int N = Integer.parseInt(args[2]);

        int width = 352;
        int height = 288;

        MyRGBImage originalImg = new MyRGBImage(imgName, width, height);
        MyRGBImage newImg = new MyRGBImage(imgName, width, height);

        MyCompression myVecQtz = new MyCompression();
        myVecQtz.checkMN(M, N);
        myVecQtz.byte2vec(newImg.bytes);
        myVecQtz.initCodeWords(M, N);
        myVecQtz.findCodeWords(M, N);
        myVecQtz.quantizeBytes(newImg.bytes);
        myVecQtz.showImg(originalImg, newImg);

    }
}