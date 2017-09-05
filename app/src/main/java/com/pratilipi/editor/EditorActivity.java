package com.pratilipi.editor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Layout.Alignment;
import android.text.Spannable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;

import com.commonsware.cwac.richedit.BulletEffect;
import com.commonsware.cwac.richedit.Effect;
import com.commonsware.cwac.richedit.LineAlignmentEffect;
import com.commonsware.cwac.richedit.QuoteEffect;
import com.commonsware.cwac.richedit.RichEditText;
import com.pratilipi.editor.database.MyDbHandler;
import com.pratilipi.editor.preferences.PagePreferences;
import com.pratilipi.editor.utils.AppConstants.BundleKeys;
import com.pratilipi.editor.utils.CodeSnippet;

import java.util.List;
import java.util.Locale;

import static com.pratilipi.editor.utils.AppConstants.EMPTY;
import static com.pratilipi.editor.utils.AppConstants.ZERO;

/**
 * Created by jeeva on 03/09/17.
 */
public class EditorActivity extends AppCompatActivity implements EditorView {

    private RichEditText mEditor;

    private TextView mTvWordCount;

    private ImageView mIvLeftAlign;

    private ImageView mIvCenterAlign;

    private ImageView mIvRightAlign;

    private ImageView mIvBullet;

    private ImageView mIvBlockQuote;

    private String mWordCountPrefix;

    private EditorPresenter mEditorPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        initToolBar();

        initWordCount();

        initActions();

        initEditor();

        initPresenter();
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initWordCount() {
        mTvWordCount = (TextView) findViewById(R.id.editor_tv_word_count);
        mWordCountPrefix = getString(R.string.word_count_prefix);

        setWordCount(ZERO);
    }

    private void initActions() {
        mIvLeftAlign = (ImageView) findViewById(R.id.actions_ibtn_left);
        mIvCenterAlign = (ImageView) findViewById(R.id.actions_ibtn_center);
        mIvRightAlign = (ImageView) findViewById(R.id.actions_ibtn_right);
        mIvBullet = (ImageView) findViewById(R.id.actions_ibtn_bullet);
        mIvBlockQuote = (ImageView) findViewById(R.id.actions_ibtn_quote);
    }

    private void initEditor() {
        mEditor = (RichEditText) findViewById(R.id.editor);
        mEditor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                mEditorPresenter.onEditorUpdated(editable.toString());
            }
        });
        mEditor.setOnSelectionChangedListener(new RichEditText.OnSelectionChangedListener() {
            @Override
            public void onSelectionChanged(int start, int end, List<Effect<?>> effects) {
                boolean foundEffect = false;
                for (Effect<?> effect : effects) {
                    if(effect instanceof BulletEffect) {
                        setAlignmentEnabled(false);
                        setQuoteEnabled(false);
                        setBulletEnabled(true);

                        foundEffect = true;
                        break;
                    } else if(effect instanceof LineAlignmentEffect) {
                        boolean enabled = ((LineAlignmentEffect) effect).valueInSelection(mEditor) == Alignment.ALIGN_NORMAL;
                        setBulletEnabled(enabled);
                        setQuoteEnabled(enabled);
                        setAlignmentEnabled(true);

                        foundEffect = true;
                        break;
                    } else if(effect instanceof QuoteEffect) {
                        setAlignmentEnabled(false);
                        setBulletEnabled(false);
                        setQuoteEnabled(true);

                        foundEffect = true;
                        break;
                    }
                }

                if(!foundEffect) {
                    setAlignmentEnabled(true);
                    setQuoteEnabled(true);
                    setBulletEnabled(true);
                }
            }
        });
    }

    private void initPresenter() {
        mEditorPresenter = EditorPresenterImpl.newInstance(MyDbHandler.newInstance(this), PagePreferences.newInstance(this));
        mEditorPresenter.init(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_submit:
                mEditorPresenter.onSubmit(getEditorHtmlContent());
                return true;
            case R.id.menu_save:
                mEditorPresenter.onSave(getEditorHtmlContent());
                return true;
            case R.id.menu_discard:
                mEditor.setText(EMPTY);
                mEditorPresenter.onDiscard();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mEditorPresenter.destroy();
    }

    public void onClickBold(View view) {
        mEditor.toggleEffect(RichEditText.BOLD);
        removeSelection();
    }

    public void onClickItalic(View view) {
        mEditor.toggleEffect(RichEditText.ITALIC);
        removeSelection();
    }

    public void onClickUnderline(View view) {
        mEditor.toggleEffect(RichEditText.UNDERLINE);
        removeSelection();
    }

    public void onClickLeftAlign(View view) {
        mEditor.applyEffect(RichEditText.LINE_ALIGNMENT, Alignment.ALIGN_NORMAL);
        notifySelectionChange();
        removeSelection();
    }

    public void onClickCenterAlign(View view) {
        mEditor.applyEffect(RichEditText.LINE_ALIGNMENT, Alignment.ALIGN_CENTER);
        notifySelectionChange();
        removeSelection();
    }

    public void onClickRightAlign(View view) {
        mEditor.applyEffect(RichEditText.LINE_ALIGNMENT, Alignment.ALIGN_OPPOSITE);
        notifySelectionChange();
        removeSelection();
    }

    public void onClickBullet(View view) {
        mEditor.toggleEffect(RichEditText.BULLET);
        notifySelectionChange();
        removeSelection();
    }

    public void onClickBlockQuote(View view) {
        mEditor.toggleEffect(RichEditText.QUOTE);
        notifySelectionChange();
        removeSelection();
    }

    private void removeSelection() {
        mEditor.clearFocus();
    }

    private void notifySelectionChange() {
        mEditor.onSelectionChanged(mEditor.getSelectionStart(), mEditor.getSelectionEnd());
    }

    private void setAlignmentEnabled(boolean enabled) {
        setViewEnabled(mIvLeftAlign, enabled);
        setViewEnabled(mIvCenterAlign, enabled);
        setViewEnabled(mIvRightAlign, enabled);
    }

    private void setBulletEnabled(boolean enabled) {
        setViewEnabled(mIvBullet, enabled);
    }

    private void setQuoteEnabled(boolean enabled) {
        setViewEnabled(mIvBlockQuote, enabled);
    }

    private void setViewEnabled(View view, boolean enabled) {
        view.setEnabled(enabled);
        view.setAlpha(getAlphaByEnabled(enabled));
    }

    private float getAlphaByEnabled(boolean enabled) {
        return enabled ? 1f : 0.6f;
    }

    @Override
    public void showPageSavedMessage() {
        showMessage(R.string.page_saved);
    }

    @Override
    public void showPageDiscardMessage() {
        showMessage(R.string.page_discarded);
    }

    @Override
    public void setEditorContent(String htmlContent) {
        Spannable spannable = CodeSnippet.convertToSpan(htmlContent);
        mEditor.setText(spannable, BufferType.SPANNABLE);
    }

    @Override
    public void setWordCount(int wordCount) {
        mTvWordCount.setText(String.format(Locale.getDefault(), mWordCountPrefix, wordCount));
    }

    @Override
    public void navigateToPreview(int pageId) {
        Intent intent = new Intent(this, PreviewActivity.class);
        intent.putExtra(BundleKeys.PAGE_ID, pageId);
        startActivity(intent);
    }

    private String getEditorHtmlContent() {
        mEditor.clearComposingText();
        mEditor.clearFocus();
        return CodeSnippet.convertToHtml(mEditor.getEditableText());
    }

    private void showMessage(int msgResId) {
        Toast.makeText(this, msgResId, Toast.LENGTH_SHORT);
    }
}