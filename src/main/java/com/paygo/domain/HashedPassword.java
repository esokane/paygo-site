package com.paygo.domain;

/**
 * class for coded password with salt
 */
public class HashedPassword {
    
    String pass;
    String salt;

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    
}
