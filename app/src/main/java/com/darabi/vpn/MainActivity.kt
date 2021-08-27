package com.darabi.vpn

import android.content.Intent
import android.net.VpnService
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.darabi.vpn.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val vpnRequest = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            makeToast("Permission Granted!")
            startService(getVpnIntent())
        } else {
            makeToast("Permission Granted!")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
    }

    private fun initViews() {

        binding.btnConnect.setOnClickListener {
            VpnService.prepare(this@MainActivity).let {
                if (it != null)
                    vpnRequest.launch(it)
                else
                    startService(getVpnIntent())
            }
        }

        binding.btnDisconnect.setOnClickListener {  }
    }

    private fun getVpnIntent() =
        Intent(this, PPTPService::class.java).setAction(PPTPService.ACTION_CONNECT)

    private fun makeToast(message: String) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}