package com.kimjinhwan.android.servernodejs;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

/**
 * Created by XPS on 2017-07-25.
 */

public interface IBbs {
    //서버 주소 끝에 '/' 꼭 넣어줄 것.
    public static final String SERVER = "http://192.168.10.248:4567/";

    //여러가지의 데이터를 가져와야 하기 때문에 제네릭에 Bbs가 아닌 List<Bbs>로.
    @GET("bbs")
    public Observable<ResponseBody> read();

    //Gson 컨버터를 사용하지 않고 있기 때문에
    //@Body 옆에 RequestBody나 String 타입으로 기재해야 함.
    @POST("bbs")
    public Observable<ResponseBody> write(@Body RequestBody bbs);

    @PUT("bbs")
    public Observable<ResponseBody> update(Bbs bbs);

    @DELETE("bbs")
    public Observable<ResponseBody> delete(Bbs bbs);


}
