package com.ipfsoftwares.mangi360.model;

import android.net.Uri;

public class Transaction {
    private String mDisplayName;
    private String mMessage;
    private String mPhotoUrl;

    public Transaction(String displayName, String message) {
        this.mDisplayName = displayName;
        this.mMessage = message;
    }

    public String getDisplayName() {
        return this.mDisplayName;
    }

    public String getMessage() {
        return this.mMessage;
    }

    public Uri getPhotoUrl() {
       return Uri.parse(this.mPhotoUrl);
    }
}
