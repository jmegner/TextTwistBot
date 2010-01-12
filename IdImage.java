import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class IdImage {
    public static final int defaultDiffThresh = 20000000;
    public String fileName;
    public Point offset;
    public RgbMat mat;
    public HashSet<Integer> rgbSet;
    public Screen screen;
    public int diffThresh;

    public IdImage(String _fileName, Screen _screen, int xOffset, int yOffset)
        throws IOException
    {
        init(_fileName, _screen, xOffset, yOffset, defaultDiffThresh);
    }

    public IdImage(String _fileName, Screen _screen, int xOffset, int yOffset, int threshold)
        throws IOException
    {
        init(_fileName, _screen, xOffset, yOffset, threshold);
    }

    public void init(String _fileName, Screen _screen, int xOffset, int yOffset, int threshold)
        throws IOException
    {
        fileName = _fileName;
        screen = _screen;
        offset = new Point(xOffset, yOffset);
        mat = new RgbMat(ImageIO.read(new File(fileName)));
        rgbSet = IU.rgbSet(mat);
        diffThresh = threshold;
    }
}
