package by.bsuir.httpserver.entity;

import java.util.Date;

/**
 * Created by Dzmitry_Dydyshka on 11/27/2015.
 */
public class ResponceHeader {

    private int code;
    private String mime;

    public ResponceHeader(int code, String mime) {
        this.code = code;
        this.mime = mime;
    }

    public String getHeaderAsStirng(){

        StringBuffer buffer = new StringBuffer();
        buffer.append("HTTP/1.1 " + code + " " + getAnswer(code) + "\n");
        buffer.append("Date: " + new Date().toGMTString() + "\n");
        buffer.append("Accept-Ranges: none\n");
        if (mime != null && !mime.isEmpty()) {
            buffer.append("Content-Type: " + mime + "\n");
        }
        buffer.append("\n");
        return buffer.toString();

    }

    private String getAnswer(int code) {
        switch (code) {
            case 200:
                return "OK";
            case 404:
                return "Not Found";
            default:
                return "Internal Server Error";
        }
    }
}
