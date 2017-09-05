package com.pratilipi.editor;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.view.MenuItem;
import android.widget.TextView;

import com.pratilipi.editor.database.Page;
import com.pratilipi.editor.utils.AppConstants.BundleKeys;
import com.pratilipi.editor.database.MyDbHandler;
import com.pratilipi.editor.utils.CodeSnippet;

/**
 * Created by jeeva on 03/09/17.
 */
public class PreviewActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        initToolBar();

        Bundle bundle = getIntent().getExtras();
        if(null != bundle && bundle.containsKey(BundleKeys.PAGE_ID)) {
            setPreview(getHtmlContent(bundle.getInt(BundleKeys.PAGE_ID)));
        }
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setPreview(String htmlContent) {
        Spannable spannableContent = CodeSnippet.convertToSpan(htmlContent);

        TextView tvPreview = (TextView) findViewById(R.id.preview_tv_preview);
        tvPreview.setText(spannableContent, TextView.BufferType.SPANNABLE);
    }

    private String getHtmlContent(int pageId) {
        Page page = MyDbHandler.newInstance(this).getContent(pageId);
        return page.getContent();
    }
}