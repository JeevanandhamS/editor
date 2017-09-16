package com.pratilipi.editor.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.pratilipi.editor.utils.AppConstants.ZERO;

/**
 * Created by jeeva on 03/09/17.
 */
public class CodeSnippet {

    public static String convertToHtml(Spanned spannableContent) {
        /*SpannedXhtmlGenerator gen = new SpannedXhtmlGenerator();
        String htmlContent = gen.toXhtml(spannableContent);
        if(null == htmlContent) {
            return EMPTY;
        }
        return formatHtmlContent(htmlContent);*/

        return Html.toHtml(spannableContent);
    }

    public static Spannable convertToSpan(String htmlContent) {
        /*Spannable spannableContent = new SpannableString(EMPTY);

        try {
            spannableContent = new SpannableStringGenerator().fromXhtml(htmlContent);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return spannableContent;*/
        return new SpannableString(Html.fromHtml(htmlContent));
    }

    public static int calculateWordCount(String content) {
        if(null != content) {
            content = content.trim(); // It will remove the leading and trailing spaces
        }

        if(!TextUtils.isEmpty(content)) {
            return content.split("\\s+").length;
        }

        return ZERO;
    }

    public static void hideSoftKeyBoard(View view) {
        try {
            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static String formatHtmlContent(String htmlContent) {
        return htmlContent
                .replaceAll("align=\"center\"", "style=\"text-align:CENTER\"")
                /*.replaceAll("<div", "<p")
                .replaceAll("</div>", "</p>")*/;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static Drawable drawableFromUrl(String url) {
        try {
            Bitmap x;

            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.connect();
            InputStream input = connection.getInputStream();

            x = BitmapFactory.decodeStream(input);
            return new BitmapDrawable(x);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Drawable drawableFromFile(Resources res, String path) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);

        if(null == bitmap) {
            return null;
        }
        return new BitmapDrawable(res, bitmap);
    }

    public static List<String> getImagePath(String pageContent) {
        List<String> imagePaths = new ArrayList<>();

        Document parse = Jsoup.parse(pageContent);
        Elements imgElements = parse.body().getElementsByTag("img");
        String src;
        if(imgElements.size() > 0) {
            for (Element img : imgElements) {
                src = img.attr("src");
                if(!src.startsWith("http")) {
                    imagePaths.add(src);
                }
                Log.d("SyncService", src);
            }
        }
        return imagePaths;
    }
}