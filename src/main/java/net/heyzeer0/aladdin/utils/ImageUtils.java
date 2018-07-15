package net.heyzeer0.aladdin.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

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

    public static void drawStringWithSizeLimit(Graphics2D g, String text, int x, int y, int width) {
        FontMetrics metrics = g.getFontMetrics();
        float initSize = g.getFont().getSize();

        while(metrics.stringWidth(text) > width) {
            g.setFont(g.getFont().deriveFont(g.getFont().getSize() - 1f));
            metrics = g.getFontMetrics();
        }

        g.drawString(text, x, y + (initSize - g.getFont().getSize()));
        g.setFont(g.getFont().deriveFont(initSize));
    }

    public static BufferedImage getImageFromUrl(String url) throws Exception {
        System.setProperty("http.agent", "AladdinBOT");
        URL input = new URL(url);
        if (input == null) {
            return null;
        }

        URLConnection cnc = input.openConnection();
        cnc.setRequestProperty("User-Agent", "AladdinBOT");
        cnc.connect();

        InputStream istream = null;
        try {
            istream = cnc.getInputStream();
        } catch (IOException ignored) { return null; }
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

    public static BufferedImage scale(BufferedImage src, int w, int h)
    {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        int x, y;
        int ww = src.getWidth();
        int hh = src.getHeight();
        for (x = 0; x < w; x++) {
            for (y = 0; y < h; y++) {
                int col = src.getRGB(x * ww / w, y * hh / h);
                img.setRGB(x, y, col);
            }
        }
        return img;
    }

    public static void drawCenteredString(Graphics g, String text, Rectangle rect, Font font) {
        FontMetrics metrics = g.getFontMetrics(font);
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        g.setFont(font);
        g.drawString(text, x, y);
    }

}
