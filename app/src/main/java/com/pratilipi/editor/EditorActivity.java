package com.pratilipi.editor;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;

import com.pratilipi.editor.database.MyDbHandler;
import com.pratilipi.editor.preferences.PagePreferences;
import com.pratilipi.editor.sync.SyncService;
import com.pratilipi.editor.utils.AppConstants.BundleKeys;
import com.pratilipi.editor.utils.CodeSnippet;
import com.pratilipi.editor.utils.FileUtils;
import com.pratilipi.editor.utils.URLImageParser;

import java.io.File;
import java.util.Locale;

import static com.pratilipi.editor.utils.AppConstants.EMPTY;
import static com.pratilipi.editor.utils.AppConstants.ZERO;

/**
 * Created by jeeva on 03/09/17.
 */
public class EditorActivity extends AppCompatActivity implements EditorView {

    private static final int PICK_SCREENSHOT = 23;

    private EditText mEditor;

    private TextView mTvWordCount;

    private ImageView mIvLeftAlign;

    private ImageView mIvCenterAlign;

    private ImageView mIvRightAlign;

    private ImageView mIvBullet;

    private ImageView mIvBlockQuote;

    private String mWordCountPrefix;

    private URLImageParser mImageParser;

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

        checkStoragePermission();
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
        mEditor = (EditText) findViewById(R.id.editor);
        mImageParser = new URLImageParser(mEditor, this);
        mEditor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
//                mEditorPresenter.onEditorUpdated(editable.toString());
            }
        });

        /*mEditor.setOnSelectionChangedListener(new RichEditText.OnSelectionChangedListener() {
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
        });*/
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
                save();
                return true;
            case R.id.menu_discard:
                mEditor.setText(EMPTY);
                mEditorPresenter.onDiscard();
                return true;
            case R.id.menu_add_image:
                onClickImageButton();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void save() {
        mEditorPresenter.onSave(getEditorHtmlContent());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        save();
        mEditorPresenter.destroy();
    }

    public void onClickBold(View view) {
//        mEditor.toggleEffect(RichEditText.BOLD);
        removeSelection();
    }

    public void onClickItalic(View view) {
//        mEditor.toggleEffect(RichEditText.ITALIC);
        removeSelection();
    }

    public void onClickUnderline(View view) {
//        mEditor.toggleEffect(RichEditText.UNDERLINE);
        removeSelection();
    }

    public void onClickLeftAlign(View view) {
//        mEditor.applyEffect(RichEditText.LINE_ALIGNMENT, Alignment.ALIGN_NORMAL);
        notifySelectionChange();
        removeSelection();
    }

    public void onClickCenterAlign(View view) {
//        mEditor.applyEffect(RichEditText.LINE_ALIGNMENT, Alignment.ALIGN_CENTER);
        notifySelectionChange();
        removeSelection();
    }

    public void onClickRightAlign(View view) {
//        mEditor.applyEffect(RichEditText.LINE_ALIGNMENT, Alignment.ALIGN_OPPOSITE);
        notifySelectionChange();
        removeSelection();
    }

    public void onClickBullet(View view) {
//        mEditor.toggleEffect(RichEditText.BULLET);
        notifySelectionChange();
        removeSelection();
    }

    public void onClickBlockQuote(View view) {
//        mEditor.toggleEffect(RichEditText.QUOTE);
        notifySelectionChange();
        removeSelection();
    }

    private void removeSelection() {
        mEditor.clearFocus();
    }

    private void notifySelectionChange() {
//        mEditor.onSelectionChanged(mEditor.getSelectionStart(), mEditor.getSelectionEnd());
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
        CodeSnippet.getImagePath(htmlContent);

        Spannable spannable = new SpannableString(Html.fromHtml(htmlContent, mImageParser, null));
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

    @Override
    public void startSync() {
        SyncService.start(this);
    }

    private String getEditorHtmlContent() {
        mEditor.clearComposingText();
        mEditor.clearFocus();
        return CodeSnippet.convertToHtml(mEditor.getEditableText());
    }

    private void showMessage(int msgResId) {
        Toast.makeText(this, msgResId, Toast.LENGTH_SHORT);
    }

    public void onClickImageButton() {
        if(!checkStoragePermission()) {
            return;
        }

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        try {
            startActivityForResult(Intent.createChooser(intent, "Pick Image"), PICK_SCREENSHOT);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            showMessage(R.string.image_pick_failed);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /**
         * get the selected screenshot image and pass it to help section
         */
        if (requestCode == PICK_SCREENSHOT
                && resultCode == Activity.RESULT_OK
                && data != null) {
            Uri selectedImage = data.getData();
            addImage(selectedImage);
        }
    }

    private void addImage(final Uri selectedImage) {
        int position = getCursorPosition();

        File file = FileUtils.getFile(this, selectedImage);

//        Spanned imageSpan = Html.fromHtml("<br><img src=\"" + "http://awallpapersimages.com/wp-content/uploads/2016/09/Sachin-Tendulkar-HD-Wallpapers-1.jpg" + "\">", mImageParser, null);
        Spanned imageSpan = Html.fromHtml("<br><img src=\"" + file.getAbsolutePath() + "\"></img>", mImageParser, null);
        mEditor.getText().insert(position, imageSpan);
    }

    private int getCursorPosition() {
        return android.text.Selection.getSelectionStart(mEditor.getEditableText());
    }

    private boolean checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {
                        android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        1
                );
                return false;
            }
        }
        return true;
    }
}