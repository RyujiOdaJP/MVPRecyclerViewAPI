package com.example.mvp_recyclerview_api

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

//データリストに保存し、そのデータの取得
fun fetchAllUserData(): List<Model> {

    val dataList = mutableListOf<Model>()
    //リクエストURl作成してデータとる パラメータの引数の設定も行う
    Retrofit.createService().apiDemo(page = 1, perPage = 20).enqueue(object :
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
                    recyclerView.adapter?.notifyDataSetChanged()
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