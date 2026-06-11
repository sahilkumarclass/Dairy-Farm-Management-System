package com.sahilkumar.dfms.core.util

import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

/** Best-effort, user-facing message from an API/network failure. */
fun Throwable.userMessage(): String = when (this) {
    is HttpException -> parseHttpMessage(this)
    is IOException -> "Network error. Check your connection and that the server is running."
    else -> message ?: "Something went wrong."
}

private fun parseHttpMessage(e: HttpException): String {
    val raw = runCatching { e.response()?.errorBody()?.string() }.getOrNull()
    if (!raw.isNullOrBlank()) {
        runCatching {
            val json = JSONObject(raw)
            // Spring may return {"message": ...} or a validation {"errors":[{"defaultMessage":...}]}
            json.optString("message").takeIf { it.isNotBlank() }?.let { return it }
            val errors = json.optJSONArray("errors")
            if (errors != null && errors.length() > 0) {
                val first = errors.optJSONObject(0)
                first?.optString("defaultMessage")?.takeIf { it.isNotBlank() }?.let { return it }
            }
        }
    }
    return when (e.code()) {
        400 -> "Invalid input. Please check the form."
        401 -> "Session expired. Please sign in again."
        403 -> "You don't have permission to do that."
        404 -> "Not found."
        409 -> "Conflict — this value already exists."
        else -> "Request failed (${e.code()})."
    }
}
