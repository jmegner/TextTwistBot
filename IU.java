import java.util.*;
import java.awt.*;

public class IU {
    public static final double SQRT2 = Math.sqrt(2);

    public static final int BLACK_RGB   = 0xff000000;
    public static final int WHITE_RGB   = 0xffffffff;

    public static final int RED_RGB     = 0xffff0000;
    public static final int GREEN_RGB   = 0xff00ff00;
    public static final int BLUE_RGB    = 0xff0000ff;

    public static final int YELLOW_RGB  = 0xffffff00; //RED + GREEN
    public static final int MAGENTA_RGB = 0xffff00ff; //RED + BLUE
    public static final int CYAN_RGB    = 0xff00ffff; //GREEN + BLUE

    public static final int SGRAY_RGB   = 0xff010101;
    public static final int SRED_RGB    = 0xff010000;
    public static final int SGREEN_RGB  = 0xff000100;
    public static final int SBLUE_RGB   = 0xff000001;

    public static Rectangle getResolution(){
        GraphicsEnvironment graphicsEnvironment
            = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice graphicsDevice
            = graphicsEnvironment.getDefaultScreenDevice();
        DisplayMode displayMode = graphicsDevice.getDisplayMode();
        return new Rectangle(displayMode.getWidth(), displayMode.getHeight());
    }

    public static int toRgb(int r, int g, int b){
        r = Math.min(0xff, Math.max(0, r));
        g = Math.min(0xff, Math.max(0, g));
        b = Math.min(0xff, Math.max(0, b));
        return BLACK_RGB | (r << 16) | (g << 8) | b;
    }

    public static int getR(int rgb){
        return (rgb & 0x00ff0000) >> 16;
    }

    public static int getG(int rgb){
        return (rgb & 0x0000ff00) >> 8;
    }

    public static int getB(int rgb){
        return rgb & 0x000000ff;
    }

    public static int getRgbSum(int rgb){
        return getR(rgb) + getG(rgb) + getB(rgb);
    }

    public static double getRRatio(int rgb){
        return ((double)getR(rgb)) / getRgbSum(rgb);
    }

    public static double getGRatio(int rgb){
        return ((double)getG(rgb)) / getRgbSum(rgb);
    }

    public static double getBRatio(int rgb){
        return ((double)getB(rgb)) / getRgbSum(rgb);
    }

    //r=2 g=1 b=0
    public static int getColor(int rgb, int which){
        return (rgb & (0xff << (which * 8))) >> (which * 8);
    }

    public static int invertRgb(int rgb){
        int r = getR(rgb);
        int g = getG(rgb);
        int b = getB(rgb);
        return toRgb(0xff - r, 0xff - g, 0xff - b);
    }

    public static int rgbDiff(int rgb1, int rgb2){
        return Math.abs(getR(rgb1) - getR(rgb2))
            + Math.abs(getG(rgb1) - getG(rgb2))
            + Math.abs(getB(rgb1) - getB(rgb2));
    }

    public static int rgbDiff(RgbMat mat1, RgbMat mat2){
        assert(mat1.width == mat2.width && mat1.height == mat2.height);
        int diff = 0;
        for(int y = 0; y < mat1.height; y++){
            for(int x = 0; x < mat1.width; x++){
                diff += rgbDiff(mat1.get(x, y), mat2.get(x, y));
            }
        }
        return diff;
    }

    public static HashSet<Integer> rgbSet(RgbMat mat){
        HashSet<Integer> rgbs = new HashSet<Integer>();
        for(int x = 0; x < mat.width; x++){
            for(int y = 0; y < mat.height; y++){
                rgbs.add(mat.get(x, y));
            }
        }
        return rgbs;
    }

    public static HashSet<Integer> rgbSetForRow(RgbMat mat, int y){
        HashSet<Integer> rgbs = new HashSet<Integer>();
        for(int x = 0; x < mat.width; x++){
            rgbs.add(mat.get(x, y));
        }
        return rgbs;
    }

    public static HashSet<Integer> rgbSetForCol(RgbMat mat, int x){
        HashSet<Integer> rgbs = new HashSet<Integer>();
        for(int y = 0; y < mat.height; y++){
            rgbs.add(mat.get(x, y));
        }
        return rgbs;
    }

    public static HashMap<Integer,Integer> rgbHistogram(RgbMat mat){
        HashMap<Integer,Integer> rgbs = new HashMap<Integer,Integer>();
        for(int x = 0; x < mat.width; x++){
            for(int y = 0; y < mat.height; y++){
                int rgb = mat.get(x, y);
                if(rgbs.containsKey(rgb)){
                    rgbs.put(rgb, rgbs.get(rgb) + 1);
                }
                else{
                    rgbs.put(rgb, 1);
                }
            }
        }
        return rgbs;
    }

}

