package de.jansauer.poeditor

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.workers.WorkParameters
import java.io.File

/**
 * Parameters to be passed to the [org.gradle.workers.WorkQueue] action.
 */
interface PullWorkParameters : WorkParameters {
    val apiKey: Property<String?>
    val projectId: Property<String?>
    val lang: Property<String?>
    val type: Property<String?>
    val file: Property<File?>
    val tags: ListProperty<String?>
}
