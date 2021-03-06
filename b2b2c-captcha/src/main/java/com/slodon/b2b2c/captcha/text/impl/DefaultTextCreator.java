package com.slodon.b2b2c.captcha.text.impl;

import com.slodon.b2b2c.captcha.text.TextProducer;
import com.slodon.b2b2c.captcha.util.Configurable;

import java.security.SecureRandom;
import java.util.Random;

/**
 * {@link DefaultTextCreator} creates random text from an array of characters
 * with specified length.
 */
public class DefaultTextCreator extends Configurable implements TextProducer {
    /**
     * @return the random text
     */
    public String getText() {
        int length = getConfig().getTextProducerCharLength();
        char[] chars = getConfig().getTextProducerCharString();
        Random rand = new SecureRandom();
        StringBuffer text = new StringBuffer();
        for (int i = 0; i < length; i++) {
            text.append(chars[rand.nextInt(chars.length)]);
        }
        return text.toString();
    }
}
