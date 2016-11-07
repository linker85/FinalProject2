package info.androidhive.navigationdrawer.retrofit_helpers;

import java.util.List;

import info.androidhive.navigationdrawer.models.CheckinMock;
import info.androidhive.navigationdrawer.models.User;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by raul on 04/11/2016.
 */

public class LoginRetrofitHelper {
    public static final String BASE_URL = "http://www.mocky.io";

    public static class Factory {
        public static Retrofit create() {
            return new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
                    .build();
        }

        public static Observable<List<User>> createLogin(String key) {
            Retrofit retrofit = create();
            LoginService loginService = retrofit.create(LoginService.class);
            return loginService.getUsers(key);
        }
        public static Observable<CheckinMock> createIsRegistered(String key) {
            Retrofit retrofit = create();
            LoginService checkedService = retrofit.create(LoginService.class);
            return checkedService.isChecked(key);
        }
    }

    public interface LoginService {
        @GET("/v2/{key}")
        Observable<List<User>> getUsers(@Path("key") String key);
        @GET("/v2/{key}")
        Observable<CheckinMock> isChecked(@Path("key") String key);
    }
}
