import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;


public class ImageFinder {

    public static void main(String[] args)
        throws java.awt.AWTException, IOException
    {
        /*
        if(args.length < 2){
            System.out.println("supply wholePicture partPicture");
            System.exit(1);
        }

        BufferedImage whole = ImageIO.read(new File(args[0]));
        BufferedImage part = ImageIO.read(new File(args[1]));
        
        ImageFinder finder = new ImageFinder(whole, part);
        BufferedImage match = finder.matchImage();
        ImageIO.write(match, "png", new File("match.png"));
        */

        Robot robot = new Robot();
        BufferedImage whole = robot.createScreenCapture(IU.getResolution());
        BufferedImage part = ImageIO.read(new File("blot_158_82_80x80.png"));
        
        ImageFinder finder = new ImageFinder(whole, part);
        BufferedImage match = finder.getMatchImage();
        ImageIO.write(match, "png", new File("match.png"));
    }

///////////////////////////////////////////////////////////////////////////////

    public RgbMat WholeMat;
    public RgbMat PartMat;
    public HashSet<Integer> PartRgbSet;

    ImageFinder(BufferedImage wholeImage, BufferedImage partImage){
        init(new RgbMat(wholeImage), new RgbMat(partImage));
    }


    ImageFinder(RgbMat wholeMat, RgbMat partMat){
        init(wholeMat, partMat);
    }


    ImageFinder(RgbMat wholeMat, RgbMat partMat, HashSet<Integer> partRgbSet){
        init(wholeMat, partMat, partRgbSet);
    }


    public void init(RgbMat wholeMat, RgbMat partMat){
        init(wholeMat, partMat, IU.rgbSet(partMat));
    }


    public void init(RgbMat wholeMat, RgbMat partMat, HashSet<Integer> partRgbSet){
        WholeMat = wholeMat;
        PartMat = partMat;
        PartRgbSet = partRgbSet;
    }


    public BufferedImage getMatchImage(){
        Point offset = getOffset();
        RgbMat MatchMat = new RgbMat(WholeMat);
        for(int y = 0; y < PartMat.height; y++){
            for(int x = 0; x < PartMat.width; x++){
                MatchMat.set(offset.x + x, offset.y + y, IU.GREEN_RGB);
            }
        }
        return MatchMat.toImage();
    }


    public Point getOffset(){
        HashSet<Integer> partRgbSetForTopRow = IU.rgbSetForRow(PartMat, 0);

        boolean matchFound = false;
        for(int wsy = WholeMat.height - PartMat.height - 1; !matchFound && wsy >= 0; wsy--){
            //check if this row has ANY of part's colors
            HashSet<Integer> wholeRgbSetForRow = IU.rgbSetForRow(WholeMat, wsy);
            HashSet<Integer> partCopy = new HashSet<Integer>(PartRgbSet);
            partCopy.retainAll(wholeRgbSetForRow);
            //doesn't have ANY of the colors means we can skip up quite a bit
            if(partCopy.size() == 0){
                wsy -= PartMat.height - 1;
                continue;
            }

            //check if row has all of part's top row's colors
            HashSet<Integer> partTopRowCopy = new HashSet<Integer>(partRgbSetForTopRow);
            partTopRowCopy.retainAll(wholeRgbSetForRow);
            if(partTopRowCopy.size() < partRgbSetForTopRow.size()){
                continue;
            }

            for(int wsx = 0; !matchFound && wsx < WholeMat.width - PartMat.width;  wsx++){

                boolean blockDone = false;
                for(int px = PartMat.width - 1; !blockDone && px >= 0; px--){
                    for(int py = 0; !blockDone && py < PartMat.height; py++){
                        int wRgb = WholeMat.get(wsx + px, wsy + py);
                        if(!PartRgbSet.contains(wRgb)){
                            blockDone = true;
                            wsx += px;
                        }
                    }
                }

                boolean offsetFine = true;
                for(int px = 0; offsetFine && px < PartMat.width; px++){
                    for(int py = 0; offsetFine && py < PartMat.height; py++){
                        int wRgb = WholeMat.get(wsx + px, wsy + py);
                        int pRgb = PartMat.get(px, py);
                        if(wRgb != pRgb){
                            offsetFine = false;
                        }
                    }
                }
                if(offsetFine){
                    return new Point(wsx, wsy);
                }
            }
        }

        return null;
    }


    public boolean partAtOffset(Point offset){
        for(int y = 0; y < PartMat.height; y++){
            for(int x = 0; x < PartMat.width; x++){
                if(PartMat.get(x, y) != WholeMat.get(x + offset.x, y + offset.y)){
                    return false;
                }
            }
        }
        return true;
    }


}


