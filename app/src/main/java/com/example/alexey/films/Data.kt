package com.example.alexey.films

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey


class Data (val results: List<Film>)

open class Film : RealmObject() {
    @PrimaryKey var id: Int = 0
    var title: String = ""
    var poster_path: String = ""
    var overview: String = ""
    var release_date: String = ""
    var isFavorite: Boolean = false
        get() = field
        set(value) {
            field = value
        }
}
