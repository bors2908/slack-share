package me.bors.slack.share.entity

import java.io.File

data class FileExclusion(val file: File, val reason: String) {
    override fun toString(): String {
        return "File: [$file], Reason: [$reason]"
    }
}
