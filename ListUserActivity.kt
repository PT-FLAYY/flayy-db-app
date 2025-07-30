
package com.flayy.app

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

class ListUserActivity: AppCompatActivity() {
    private val scope = MainScope()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_user)

        val layout = findViewById<LinearLayout>(R.id.tvPlaceholder).parent as LinearLayout
        layout.removeAllViews()

        scope.launch {
            val nums = withContext(Dispatchers.IO) {
                GitHubHelper.listNumbers() ?: listOf()
            }
            nums.forEach {
                val tv = TextView(this@ListUserActivity).apply { text = it }
                layout.addView(tv)
            }
        }
    }
    override fun onDestroy() { super.onDestroy(); scope.cancel() }
}
