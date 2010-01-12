import java.util.*;
import java.text.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*; //KeyEvent, InputEvent
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;


public class TextTwist {

    public static enum Zone {
        NORMAL, TELL, RUN, TERMINATE,
    };


    public static final int DefaultTimeBetweenActionsMs = 250;
    public static final int ZonePixelLen = 50;
    public static Dimension GameDim = new Dimension(500, 400);
    public static Rectangle BigLetterZone = new Rectangle(163, 181, 315, 45);
    public static Point ClearPoint = new Point(450, 250);

    public static Robot MyRobot;
    public static Rectangle GameRectangle;
    public static File TmpDir;

    public static IdImage[] OffsetImages;
    public static IdImage TwistEnabled;
    public static IdImage Start;
    public static IdImage Go;
    public static IdImage QualifyNoGo;
    public static IdImage Return;
    public static IdImage TStem;

    public static TreeMap<String,int[]> WordMap;

    public static HashSet<Integer> BigLetterRgbSet;
    public static File[] BigLetterFiles = {
        new File("big_e.png"),
        new File("big_r.png"),
    };

    public static File DictFile = new File("text_twist_words.txt");


    public static void main(String[] args) throws IOException, AWTException {
        MyRobot = new Robot();
        TmpDir = new File("tmp");

        System.out.println("getting and processing word list");
        WordMap = new TreeMap<String,int[]>(new LengthComparator());
        addDictToWordMap(DictFile, WordMap);

        System.out.println("initializing big letter data");
        initBigLetterStuff();

        System.out.println("processing helper pngs");
        processHelperPngs();

        System.out.println("determining game rectangle");
        GameRectangle = determineGameRectangle();
        if(GameRectangle == null){
            System.out.println("could not find game; make sure that level"
                + " select screen is visible");
            System.exit(1);
        }
        else{
            RgbMat gameMat = getGameMat();
            System.out.println("we seem to be on the "
                + determineScreen(gameMat) + " screen");
            ImageIO.write(gameMat.toImage(), "png", new File("game.png"));
        }

        System.out.println("ready");

        int timeBetweenActionsMs = DefaultTimeBetweenActionsMs;

        Zone lastZone = Zone.NORMAL;
        Screen lastScreen = Screen.UNKNOWN;
        ArrayList<String> subwords = null;
        int subwordsIndex = -1;

        while(true) {
            try { Thread.sleep(timeBetweenActionsMs); }
            catch(InterruptedException e) {}

            timeBetweenActionsMs = DefaultTimeBetweenActionsMs;

            Point mousePoint = MouseInfo.getPointerInfo().getLocation();
            Zone zone = mouseInWhichZone(mousePoint, ZonePixelLen);

            if(zone.equals(Zone.RUN)){
                RgbMat gameMat = getGameMat();
                Screen screen = determineScreen(gameMat);

                if(!zone.equals(lastZone)){
                    System.out.println("inside run zone: " + screen);
                }

                if(!screen.equals(lastScreen)){
                    System.out.println("screen: " + screen);
                }

                if(screen.equals(Screen.PLAY)){
                    RgbMat bigLetterMat = new RgbMat(gameMat, BigLetterZone);
                    RgbMat blackLetterMat = bigLettersToBlack(bigLetterMat);
                    String rawLetters = getLetters(blackLetterMat);
                    String letters = correctLetters(rawLetters);
                    int[] letterHist = letterHistogram(letters);

                    if(letters.length() < 6){
                        gameClickAndReturn(ClearPoint);
                    }
                    else{
                        if(subwords == null || subwords.size() == 0
                            || subwordsIndex >= subwords.size()
                            || overlap(letterHistogram(subwords.get(0)),
                            letterHistogram(letters)) < letters.length())
                        {
                            subwords = new ArrayList<String>(getSubwords(letters));
                            subwordsIndex = 0;
                        }

                        String subword = subwords.get(subwordsIndex);
                        System.out.println(rawLetters + " => " + letters + ": "
                            + subword);
                        subwordsIndex++;

                        gameClickAndReturn(TStem.offset);
                        enterWord(subword);
                    }

                }
                else if(screen.equals(Screen.START)){
                    gameClickAndReturn(Start.offset);
                }
                else if(screen.equals(Screen.QUALIFY_WITH_GO)){
                    gameClickAndReturn(Go.offset);
                }
                else if(screen.equals(Screen.LOST)){
                    gameClickAndReturn(Return.offset);
                }

                lastScreen = screen;
            }
            else{
                lastScreen = null;
                if(lastZone.equals(Zone.RUN)){
                    System.out.println("outside run zone");
                }
            }

            if(zone.equals(Zone.TELL)){
                if(!zone.equals(lastZone)){
                }
            }
            if(zone.equals(Zone.TERMINATE)){
                System.out.println("inside terminate zone - terminating");
                break;
            }

            lastZone = zone;
        }
    }


