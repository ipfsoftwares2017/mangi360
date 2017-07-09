package com.ipfsoftwares.mangi360.model;

import android.net.Uri;

public class Transaction {
    private String mDisplayName;
    private String mMessage;
    private String mPhotoUrl;

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
