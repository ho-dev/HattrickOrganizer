import com.install4j.gradle.Install4jTask
import java.util.Properties
import org.kordamp.gradle.plugin.markdown.tasks.MarkdownToHtmlTask

// Some useful commands:
//
//   `./gradlew tasks` lists the available tasks
//   `./gradlew run` runs HO locally
//   `./gradlew tiTree <task>` shows the task graph for the task <task>

// Temporary workaround for #2116
// Thx to https://github.com/kordamp/markdown-gradle-plugin/issues/36#issuecomment-2277317737
buildscript {
    configurations.all {
        resolutionStrategy.dependencySubstitution {
            substitute(module("com.overzealous:remark:1.1.0"))
                .using(module("com.wavefront:remark:2023-07.07"))
                .because("not available on maven central anymore")
        }
    }
}

plugins {
    java
    groovy
    id("org.jetbrains.kotlin.jvm") version "2.2.0"
    application
    id("org.kordamp.gradle.markdown") version "2.2.0"
    id("com.github.breadmoirai.github-release") version "2.5.2"
    id("com.install4j.gradle") version "12.0"
    id("org.barfuin.gradle.taskinfo") version "2.2.0"
    id("io.freefair.lombok") version "8.13"
}
apply<de.jansauer.poeditor.POEditorPlugin>()

version = "10.0"

// Development_stage (DEV:0  BETA:1  STABLE:2)
val development_stage: Int = (project.findProperty("DEV_STAGE") as? String)?.toIntOrNull() ?: 0
val version_type = listOf("-DEV", "-BETA", "").getOrElse(development_stage) { "" }
val target_dir = "${layout.buildDirectory.get()}/artefacts"

extra["development_stage"] = development_stage
extra["version_type"] = version_type
extra["target_dir"] = target_dir


repositories {
    mavenCentral()
    maven(url = "https://maven.ej-technologies.com/repository")
}

dependencies {
    implementation("com.install4j:install4j-runtime:12.0")
    implementation("com.fasterxml.jackson.core:jackson-core:2.21.0")
    implementation("com.github.scribejava:scribejava-core:8.3.3")
    implementation("org.hsqldb:hsqldb:2.7.4")
    implementation("com.google.code.gson:gson:2.12.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:okhttp-tls:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.github.weisj:darklaf-core:3.1.1")
    implementation("com.github.weisj:darklaf-theme:3.1.1")
    implementation("com.github.weisj:darklaf-property-loader:3.1.1")
    implementation("org.javatuples:javatuples:1.2")
    implementation("org.apache.commons:commons-text:1.15.0")
    implementation("org.knowm.xchart:xchart:3.8.8")
    implementation("org.jsoup:jsoup:1.19.1")
    implementation("org.jetbrains:annotations:26.0.2")
    implementation("org.codehaus.groovy:groovy-all:3.0.24")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation("org.junit.jupiter:junit-jupiter:5.14.2")
    testImplementation("org.assertj:assertj-core:3.26.3")
    testImplementation("org.jetbrains.kotlin:kotlin-test:2.1.20")
}

tasks.test {
    useJUnitPlatform()
}

//  configure application plugin --------------------------------
application {
    mainClass.set("core.HOLauncher")
}

//  configure java plugin --------------------------------
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    withSourcesJar()
}

// No need to explicitly set encoding in newer Gradle, but keeping for compatibility if needed
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

sourceSets {
    main {
        java {
            srcDirs("src/main/java")
        }
        resources {
            srcDirs("src/main/java", "src/main/resources")
        }
    }
    test {
        java {
            srcDirs("src/test/java")
        }
        resources {
            srcDirs("src/test/resources")
        }
    }
}

// region configuration ==============================================================================================
fun callGit(param: String): String {
    val p = Runtime.getRuntime().exec(param)
    p.waitFor()
    //println("error: " + p.errorStream.bufferedReader().readText())
    return p.inputStream.bufferedReader().readText().trim()
}

// Used to filter contributors
fun filter(string: String, filters: List<String>): String {
    val lines = string.split('\n')
    var res = ""
    lines.forEach { l ->
        var skip = false
        for (f in filters) {
            if (l.contains(f)) {
                skip = true
                break
            }
        }
        if (!skip) {
            res = res + l + "\n"
        }
    }
    return res
}


// This fails, need to find an alternative
//  configure markdownToHtml --------------------------------
tasks.named<MarkdownToHtmlTask>("markdownToHtml") {
    tables = true
    // create html from md sources for both release notes and changelog
    sourceDir = file("${projectDir}/docs/md")
    outputDir = file("${projectDir}/docs/html")
    hardwraps = true
}


