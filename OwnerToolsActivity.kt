
package com.flayy.app

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

class OwnerToolsActivity: AppCompatActivity() {
    private val scope = MainScope()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_owner_tools)

        val layout = findViewById<LinearLayout>(R.id.tvPlaceholder).parent as LinearLayout
        layout.removeAllViews()

        val modeSwitch = Switch(this).apply { text = "Add New User" }
        val edtUser = EditText(this).apply { hint = "username" }
        val edtPass = EditText(this).apply { hint = "password" }
        val btn = Button(this).apply { text = "Submit" }

        layout.addView(modeSwitch)
        layout.addView(edtUser)
        layout.addView(edtPass)
        layout.addView(btn)

        btn.setOnClickListener {
            val u = edtUser.text.toString().trim()
            val p = edtPass.text.toString().trim()
            if (u.isEmpty() || p.isEmpty()) {
                Toast.makeText(this, "Fill fields", Toast.LENGTH_SHORT).show(); return@setOnClickListener
            }
            scope.launch {
                val ok = withContext(Dispatchers.IO) {
                    if (modeSwitch.isChecked) GitHubHelper.addUserLogin(u,p)
                    else GitHubHelper.changePassword(u,p)
                }
                Toast.makeText(this@OwnerToolsActivity,
                    if (ok) "Success" else "Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onDestroy() { super.onDestroy(); scope.cancel() }
}
