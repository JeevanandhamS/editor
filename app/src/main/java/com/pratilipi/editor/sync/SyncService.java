package com.pratilipi.editor.sync;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.pratilipi.editor.database.MyDbHandler;
import com.pratilipi.editor.database.Page;
import com.pratilipi.editor.preferences.PagePreferences;
import com.pratilipi.editor.rest.ImageUploadResponse;
import com.pratilipi.editor.rest.PageDto;
import com.pratilipi.editor.rest.RestServiceFactory;
import com.pratilipi.editor.utils.CodeSnippet;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SyncService extends Service {

    private static final String PRATILIPI_ID = "5936574609489920";

    private boolean mSyncing;

    private List<String> mSyncList = new ArrayList<>();

    private String mSyncId;

    private String mPageContent;

    private List<String> imagePathList;

    private PagePreferences mPagePreferences;

    private MyDbHandler mDbHandler;

    public static void start(Context context) {
        if(!CodeSnippet.isNetworkAvailable(context)) {
            return;
        }

        Intent intent = new Intent(context, SyncService.class);
        context.startService(intent);
    }

    public static void stop(Context context) {
        context.stopService(new Intent(context, SyncService.class));
    }

    public SyncService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPagePreferences = PagePreferences.newInstance(this);
        mDbHandler = MyDbHandler.newInstance(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!mSyncing) {
            startSync();
        }
        return START_STICKY;
    }

    private void startSync() {
        mSyncing = true;

        mSyncList = mPagePreferences.getSyncList();

        syncNext();
    }

    private void syncNext() {
        if(mSyncList.isEmpty()) {
            onSyncCompleted();
            return;
        }

        mSyncId = mSyncList.get(0);

        mPageContent = getPageContent(Integer.parseInt(mSyncId));
        if(null == mPageContent) {
            onPageSyncDone();
        } else {
            syncImageFileIfAvailable();
        }
    }

    private String getPageContent(int contentId) {
        Page content = mDbHandler.getContent(contentId);
        if(null != content) {
            return content.getContent();
        } return null;
    }

    private void syncImageFileIfAvailable() {
        imagePathList = CodeSnippet.getImagePath(mPageContent);
        uploadNextImage();
    }

    private void uploadNextImage() {
        if(imagePathList.isEmpty()) {
            syncPage();
            return;
        }

        syncImage();
    }

    private void syncPage() {
        PageDto pageDto = new PageDto(PRATILIPI_ID, mPageContent);
        RestServiceFactory.getInstance().updatePage(pageDto).enqueue(new Callback<PageDto>() {
            @Override
            public void onResponse(Call<PageDto> call, Response<PageDto> response) {
                mPagePreferences.deleteSyncId(mSyncId);

                onPageSyncDone();
            }

            @Override
            public void onFailure(Call<PageDto> call, Throwable t) {
                onPageSyncDone();
            }
        });
    }

    private void syncImage() {
        RestServiceFactory.getInstance().uploadImage(PRATILIPI_ID, new File(imagePathList.get(0)))
                .enqueue(new Callback<ImageUploadResponse>() {
                    @Override
                    public void onResponse(Call<ImageUploadResponse> call, Response<ImageUploadResponse> response) {
                        mPageContent.replace(imagePathList.get(0), getImageUrl(response.body().getName()));
                        onImageSyncDone();
                    }

                    @Override
                    public void onFailure(Call<ImageUploadResponse> call, Throwable t) {
                        onImageSyncDone();
                    }
                });
    }

    private void onPageSyncDone() {
        if(!mSyncList.isEmpty()) {
            mSyncList.remove(0);
        }
        syncNext();
    }

    private void onImageSyncDone() {
        if(!imagePathList.isEmpty()) {
            imagePathList.remove(0);
        }
        uploadNextImage();
    }

    private void onSyncCompleted() {
        mSyncing = false;
        stop(getApplicationContext());
    }

    private String getImageUrl(String imageName) {
        return "https://android.pratilipi.com/pratilipi/content/image?pratilipiId=" + PRATILIPI_ID
                + "&name=" + imageName + "&width=100";
    }
}