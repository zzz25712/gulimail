package com.itdemo.common.constant;

public class ProductConstant {

    public enum attrtype{

        ATTR_TYPE_BASE(1,"base"),ATTR_TYPE_SALE(0,"sale");
        private int code;
        private String msg;

        attrtype(int code,String msg){
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }

    }
    public enum productStatus{

        NEW_PRO(0,"新建"),PRO_UP(1,"上架"),PRO_DOWN(2,"下架");
        private int code;
        private String msg;

        productStatus(int code,String msg){
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }

    }

}
