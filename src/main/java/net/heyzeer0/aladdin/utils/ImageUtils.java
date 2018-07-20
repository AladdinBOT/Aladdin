package net.heyzeer0.aladdin.utils;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by HeyZeer0 on 19/10/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class ImageUtils {

    public static void drawStringWithSizeLimit(Graphics2D g, String text, int x, int y, int max_width) {
        FontMetrics metrics = g.getFontMetrics();
        float initSize = g.getFont().getSize();

        int initialHeight = metrics.getHeight();
        while(metrics.stringWidth(text) > max_width) {
            g.setFont(g.getFont().deriveFont(g.getFont().getSize() - 1f));
            metrics = g.getFontMetrics();
        }

        g.drawString(text, x, y - ((initialHeight - metrics.getHeight())/2));
        g.setFont(g.getFont().deriveFont(initSize));
    }

    public static BufferedImage getImageFromUrl(String url) throws Exception {
        return new Router(url).acceptImage().getResponse().asImage();
    }

    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }

    public static BufferedImage scale(BufferedImage src, int w, int h) {
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
