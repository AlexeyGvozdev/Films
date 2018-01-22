package com.example.alexey.films

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.Editable
import android.text.TextWatcher
import io.realm.Realm
import org.jetbrains.anko.toast
import java.util.ArrayList


class MainActivity : AppCompatActivity() {


    val adapter = MyAdapter() {
        toast(it.title)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recycler_view.adapter = adapter
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.setHasFixedSize(true)
        refresh.setOnRefreshListener { refreshFilms() }
        refresh.setColorSchemeResources(R.color.themeLoadOrRefresh)
        et_search.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().length == 0) {
                    adapter.setListFilms(listFilms)
                } else {
                    searchInListFlms(p0.toString())
                }
            }


        })

        loadFilms(false)
    }

    private fun searchInListFlms(textToSearch: String) {
        iv_error.visibility = View.GONE
        tv_error.visibility = View.GONE
        val cutomListFilm: ArrayList<Film> = ArrayList()
        for (film in listFilms) {
            if (film.title.toLowerCase().contains(textToSearch.toLowerCase())) {
                cutomListFilm.add(film)
            }
        }
        if (cutomListFilm.isEmpty()) {
            iv_error.setImageResource(R.drawable.ic_big_search)
            tv_error.text = "По запросу \"${textToSearch}\" ничего не найдено"
            iv_error.visibility = View.VISIBLE
            tv_error.visibility = View.VISIBLE
        }
        adapter.setListFilms(cutomListFilm)

    }

    private fun refreshFilms() {
        refresh.isRefreshing = true
        loadFilms(true)
    }

    private fun loadFilms(restart: Boolean) {
        iv_error.visibility = View.GONE
        tv_error.visibility = View.GONE
        val callbacks: LoaderManager.LoaderCallbacks<List<Film>> = FilmsCallback()
        if (restart) {
            refresh.isRefreshing = true
            supportLoaderManager.restartLoader(1, Bundle.EMPTY, callbacks)
        } else {
            supportLoaderManager.initLoader(1, Bundle.EMPTY, callbacks)
        }
    }



    private fun closeProgressBar() {
        progress.visibility = View.GONE
    }

    inner class FilmsCallback : LoaderManager.LoaderCallbacks<List<Film>> {
        override fun onLoaderReset(loader: Loader<List<Film>>?) {

        }

        override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<Film>> = RetrofitFilmsLoader(this@MainActivity)

        override fun onLoadFinished(loader: Loader<List<Film>>?, data: List<Film>?) {
            showFilms(data)
        }
    }

    private var listFilms: List<Film> = emptyList()
    private fun showFilms(listFilms: List<Film>?) {
        if (refresh.isRefreshing) {
            refresh.isRefreshing = false
        } else {
            closeProgressBar()
        }
        if (listFilms == null) {
            showError()
            return;
        }
        this.listFilms = listFilms
//        val realm = Realm.getDefaultInstance()
//        realm.beginTransaction()
//        realm.insert(listFilms)
//        realm.commitTransaction()
        var text = "Звё"

        adapter.setListFilms(listFilms)

        progress.visibility = View.GONE
    }

    override fun onPause() {
        super.onPause()
        val realm = Realm.getDefaultInstance()
        realm.close()
    }

    private fun showError() {
        iv_error.setImageResource(R.drawable.ic_alert_triangle)
        iv_error.visibility = View.VISIBLE
        tv_error.text = "Не удалось обработать ваш запрос. Попробуйте ещё раз"
        tv_error.visibility = View.VISIBLE
        Snackbar.make(recycler_view, "Проверьте соединение с интернетом и попробуйте еще раз", Snackbar.LENGTH_LONG).show()
    }
}

