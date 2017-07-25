package com.kimjinhwan.android.servernodejs;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.constraint.solver.SolverVariable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import static com.kimjinhwan.android.servernodejs.WriteActivity.REFRESH_LIST;


public class MainActivity extends AppCompatActivity {

    private Button btnWrite;
    private RecyclerView recyclerView;
    private List<Bbs> data;
    private RecyclerAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        lambdaTest();
        data = new ArrayList<>();
        setAdapter();

        loader();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REFRESH_LIST){
         adapter.notifyDataSetChanged();
        }
    }

    private void initView(){
        btnWrite = (Button) findViewById(R.id.btnWrite);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, WriteActivity.class);
                startActivity(intent);

            }
        });
    }

    private void setAdapter(){
        adapter = new RecyclerAdapter(this, data);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.notifyDataSetChanged();


    }

    private void lambdaTest(){
        new Thread(
        () -> Log.i("Lambda","running==================Ok")
        ).start();
    }

    private void loader(){
        //1. 레트로핏 생성

        //얘네 다 뭐하는거게~~??
        //GsonFactory를 이용하지 않는 방법.
        Retrofit client = new Retrofit.Builder().baseUrl(IBbs.SERVER)
                                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build();


        //2. 서비스 연결

        IBbs IBbs = client.create(IBbs.class);

        //3. 서비스의 특정 함수 호출 -> Observable 생성
        //GsonFactory를 이용하지 않는 방법.
        Observable<ResponseBody> observable = IBbs.read();

        //4. subscribe 등록
        observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            responseBody -> {
                                //1. 데이터를 꺼냄.
                                //responseBody는 Stream이므로 한 번 사용하고 나면 사라짐.(읽고 나면 close 되어버림.)
                                String jsonString = responseBody.string();
                                Gson gson = new Gson();
                                //컨버팅 하기 위한 타입 지정 (나중에 List<Bbs>만 바꿔주면 됨.)
                                Type type = new TypeToken<List<Bbs>>() {}.getType();
                                //Gson 컨버터로 data에 들어감.
                                //List<Bbs> data = gson.fromJson(jsonString, type);
                                //Bbs를 배열로 하여 데이터를 넣는 방법도 있음.
                                Bbs data[] = gson.fromJson(jsonString, Bbs[].class);

                                //2. 데이터를 아답터에 세팅하고
                                for(Bbs bbs : data){
                                   this.data.add(bbs);
                                }

                                //3. 아답터 갱신
                                adapter.notifyDataSetChanged();
                            }
                    );
    }







    //public void adapterChange(){
    //    adapter.notifyDataSetChanged();
    //}

}
