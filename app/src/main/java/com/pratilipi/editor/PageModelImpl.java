package com.pratilipi.editor;

import android.util.Log;

import com.pratilipi.editor.database.MyDbHandler;
import com.pratilipi.editor.database.Page;
import com.pratilipi.editor.model.PageModel;
import com.pratilipi.editor.preferences.PagePreferences;
import com.pratilipi.editor.rest.PageDto;
import com.pratilipi.editor.rest.RestServiceFactory;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.pratilipi.editor.utils.AppConstants.INVALID_ID;

/**
 * Created by jeeva on 04/09/17.
 */
public class PageModelImpl implements PageModel {

    private static final String TAG = PageModel.class.getSimpleName();

    private static final String PRATILIPI_ID = "5936574609489920";

    private int mPageId = INVALID_ID;

    private MyDbHandler mMyDbHandler;

    private PagePreferences mPagePreferences;

    private OnPageModelListener mPageModelListener;

    private PageModelImpl(MyDbHandler myDbHandler, PagePreferences pagePreferences,
                          OnPageModelListener pageModelListener) {
        mMyDbHandler = myDbHandler;
        mPagePreferences = pagePreferences;
        mPageModelListener = pageModelListener;
    }

    public static PageModel newInstance(MyDbHandler myDbHandler, PagePreferences pagePreferences,
                                        OnPageModelListener pageModelListener) {
        return new PageModelImpl(myDbHandler, pagePreferences, pageModelListener);
    }

    @Override
    public void loadInitialPage() {
        int pageId = mPagePreferences.getSavedPageId();
        if(pageId != INVALID_ID) {
            mPageId = pageId;

            Page page = mMyDbHandler.getContent(pageId);
            if(null != page) {
                Log.d(TAG, "Existing Page --> " + page.getContent());
                mPageModelListener.onGetPageContent(page.getContent());
            }
        } else {
            RestServiceFactory.getInstance().getPage(PRATILIPI_ID)
                    .enqueue(new Callback<PageDto>() {
                        @Override
                        public void onResponse(Call<PageDto> call, Response<PageDto> response) {
                            String htmlContent = response.body().getSummary();
                            savePage(htmlContent);
                            mPageModelListener.onGetPageContent(htmlContent);
                        }

                        @Override
                        public void onFailure(Call<PageDto> call, Throwable t) {}
                    });
        }
    }

    @Override
    public void savePage(String htmlContent) {
        Log.d(TAG, "Before Saving --> " + htmlContent);

        Page page = new Page();
        page.setContent(htmlContent);
        if(mPageId == INVALID_ID) { // Insert Page
            mPageId = mMyDbHandler.addContent(page);
            mPagePreferences.savePageId(mPageId);
        } else { // Update Page
            page.setPageId(mPageId);
            mMyDbHandler.updateContent(page);
        }

        mPagePreferences.saveSyncId(String.valueOf(mPageId));

        mPageModelListener.onPageSaved();
    }

    @Override
    public void discardPage() {
        mMyDbHandler.deleteContent(mPageId);
        mPagePreferences.clearPageId();
        mPageId = INVALID_ID;

        mPageModelListener.onPageDiscarded();
    }

    @Override
    public int getPageId() {
        return mPageId;
    }

    @Override
    public void clearAllInstances() {
        mMyDbHandler = null;
        mPagePreferences = null;
        mPageModelListener = null;
    }

    public interface OnPageModelListener {

        void onGetPageContent(String pageContent);

        void onPageSaved();

        void onPageDiscarded();
    }
}
