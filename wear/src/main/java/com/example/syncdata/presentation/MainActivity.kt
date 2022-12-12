package com.example.syncdata.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.wear.ambient.AmbientModeSupport
import androidx.wear.compose.material.*
import com.example.syncdata.R
import com.example.syncdata.presentation.theme.SyncdataTheme
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.sp
import com.example.syncdata.presentation.NavRoute.Correr
import com.example.syncdata.presentation.NavRoute.Datos_personales
import com.example.syncdata.presentation.NavRoute.Sentadillas
import kotlinx.coroutines.delay


var dvalue by mutableStateOf("Esperando")
class MainActivity : FragmentActivity(), AmbientModeSupport.AmbientCallbackProvider {
    private val STEP_SENSOR_CODE = 10
    private lateinit var ambientModeSupport: AmbientModeSupport.AmbientController

    private lateinit var mSensorManager: SensorManager
    private var mAccelerometer: Sensor? = null
    private var resume = false

    //TODO ambient mode support
    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.BODY_SENSORS
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i("permisos", "Permisos Concedidos")
            //TODO
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf( Manifest.permission.ACTIVITY_RECOGNITION,
                Manifest.permission.BODY_SENSORS),
            STEP_SENSOR_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode,
            permissions, grantResults
        )
        when (requestCode) {
            STEP_SENSOR_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.i("DENIED", "Permisos Denegados")
                } else {
                    Log.i("GRANTED", "Permisos Concedidos")
                }
            }
        }
        fun getAmbientCallBack(): AmbientModeSupport.AmbientCallback =
            MyAmbientCallback()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
        super.onCreate(savedInstanceState)
        setupPermissions()
        ambientModeSupport = AmbientModeSupport.attach(this)
        setContent {
            WearApp()
        }
    }


    override fun getAmbientCallback(): AmbientModeSupport.AmbientCallback = MyAmbientCallback()
}

private class MyAmbientCallback : AmbientModeSupport.AmbientCallback() {
    override fun onEnterAmbient(ambientDetails: Bundle?) {
        super.onEnterAmbient(ambientDetails)
    }
    override fun onExitAmbient() {
        super.onExitAmbient()
    }
    override fun onUpdateAmbient() {
        super.onUpdateAmbient()
    }
}


////////////SENSORES

//imprime el valor del sensor
@Composable
fun GetDataAcelerometer(): Int {
    var value by remember { mutableStateOf(0) }
    val context = LocalContext.current
    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
    val listener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        }
        override fun onSensorChanged(event: SensorEvent?) {
            if (event != null) {
                value = event.values[0].toInt()
            }
        }
    }
    LaunchedEffect(key1 = true) {
        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }
    return value
}

@Composable
fun getStepData(): Int {
    val ctx = LocalContext.current
    val sensorManager : SensorManager =
        ctx.getSystemService(
            Context.SENSOR_SERVICE
        ) as SensorManager
    val StepSensor: Sensor =
        sensorManager.getDefaultSensor(
            Sensor.TYPE_STEP_COUNTER
        )
    var stepvalue = remember {
        mutableStateOf(0f)
    }
    val stepSensorListener = object: SensorEventListener {
        override fun onSensorChanged(p0: SensorEvent?) {
            p0?:return
            p0.values.firstOrNull()?.let {
                stepvalue.value= p0.values[0]
            }
        }
        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
            println("onAccuracyChanged : Sensor: $p0; accuracy$p1")
        }
    }
    sensorManager.registerListener(
        stepSensorListener,
        StepSensor,
        SensorManager.SENSOR_DELAY_FASTEST
    )
    return stepvalue.value.toInt()
}

@Composable
fun getHeartRate():String{
    val ctx = LocalContext.current
    val sensorManager : SensorManager =
        ctx.getSystemService(
            Context.SENSOR_SERVICE
        ) as SensorManager
    val hearRateSensor: Sensor =
        sensorManager.getDefaultSensor(
            Sensor.TYPE_HEART_RATE
        )
    var hrStatus = remember {
        mutableStateOf("")
    }
    val hearRateSensorListener = object: SensorEventListener {
        override fun onSensorChanged(p0: SensorEvent?) {
            p0?:return
            p0.values.firstOrNull()?.let {
                hrStatus.value= p0.values[0].toInt().toString()
            }
        }
        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
            println("onAccuracyChanged : Sensor: $p0; accuracy$p1")
        }

    }
    sensorManager.registerListener(
        hearRateSensorListener,
        hearRateSensor,
        SensorManager.SENSOR_DELAY_NORMAL
    )
    return hrStatus.value
}

