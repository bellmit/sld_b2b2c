package com.slodon.b2b2c.core.decoration;

/**
 * 首页弹出广告
 */
public class DecoFirstAdv extends DecoImg {

    private boolean showSwitch;
    private String showRadioSele;

    public boolean isShowSwitch() {
        return showSwitch;
    }

    public void setShowSwitch(boolean showSwitch) {
        this.showSwitch = showSwitch;
    }

    public String getShowRadioSele() {
        return showRadioSele;
    }

    public void setShowRadioSele(String showRadioSele) {
        this.showRadioSele = showRadioSele;
    }
}