    public static void processHelperPngs() throws IOException {
        TwistEnabled = new IdImage(
            "twist_enabled_167_243_60x13.png", Screen.PLAY, 167, 243);
        Start = new IdImage(
            "start_264_218_60x13.png", Screen.START, 264, 218);
        Go = new IdImage(
            "go_237_205_30x13.png", Screen.QUALIFY_WITH_GO, 237, 205);
        QualifyNoGo = new IdImage(
            "qualify_no_go_212_157_25x64.png", Screen.QUALIFY_NO_GO, 212, 157);
        Return = new IdImage(
            "return_296_266_52x13.png", Screen.LOST, 296, 266);
        TStem = new IdImage(
            "t_stem_363_36_19x45.png", Screen.UNKNOWN, 363, 36);

        OffsetImages = new IdImage[] {
            TwistEnabled, Start, Go, QualifyNoGo, Return, TStem,
        };
    }


    public static Rectangle determineGameRectangle() throws IOException {
        BufferedImage screenImage = MyRobot.createScreenCapture(IU.getResolution());
        RgbMat screenMat = new RgbMat(screenImage);

        for(int i = 0; i < OffsetImages.length; i++){
            ImageFinder finder = new ImageFinder(screenMat,
                OffsetImages[i].mat, OffsetImages[i].rgbSet);
            Point findPoint = finder.getOffset();
            if(findPoint != null){
                return new Rectangle(findPoint.x - OffsetImages[i].offset.x,
                    findPoint.y - OffsetImages[i].offset.y,
                    GameDim.width, GameDim.height);
            }
        }
        return null;
    }


    public static Screen determineScreen(RgbMat gameMat){
        for(int i = 0; i < OffsetImages.length; i++){
            ImageFinder finder = new ImageFinder(gameMat,
                OffsetImages[i].mat, OffsetImages[i].rgbSet);
            boolean match = finder.partAtOffset(OffsetImages[i].offset);
            if(match){
                return OffsetImages[i].screen;
            }
        }

        return Screen.ERROR;
    }


    public static void addDictToWordMap(File dictFile,
        TreeMap<String,int[]> wordMap)
        throws IOException
    {
        BufferedReader reader = new BufferedReader(new FileReader(dictFile));
        String line = null;
        while((line = reader.readLine()) != null){
            line = line.toLowerCase();
            wordMap.put(line, letterHistogram(line));
        }
    }


    public static void initBigLetterStuff() throws IOException {
        BigLetterRgbSet = new HashSet<Integer>();

        for(File file : BigLetterFiles){
            HashSet<Integer> rgbSet = IU.rgbSet(new RgbMat(ImageIO.read(file)));
            for(int rgb : rgbSet){
                int r = IU.getR(rgb);
                int b = IU.getB(rgb);
                if(b > 5 * r){
                    BigLetterRgbSet.add(rgb);
                }
            }
        }
    }


    public static RgbMat bigLettersToBlack(RgbMat inMat){
        RgbMat outMat = new RgbMat(inMat.width, inMat.height, IU.WHITE_RGB);
        for(int i = 0; i < inMat.rgbs.length; i++){
            if(BigLetterRgbSet.contains(inMat.rgbs[i])){
                outMat.rgbs[i] = IU.BLACK_RGB;
            }
        }
        return outMat;
    }