object NavRoute{
    const val HomeScreen        = "Home"
    const val Datos_personales  = "datos_personales"
    const val Correr            = "correr"
    const val Sentadillas       = "sentadillas"
    const val Abdominales       = "abdominales"
    const val Registros         = "registros"

}

@Composable
fun navegaciones (){
    val navController = rememberSwipeDismissableNavController()
    SwipeDismissableNavHost(
        navController = navController,
        startDestination = NavRoute.HomeScreen,
    ) {
        composable(NavRoute.HomeScreen) {
            HomeScreen(navController)
        }
        composable(NavRoute.Datos_personales) {
            Datos_personales(navigation = navController, viewModel = datosView())
        }
        composable(NavRoute.Correr){
            Correr( navigation = navController, viewModel= correrView())
        }
        composable(NavRoute.Sentadillas) {
            Sentadillas(navigation = navController, viewModel= sentadillasView())
        }
        composable(NavRoute.Abdominales) {
            Abdominales(navigation = navController, viewModel = abdominalesView())

        }
        /*composable(NavRoute.Registros) {
            Registros(navigation = navController, viewModel = workOutviewmodel(), distancia = Int, calorias_quemadas = Int)
        }*/
    }
}




//Navegacion entre pantallas
@Composable
fun HomeScreen(navigation: NavController) {
    val state = rememberScrollState()
    LaunchedEffect(Unit) { state.animateScrollTo(100) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(state),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Chip(
            label = { Text(text = "Correr") },
            modifier = Modifier
                .fillMaxWidth(),
            onClick = { navigation.navigate(NavRoute.Correr)},
            colors = ChipDefaults.imageBackgroundChipColors(
                backgroundImagePainter = painterResource(id = R.drawable.ejercicio)
            )
        )
        Spacer(modifier = Modifier.height(5.dp))
        Chip(
            label = { Text(text = "Abdominales") },
            modifier = Modifier
                .fillMaxWidth(),
            onClick = { navigation.navigate(NavRoute.Abdominales) },
            colors = ChipDefaults.imageBackgroundChipColors(
                backgroundImagePainter = painterResource(id = R.drawable.ejercicio)
            )
        )
        Spacer(modifier = Modifier.height(5.dp))
        Chip(
            label = { Text(text = "Sentadilas") },
            modifier = Modifier
                .fillMaxWidth(),
            onClick = { navigation.navigate(NavRoute.Sentadillas) },
            colors = ChipDefaults.imageBackgroundChipColors(
                backgroundImagePainter = painterResource(id = R.drawable.ejercicio)
            )
        )
        Spacer(modifier = Modifier.height(5.dp))
        Chip(
            label = { Text(text = "Datos personales") },
            modifier = Modifier
                .fillMaxWidth(),
            onClick = { navigation.navigate(NavRoute.Datos_personales) },
            colors = ChipDefaults.imageBackgroundChipColors(
                backgroundImagePainter = painterResource(id = R.drawable.datos)
            ),
        )
        Spacer(modifier = Modifier.height(5.dp))
        Chip(
            label = { Text(text = "Registros") },
            modifier = Modifier
                .fillMaxWidth(),
            onClick = { navigation.navigate(NavRoute.Registros) },
            colors = ChipDefaults.imageBackgroundChipColors(
                backgroundImagePainter = painterResource(id = R.drawable.ejercicio)
            ),
        )
        Spacer(modifier = Modifier.height(25.dp))
    }
}

data class datos_personales_f(
    var nombre: String = "",
    val sexo: String = "",
    val edad: String = "",
    val altura: String = "",
    val peso: String = ""
)

var xd= mutableStateOf<datos_personales_f>(datos_personales_f("","", "", "", ""))
class datosView : ViewModel() {
    private val database = Firebase.database("https://esp8266-demo-e7191-default-rtdb.firebaseio.com")
    private var _datos = mutableStateOf<List<datos_personales_f>>(emptyList())
    val Datos_personales: State<List<datos_personales_f>> = _datos

    fun getData() {
        database.getReference("datos_personales").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    _datos.value = snapshot.getValue<List<datos_personales_f>>()!!
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Error getting data", error.toException())
                }
            }
        )
    }
}

