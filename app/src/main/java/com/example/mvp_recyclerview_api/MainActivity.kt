package com.example.mvp_recyclerview_api

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fetchAllUserData()
    }

    val recyclerView : RecyclerView = findViewById(R.id.recyclerView)

    //Clientを作成
    val httpBuilder: OkHttpClient.Builder
        get() {
            //httpClinetのBuilderを作る
            val httpClient = OkHttpClient.Builder()
            //create http client　headerの追加
            httpClient.addInterceptor(Interceptor { chain ->
                val original = chain.request()
                val request = original.newBuilder()
                        .header("Accept", "application/json")
                        .method(original.method, original.body)
                        .build()
                //proceedメソッドは再びパーミッション許可ダイアログを表示してその結果を返します

                return@Interceptor chain.proceed(request)
            })
                    .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)

            //log
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            httpClient.addInterceptor(loggingInterceptor)

            return httpClient
        }

    //繋ぎこみ
    fun createService(): QiitaApiInterface {
        var client = httpBuilder.build()
        var retrofit = Retrofit.Builder()
                .baseUrl("https://qiita.com/")//基本のurl設定
                .addConverterFactory(GsonConverterFactory.create())//Gsonの使用
                .client(client)//カスタマイズしたokhttpのクライアントの設定
                .build()
        //Interfaceから実装を取得
        var API = retrofit.create(QiitaApiInterface::class.java)

        return API
    }

    //データリストに保存し、そのデータの取得
    fun fetchAllUserData(): List<Model> {

        val dataList = mutableListOf<Model>()
        //リクエストURl作成してデータとる パラメータの引数の設定も行う
        this.createService().apiDemo(page = 1, perPage = 20).enqueue(object :
                Callback<List<QiitaResponse>> {

            //非同期処理
            override fun onResponse(call: Call<List<QiitaResponse>>, response: Response<List<QiitaResponse>>) {
                Log.d("TAGres","onResponse")

                //ステータスコードが200：OKなので、ここではちゃんと通信できたよ
                if (response.isSuccessful) {
                    response.body()?.let {
                        for (item in it) {
                            val data: Model = Model().also {
                                //取得したいものをAPIから手元のリスト（Model）に
                                it.title = item.title
                                it.url = item.url
                                it.id = item.user!!.id
                            }
                            //取得したデータをModelに追加
                            dataList.add(data)
                        }
                        //今回recyclerViewを利用しているが、これを書かないと先に画面の処理が終えてしまうので表示されなくなります。
                        Retrofit.recyclerView.adapter?.notifyDataSetChanged()
                    }!!
                } else {
                }
            }
            override fun onFailure(call: Call<List<QiitaResponse>>, t: Throwable) {
                Log.d("TAGres","onFailure")
            }
        })
        return dataList
    }
}