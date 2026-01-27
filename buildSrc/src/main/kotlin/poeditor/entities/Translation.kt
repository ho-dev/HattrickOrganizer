package de.jansauer.poeditor.entities

import java.io.Serializable

class Translation : Serializable {
    lateinit var lang: String
    lateinit var type: String
    lateinit var file: String
    var tags: List<String> = emptyList()
}
