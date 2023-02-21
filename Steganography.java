import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

public class Steganography {
    public static void main(String[] args) {
        //Picture beach = new Picture("beach.jpg");
        //beach.explore();
        //Picture copy = testClearLow(beach);
        //copy.explore();

        //Picture beach2 = new Picture("beach.jpg");
        //beach2.explore();
        //Picture copy2 = testSetLow(beach2, Color.PINK);
        //copy2.explore();

        //Picture copy3 = revealPicture(copy2);
        //copy3.explore();

        Picture beach = new Picture("beach.jpg");
        Picture beach2 = new Picture("beach.jpg");

        Picture mark = new Picture("blue-mark.jpg");
        Picture hidden = hidePicture(beach, mark, 100, 100);

        hidden.explore();
        revealPicture(hidden).explore();

        showDifferentArea(hidden, findDifferences(beach, hidden)).explore();
    }

    public static void clearLow(Pixel p) {
        p.setBlue((p.getBlue() >> 2) << 2);
        p.setGreen((p.getGreen() >> 2) << 2);
        p.setRed((p.getRed() >> 2) << 2);
    }

    public static Picture testClearLow(Picture p) {
        for (int i = 0; i < p.getHeight(); i++) {
            for (int j = 0; j < p.getWidth(); j++) {
                clearLow(p.getPixel(j, i));
            }
        }

        return p;
    }

    public static void setLow(Pixel p, Color c) {
        clearLow(p);
        p.setBlue(p.getBlue() + (c.getBlue() >> 6));
        p.setGreen(p.getGreen() + (c.getGreen() >> 6));
        p.setRed(p.getRed() + (c.getRed() >> 6));
    }

    public static Picture testSetLow(Picture p, Color c) {
        for (int i = 0; i < p.getHeight(); i++) {
            for (int j = 0; j < p.getWidth(); j++) {
                setLow(p.getPixel(j, i), c);
            }
        }

        return p;
    }

    public static Picture revealPicture(Picture hidden) {
        Picture copy = new Picture(hidden);
        Pixel[][] pixels = copy.getPixels2D();
        Pixel[][] source = hidden.getPixels2D();
        for (int r = 0; r < pixels.length; r++) {
            for (int c = 0; c < pixels[0].length; c++ /* nice */) {
                Color col = source[r][c].getColor();
                pixels[r][c].setColor(new Color((col.getRed() - ((col.getRed() >> 2) << 2)) << 6, (col.getGreen() - ((col.getGreen() >> 2) << 2)) << 6, (col.getBlue() - ((col.getBlue() >> 2) << 2)) << 6));
            }
        }
        return copy;
    }

    public static boolean canHide(Picture source, Picture secret) {
        return (secret.getWidth() <= source.getWidth() && secret.getHeight() <= source.getHeight());
    }

    public static Picture hidePicture(Picture source, Picture secret) {
        Picture copy = new Picture(source);

        for (int i = 0; i < copy.getHeight(); i++) {
            for (int j = 0; j < copy.getWidth(); j++) {
                setLow(copy.getPixel(j, i), secret.getPixel(j, i).getColor());
            }
        }

        return copy;
    }

    public static Picture hidePicture(Picture source, Picture secret, int startRow, int startColumn) {
        Picture copy = new Picture(source);

        for (int i = startRow; i < Math.min(startRow + secret.getHeight(), source.getHeight() - 1); i++) {
            for (int j = startColumn; j < Math.min(startColumn + secret.getWidth(), source.getWidth() - 1); j++) {
                setLow(copy.getPixel(j, i), secret.getPixel(j - startColumn, i - startRow).getColor());
            }
        }

        return copy;
    }

    public static boolean isSame(Picture p1, Picture p2) {
        Pixel[][] pixels1 = p1.getPixels2D();
        Pixel[][] pixels2 = p2.getPixels2D();

        for (int i = 0; i < pixels1.length; i++) {
            for (int j = 0; j < pixels1[0].length; j++) {
                if (!pixels1[i][j].getColor().equals(pixels2[i][j].getColor())) {
                    return false;
                }
            }
        }

        return true;
    }

    public static ArrayList<Point> findDifferences(Picture p1, Picture p2) {
        ArrayList<Point> ret = new ArrayList<Point>();

        Pixel[][] pixels1 = p1.getPixels2D();
        Pixel[][] pixels2 = p2.getPixels2D();

        for (int i = 0; i < pixels1.length; i++) {
            for (int j = 0; j < pixels1[0].length; j++) {
                if (!pixels1[i][j].getColor().equals(pixels2[i][j].getColor())) {
                    ret.add(new Point(j, i));
                }
            }
        }

        return ret;
    }

    public static Picture showDifferentArea(Picture p, ArrayList<Point> points) {
        Picture copy = new Picture(p);

        int leastX = copy.getWidth(), mostX = 0, leastY = copy.getHeight(), mostY = 0;
        for (Point pt : points) {
            leastX = Math.min(pt.x, leastX);
            leastY = Math.min(pt.y, leastY);
            mostX = Math.max(pt.x, mostX);
            mostY = Math.max(pt.y, mostY);
        }

        for (int i = leastY; i < mostY; i++) {
            if (i == leastY || i == mostY - 1) {
                for (int j = leastX; j < mostX; j++) {
                    copy.setBasicPixel(j, i, 0);
                }
            } else {
                copy.setBasicPixel(leastX, i, 0);
                copy.setBasicPixel(mostX, i, 0);
            }
        }

        return copy;
    }
}