@Composable
fun Datos_personales(viewModel: datosView, navigation: NavController) {
    viewModel.getData()
    val state = rememberScrollState()
    LaunchedEffect(Unit) { state.animateScrollTo(100) }
    ScalingLazyColumn() {
        items(viewModel.Datos_personales.value) { workout ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        MaterialTheme.colors.background
                    ),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                //muestra los datos uno debajo de otros con una separacion de 20dp
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Nombre: ${workout.nombre}"
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Sexo: ${workout.sexo}"
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Edad: ${workout.edad}"
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Altura: ${workout.altura}"
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Peso: ${workout.peso}"
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}


data class correrData(
    val calorias_quemadas: String = "",
    var distancia: String = "",
    val pasos: String = "",
    val ritmo_cardiaco: String = "",
)

class correrView : ViewModel() {
    private val database = Firebase.database("https://esp8266-demo-e7191-default-rtdb.firebaseio.com")
    private var _datos = mutableStateOf<List<correrData>>(emptyList())
    val Datos_correr: State<List<correrData>> = _datos

    fun getData() {
        database.getReference("correr").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    _datos.value = snapshot.getValue<List<correrData>>()!!
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Error getting data", error.toException())
                }
            }
        )
    }
    fun writeToDB(data: correrData, index: Int) {
        val database = Firebase.database("https://esp8266-demo-e7191-default-rtdb.firebaseio.com")
        val myRef = database.getReference("correr")
        listOf(data).forEach {
            myRef.child(index.toString()).setValue(it)
        }

    }
}

@Composable
fun calorias_quemadas(): Int {

    val stepvalue = getStepData()
    val distancia = stepvalue * 0.762
    val calorias_quemadas = distancia * 0.05

    return calorias_quemadas.toInt()
}

@Composable
fun distancia(): Int {
    val stepvalue = getStepData()
    val distancia = stepvalue * 0.762

    return distancia.toInt()
}

@Composable
fun Correr(viewModel: correrView, navigation: NavController) {
    viewModel.getData()
    val index = viewModel.Datos_correr.value.size
    val calorias_quemadas = calorias_quemadas().toString()
    val distancia = distancia().toString()
    val pasos = getStepData().toString()
    val ritmo_cardiaco = getHeartRate().toString()
    println("pasos: $pasos"+ "ritmo cardiaco: $ritmo_cardiaco"+ "calorias quemadas: $calorias_quemadas"+ "distancia: $distancia")

    ScalingLazyColumn() {
        items(viewModel.Datos_correr.value) { workout ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        MaterialTheme.colors.background
                    ),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Calorias quemadas: " + workout.calorias_quemadas.toString(),
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = "Distancia: " + workout.distancia.toString() + " m",
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = "Pasos: " + workout.pasos.toString(),
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = "Ritmo cardiaco: " + workout.ritmo_cardiaco.toString(),
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
        item {
            Button(onClick = {
                viewModel.writeToDB(
                    correrData(
                        calorias_quemadas,
                        distancia,
                        pasos,
                        ritmo_cardiaco
                    ), index
                )
            }) {
                Text(
                    text = "Guardar",
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

data class abdominalesData(
    val calorias_quemadas: String = "",
    val repeticiones: String = "",
    val ritmo_cardiaco: String = "",
)

class abdominalesView : ViewModel() {
    private val database = Firebase.database("https://esp8266-demo-e7191-default-rtdb.firebaseio.com")
    private var _datos = mutableStateOf<List<abdominalesData>>(emptyList())
    val Datos_abdominales: State<List<abdominalesData>> = _datos

    fun getData() {
        database.getReference("datos_abdominales").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    _datos.value = snapshot.getValue<List<abdominalesData>>()!!
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Error getting data", error.toException())
                }
            }
        )
    }
    fun writeToDB(data: abdominalesData, index: Int) {
        val database = Firebase.database("https://esp8266-demo-e7191-default-rtdb.firebaseio.com")
        val myRef = database.getReference("datos_abdominales")
        listOf(data).forEach {
            myRef.child(index.toString()).setValue(it)
        }

    }
}

@Composable
fun calorias_quemadas_abdominales(): Int {

    val repeticiones = getRepeticionesData()
    val calorias_quemadas = repeticiones * 0.05

    return calorias_quemadas.toInt()
}

@Composable
fun getRepeticionesData(): Int {
    //repiciones con el acelerometro
    var acelerometro = GetDataAcelerometer().toInt()
    var repeticiones = 0
    if (acelerometro < 0.5) {
        repeticiones = repeticiones + 1
    }
    return repeticiones
}

@Composable
fun Abdominales(viewModel: abdominalesView, navigation: NavController) {
    viewModel.getData()
    val index = viewModel.Datos_abdominales.value.size
    val calorias_quemadas = calorias_quemadas_abdominales().toString()
    val repeticiones = getRepeticionesData().toString()
    val ritmo_cardiaco = getHeartRate().toString()
    println("repeticiones:-------------- $repeticiones"+ "ritmo cardiaco: $ritmo_cardiaco"+ "calorias quemadas: $calorias_quemadas")

    ScalingLazyColumn() {
        items(viewModel.Datos_abdominales.value) { workout ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        MaterialTheme.colors.background
                    ),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Calorias quemadas: " + workout.calorias_quemadas.toString(),
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Repeticiones: " + workout.repeticiones.toString(),
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Ritmo cardiaco: " + workout.ritmo_cardiaco.toString(),
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
        item {
            Button(onClick = {
                viewModel.writeToDB(
                    abdominalesData(
                        calorias_quemadas,
                        repeticiones,
                        ritmo_cardiaco
                    ), index
                )
            }) {
                Text(text = "Guardar")
            }
        }
    }
}

data class sentadillasData(
    val calorias_quemadas: String = "",
    val repeticiones: String = "",
    val ritmo_cardiaco: String = "",
)

class sentadillasView : ViewModel() {
    private val database = Firebase.database("https://esp8266-demo-e7191-default-rtdb.firebaseio.com")
    private var _datos = mutableStateOf<List<sentadillasData>>(emptyList())
    val Datos_sentadillas: State<List<sentadillasData>> = _datos

    fun getData() {
        database.getReference("datos_sentadillas").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    _datos.value = snapshot.getValue<List<sentadillasData>>()!!
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Error getting data", error.toException())
                }
            }
        )
    }
    fun writeToDB(data: sentadillasData, index: Int) {
        val database = Firebase.database("https://esp8266-demo-e7191-default-rtdb.firebaseio.com")
        val myRef = database.getReference("datos_sentadillas")
        listOf(data).forEach {
            myRef.child(index.toString()).setValue(it)
        }

    }
}

