
package com.flayy.app

import android.util.Base64
import okhttp3.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

object GitHubHelper {
    private val client = OkHttpClient()
    private val gson = Gson()
    private const val API_BASE = "https://api.github.com"

    private fun buildRequest(url: String, body: RequestBody? = null, method: String = "GET"): Request {
        val builder = Request.Builder()
            .url(url)
            .addHeader("Authorization", "token " + BuildConfig.GITHUB_TOKEN)
            .addHeader("Accept", "application/vnd.github+json")
        when (method) {
            "GET" -> builder.get()
            "PUT" -> builder.put(body!!)
        }
        return builder.build()
    }

    private data class GitHubFile(
        val content: String,
        val sha: String
    )

    private fun <T> fetchJson(path: String, typeToken: java.lang.reflect.Type): Pair<T, String>? {
        val url = "\$API_BASE/repos/\${BuildConfig.GITHUB_REPO}/contents/\$path"
        val request = buildRequest(url)
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return null
            val bodyStr = response.body?.string() ?: return null
            val fileObj = gson.fromJson(bodyStr, GitHubFile::class.java)
            val decoded = String(Base64.decode(fileObj.content, Base64.DEFAULT))
            val data: T = gson.fromJson(decoded, typeToken)
            return Pair(data, fileObj.sha)
        }
    }

    private fun <T> pushJson(path: String, data: T, sha: String, message: String): Boolean {
        val url = "\$API_BASE/repos/\${BuildConfig.GITHUB_REPO}/contents/\$path"
        val jsonStr = gson.toJson(data)
        val contentBase64 = Base64.encodeToString(jsonStr.toByteArray(), Base64.NO_WRAP)
        val payload = mapOf(
            "message" to message,
            "content" to contentBase64,
            "sha" to sha
        )
        val body = RequestBody.create("application/json".toMediaTypeOrNull(), gson.toJson(payload))
        val request = buildRequest(url, body, "PUT")
        client.newCall(request).execute().use { response ->
            return response.isSuccessful
        }
    }

    // ===== DB functions =====
    fun addNumber(number: String): Boolean {
        val listType = object : TypeToken<MutableList<Map<String,String>>>() {}.type
        val pair = fetchJson<MutableList<Map<String,String>>>("db.json", listType) ?: return false
        val users = pair.first
        if (users.any { it["nomor"] == number }) return false
        users.add(mapOf("nomor" to number))
        return pushJson("db.json", users, pair.second, "Add user \$number")
    }

    fun deleteNumber(number: String): Boolean {
        val listType = object : TypeToken<MutableList<Map<String,String>>>() {}.type
        val pair = fetchJson<MutableList<Map<String,String>>>("db.json", listType) ?: return false
        val users = pair.first
        val newUsers = users.filter { it["nomor"] != number }
        return pushJson("db.json", newUsers, pair.second, "Remove user \$number")
    }

    fun listNumbers(): List<String>? {
        val listType = object : TypeToken<MutableList<Map<String,String>>>() {}.type
        val pair = fetchJson<MutableList<Map<String,String>>>("db.json", listType) ?: return null
        return pair.first.mapNotNull { it["nomor"] }
    }

    // ===== Login functions =====
    data class User(val username: String, var password: String)

    fun verifyLogin(username: String, password: String): Boolean {
        val listType = object : TypeToken<MutableList<User>>() {}.type
        val pair = fetchJson<MutableList<User>>("user_login.json", listType) ?: return false
        return pair.first.any { it.username == username && it.password == password }
    }

    fun addUserLogin(username: String, password: String): Boolean {
        val listType = object : TypeToken<MutableList<User>>() {}.type
        val pair = fetchJson<MutableList<User>>("user_login.json", listType) ?: return false
        val users = pair.first
        if (users.any { it.username == username }) return false
        users.add(User(username, password))
        return pushJson("user_login.json", users, pair.second, "Add login user \$username")
    }

    fun changePassword(username: String, newPassword: String): Boolean {
        val listType = object : TypeToken<MutableList<User>>() {}.type
        val pair = fetchJson<MutableList<User>>("user_login.json", listType) ?: return false
        val users = pair.first
        users.find { it.username == username }?.password = newPassword
        return pushJson("user_login.json", users, pair.second, "Change password for \$username")
    }
}
