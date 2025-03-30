package de.jansauer.poeditor

import de.jansauer.poeditor.entities.Terms
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

interface POEditorExtension {
    val apiKey: Property<String>
    val projectId: Property<String>
    val terms: ListProperty<Terms>
    val trans: ListProperty<LinkedHashMap<String, Any>>
}