// Configure poeditor plugin ================================
configure<de.jansauer.poeditor.POEditorExtension> {
    apiKey.set(System.getenv("POEDITOR_APIKEY") ?: "xxx")
    projectId.set("206221")

    val langs = listOf(
        "bg" to "Bulgarian",
        "ca" to "Catalan",
        "hr" to "Hrvatski(Croatian)",
        "cs" to "Czech",
        "da" to "Danish",
        "nl" to "Nederlands",
        "fi" to "Finnish",
        "fr" to "French",
        "de" to "German",
        "el" to "Greek",
        "he" to "Hebrew",
        "hu" to "Magyar",
        "it" to "Italiano",
        "ko" to "Hangul(Korean)",
        "lv" to "Latvija",
        "lt" to "Lithuanian",
        "fa" to "Persian",
        "pl" to "Polish",
        "pt" to "Portugues",
        "pt-br" to "PortuguesBrasil",
        "ro" to "Romanian",
        "ru" to "Russian",
        "sr-cyrl" to "Serbian(Cyrillic)",
        "sk" to "Slovak",
        "es" to "Spanish",
        "sv" to "Svenska",
        "tr" to "Turkish",
        "no" to "Norsk",
        "id" to "Indonesian",
        "ja" to "Japanese",
        "sl" to "Slovenian",
        "uk" to "Ukranian",
        "ka" to "Georgian",
        "zh-CN" to "Chinese",
        "gl" to "Galego",
        "nl-be" to "Vlaams",
        "es-ar" to "Spanish(AR)",
        "et" to "Estonian"
    )

    langs.forEach { (lang, fileName) ->
        val map = linkedMapOf<String, Any>(
            "type" to "properties",
            "lang" to lang,
            "file" to "${projectDir}/src/main/resources/language/${fileName}.properties"
        )
        trans.add(map)
    }
}


// Configure jar task ================================
tasks.jar {
    archiveFileName.set("HO.jar")
    manifest {
        attributes(
            "Manifest-Version" to "1.0",
            "Implementation-Title" to "HO",
            "Implementation-Version" to project.version,
            "Main-Class" to "core.HOLauncher",
            "Class-Path" to configurations.runtimeClasspath.get().map { it.name }.joinToString(" ")
        )
    }
    exclude("/*.jar", "/*.ico", "/*.png", "/*.md", "/*.html")
}

// Configure distribution task =======================
distributions {
    main {
        distributionBaseName.set("XXX")
    }
}

configure<com.install4j.gradle.Install4jExtension> {
    if (project.hasProperty("install4jHomeDir") && project.property("install4jHomeDir") != "") {
        installDir = file(project.property("install4jHomeDir")!!)
    } else if (System.getenv("INSTALL4J_HOME") != null) {
        installDir = file(System.getenv("INSTALL4J_HOME"))
    }
    disableSigning = true
    license = System.getenv("INSTALL4J_LICENSE")
}


tasks.named("poeditorPull") {
    outputs.upToDateWhen { false }
}


//endregion ======================================================================================================================


// region my tasks ================================================================================================

tasks.register("createVersion") {
    description = "Creates the version file required by the GitHub Actions release process."
    doLast {
        val coords = project.version.toString().split("\\.".toRegex())
        val major = coords[0]
        val minor = coords[1]
        val developmentStage = project.extra["development_stage"] as Int

        println("development stage: $developmentStage")

        val currentBranch = callGit("git branch --show-current")
        val versionProps = Properties()
        val buildNumber = System.getenv("RUN_NUMBER") ?: "0"

        project.version = "$major.$minor.$buildNumber.$developmentStage"
        // Version in jar extension is set eagerly, override after computing version.
        tasks.jar.configure {
            manifest {
                attributes("Implementation-Version" to project.version)
            }
        }

        versionProps["buildNumber"] = buildNumber
        versionProps["version"] = project.version.toString()
        versionProps["shortVersion"] = "$major.$minor"
        versionProps["tag"] = listOf("dev", "beta", "tag_stable")[developmentStage]
        versionProps["branch"] = currentBranch

        // Write to version.properties file
        val versionPropsFile = file("version.properties")
        versionProps.store(versionPropsFile.writer(), null)

        println("Project:  ${project.name} ${project.version} .....................")
    }
}

