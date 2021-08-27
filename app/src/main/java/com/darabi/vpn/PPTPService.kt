package com.darabi.vpn

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.net.VpnManager
import android.net.VpnService
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.StringBuilder
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel
import java.nio.channels.Selector
import java.nio.charset.StandardCharsets

class PPTPService : VpnService() {

    companion object {

        const val ACTION_CONNECT = "action_connect"
        const val ACTION_DISCONNECT = "action_disconnect"
        const val NOTIFICATION_ID = "notification_channel_id"
    }

    private val maxMtuSize = 512

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return if (intent?.action == ACTION_CONNECT) {
            showNotification()
            connect()
            Log.d("test","============ACTION_CONNECT")
            START_STICKY
        } else {
            disconnect()
            Log.d("test","============ACTION_DISCONNECT")
            START_NOT_STICKY
        }
    }

    private fun connect() {
        CoroutineScope(Dispatchers.IO).launch {
            DatagramChannel.open().use { datagramChannel ->

                val packet = ByteBuffer.allocate(maxMtuSize)
                val inetAddress = InetSocketAddress("51.79.165.93", 2831)

                // For simplicity, we use the same thread for both reading and
                // writing. Here we put the tunnel into non-blocking mode.
                datagramChannel.configureBlocking(false)

                // Protect the tunnel before connecting to avoid loopback.
                protect(datagramChannel.socket())

                // Connect to the server.
                datagramChannel.connect(inetAddress)

                // To build a secured tunnel, we should perform mutual authentication
                // and exchange session keys for encryption. To keep things simple in
                // this demo, we just send the shared secret in plaintext and wait
                // for the server to send the parameters.

                // Allocate the buffer for handshaking.

                // Control messages always start with zero.


                Log.d("test", "=====is isOpen? : ${datagramChannel.isOpen}")
                val vpnInterface = Builder()
                    .addAddress("192.168.0.1", 24)
                    .addDnsServer("8.8.8.8")
                    .addRoute("0.0.0.0", 0).establish()

                // Packets to be sent are queued in this input stream.
//                val inputStream = FileInputStream(vpnInterface?.fileDescriptor)

                // Packets received need to be written to this output stream.
//                val outputStream = FileOutputStream(vpnInterface?.fileDescriptor)

                var length = 0
                var count = 0
            }
        }
    }

    private fun disconnect() {}

    private fun showNotification() = startForeground(1, notificationVBuilder().build())

    private fun mainActivityIntent() = Intent(this, MainActivity::class.java)//missing action

    private fun notificationIntent() =
        PendingIntent.getActivity(this, 0, mainActivityIntent(), PendingIntent.FLAG_UPDATE_CURRENT)

    private fun notificationVBuilder() = NotificationCompat.Builder(this, NOTIFICATION_ID)
        .setSmallIcon(R.drawable.ic_launcher_background)
        .setContentTitle("Vpn App")
        .setContentText("Connecting . . .")
        .setContentIntent(notificationIntent())
}