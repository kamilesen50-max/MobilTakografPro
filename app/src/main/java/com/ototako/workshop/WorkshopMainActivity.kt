package com.ototako.workshop
​import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
​class WorkshopMainActivity : ComponentActivity() {
override fun onCreate(savedInstanceState: Bundle?) {
super.onCreate(savedInstanceState)
setContent {
MaterialTheme {
Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
WorkshopAppScreen()
}
}
}
}
}
​@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkshopAppScreen() {
var selectedBrand by remember { mutableStateOf(TachographBrand.VDO) }
var wFactor by remember { mutableStateOf("8000") }
var lFactor by remember { mutableStateOf("3180") }
var vrnText by remember { mutableStateOf("27 ABC 123") }
var sensorSerial by remember { mutableStateOf("12345678") }
var selectedLogo by remember { mutableStateOf("MERCEDES") }
​val engine = remember { TachographEngine() }
val scope = rememberCoroutineScope()
val scrollState = rememberScrollState()
​Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(scrollState)) {
Text("EVRENSEL TAKOGRAF ATÖLYESİ", style = MaterialTheme.typography.headlineSmall)
Text("Smartlink Bluetooth Bağlantılı", style = MaterialTheme.typography.bodySmall)
​Spacer(modifier = Modifier.height(16.dp))
​Text("Takograf Markası:", style = MaterialTheme.typography.titleSmall)
Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
TachographBrand.values().forEach { brand ->
FilterChip(
selected = selectedBrand == brand,
onClick = { selectedBrand = brand },
label = { Text(brand.name) }
)
}
}
​Spacer(modifier = Modifier.height(16.dp))
​Text("Ekran Logosu Gönder:", style = MaterialTheme.typography.titleSmall)
Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
listOf("MERCEDES", "VOLVO", "FORD", "MAN").forEach { logo ->
FilterChip(
selected = selectedLogo == logo,
onClick = { selectedLogo = logo },
label = { Text(logo) }
)
}
}
Button(
onClick = {
scope.launch { engine.sendHex(engine.buildSendLogo(selectedLogo)) }
},
modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
) {
Text("LOGOYU CİHAZA YÜKLE")
}
​Spacer(modifier = Modifier.height(16.dp))
HorizontalDivider()
Spacer(modifier = Modifier.height(16.dp))
​OutlinedTextField(value = wFactor, onValueChange = { wFactor = it }, label = { Text("W Katsayısı (imp/km)") }, modifier = Modifier.fillMaxWidth())
Spacer(modifier = Modifier.height(8.dp))
OutlinedTextField(value = lFactor, onValueChange = { lFactor = it }, label = { Text("L Lastik Çevresi (mm)") }, modifier = Modifier.fillMaxWidth())
Spacer(modifier = Modifier.height(8.dp))
OutlinedTextField(value = vrnText, onValueChange = { vrnText = it }, label = { Text("Araç Plakası (VRN)") }, modifier = Modifier.fillMaxWidth())
​Spacer(modifier = Modifier.height(16.dp))
​OutlinedTextField(value = sensorSerial, onValueChange = { sensorSerial = it }, label = { Text("KITAS Sensör Seri No") }, modifier = Modifier.fillMaxWidth())
Button(
onClick = {
scope.launch { engine.sendHex(engine.buildPairSensor(sensorSerial)) }
},
modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
) {
Text("KITAS SENSÖRÜ EŞLEŞTİR VE MÜHÜRLE")
}
​Spacer(modifier = Modifier.height(24.dp))
​Button(
onClick = {
scope.launch {
engine.sendHex(engine.buildWriteWFactor(wFactor.toIntOrNull() ?: 8000))
engine.sendHex(engine.buildWriteLFactor(lFactor.toIntOrNull() ?: 3180))
engine.sendHex(engine.buildWriteVRN(vrnText))
}
},
modifier = Modifier.fillMaxWidth().height(56.dp)
) {
Text("TÜM KALİBRASYON VERİLERİNİ YAZ")
}
}
}
