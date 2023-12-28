package de.jansauer.poeditor.entities

import java.io.Serializable

class Terms : Serializable {
    var updating: String? = null
    var file: String? = null
    var lang: String? = null
    var overwrite = false
    var sync_terms = false
}
