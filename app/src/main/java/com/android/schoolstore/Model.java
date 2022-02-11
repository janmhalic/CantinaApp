package com.android.schoolstore;

public class Model {
    private int storeId;
    private String storeName;
    private String storeAddress;
    private byte[] storePicture;
    private String storeContact;
    private String storeEmail;
    private String storeOpening;
    private String storeAdd;
    private String shipping;
    //constructor


    public Model(int storeId, String storeName, String storeAddress, byte[] storePicture, String storeContact, String storeEmail, String storeOpening, String storeAdd, String shipping) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.storeAddress = storeAddress;
        this.storePicture = storePicture;
        this.storeContact = storeContact;
        this.storeEmail = storeEmail;
        this.storeOpening = storeOpening;
        this.storeAdd = storeAdd;
        this.shipping = shipping;
    }

    //getter and setter method

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
    }

    public byte[] getStorePicture() {
        return storePicture;
    }

    public void setStorePicture(byte[] storePicture) {
        this.storePicture = storePicture;
    }

    public String getStoreContact() {
        return storeContact;
    }

    public void setStoreContact(String storeContact) {
        this.storeContact = storeContact;
    }

    public String getStoreEmail() {
        return storeEmail;
    }

    public void setStoreEmail(String storeEmail) {
        this.storeEmail = storeEmail;
    }

    public String getStoreOpening() {
        return storeOpening;
    }

    public void setStoreOpening(String storeOpening) {
        this.storeOpening = storeOpening;
    }

    public String getStoreAdd() {
        return storeAdd;
    }

    public void setStoreAdd(String storeAdd) {
        this.storeAdd = storeAdd;
    }

    public String getShipping() {
        return shipping;
    }

    public void setShipping(String shipping) {
        this.shipping = shipping;
    }
}
