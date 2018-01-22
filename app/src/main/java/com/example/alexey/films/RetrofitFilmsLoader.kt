package com.example.alexey.films

import android.content.Context
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log
import io.realm.Realm
import io.realm.kotlin.delete
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.function.Predicate

import io.realm.RealmResults



/**
 * Created by Alexey on 22.01.2018.
 */
class RetrofitFilmsLoader(mContext: Context) : Loader<List<Film>>(mContext) {

    var listFilms: List<Film>
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
                Log.d("My", "tuta")
            }

            override fun onFailure(call: Call<Data>, t: Throwable) {
                deliverResult(null)
            }
        })
    }

    private fun addFavoriteFromRealm(){
        val realm = Realm.getDefaultInstance()
        val listFilmsRealm = realm.where(Film::class.java).findAll()

        realm.executeTransaction( {
            val result = realm.where(Film::class.java).equalTo("isFavorite", false).findAll()
            result.deleteAllFromRealm()
        })
        for (favorite in listFilmsRealm) {
            if (favorite.isFavorite) {
                Log.d("Myrealm", "${favorite.title} : ${favorite.isFavorite}")
                for (x in listFilms) {
                    if (favorite.title.equals(x.title)) {
                        x.isFavorite = favorite.isFavorite
                        Log.d("Myrealm", "${x.title} : ${x.isFavorite}")
                    }
                }
            } else {


            }

        }

    }

    override fun onStopLoading() {
        super.onStopLoading()
        mCall.cancel()
    }
}