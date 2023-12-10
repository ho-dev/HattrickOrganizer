package de.jansauer.poeditor

import de.jansauer.poeditor.entities.Translation
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkerExecutor
import java.nio.file.Paths
import java.util.function.Consumer
import java.util.stream.Collectors
import javax.inject.Inject

abstract class PullTask @Inject constructor() : DefaultTask() {
    @get:Input
    abstract val apiKey: Property<String?>

    @get:Input
    abstract val projectId: Property<String?>

    @get:Input
    abstract val trans: ListProperty<Translation>

    @get:Inject
    abstract val workerExecutor: WorkerExecutor

    init {
        description = "Download translations from POEditor."
        group = "poeditor"
    }

    @get:OutputFiles
    val outOfDateOutputs: ConfigurableFileCollection
        get() = project.files(
            trans.get().stream().map { translation: Translation -> Paths.get(translation.file) }
                .collect(Collectors.toList())
        )

    @TaskAction
    fun pullTranslations() {
        val workQueue = workerExecutor.noIsolation()
        trans.get().forEach(
            Consumer { translation: Translation ->
                workQueue.submit(PullRunnable::class.java) { params: PullWorkParameters ->
                    params.projectId.set(projectId.get())
                    params.apiKey.set(apiKey.get())
                    params.lang.set(translation.lang)
                    params.file.set(project.file(translation.file))
                    params.tags.addAll(translation.tags)
                    params.type.set(translation.type)
                }
            }
        )
    }
}
