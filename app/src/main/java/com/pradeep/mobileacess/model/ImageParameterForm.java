package com.pradeep.mobileacess.model;

public class ImageParameterForm {
    private String mPath;
    private boolean isSelect;
    public ImageParameterForm(String path, boolean select){
        mPath = path;
        isSelect = select;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public String getmPath() {
        return mPath;
    }

    public void setmPath(String mPath) {
        this.mPath = mPath;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

}
