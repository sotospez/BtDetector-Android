package com.sotosgr.btdetector;

public class SpinnerItem {

        private Integer item_title;
        private Integer item_id;

    public SpinnerItem() {
    }
    public SpinnerItem(Integer item_title, Integer item_id) {
        this.item_title = item_title;
        this.item_id = item_id;
    }

    public Integer getItem_title() {
        return item_title;
    }

    public void setItem_title(Integer item_title) {
        this.item_title = item_title;
    }

    public Integer getItem_id() {
        return item_id;
    }

    public void setItem_id(Integer item_id) {
        this.item_id = item_id;
    }

    @Override
    public String toString() {
        return item_title.toString() ;

    }
}
