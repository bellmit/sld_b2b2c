package com.slodon.b2b2c.captcha.work;

import java.awt.image.BufferedImage;

/**
 * Responsible for creating captcha image with a text drawn on it.
 */
public interface Producer {
    /**
     * Create an image which will have written a distorted text.
     *
     * @param text the distorted characters
     * @return image with the text
     */
    public BufferedImage createImage(String text);

    /**
     * @return the text to be drawn
     */
    public abstract String createText();

    /**
     * @param type   1-存数字, 2-纯字母, 3-字母数字组合, 4-汉字
     * @param length
     * @return the text to be drawn
     */
    public abstract String createText(int type, int length);

}