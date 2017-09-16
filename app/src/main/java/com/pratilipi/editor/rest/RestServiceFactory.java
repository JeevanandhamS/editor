package com.pratilipi.editor.rest;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by jeeva on 16-09-2016.
 */
public class RestServiceFactory extends BaseRestService {

    private static final String BASE_URL = "https://android.pratilipi.com/";

    private static final String ACCESS_TOKEN = "6b0f80a0-9811-4654-bbd1-cec1281a26c0";

    private static final int API_VERSION = 2;

    private static final int PAGE_NO = 1;

    private ConfigService mConfigService;

    private static RestServiceFactory INSTANCE;

    private RestServiceFactory() {
        mConfigService = createRetrofitWithGsonConverter(BASE_URL).create(ConfigService.class);
    }

    public static RestServiceFactory getInstance() {
        if(null == INSTANCE) {
            synchronized (RestServiceFactory.class) {
                if(null == INSTANCE) {
                    INSTANCE = new RestServiceFactory();
                }
            }
        }
        return INSTANCE;
    }

    public interface ConfigService {
        @GET("pratilipi")
        Call<PageDto> getPage(@Header("AccessToken") String accessToken,
                              @Query("_apiVer") int apiVersion,
                              @Query("pratilipiId") String pratilipiId);

        @POST("pratilipi")
        Call<PageDto> updatePage(@Header("AccessToken") String accessToken,
                        @Query("_apiVer") int apiVersion,
                        @Body PageDto page);

        // api/v2/tickets - helpdesk/tickets.json
        @Multipart
        @POST("pratilipi/content/image")
        Call<ImageUploadResponse> uploadImage(
                @Query("AccessToken") String accessToken,
                @Query("pratilipiId") String pratilipiId,
                @Query("pageNo") int pageNo,
                @Part MultipartBody.Part image);
    }

    public Call<PageDto> getPage(String pratilipiId) {
        return mConfigService.getPage(ACCESS_TOKEN, API_VERSION, pratilipiId);
    }

    public Call<PageDto> updatePage(PageDto updatedPage) {
        return mConfigService.updatePage(ACCESS_TOKEN, API_VERSION, updatedPage);
    }

    public Call<ImageUploadResponse> uploadImage(String pratilipiId, File file) {
        RequestBody fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("upload", file.getName(), fileBody);

        return mConfigService.uploadImage(ACCESS_TOKEN, pratilipiId, PAGE_NO, body);
    }
}