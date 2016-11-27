package info.androidhive.navigationdrawer.retrofit_helpers;

import info.androidhive.navigationdrawer.models.CheckinMock;
import info.androidhive.navigationdrawer.models.LoginResponse;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by raul on 04/11/2016.
 */

public class LoginRetrofitHelper {
    public static final String BASE_URL = "http://52.15.174.153:8080";

    public static class Factory {
        public static Retrofit create() {
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

        public static Observable<LoginResponse> createLogin(String email, String password, String userId) {
            Retrofit retrofit = create();
            LoginService loginService = retrofit.create(LoginService.class);
            return loginService.getLogin(email, password, userId);
        }
        public static Observable<CheckinMock> createHasCheckIn(String email) {
            Retrofit retrofit = create();
            LoginService checkedService = retrofit.create(LoginService.class);
            return checkedService.isChecked(email);
        }
        public static Observable<CheckinMock> createUpdateUserId(String email, String userId) {
            Retrofit retrofit = create();
            LoginService checkedService = retrofit.create(LoginService.class);
            return checkedService.updateUserId(email, userId);
        }
    }

    public interface LoginService {
        @GET("/pushAWS/rest/users_service/login.do")
        Observable<LoginResponse> getLogin(@Query("email") String email,
                                           @Query("password") String password,
                                           @Query("userId") String userId);
        @GET("/pushAWS/rest/users_service/isChecked.do")
        Observable<CheckinMock> isChecked(@Query("email") String email);
        @GET("/pushAWS/rest/users_service/updateUserId.do")
        Observable<CheckinMock> updateUserId(@Query("email") String email, @Query("userId") String userId);
    }
}