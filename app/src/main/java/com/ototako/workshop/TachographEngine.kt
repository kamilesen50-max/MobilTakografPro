package com.ototako.workshop
​import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID
​enum class TachographBrand { VDO, STONERIDGE, ASELSAN, EFAS, PARS }
​class TachographEngine {
private val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
private var bluetoothSocket: BluetoothSocket? = null
private var outputStream: OutputStream? = null
private var inputStream: InputStream? = null
​@SuppressLint("MissingPermission")
suspend fun connect(device: BluetoothDevice): Boolean = withContext(Dispatchers.IO) {
return@withContext try {
bluetoothSocket = device.createRfcommSocketToServiceRecord(SPP_UUID)
bluetoothSocket?.connect()
outputStream = bluetoothSocket?.outputStream
inputStream = bluetoothSocket?.inputStream
true
} catch (e: Exception) {
false
}
}
​suspend fun sendHex(hexString: String): Boolean = withContext(Dispatchers.IO) {
return@withContext try {
val bytes = hexStringToByteArray(hexString)
outputStream?.write(bytes)
outputStream?.flush()
true
} catch (e: Exception) {
false
}
}
​fun buildWriteWFactor(wValue: Int): String {
val hexValue = String.format("%04X", wValue)
return "2EF1A0$hexValue"
}
​fun buildWriteLFactor(lValue: Int): String {
val hexValue = String.format("%04X", lValue)
return "2EF1A1$hexValue"
}
​fun buildWriteVRN(vrn: String): String {
val hexVrn = vrn.toByteArray(Charsets.US_ASCII).joinToString("") { "%02X".format(it) }
return "2EF190$hexVrn"
}
​fun buildPairSensor(sensorSerial: String): String {
val hexSerial = sensorSerial.toByteArray(Charsets.US_ASCII).joinToString("") { "%02X".format(it) }
return "2EF200$hexSerial"
}
​fun buildSendLogo(logoName: String): String {
val logoCode = when (logoName.uppercase()) {
"MERCEDES" -> "01"
"VOLVO" -> "02"
"MAN" -> "03"
"FORD" -> "04"
"SCANIA" -> "05"
else -> "00"
}
return "3101FF$logoCode"
}
​private fun hexStringToByteArray(s: String): ByteArray {
val len = s.length
val data = ByteArray(len / 2)
var i = 0
while (i < len) {
data[i / 2] = ((Character.digit(s[i], 16) shl 4) + Character.digit(s[i + 1], 16)).toByte()
i += 2
}
return data
}
}