@Composable
fun calorias_quemadas_sentadillas(): Int {
    val repeticiones = getRepeticionesData()
    val calorias_quemadas = repeticiones * 0.05
    return calorias_quemadas.toInt()
}

@Composable
fun Sentadillas(viewModel: sentadillasView, navigation: NavController) {
    viewModel.getData()
    val index = viewModel.Datos_sentadillas.value.size
    val calorias_quemadas = calorias_quemadas_sentadillas().toString()
    val repeticiones = getRepeticionesData().toString()
    val ritmo_cardiaco = getHeartRate().toString()
    val acelerometro = GetDataAcelerometer().toString()
    println("Acelerometro:--------------------------- $acelerometro----------------------------")
    println("repeticiones:-------------- $repeticiones"+ "ritmo cardiaco: $ritmo_cardiaco"+ "calorias quemadas: $calorias_quemadas")

    ScalingLazyColumn() {
        items(viewModel.Datos_sentadillas.value) { workout ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        MaterialTheme.colors.background
                    ),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Calorias quemadas: " + workout.calorias_quemadas.toString(),
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Repeticiones: " + workout.repeticiones.toString(),
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Ritmo cardiaco: " + workout.ritmo_cardiaco.toString(),
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
        item {
            Button(onClick = {
                viewModel.writeToDB(
                    sentadillasData(
                        calorias_quemadas,
                        repeticiones,
                        ritmo_cardiaco
                    ), index
                )
                navigation.navigate(NavRoute.HomeScreen)
            }) {
                Text(text = "Guardar")
            }
        }
    }
}



@Composable
fun WearApp() {
    val ctx = LocalContext.current;
    SyncdataTheme {
        val listState = rememberScalingLazyListState()
        Scaffold(timeText = {
            if (!listState.isScrollInProgress) {
                TimeText()
            }
        },
            vignette = {
                Vignette(vignettePosition = VignettePosition.Top)
            },
            positionIndicator = {
                PositionIndicator(scalingLazyListState = listState)
            }
        ) {
            navegaciones()
        }
    }
}