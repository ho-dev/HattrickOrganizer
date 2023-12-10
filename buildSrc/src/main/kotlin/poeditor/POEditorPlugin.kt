package de.jansauer.poeditor

import de.jansauer.poeditor.entities.Translation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import java.util.function.Consumer

class POEditorPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.pluginManager.apply(JavaPlugin::class.java)
        val extension = project.extensions.create("poeditor", POEditorExtension::class.java)

        project.tasks.register("poeditorPull", PullTask::class.java) { task: PullTask ->
            task.group = "poeditor"
            task.description = "Task pulling translation files from POEditor."
            task.apiKey.set(extension.apiKey)
            task.projectId.set(extension.projectId)
            extension.trans.get().forEach { linkedHashMap ->
                val t = Translation()
                t.file = linkedHashMap["file"].toString()
                t.lang = linkedHashMap["lang"].toString()
                t.type = linkedHashMap["type"].toString()
                t.tags = ArrayList()
                task.trans.add(t)
            }
        }
        project.tasks.register("poeditorPush", PushTask::class.java) { pushTask: PushTask ->
            pushTask.group = "poeditor"
            pushTask.description = "Task pushing translation files to POEditor."
            pushTask.apiKey.set(extension.apiKey)
            pushTask.projectId.set(extension.projectId)
            pushTask.terms.set(extension.terms)
        }
    }
}
