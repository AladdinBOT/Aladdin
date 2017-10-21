package net.heyzeer0.aladdin.utils;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by HeyZeer0 on 19/10/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class ImageUtils {

    public static void drawCenteredString(Graphics g, String text, BufferedImage rect, Font font) {
        FontMetrics metrics = g.getFontMetrics(font);
        int x = rect.getMinX() + (rect.getWidth() - metrics.stringWidth(text)) / 2;
        int y = rect.getMinY() + ((rect.getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
        g.setFont(font);
        g.drawString(text, x, y);
    }

    public static BufferedImage getImageFromUrl(String url) throws Exception {
        System.setProperty("http.agent", "AladdinBOT");
        URL input = new URL(url);
        if (input == null) {
            throw new IllegalArgumentException("input == null!");
        }

        InputStream istream = null;
        try {
            istream = input.openStream();
        } catch (IOException e) {
            throw new IIOException("Can't get input stream from URL!", e);
        }
        BufferedImage bi = ImageIO.read(istream);
        return bi;
    }

    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }

}
