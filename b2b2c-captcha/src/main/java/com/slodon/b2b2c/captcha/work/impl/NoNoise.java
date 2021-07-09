package com.slodon.b2b2c.captcha.work.impl;

import com.slodon.b2b2c.captcha.util.Configurable;
import com.slodon.b2b2c.captcha.work.NoiseProducer;

import java.awt.image.BufferedImage;

public class NoNoise extends Configurable implements NoiseProducer {
    /**
     *
     */
    public void makeNoise(BufferedImage image, float factorOne,
                          float factorTwo, float factorThree, float factorFour) {
        //Do nothing.
    }
}