    public static String getLetters(RgbMat mat) throws IOException {
        String lettersBaseName = "letters.png";
        File lettersFile = new File(TmpDir, lettersBaseName);
        ImageIO.write(mat.toImage(), "png", lettersFile);

        Runtime run = Runtime.getRuntime();
        String[] cmdArray = { "gocr", lettersFile.getPath() };
        Process proc = run.exec(cmdArray);
        try{proc.waitFor(); } catch(Exception e){}

        StringBuilder letters = new StringBuilder();

        BufferedReader reader = new BufferedReader(new InputStreamReader(
            proc.getInputStream()));
        String line = null;
        while((line = reader.readLine()) != null){
            letters.append(line);
        }

        return letters.toString();
    }

    public static String correctLetters(String letters){
        return letters
            .replaceAll("l", "I")
            .replaceAll("v", "Y")
            .replaceAll(" ", "");
    }


    public static TreeSet<String> getSubwords(String letters){
        TreeSet<String> words = new TreeSet<String>(
            new ReverseLengthComparator());
        int[] hist = letterHistogram(letters);
        for(Map.Entry<String,int[]> entry : WordMap.entrySet()){
            String word = entry.getKey();
            int[] wordHist = entry.getValue();
            if(overlap(wordHist, hist) == word.length()){
                words.add(word);
            }
        }
        return words;
    }


    public static int[] letterHistogram(String str){
        int[] letters = new int[26];
        for(int i = 0; i < str.length(); i++){
            char ch = str.charAt(i);
            if(Character.isLetter(ch)){
                letters[Character.toLowerCase(ch) - 'a']++;
            }
        }
        return letters;
    }


    public static int overlap(int[] lhs, int[] rhs){
        int sum = 0;
        for(int i = 0; i < lhs.length; i++){
            sum += Math.min(lhs[i], rhs[i]);
        }
        return sum;
    }


    public static Zone mouseInWhichZone(Point mousePoint, int zonePixelLen){
        Rectangle resolution = IU.getResolution();
        if(mousePoint.x < zonePixelLen && mousePoint.y < zonePixelLen){
            return Zone.RUN;
        }
        else if(mousePoint.x >= (resolution.width - zonePixelLen)
            && mousePoint.y < zonePixelLen)
        {
            return Zone.TELL;
        }
        else if(mousePoint.x >= (resolution.width - zonePixelLen)
            && mousePoint.y >= (resolution.height - zonePixelLen))
        {
            return Zone.TERMINATE;
        }
        return Zone.NORMAL;
    }


    public static RgbMat getGameMat() throws AWTException, IOException {
        return new RgbMat(getGameImage());
    }


    public static BufferedImage getGameImage() throws AWTException, IOException {
        return MyRobot.createScreenCapture(GameRectangle);
    }


    public static void enterWord(String word){
        for(int i = 0; i < word.length(); i++){
            char ch = word.charAt(i);
            if(Character.isLetter(ch)){
                press(Character.toUpperCase(ch));
            }
        }
        press(KeyEvent.VK_ENTER);
    }


    public static void press(int key){
        MyRobot.keyPress(key);
        MyRobot.keyRelease(key);
    }


    public static void gameClickAndReturn(Point gameClickPoint){
        Point returnPoint = MouseInfo.getPointerInfo().getLocation();
        Point absoluteClickPoint = new Point(gameClickPoint.x + GameRectangle.x,
            gameClickPoint.y + GameRectangle.y);

        moveMouse(absoluteClickPoint);
        leftClick();
        moveMouse(returnPoint);
    }


    public static void moveMouse(Point point){
        MyRobot.mouseMove(point.x, point.y);
    }


    public static void leftClick(){
        MyRobot.mousePress(InputEvent.BUTTON1_MASK);
        MyRobot.mouseRelease(InputEvent.BUTTON1_MASK);
    }


    public static class LengthComparator implements Comparator<String> {
        public int compare(String lhs, String rhs){
            int lenComp = lhs.length() - rhs.length();
            if(lenComp != 0){
                return lenComp;
            }
            return lhs.compareTo(rhs);
        }
    }


    public static class ReverseLengthComparator implements Comparator<String> {
        public int compare(String lhs, String rhs){
            int lenComp = lhs.length() - rhs.length();
            if(lenComp != 0){
                return -lenComp;
            }
            return lhs.compareTo(rhs);
        }
    }


}
