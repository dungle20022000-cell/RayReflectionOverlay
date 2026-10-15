package com.rayreflect.overlay

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnStart = findViewById<Button>(R.id.btnStart)
        val btnStop = findViewById<Button>(R.id.btnStop)
        val btnPermission = findViewById<Button>(R.id.btnPermission)
        val tvStatus = findViewById<TextView>(R.id.tvStatus)

        updateStatus(tvStatus)

        btnPermission.setOnClickListener {
            requestOverlayPermission()
        }

        btnStart.setOnClickListener {
            if (Settings.canDrawOverlays(this)) {
                startOverlay()
                Toast.makeText(this, "Đã bật overlay!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Cần cấp quyền Overlay trước!", Toast.LENGTH_SHORT).show()
                requestOverlayPermission()
            }
        }

        btnStop.setOnClickListener {
            stopService(Intent(this, OverlayService::class.java))
            Toast.makeText(this, "Đã tắt overlay", Toast.LENGTH_SHORT).show()
            updateStatus(tvStatus)
        }
    }

    override fun onResume() {
        super.onResume()
        updateStatus(findViewById(R.id.tvStatus))
    }

    private fun updateStatus(tv: TextView) {
        val hasPermission = Settings.canDrawOverlays(this)
        tv.text = if (hasPermission) {
            "✅ Đã có quyền Overlay\nNhấn 「Bật Overlay」 để hiện khung tia phản xạ"
        } else {
            "❌ Chưa có quyền Overlay\nNhấn 「Cấp quyền Overlay」 trước"
        }
    }

    private fun requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivity(intent)
        }
    }

    private fun startOverlay() {
        val intent = Intent(this, OverlayService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }
}
