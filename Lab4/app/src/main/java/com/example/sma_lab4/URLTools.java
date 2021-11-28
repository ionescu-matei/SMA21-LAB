package com.example.sma_lab4;

import static java.lang.Thread.sleep;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;

public class URLTools {

    public static String getLongUrl(String shortUrl) throws MalformedURLException, IOException {
        String result = shortUrl;
        String header;
        int index;
        do {
            URL url = new URL(result);
            URLConnection conn = url.openConnection();
            header = conn.getHeaderField(null);
            URL location = conn.getURL();
            if (location != null) {
                result = location.getFile();
            }
        } while (header.contains("301"));

        // also decode URL
        result = URLDecoder.decode(result, "UTF-8");
        // trim to extract bitmap
        index = result.indexOf("imgurl=");
        if (index >= 0)
            result = result.substring(index + "imgurl=".length());
        index = result.indexOf("&");
        if (index >= 0)
            result = result.substring(0, index);

        return result;
    }
}