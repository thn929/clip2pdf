package com.example.clip2pdf

import android.app.Activity
import android.os.Bundle
import android.widget.Toast

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Toast.makeText(this, R.string.launcher_hint, Toast.LENGTH_LONG).show()
        finish()
    }
}
