package info.androidhive.navigationdrawer.retrofit_helpers;

import info.androidhive.navigationdrawer.models.Success;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by raul on 05/11/2016.
 */

public class SaveApiRetroFitHelper {
    public static final String BASE_URL = "http://52.15.174.153:8080";

    public static class Factory {
        public static Retrofit createSave() {
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            clientBuilder.addInterceptor(loggingInterceptor);

            return new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(clientBuilder.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
                    .build();
        }
        public static Observable<Success> createSaveCard(String key) {
            Retrofit retrofit = createSave();
            SaveService saveService = retrofit.create(SaveService.class);
            return saveService.saveCard(key);
        }
        public static Observable<Success> createCheckInOut(String email, int type) {
            Retrofit retrofit = createSave();
            SaveService saveService = retrofit.create(SaveService.class);
            return saveService.saveCheckInOut(email, type);
        }
        public static Observable<Success> createUser(String email, String password, String name, String plate, int type) {
            Retrofit retrofit = createSave();
            SaveService saveService = retrofit.create(SaveService.class);
            return saveService.saveUser(email, password, name, plate, type);
        }
    }

    public interface SaveService {
        @GET("/v2/{key}")
        Observable<Success> saveCard(@Path("key") String key);
        @GET("/pushAWS/rest/users_service/checkInOut1.do")
        Observable<Success> saveCheckInOut(@Query("email") String email,
                                           @Query("type") int type);
        @GET("/pushAWS/rest/users_service/registerUser.do")
        Observable<Success> saveUser(@Query("email") String email,
                                     @Query("password") String password,
                                     @Query("name") String name,
                                     @Query("plate") String plate,
                                     @Query("type") int type);
    }
}