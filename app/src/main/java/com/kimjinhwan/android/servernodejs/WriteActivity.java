package com.kimjinhwan.android.servernodejs;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class WriteActivity extends AppCompatActivity {

    EditText editTitle, editAuthor, editContent;
    Button btnPost;

    public static final int REFRESH_LIST = 1004;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        initView();



        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = editTitle.getText().toString();
                Log.i("Title", title+"");
                String author = editAuthor.getText().toString();
                String content = editContent.getText().toString();
                //셋 중에 하나라도 비어있을 때 버튼 누르면 데이터 포스팅 못함.
                if(!title.matches("") && !author.matches("") && !content.matches("")) {
                    postData(title, author, content);
                    Intent intent = new Intent(WriteActivity.this, MainActivity.class);
                    //WriteActivity로 오기 전 띄워져있던 MainActivity를 destroy하기 위해 intent에 flag를 추가한다.
                    //Intent.FLAG_ACTIVITY_CLEAR_TOP를 하면 WriteActivity에 갔다왔어도, MainAcitivity에서 back 버튼을 누를 시 바로 앱이 종료된다.
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    //실어보내는 인텐트, requestCode
                    //MainActivity의 onActivityResult에서
                    startActivityForResult(intent, REFRESH_LIST);
                } else {
                    Toast.makeText(WriteActivity.this, "제목, 작성자, 내용을 모두 입력하세요!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(WriteActivity.this, "취소되었습니다", Toast.LENGTH_SHORT).show();
        super.onBackPressed();
    }

    private void postData(String title, String author, String content){
        //0. 입력할 객체 생성
        Bbs bbs = new Bbs();
        bbs.title = title;
        Log.i("Title", title);
        bbs.author = author;
        Log.i("author", author);
        bbs.content = content;
        Log.i("content", content);

        //1. 레트로핏 생성

        //얘네 다 뭐하는거게~~??
        //GsonFactory를 이용하지 않는 방법.
        Retrofit client = new Retrofit.Builder().baseUrl(IBbs.SERVER)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build();



        //2. 서비스 연결

        IBbs IBbs = client.create(IBbs.class);

        //3. 서비스의 특정 함수 호출 -> Observable 생성
        //GsonFactory를 이용하지 않는 방법.

        //bbs 객체를 수동으로 전송하기 위해서는
        //bbs 객체를 json String으로 변환하고
        //RequestBody에 미디어타입과 String으로 변환된 데이터를 담아서 전송
        Gson gson = new Gson();
        RequestBody body = RequestBody.create(
                            //byteArray로 변환되어 전송된다?
                            MediaType.parse("application/json"),
                            gson.toJson(bbs)
                            );

        //"쓰기'를 위한 옵저버블 생성
        Observable<ResponseBody> observable = IBbs.write(body);

        //4. 옵저버블에 subscribe 등록
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        //responseBody : 받아오는 데이터.
                        responseBody -> {

                                    String result = responseBody.string(); // 결과 코드를 넘겨서 처리함.
                                    Log.i("Result", result); // 결과 : I/Result: {"result":"ok"}


                                /*
                                    1. 내가 정상적으로 새글을 입력한 후 데이터를 전송하고 종료
                                    ->onClick에서 startAcitivityForResult

                                    2. 새글을 전송하지 않고 화면을 그냥 종료
                                    -> onBackPressed를 override하여 ToastMessage를 출력함과 동시에 requestCode를 보내지 않음으로써 리스트가 갱신되지 않도록 함.
                                    (어차피 갱신 안되도 바로 직전 내용이기 때문에 무관하다고 봄.)


                                    1,2를 구분해서 결과값을 호출한 MainActivity로 넘겨서 처리
                                 */

                        }
                );
    }

    private void initView(){
        editTitle = (EditText) findViewById(R.id.editTitle);
        editAuthor = (EditText) findViewById(R.id.editAuthor);
        editContent = (EditText) findViewById(R.id.editContent);
        btnPost = (Button) findViewById(R.id.btnPost);
    }


}
