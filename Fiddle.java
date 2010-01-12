import java.util.*;
import java.text.*;
import java.io.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;


public class Fiddle {

    public static Rectangle GameZone = new Rectangle(81, 147, 500, 400);
    public static String[] FileNames = {
        "already_guessed_that_word.png",
        "click_to_start.png",
        "empty.png",
        "got_the_big_word.png",
        "invalid_word.png",
        "lost_game.png",
        "qualify_no_go.png",
        "qualify_with_go.png",
        "some_words.png",
    };

    public static void main(String[] args)
        throws java.awt.AWTException, IOException
    {
        /*
        Robot robot = new Robot();
        BufferedImage whole = robot.createScreenCapture(IU.getResolution());
        BufferedImage part = ImageIO.read(new File("blot_158_82_80x80.png"));
        
        ImageFinder finder = new ImageFinder(whole, part);
        BufferedImage match = finder.getMatchImage();
        ImageIO.write(match, "png", new File("match.png"));
        /**/

        /*
        File tmpDir = new File("tmp");
        tmpDir.mkdir();
        for(int i = 0; i < FileNames.length; i++){
            BufferedImage image = ImageIO.read(new File(FileNames[i]));
            BufferedImage gameImage = image.getSubimage(GameZone.x, GameZone.y,
                GameZone.width, GameZone.height);
            ImageIO.write(gameImage, "png", new File(tmpDir, FileNames[i]));
        }
        /**/

        /*
        File tmpDir = new File("tmp");
        for(int i = 0; i < FileNames.length; i++){
            lettersToBlack(new File(FileNames[i]), new File(tmpDir, FileNames[i]));
        }
        /**/

        /*
        RgbMat eMat = new RgbMat(ImageIO.read(new File("big_e.png")));
        RgbMat rMat = new RgbMat(ImageIO.read(new File("big_r.png")));
        HashSet<Integer> rgbSet = IU.rgbSet(eMat);
        rgbSet.addAll(IU.rgbSet(rMat));
        HashSet<Integer> letterRgbSet = new HashSet<Integer>();
        for(int rgb : rgbSet){
            int r = IU.getR(rgb);
            int g = IU.getG(rgb);
            int b = IU.getB(rgb);
            if(b > 5 * r){
                //System.out.println("letter.rgbSet.add(new RGB("
                //    + r + ", " + g + ", " + b + "));");
                letterRgbSet.add(rgb);
            }
        }

        RgbMat someWordsMat = new RgbMat(ImageIO.read(new File("some_words.png")));
        for(int i = 0; i < someWordsMat.rgbs.length; i++){
            if(letterRgbSet.contains(someWordsMat.rgbs[i])){
                someWordsMat.rgbs[i] = IU.BLACK_RGB;
            }
            else{
                someWordsMat.rgbs[i] = IU.WHITE_RGB;
            }
        }
        ImageIO.write(someWordsMat.toImage(), "png", new File("blackened_some_words.png"));
        /*
        for(int i = 0; i < eMat.rgbs.length; i++){
            if(IU.getB(eMat.rgbs[i]) > 5 * IU.getR(eMat.rgbs[i])){
                eMat.rgbs[i] = IU.BLACK_RGB;
            }
            else{
                eMat.rgbs[i] = IU.WHITE_RGB;
            }
        }
        ImageIO.write(eMat.toImage(), "png", new File("black_big_e.png"));
        /**/

        Runtime run = Runtime.getRuntime();
        String[] cmdArray = { "gocr", "blackened_some_words.png" };
        Process proc = run.exec(cmdArray);
        try{proc.waitFor(); } catch(Exception e){}
        BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        String line = null;
        while((line = reader.readLine()) != null){
            System.out.println("<" + line + ">");
        }

    }


    public static void lettersToBlack(File inFile, File outFile)
        throws IOException
    {
        RgbMat mat = new RgbMat(ImageIO.read(inFile));
        int bigRgb = IU.toRgb(0, 151, 201);
        int smallRgb = IU.toRgb(0, 51, 102);
        HashSet<Integer> letterRgbSet = new HashSet<Integer>();
        letterRgbSet.add(bigRgb);
        letterRgbSet.add(smallRgb);
        for(int i = 0; i < mat.rgbs.length; i++){
            if(letterRgbSet.contains(mat.rgbs[i])){
                mat.rgbs[i] = IU.BLACK_RGB;
            }
            else{
                mat.rgbs[i] = IU.WHITE_RGB;
            }
        }
        ImageIO.write(mat.toImage(), "png", outFile);
    }


}


