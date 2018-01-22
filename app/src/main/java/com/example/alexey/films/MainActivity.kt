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

    private val adapter = MyAdapter() {
        toast(it.title)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initUI()
        loadFilms(false)
    }

    private fun initUI() {
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
                // Если ничего не введено, то выводим полный список фильмов
                if (p0.toString().length == 0) {
                    adapter.setListFilms(listFilms)
                } else {
                    searchInListFlms(p0.toString())
                }
            }
        })
    }

    /*
        Функция ищет в списке фильмов соответсвия вводу в поисковую строку и передаёт
        в адаптер полученниый списов фильмов
        В случае, если не найдено выводит на экран картинку и соответствуюющий текст
    */
    private fun searchInListFlms(textToSearch: String) {
        hideErrorTextAndImage()
        val cutomListFilm: ArrayList<Film> = ArrayList()
        for (film in listFilms) {
            if (film.title.toLowerCase().contains(textToSearch.toLowerCase())) {
                cutomListFilm.add(film)
            }
        }
        if (cutomListFilm.isEmpty()) {
            iv_error.setImageResource(R.drawable.ic_big_search)
            tv_error.text = "По запросу \"${textToSearch}\" ничего не найдено"
            showErrorTextAndImage()
        }
        adapter.setListFilms(cutomListFilm)

    }

    private fun refreshFilms() {
        refresh.isRefreshing = true
        loadFilms(true)
    }

    /*
    * Функция загружает данные о фильмах из интернета
    * Для обработки поворота экрана используется Loader
    * */
    private fun loadFilms(restart: Boolean) {
        hideErrorTextAndImage()
        val callbacks: LoaderManager.LoaderCallbacks<List<Film>> = FilmsCallback()
        if (restart) {
            refresh.isRefreshing = true
            supportLoaderManager.restartLoader(1, Bundle.EMPTY, callbacks)
        } else {
            supportLoaderManager.initLoader(1, Bundle.EMPTY, callbacks)
        }
    }


    inner class FilmsCallback : LoaderManager.LoaderCallbacks<List<Film>> {
        override fun onLoaderReset(loader: Loader<List<Film>>?) {   }
        override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<Film>> = RetrofitFilmsLoader(this@MainActivity)
        override fun onLoadFinished(loader: Loader<List<Film>>?, data: List<Film>?) { showFilms(data) }
    }

    private var listFilms: List<Film> = emptyList()
    private fun showFilms(listFilms: List<Film>?) {
        if (refresh.isRefreshing) {
            refresh.isRefreshing = false
        } else {
            hideProgressBar()
        }
        if (listFilms == null) {
            showError()
            return;
        }
        this.listFilms = listFilms

        adapter.setListFilms(listFilms)

    }

    override fun onPause() {
        super.onPause()
        val realm = Realm.getDefaultInstance()
        realm.close()
    }

    private fun showError() {
        iv_error.setImageResource(R.drawable.ic_alert_triangle)
        tv_error.text = "Не удалось обработать ваш запрос. Попробуйте ещё раз"
        showErrorTextAndImage()
        Snackbar.make(recycler_view, "Проверьте соединение с интернетом и попробуйте еще раз", Snackbar.LENGTH_LONG).show()
    }

    private fun showErrorTextAndImage() {
        iv_error.visibility = View.VISIBLE
        tv_error.visibility = View.VISIBLE
    }
    private fun hideErrorTextAndImage() {
        iv_error.visibility = View.GONE
        tv_error.visibility = View.GONE
    }

    private fun hideProgressBar() {
        progress.visibility = View.GONE
    }
}

