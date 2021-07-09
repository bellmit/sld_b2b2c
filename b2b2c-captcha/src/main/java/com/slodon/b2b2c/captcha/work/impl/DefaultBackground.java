package com.slodon.b2b2c.captcha.work.impl;

import com.slodon.b2b2c.captcha.util.Configurable;
import com.slodon.b2b2c.captcha.work.BackgroundProducer;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class DefaultBackground extends Configurable implements BackgroundProducer {
    /**
     * @param baseImage the base image
     * @return an image with a gradient background added to the base image.
     */
    public BufferedImage addBackground(BufferedImage baseImage) {
        Color colorFrom = getConfig().getBackgroundColorFrom();
        Color colorTo = getConfig().getBackgroundColorTo();

        int width = baseImage.getWidth();
        int height = baseImage.getHeight();

        // create an opaque image
        BufferedImage imageWithBackground = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);

        Graphics2D graph = (Graphics2D) imageWithBackground.getGraphics();
        RenderingHints hints = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_OFF);

        hints.add(new RenderingHints(RenderingHints.KEY_COLOR_RENDERING,
                RenderingHints.VALUE_COLOR_RENDER_QUALITY));
        hints.add(new RenderingHints(RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY));

        hints.add(new RenderingHints(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY));

        graph.setRenderingHints(hints);

        GradientPaint paint = new GradientPaint(0, 0, colorFrom, width, height,
                colorTo);
        graph.setPaint(paint);
        graph.fill(new Rectangle2D.Double(0, 0, width, height));

        // draw the transparent image over the background
        graph.drawImage(baseImage, 0, 0, null);

        return imageWithBackground;
    }
}
