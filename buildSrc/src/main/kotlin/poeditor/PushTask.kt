package de.jansauer.poeditor

import de.jansauer.poeditor.entities.Terms
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction
import java.nio.file.Paths
import java.util.stream.Collectors

abstract class PushTask : DefaultTask() {
    @get:Input
    abstract val apiKey: Property<String?>

    @get:Input
    abstract val projectId: Property<String?>

    @get:Input
    abstract val terms: ListProperty<Terms>

//    @get:OutputFile
//    abstract val output: File?

    @get:InputFiles
    val outOfDateInputs: ConfigurableFileCollection
        get() = project.files(
            terms.get().stream().map { translation: Terms -> Paths.get(translation.file) }
                .collect(Collectors.toList())
        )

    // return project.files(*terms.get().collect {Paths.get(it.file)})
    @TaskAction
    fun pushTerms() {
        // @See https://poeditor.com/docs/api#projects_upload
//    logger.debug("Pushing '{}' sets of terms", getTerms().get().size());
//
//    terms.get().each { // TODO: Handle more then one term set
//      logger.debug("Pushing language '{}' from '{}' to project '{}'", it.lang, it.file, projectId.get())
//      def updating = it.updating
//      def termsFile = project.file(it.file)
//      def lang = it.lang
//      def overwrite = (it.overwrite) ? '1' : '0'
//      def sync_terms = (it.sync_terms) ? '1' : '0'
//
//      def retries = 8
//      while (retries > 0) {
//
//        def result = configure {
//          request.uri = 'https://api.poeditor.com/v2/projects/upload'
//        }.post {
//          request.contentType = 'multipart/form-data'
//          request.body = multipart {
//            field 'api_token', apiKey.get()
//            field 'id', projectId.get()
//            field 'updating', updating
//            part 'file', termsFile.name, 'text/plain', termsFile
//            field 'language', lang
//            field 'overwrite', overwrite
//            field 'sync_terms', sync_terms
//          }
//          request.encoder 'multipart/form-data', OkHttpEncoders.&multipart
//
//          response.exception { exception ->
//            logger.error("{}", exception.printStackTrace())
//            throw new GradleException(exception)
//          }
//
//          response.failure { FromServer fs ->
//            logger.error("{} {}", fs.message, fs.statusCode)
//            throw new GradleException(fs.message)
//          }
//        }
//
//        if (result.response.status == 'success') {
//          logger.info("{}", result.result)
//          outputFile.text = result.response.code
//          break
//        } else if (result.response.code == "4048") {
//          logger.warn("Too many upload requests in a short period of time; waiting for a retry...")
//          retries -= 1
//          sleep(15000)
//        } else {
//          logger.error("{} {}", result.response.code, result.response.message)
//          throw new GradleException(result.response.message)
//        }
//
//        if (retries == 0) {
//          logger.error("Too many upload retires")
//          throw new GradleException("Too many upload retires")
//        }
//
//      }
//    }
    }
}
