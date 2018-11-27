package com.example.user.testmlkit;

public class Upload {

    private String mName;
    private String mImageUrl;

    public Upload (){
        // empty constructor needed
    }

    public Upload(String name, String mImageUrl){

        if(name.trim().equals("")){
            name ="No Name";

        }

        this.mName = name;
        this.mImageUrl = mImageUrl;
    }

    public String getName(){
        return mName;
    }

    public void setName(String name){
        mName = name;
    }

    public String getImageUrl(){
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    @Override
    public String toString() {
        return "Upload{" +
                "mName='" + mName + '\'' +
                ", mImageUrl='" + mImageUrl + '\'' +
                '}';
    }
}
