package com.paygo.domain;

/**
 * class for user's credit card
 */
public class Card {

    private int cardId;
    private String firstName;
    private String lastName;
    private String cardNumber;
    private int expireMM;
    private int expireYY;
    private int cvv;

    public int getExpireMM() {
        return expireMM;
    }

    public void setExpireMM(int expireMM) {
        this.expireMM = expireMM;
    }

    public int getExpireYY() {
        return expireYY;
    }

    public void setExpireYY(int expireYY) {
        this.expireYY = expireYY;
    }

    public int getCvv() {
        return cvv;
    }

    public void setCvv(int cvv) {
        this.cvv = cvv;
    }

    private String cardType;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }


    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }
}
