package com.aryomtech.dhitifoundation.slider.Model;

public class ModelSmoolider {

    private String image_url;
    private String head_text;
    private String des_text;
    private String pushkey;

    public ModelSmoolider() {
    }

    public void setPushkey(String pushkey) {
        this.pushkey = pushkey;
    }

    public String getPushkey() {
        return pushkey;
    }
    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public void setHead_text(String head_text) {
        this.head_text = head_text;
    }

    public void setDes_text(String des_text) {
        this.des_text = des_text;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getHead_text() {
        return head_text;
    }

    public String getDes_text() {
        return des_text;
    }
}
