
package com.flayy.app

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

class DeleteUserActivity: AppCompatActivity() {
    private val scope = MainScope()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_user)

        val edt = EditText(this).apply { hint = "62xxxxxxxxxxx" }
        val btn = Button(this).apply { text = "Delete" }
        val layout = findViewById<LinearLayout>(R.id.tvPlaceholder).parent as LinearLayout
        layout.removeAllViews()
        layout.addView(edt)
        layout.addView(btn)

        btn.setOnClickListener {
            val num = edt.text.toString().trim()
            scope.launch {
                val ok = withContext(Dispatchers.IO) {
                    GitHubHelper.deleteNumber(num)
                }
                Toast.makeText(this@DeleteUserActivity,
                    if (ok) "Deleted" else "Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onDestroy() { super.onDestroy(); scope.cancel() }
}
