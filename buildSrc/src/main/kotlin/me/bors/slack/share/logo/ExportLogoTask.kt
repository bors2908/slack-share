package me.bors.slack.share.logo

import java.nio.file.Files
import java.nio.file.StandardCopyOption
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class ExportLogoTask : DefaultTask() {

    @get:InputFile
    abstract val originalFile: RegularFileProperty

    @get:OutputFile
    abstract val resultingFile: RegularFileProperty

    init {
        group = "logo"
        description = "export-logo"
    }

    @TaskAction
    fun run() {
        Files.copy(
            originalFile.get().asFile.toPath(),
            resultingFile.get().asFile.toPath(),
            StandardCopyOption.REPLACE_EXISTING
        )
    }
}
