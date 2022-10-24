package me.bors.slack.share.logo

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.refcodes.properties.PropertiesSugar
import org.refcodes.properties.ext.obfuscation.ObfuscationPropertiesSugar
import java.io.File
import java.util.*

open class ExportLogoTask : DefaultTask() {
    init {
        group = "logo"
        description = "export-logo"
    }

    @TaskAction
    fun run() {
        val l = System.getProperty("file.separator")

        val originalPath = "${project.rootDir}${l}logo${l}logo_40x40.svg"

        val originalFile = File(originalPath)

        val resultingPath = "${project.projectDir}${l}src${l}main${l}resources${l}META-INF${l}pluginIcon.svg"

        val resultingFile = File(resultingPath)

        originalFile.copyTo(resultingFile, true)
    }
}