package com.pratilipi.editor;

import com.pratilipi.editor.database.MyDbHandler;
import com.pratilipi.editor.model.PageModel;
import com.pratilipi.editor.preferences.PagePreferences;
import com.pratilipi.editor.utils.CodeSnippet;

/**
 * Created by jeeva on 04/09/17.
 */
public class EditorPresenterImpl implements EditorPresenter, PageModelImpl.OnPageModelListener {

    private EditorView mEditorView;

    private PageModel mPageModel;

    private EditorPresenterImpl(MyDbHandler myDbHandler, PagePreferences pagePreferences) {
        this.mPageModel = PageModelImpl.newInstance(myDbHandler, pagePreferences, this);
    }

    public static EditorPresenter newInstance(MyDbHandler myDbHandler, PagePreferences pagePreferences) {
        return new EditorPresenterImpl(myDbHandler, pagePreferences);
    }

    @Override
    public void init(EditorView editorView) {
        this.mEditorView = editorView;

        mPageModel.loadInitialPage();
    }

    @Override
    public void onEditorUpdated(String content) {
        mEditorView.setWordCount(CodeSnippet.calculateWordCount(content));
    }

    @Override
    public void onSubmit(String content) {
        mPageModel.savePage(content);
        mEditorView.navigateToPreview(mPageModel.getPageId());
    }

    @Override
    public void onSave(String content) {
        mPageModel.savePage(content);
    }

    @Override
    public void onDiscard() {
        mPageModel.discardPage();
    }

    @Override
    public void destroy() {
        mEditorView = null;
        mPageModel.clearAllInstances();
        mPageModel = null;
    }

    @Override
    public void onGetPageContent(String htmlContent) {
        mEditorView.setEditorContent(htmlContent);
    }

    @Override
    public void onPageSaved() {
        mEditorView.showPageSavedMessage();
        mEditorView.startSync();
    }

    @Override
    public void onPageDiscarded() {
        mEditorView.showPageDiscardMessage();
    }
}