tasks.register<Install4jTask>("media") {
    group = "sub tasks"
    dependsOn(tasks.named("createDistribution"))
    val developmentStage = project.extra["development_stage"]
    val buildNumber = System.getenv("RUN_NUMBER") ?: "0"
    val versionType = project.extra["version_type"]

    projectFile = file("${projectDir}/utils/buildResources/HO.install4j")

    // 60 -> Windows Setup
    // 1203  -> Windows Setup (32 bits)
    // 485 -> Windows JRE
    // 1206 -> Windows JRE (32 bits)

    // 64 -> Windows archive
    // 481 -> Windows archive JRE
    // 1208 -> Windows archive (32bits)
    // 1211 -> Windows archive JRE (32bits)

    // 1225 -> macOS Single Bundle
    // 1224 -> macOS Single Bundle JRE

    // 173 -> Unix Archive
    // 477 -> Unix Archive JRE

    // 1062 -> Unix installer
    // 1064 -> Unix installer JRE

    // 62  -> Linux rpm
    // 63 -> Linux deb
    // 464  -> Linux rpm JRE
    // 471 -> Linux deb JRE

    buildIds = listOf("60", "485", "64", "481", "1225", "1224", "173", "477", "1062", "1064", "1203", "1206", "1208", "1211")

    variables = mapOf(
        "HO_version" to "${project.version}.$buildNumber.$developmentStage",
        "projectDir" to projectDir.absolutePath,
        "versionType" to versionType.toString()
    )
}

tasks.register("makeAllMedias") {
    dependsOn(tasks.named("media"))
    doLast {
        delete("${projectDir}/build/artefacts/output.txt")
        val targetDir = project.extra["target_dir"]

        println("copy version.properties into $targetDir")  // ensures version.properties.html ends up with the artefacts in the release
        copy {
            from("${projectDir}/version.properties")
            into(targetDir!!)
        }

        // to ensure changelog.html ends up within the distribution
        println("copy release_notes.html")   // ensures release_notes.html ends up with the artefacts in the release
        copy {
            from("${projectDir}/docs/html/release_notes.html")
            into(targetDir!!)
        }

        // compatibility to HO3 Updater
        // which used other zip file name
        val versionType = project.extra["version_type"] ?: "-DEV"
        println("copy $targetDir/HO-${project.version}-portable-win${versionType}.zip -> HO_${project.version}.zip")
        copy {
            from("$targetDir/HO-${project.version}-portable-win${versionType}.zip")
            rename("HO-${project.version}-portable-win${versionType}.zip", "HO_${project.version}.zip")
            into(targetDir!!)
        }
    }
}

tasks.register("preparingBuild") {
    group = "sub tasks"
    doLast {
        //     Deleting build project     ---------------------------------------------------------------------
        println("Deleting build and execution files ....")
        delete(files("${projectDir}/db"))
        delete(files("${projectDir}/logs"))
        delete(files("${projectDir}/themes"))
        delete(files("${projectDir}/user.xml"))
        //     Creating Target directory ...  ---------------------------------------------------------------------
        mkdir(project.extra["target_dir"]!!)
    }
}

tasks.register("pushmd")  {
    group = "sub tasks"
    dependsOn(tasks.named("preparingBuild"))
    doLast {
        val coords = project.version.toString().split("\\.".toRegex())
        val major = coords[0]
        val minor = coords[1]

        // create release notes ========
        // list the contributors that don't want to be mentioned in release notes
        val contributorFilter = listOf("Che")

        val commitCount = callGit("git rev-list 9.0..HEAD --count")
        val diff = callGit("git diff --shortstat 9.0..HEAD")
        val contributors = filter(callGit("git shortlog -s -n 9.0..HEAD"), contributorFilter)
        val latestCommit = callGit("git log -1 --pretty=format:\"%s\"")

        println("create release-notes")
        val developmentMessage = listOf(
            "Latest alpha release – planned features are still missing and the version might be unstable, so don't use it without backups of your database and program directories",
            "Latest beta release – the version is feature complete – feedback from early users is welcome",
            "Latest stable release"
        )
        val devStage = project.extra["development_stage"] as Int

        val intro = listOf(
            "# HO! $major.$minor Release Notes",
            "",
            developmentMessage[devStage],
            "",
            "latest commit: $latestCommit",
            "",
            "## Some numbers",
            "* $commitCount commits",
            "* $diff",
            "* Contributors: \n$contributors",
            ""
        )

        val mdDir = file("${projectDir}/docs/md") // create directory for MD source
        mdDir.mkdirs()
        File(mdDir, "release_notes.md")
            .writeText(intro.joinToString("\n")
                       + file("${projectDir}/src/main/resources/release_notes.md").readText()) //release notes + intro copied in md source folder

        // create changelog
        println("create changelog")
        val changelogIntro = listOf(
            "# Changelist HO! $major.$minor",
            ""
        )

        val changeLog = File(mdDir, "changelog.md")
        changeLog.writeText(changelogIntro.joinToString("\n"))

        files("${projectDir}/src/main/resources/release_notes.md", "${projectDir}/src/main/resources/changelog.md").forEach { f ->
            changeLog.appendText(f.readText())
        }
    }
}

