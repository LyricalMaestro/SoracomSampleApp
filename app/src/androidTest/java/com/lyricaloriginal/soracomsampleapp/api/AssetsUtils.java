package com.lyricaloriginal.soracomsampleapp.api;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LyricalMaestro on 2016/01/05.
 */
class AssetsUtils {

    private AssetsUtils() {
    }

    static List<String> readAssetsText(Context context, String assetName) throws IOException {
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        List<String> lines = new ArrayList<String>();
        try {
            is = context.getAssets().open(assetName);
            isr = new InputStreamReader(is, "UTF-8");
            br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } finally {
            closeQuietly(br);
            closeQuietly(isr);
            closeQuietly(is);
        }
        return lines;
    }

    private static void closeQuietly(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private static void closeQuietly(Reader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
