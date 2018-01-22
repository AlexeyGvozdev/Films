package com.example.alexey.films

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import io.realm.Realm



class MyAdapter(private val listener: (Film) -> Unit) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {


    private var listFilms: List<Film> = emptyList()

    fun setListFilms(list: List<Film>) {
        this.listFilms = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listFilms[position])
    }

    override fun getItemCount(): Int = listFilms.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item, parent, false)
        return ViewHolder(view, listener)
    }


    class ViewHolder(itemView: View,private val listener: (Film) -> Unit) : RecyclerView.ViewHolder(itemView) {
        val ivPoster: ImageView = itemView.findViewById(R.id.iv_poster)
        val cbFavorite: CheckBox = itemView.findViewById(R.id.iv_favorites)
        val tvName: TextView = itemView.findViewById(R.id.tv_name)
        val tvOverview: TextView = itemView.findViewById(R.id.tv_overview)
        val tvDate: TextView = itemView.findViewById(R.id.tv_date)

        fun bind(film: Film) {
            tvName.text = film.title
            tvDate.text = film.release_date
            tvOverview.text = film.overview
            Picasso.with(itemView.context).load("https://image.tmdb.org/t/p/w640${film.poster_path}").into(ivPoster)
            itemView.setOnClickListener{ listener(film) }
            cbFavorite.isChecked = film.isFavorite
            cbFavorite.setOnClickListener{ changeIVFavorite(cbFavorite.isChecked, film) }

        }

        private fun changeIVFavorite(checked: Boolean,film: Film) {
            film.isFavorite = checked
            val realm = Realm.getDefaultInstance()
            realm.beginTransaction()
            realm.copyToRealmOrUpdate(film)
            realm.commitTransaction()
            Log.d("My", film.title)

        }
    }
}