tasks.register("createDistribution") {
    group = "sub tasks"
    dependsOn(tasks.installDist)
    outputs.upToDateWhen { false }
    val distribDir = file("${layout.buildDirectory.get()}/install/XXX")
    doLast {

        // to ensure changelog.html ends up within the distribution
        copy {
            from("${projectDir}/docs/html/changelog.html")
            into(distribDir)
        }

        val devStage = project.extra["development_stage"] as Int
        if (devStage == 0) {
            copy {
                from("${projectDir}/utils/buildResources/Logo_dev.png")
                into("${projectDir}/utils/buildResources")
                rename("Logo_dev.png", "Logo.png")
            }
        } else if (devStage == 1) {
            copy {
                from("${projectDir}/utils/buildResources/Logo_beta.png")
                into("${projectDir}/utils/buildResources")
                rename("Logo_beta.png", "Logo.png")
            }
        } else {
            copy {
                from("${projectDir}/utils/buildResources/Logo_stable.png")
                into(distribDir)
                rename("Logo_stable.png", "Logo.png")
            }
        }

        copy {
            from("${projectDir}/prediction")
            into("$distribDir/prediction")
        }

        copy {
            from("$distribDir/lib")
            include("**/*.jar")
            into(distribDir)
        }

        copy {
            from("${projectDir}/src/main/resources/truststore.jks")
            into(distribDir)
        }

        delete("$distribDir/bin")
        delete("$distribDir/lib")

    }
}

tasks.register("createLanguageFileList")  {
    group = "sub tasks"
    dependsOn(tasks.named("poeditorPull"))
    outputs.upToDateWhen { false }

    doLast {
        val lTranslationFiles = ArrayList<String>()
        fileTree("${projectDir}/src/main/resources/language") {
            include("*.properties")
        }.visit {
            lTranslationFiles.add(this.file.name)
        }

        println("listing available translation in ${projectDir}/src/main/resources/language")
        val processedFiles = lTranslationFiles.map { it.substringBeforeLast('.') }
        processedFiles.forEach { println(it) }

        val lstFile = File("${projectDir}/src/main/resources/language/ListLanguages.txt")
        lstFile.printWriter().use { out ->
            processedFiles.forEach { out.println(it) }
        }
    }
}

tasks.register("resetDB") {
    group = "tool"
    description = "copy the database from existing install into project"

    doLast {
        try {
            val resetDir = if (project.hasProperty("resetDir")) project.property("resetDir") else null

            if (resetDir != null) {
                println("Resetting DB: copying DB from `$resetDir` into  ${projectDir}")
                delete(files("${projectDir}/db"))
                copy {
                    from("$resetDir/")
                    into("${projectDir}/db/")
                    exclude("**/logs/**")
                }

                println("Resetting DB: done !")
            } else {
                 println("Resetting DB: `resetDir` property not found.")
            }
        } catch (ignored: Exception) {
            println("Resetting DB: failed to copy DB")
        }
    }
}

tasks.register("pushDB") {
    group = "tool"
    description = "copy the database from project to temp folder for analysis"

    doLast {
        try {
            val tempDir = if (project.hasProperty("tempDir")) project.property("tempDir") else null
            if (tempDir != null) {
                println("Pushing DB: copying DB from ${projectDir} into `$tempDir/db/`")
                delete(files("$tempDir/db"))
                copy {
                    from("${projectDir}/db")
                    into("$tempDir/db")
                    exclude("**/logs/**")
                }

                println("Pushing DB: done !")
            } else {
                 println("Pushing DB: `tempDir` property not found.")
            }
        } catch (ignored: Exception) {
            println("Pushing DB: failed to copy DB")
        }
    }
}

tasks.register<GradleBuild>("release") {
    tasks = listOf("clean", "createVersion", "poeditorPull", "processResources", "createLanguageFileList", "makeAllMedias")
}

tasks.named("markdownToHtml").configure {
    dependsOn(tasks.named("pushmd"))
}
tasks.named("poeditorPull").configure {
    dependsOn(tasks.named("markdownToHtml"))
}
tasks.named("processResources").configure {
    dependsOn(tasks.named("poeditorPull"))
    (this as Copy).duplicatesStrategy = DuplicatesStrategy.INCLUDE
}
tasks.named("processTestResources").configure {
    (this as Copy).duplicatesStrategy = DuplicatesStrategy.INCLUDE
}
tasks.named("compileJava").configure {
    dependsOn(tasks.named("clean"))
    dependsOn(tasks.named("processResources"))
}
tasks.named<Jar>("sourcesJar").configure {
    dependsOn(tasks.named("poeditorPull"))
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}


// endregion ======================================================================================================================
