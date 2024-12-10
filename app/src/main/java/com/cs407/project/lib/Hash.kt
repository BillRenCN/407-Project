package com.cs407.project.lib

import java.security.MessageDigest

fun hash(input: String): String {
    return MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        .fold("") { str, it -> str + "%02x".format(it) }
}
