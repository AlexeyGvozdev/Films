package com.example.alexey.films

import android.content.Context
import android.support.v4.content.Loader;
import io.realm.Realm
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response




class RetrofitFilmsLoader(mContext: Context) : Loader<List<Film>>(mContext) {

    private var listFilms: List<Film>
    private var mCall: Call<Data>

    init {
        mCall = ApiFactory.getFulmsService().getFilms()
        listFilms = emptyList()
    }


    override fun onStartLoading() {
        super.onStartLoading()
        if(!listFilms.isEmpty()) {
            deliverResult(listFilms)
        } else {
            forceLoad()
        }
    }

    override fun onForceLoad() {
        super.onForceLoad()
        mCall.enqueue(object : Callback<Data> {
            override fun onResponse(call: Call<Data>, response: Response<Data>) {
                listFilms = response.body()?.results!!

                addFavoriteFromRealm()
                deliverResult(listFilms)
            }

            override fun onFailure(call: Call<Data>, t: Throwable) {
                deliverResult(null)
            }
        })
    }

    /*
    * Функция удаляет все Фильмы из БД, которые не избранные,
     * заполняет в списке фильмов флаги на избранность*/
    private fun addFavoriteFromRealm(){
        val realm = Realm.getDefaultInstance()
        val listFilmsRealm = realm.where(Film::class.java).findAll()

        realm.executeTransaction( {
            val result = realm.where(Film::class.java).equalTo("isFavorite", false).findAll()
            result.deleteAllFromRealm()
        })
        for (favorite in listFilmsRealm) {
            if (favorite.isFavorite) {
                for (x in listFilms) {
                    if (favorite.title.equals(x.title)) {
                        x.isFavorite = favorite.isFavorite
                    }
                }
            }
        }
    }

    override fun onStopLoading() {
        super.onStopLoading()
        mCall.cancel()
    }
}