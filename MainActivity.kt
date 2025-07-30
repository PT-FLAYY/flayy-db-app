
package com.flayy.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity: AppCompatActivity() {
    private var username: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        username = intent.getStringExtra("username")

        findViewById<Button>(R.id.btnAddNumber).setOnClickListener {
            startActivity(Intent(this, AddUserActivity::class.java))
        }

        findViewById<Button>(R.id.btnListNumbers).setOnClickListener {
            startActivity(Intent(this, ListUserActivity::class.java))
        }

        findViewById<Button>(R.id.btnDeleteNumber).setOnClickListener {
            startActivity(Intent(this, DeleteUserActivity::class.java))
        }

        findViewById<Button>(R.id.btnOwnerTools).apply {
            setOnClickListener {
                startActivity(Intent(this@MainActivity, OwnerToolsActivity::class.java))
            }
            // Show only for owner
            visibility = if (username == BuildConfig.OWNER_USERNAME) android.view.View.VISIBLE else android.view.View.GONE
        }

        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            finish()
        }
    }
}
