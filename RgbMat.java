import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;


public class RgbMat {
    public int width;
    public int height;
    public int[] rgbs;

    public RgbMat(BufferedImage image){
        init(image.getWidth(), image.getHeight(), image.getRGB(0, 0,
            image.getWidth(), image.getHeight(), null, 0,
            image.getWidth()));
    }

    public RgbMat(int _width, int _height, int fillRgb){
        int[] _rgbs = new int[_width * _height];
        Arrays.fill(_rgbs, fillRgb);
        init(_width, _height, _rgbs);
    }

    public RgbMat(RgbMat other){
        init(other.width, other.height, other.rgbs);
    }

    public RgbMat(int w, int h, int[] _rgbs){
        init(w, h, _rgbs);
    }

    public RgbMat(RgbMat other, Rectangle subset){
        init(other, subset.x, subset.y, subset.width, subset.height);
    }

    public RgbMat(RgbMat other, int x, int y, int w, int h){
        init(other, x, y, w, h);
    }

    public void init(int _width, int _height, int[] _rgbs){
        width = _width;
        height = _height;
        rgbs = new int[width * height];
        for(int i = 0; i < _rgbs.length; i++){
            rgbs[i] = _rgbs[i];
        }
    }

    public void init(RgbMat other, int x, int y, int w, int h){
        width = w;
        height = h;
        rgbs = new int[width * height];
        for(int r = 0; r < height; r++){
            for(int c = 0; c < width; c++){
                set(c, r, other.get(c + x, r + y));
            }
        }
    }

    public BufferedImage toImage(){
        BufferedImage image = new BufferedImage(width, height,
            BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0, width, height, rgbs, 0, width);
        return image;
    }

    public int get(int x, int y){
        return rgbs[y * width + x];
    }

    public void set(int x, int y, int rgb){
        rgbs[y * width + x] = rgb;
    }
}


