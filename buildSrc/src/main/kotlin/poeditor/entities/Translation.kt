package de.jansauer.poeditor.entities

import java.io.Serializable

class Translation : Serializable {
    var lang: String? = null
    var type: String? = null
    var file: String? = null
    var tags: List<String>? = null
}
