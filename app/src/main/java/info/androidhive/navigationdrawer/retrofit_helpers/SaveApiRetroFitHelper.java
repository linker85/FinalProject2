package info.androidhive.navigationdrawer.retrofit_helpers;

import info.androidhive.navigationdrawer.models.Success;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by raul on 05/11/2016.
 */

public class SaveApiRetroFitHelper {
    public static final String BASE_URL = "http://www.mocky.io";

    public static class Factory {
        public static Retrofit createSave() {
            return new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
                    .build();
        }
        public static Observable<Success> createSaveCard(String key) {
            Retrofit retrofit = createSave();
            SaveService saveService = retrofit.create(SaveService.class);
            return saveService.saveCard(key);
        }
        public static Observable<Success> createCheckInOut(String key) {
            Retrofit retrofit = createSave();
            SaveService saveService = retrofit.create(SaveService.class);
            return saveService.saveCheckInOut(key);
        }
        public static Observable<Success> createSaveUser(String key) {
            Retrofit retrofit = createSave();
            SaveService saveService = retrofit.create(SaveService.class);
            return saveService.saveUser(key);
        }
    }

    public interface SaveService {
        @GET("/v2/{key}")
        Observable<Success> saveCard(@Path("key") String key);
        @GET("/v2/{key}")
        Observable<Success> saveCheckInOut(@Path("key") String key);
        @GET("/v2/{key}")
        Observable<Success> saveUser(@Path("key") String key);
    }
}
