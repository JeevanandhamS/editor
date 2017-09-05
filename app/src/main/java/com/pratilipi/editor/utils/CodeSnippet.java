package com.pratilipi.editor.utils;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.commonsware.cwac.richtextutils.SpannableStringGenerator;
import com.commonsware.cwac.richtextutils.SpannedXhtmlGenerator;

import org.xml.sax.SAXException;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import static com.pratilipi.editor.utils.AppConstants.EMPTY;
import static com.pratilipi.editor.utils.AppConstants.ZERO;

/**
 * Created by jeeva on 03/09/17.
 */
public class CodeSnippet {

    public static String convertToHtml(Spanned spannableContent) {
        SpannedXhtmlGenerator gen = new SpannedXhtmlGenerator();
        String htmlContent = gen.toXhtml(spannableContent);
        if(null == htmlContent) {
            return EMPTY;
        }
        return formatHtmlContent(htmlContent);
    }

    public static Spannable convertToSpan(String htmlContent) {
        Spannable spannableContent = new SpannableString(EMPTY);

        try {
            spannableContent = new SpannableStringGenerator().fromXhtml(htmlContent);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return spannableContent;
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
}