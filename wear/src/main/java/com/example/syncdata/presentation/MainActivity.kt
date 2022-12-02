package com.example.syncdata.presentation



import android.Manifest
import android.Manifest.permission.BODY_SENSORS
import android.app.RemoteInput
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.wear.ambient.AmbientModeSupport
import androidx.wear.compose.material.*
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.input.RemoteInputIntentHelper
import androidx.wear.input.wearableExtender
import com.example.syncdata.R
import com.example.syncdata.presentation.theme.SyncdataTheme
import com.example.syncdata.presentation.theme.amaticsc
import com.example.syncdata.presentation.theme.dancingscript
import com.example.syncdata.presentation.theme.raleway
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : FragmentActivity(), AmbientModeSupport.AmbientCallbackProvider {
    private val STEP_SENSOR_CODE = 20
    private lateinit var ambientModeSupport: AmbientModeSupport.AmbientController

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
data class workoutData(val name: String = "", val reps: Int = 0)

class workOutviewmodel : ViewModel() {
    private val database = Firebase.database("https://esp8266-demo-e7191-default-rtdb.firebaseio.com")
    private var _workoutData = mutableStateOf<List<workoutData>>(emptyList())
    val workoutData: State<List<workoutData>> = _workoutData

    fun getData() {
        database.getReference("workout").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    _workoutData.value = snapshot.getValue<List<workoutData>>()!!
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Error getting data", error.toException())
                }
            }
        )
    }

    fun writeToDB(Workout: workoutData, index: Int) {
        val database = Firebase.database("https://esp8266-demo-e7191-default-rtdb.firebaseio.com")
        val myRef = database.getReference("workout")
        listOf(Workout).forEach() {
            myRef.child(index.toString()).setValue(it)
        }
    }
}

@Composable
fun workOutScreen(viewModel: workOutviewmodel ) {
    viewModel.getData()
    val index = viewModel.workoutData.value.size
    ScalingLazyColumn() {
        items(viewModel.workoutData.value) { workout ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = workout.name,
                    //modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
                Text(
                    text = workout.reps.toString(),
                    //modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
            }
        }
        item {
            Button(onClick = {
                viewModel.writeToDB(workoutData("carrera", 10), index)
            }) {
                Text(text = "Registrar ejercicio")
            }
        }
    }
}


    @Composable
    fun WearApp(sharedPreferences: SharedPreferences) {
        var editor = sharedPreferences.edit()
        editor.putString("REGISTRO", "")
        editor.commit()

        //workOutScreen(workOutviewmodel())/////////////////////////////////LINEA DE BRADON


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
            principal(sharedPreferences)
        }

    }







/////////////////////////////////////////////////////////////////////



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
        fun getAmbientCallBack(): AmbientModeSupport.AmbientCallback = MyAmbientCallback()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreference = getSharedPreferences("USERNAME_PREF0",0)
        setupPermissions()
        ambientModeSupport = AmbientModeSupport.attach(this)
        setContent {
            WearApp(sharedPreference)
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

object NavRoute {
    const val  HomeScreen = "home"
    const val  SCREEN_3= "screen3"
    const val  SCREEN_2= "screen2"
    const val  USER_SCREEN= "user_screen/{user}"

    const val  SCREEN_3Peso= "screen3peso"
    const val  DETAILSCREENPeso= "detailScreenpeso/{id}"
    const val  SCREEN_3Talla= "screen3talla"
    const val  DETAILSTalla= "detailScreentalla/{id}"
    const val  SCREEN_3Edad= "screen3edad"
    const val  DETAILSEdad= "detailScreenedad/{id}"
    const val tabSensores= "tabSensores"
    const val tabDatos= "tabDatos"
    const val  TEXTSCREEN ="textscreen"
    const val EJERCICIO="ejercicio"
    const val REGISTROS="registros"
}

@Composable
fun WearApp(sharedPreferences: SharedPreferences) {
    var editor = sharedPreferences.edit()
    editor.putString("REGISTRO", "")
    editor.commit()
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
            principal(sharedPreferences)
        }
    }


@Composable
fun principal(sharedPreferences: SharedPreferences){
    val navController = rememberSwipeDismissableNavController()
    SwipeDismissableNavHost(navController = navController, startDestination = NavRoute.HomeScreen)
    {

        composable(NavRoute.USER_SCREEN) {
            userscreen(username=it.arguments?.getString("username")?:"0",sharedPreferences,navController )
        }
        composable(NavRoute.HomeScreen) {
            homeScreen(navController, sharedPreferences)
        }
        composable(NavRoute.tabSensores) {
            tabSensores(navController, sharedPreferences)
        }
        composable(NavRoute.tabDatos) {
            tabDatos(navController, sharedPreferences)
        }
        composable(NavRoute.TEXTSCREEN) {
            TextInput( )
        }
        composable(NavRoute.EJERCICIO) {
            Ejercicio(navController, sharedPreferences)
        }
        composable(NavRoute.REGISTROS) {
            tabRegistros(navController, sharedPreferences)
        }
    }
}


@Composable
fun homeScreen(navigation: NavController, sharedPreferences: SharedPreferences) {
    val state= rememberScrollState()
    LaunchedEffect(Unit) { state.animateScrollTo(40)}
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .verticalScroll(state),

        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(45.dp))
        Chip(
            label = { Text(
                fontFamily = amaticsc,
                fontWeight = FontWeight.Bold,
                text = "Ejercicio",
                textAlign = TextAlign.Center,
            ) },
            modifier = Modifier
                .fillMaxWidth(),
            onClick = { navigation.navigate(NavRoute.EJERCICIO) },
            colors = ChipDefaults.imageBackgroundChipColors(
                backgroundImagePainter = painterResource(id = R.drawable.ejercicio)
            ),
        )
        Spacer(modifier = Modifier.height(5.dp))
        Chip(
            label = { Text(
                fontFamily = amaticsc,
                fontWeight = FontWeight.Bold,
                text = "Sensores",
                textAlign = TextAlign.Center,
            ) },
            modifier = Modifier
                .fillMaxWidth(),
            onClick = { navigation.navigate(NavRoute.tabSensores) },
            colors = ChipDefaults.imageBackgroundChipColors(
                backgroundImagePainter = painterResource(id = R.drawable.ndice)
            ),
        )
        Spacer(modifier = Modifier.height(5.dp))
        Chip(
            label = { Text(
                fontFamily = amaticsc,
                fontWeight = FontWeight.Bold,
                text = "Datos",
                textAlign = TextAlign.Center,
            ) },
            modifier = Modifier
                .fillMaxWidth(),
            onClick = { navigation.navigate(NavRoute.tabDatos) },
            colors = ChipDefaults.imageBackgroundChipColors(
                backgroundImagePainter = painterResource(id = R.drawable.datos)
            )
        )
    }
}


@Composable
fun userscreen(username: String, sharedPreferences: SharedPreferences,navigation: NavController){
    var user = sharedPreferences.getString("username","defaultName")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            color = Color.White,
            text = "El usuario es $user ",
            textAlign = TextAlign.Center,
        )


        Button(onClick = {
            var editor = sharedPreferences.edit()
            editor.remove("username")
            editor.commit()
            navigation.navigate("home")
        }) {
            Text(text = "Borrar usuario")

        }
    }
}


@OptIn(ExperimentalPagerApi::class)
@Composable
fun tabSensores(navigation: NavController, SharedPreferences: SharedPreferences){
    val navController = rememberSwipeDismissableNavController()
    val pagerState = rememberPagerState(
        initialPage = 0
    )
    SwipeDismissableNavHost(
        navController = navController,
        modifier = Modifier.fillMaxSize(),
        startDestination = "second"
    ) {

        composable(route = "start") {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = { navigation.navigate(NavRoute.HomeScreen) })
                {
                    Icon(
                        imageVector = Icons.Rounded.PanoramaPhotosphere,
                        contentDescription = "airplane",
                        tint = Color.Black,
                        modifier = Modifier
                            .size(ButtonDefaults.DefaultButtonSize)
                            .wrapContentSize(align = Alignment.Center),
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "Menu Principal", color = Color.Red)
                }
            }
        }

        composable(NavRoute.TEXTSCREEN) {
            textScreen(SharedPreferences, navController)
        }


        composable(route = "second") {
            val state = rememberPagerState()
            val shape = if (LocalConfiguration.current.isScreenRound) CircleShape else null
            Box(Modifier.fillMaxSize()) {
                val paperState = rememberPagerState()
                HorizontalPager(
                    count = 3,
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    if (page == 0) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            getHearRateData().toFloatOrNull()?.let {
                                CircularProgressIndicator(
                                    startAngle = 295f,
                                    endAngle = 245f,
                                    progress = it/140f,
                                    strokeWidth = 5.dp,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(all = 10.dp),
                                    trackColor = Color.Blue,
                                    indicatorColor = Color.Red
                                )
                            }
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Button(
                                    modifier = Modifier.padding(top = 5.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = Color.Red
                                    ),
                                    onClick = { /**/}, // Ação
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Favorite,
                                        contentDescription = "airplane",
                                        tint = Color.Black,
                                        modifier = Modifier
                                            .size(ButtonDefaults.DefaultButtonSize)
                                            .wrapContentSize(align = Alignment.Center),
                                    )
                                }
                                Text(
                                    text = "Heart Rate",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.SansSerif,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(15.dp)
                                )
                                Text(
                                    text = getHearRateData(),
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.SansSerif,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(5.dp)
                                )
                            }
                        }
                    }
                    if (page == 1) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            getStepData().toFloatOrNull()?.let {
                                CircularProgressIndicator(
                                    startAngle = 295f,
                                    endAngle = 245f,
                                    progress = 295f,
                                    strokeWidth = 5.dp,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(all = 10.dp),
                                    trackColor = Color.Blue,
                                    indicatorColor = Color.Blue
                                )
                            }
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Button(
                                    modifier = Modifier.padding(top = 5.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = Color.White
                                    ),
                                    onClick = { /**/}, // Ação
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.FollowTheSigns,
                                        contentDescription = "airplane",
                                        tint = Color.Black,
                                        modifier = Modifier
                                            .size(ButtonDefaults.DefaultButtonSize)
                                            .wrapContentSize(align = Alignment.Center),
                                    )
                                }
                                Text(
                                    text = "Steps",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.SansSerif,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(15.dp)
                                )
                                Text(
                                    text = getStepData(),
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.SansSerif,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(5.dp)
                                )
                            }
                        }
                    }
                    if (page == 2) {
                        Column(    modifier = Modifier.fillMaxSize(),
                            //contentAlignment = Alignment.CenterStart,
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Button(
                                onClick = { navigation.navigate(NavRoute.HomeScreen){launchSingleTop=false
                                    popUpTo("start"){
                                        inclusive = true
                                    }
                                }
                                }, colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Color.Black
                                )
                            )
                            {
                                Icon(
                                    imageVector = Icons.Rounded.Undo,
                                    contentDescription = "airplane",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(ButtonDefaults.DefaultButtonSize)
                                        .wrapContentSize(align = Alignment.Center),
                                )
                            }
                            Text(text = "Regresar", color = Color.White)
                        }
                    }}
                HorizontalPagerIndicator(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(10.dp),
                    pagerState = pagerState,
                    activeColor = Color.Red,
                    inactiveColor = Color.Blue,
                    indicatorHeight = 7.dp,
                    indicatorWidth = 13.dp,
                )
            }
        }
    }
}


@OptIn(ExperimentalPagerApi::class)
@Composable
fun tabDatos(navigation: NavController, sharedPreferences: SharedPreferences){
    val navController = rememberSwipeDismissableNavController()
    val pagerState = rememberPagerState(
        initialPage = 0
    )
    SwipeDismissableNavHost(
        navController = navController,
        modifier = Modifier.fillMaxSize(),
        startDestination = "start"
    ) {

        composable(route = "start") {
            var peso= sharedPreferences.getString("PESO", "defaultPeso")
            var edad= sharedPreferences.getString("EDAD", "defaultEdad")
            var altura= sharedPreferences.getString("ALTURA", "defaultAltura")
            var sexo= sharedPreferences.getString("SEXO", "defaultSexo")
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                Text(text = "Tu peso es: ${peso.toString()} kg.", color = Color.White)
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "Tu edad es: ${edad.toString()} años.", color = Color.White)
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "Tu altura es: ${altura.toString()} cm.", color = Color.White)
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "Tu sexo es: ${sexo.toString()} ", color = Color.White)
                Spacer(modifier = Modifier.height(10.dp))
                Row() {
                    Button (
                        modifier = Modifier.size(50.dp, 50.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.Blue
                        ),
                        onClick = { navController.navigate("second") })
                    {
                        Icon(
                            imageVector = Icons.Rounded.BorderColor,
                            contentDescription = "airplane",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(ButtonDefaults.DefaultButtonSize)
                                .wrapContentSize(align = Alignment.Center),
                        )
                        Spacer(modifier =Modifier.width(10.dp))
                        //Text(text = "Editar", color = Color.Red)
                    }
                    Button (
                        modifier = Modifier.size(50.dp, 50.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.Green
                        ),
                        onClick = { navigation.navigate(NavRoute.HomeScreen) })
                    {
                        Icon(
                            imageVector = Icons.Rounded.Roofing,
                            contentDescription = "airplane",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(ButtonDefaults.DefaultButtonSize)
                                .wrapContentSize(align = Alignment.Center),
                        )
                        Spacer(modifier =Modifier.width(10.dp))
                        // Text(text = "Menu principal", color = Color.Red)
                    }
                }

            }


        }

        composable(route = "second") {
            val state = rememberPagerState()
            val shape = if (LocalConfiguration.current.isScreenRound) CircleShape else null
            Box(Modifier.fillMaxSize()) {
                val paperState = rememberPagerState()
                HorizontalPager(
                    count = 5,
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    if (page == 0) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
                            val ctx = LocalContext.current;
                            var items = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                                "11", "12", "13", "14", "15", "16", "17", "18","19",
                                "20","21", "22","23","24","25","26","27","28","29","30",
                                "31","32","33","34","35","36","37","38","39","40",
                                "41","42","43","44","45","46","47","48","49","50",
                                "51","52","53","54","55","56","57","58","59","60",
                                "61","62","63","64","65","66","67","68","69","70",
                                "71","72","73","74","75","76","77","78","79","80",
                                "81","82","83","84","85","86","87","88","89","90",
                                "91","92","93","94","95","96","97","98","99","100"        )
                            val state = rememberPickerState(items.size)
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                /*Text(
                                    modifier = Modifier
                                        .align(Alignment.TopCenter)
                                        .padding(top = 10.dp),
                                    text = "selected: ${items[state.selectedOption]}"
                                )*/
                                Picker(
                                    state = state,
                                    modifier = Modifier
                                        .size(100.dp, 100.dp)
                                        .padding(top = 20.dp),
                                    gradientColor = Color.Blue,
                                    gradientRatio = 0.5f,
                                ) {
                                    Text(items[it], modifier = Modifier.padding(10.dp))
                                }

                            }

                            //picker 2
                            val items2 =listOf("00","100", "200", "300", "400", "500", "600", "700", "800", "900",
                                "00","100", "200", "300", "400", "500", "600", "700", "800", "900",
                                "00","100", "200", "300", "400", "500", "600", "700", "800", "900",
                                "00","100", "200", "300", "400", "500", "600", "700", "800", "900",
                                "00","100", "200", "300", "400", "500", "600", "700", "800", "900",
                                "00","100", "200", "300", "400", "500", "600", "700", "800", "900",
                                "00","100", "200", "300", "400", "500", "600", "700", "800", "900",
                                "00","100", "200", "300", "400", "500", "600", "700", "800", "900",
                                "00","100", "200", "300", "400", "500", "600", "700", "800", "900",
                                "00","100", "200", "300", "400", "500", "600", "700", "800", "900")
                            val state2 = rememberPickerState(items.size)
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    modifier = Modifier
                                        .align(Alignment.TopCenter)
                                        .padding(top = 25.dp),
                                    text =  "kg",
                                    fontWeight = FontWeight.Thin,
                                    fontFamily = raleway,
                                )
                                //Spacer(modifier = Modifier.height(20.dp))
                                Text(
                                    modifier = Modifier
                                        .align(Alignment.TopCenter)
                                        .padding(top = 48.dp),
                                    fontSize = 13.sp,
                                    fontFamily = dancingscript,
                                    text = "${items[state.selectedOption]+"."+items2[state2.selectedOption]}"
                                )
                                //   Spacer(modifier = Modifier.height(20.dp))
                                Picker(
                                    state = state2,
                                    modifier = Modifier
                                        .size(100.dp, 100.dp)
                                        .padding(top = 20.dp),
                                    gradientColor = Color.LightGray,
                                    gradientRatio = 0.5f,

                                    ) {
                                    Text(items2[it], modifier = Modifier.padding(10.dp))
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .height(10.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    var position = state.selectedOption
                                    var position2 = state2.selectedOption
                                    var value = items[position] +"."+ items2[position2]
                                    Button(
                                        onClick = {
                                            Toast.makeText(
                                                ctx,
                                                "Su peso es de: $value kg",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            var editor = sharedPreferences.edit()
                                            editor.putString("PESO", value)
                                            editor.commit()
//                                            navigation.navigate("detailScreenpeso/$value")
                                        }, colors = ButtonDefaults.buttonColors(
                                            backgroundColor = Color.Blue,
                                            contentColor = Color.Magenta
                                        )
                                    ) {
                                        Icon(imageVector = Icons.Rounded.Save,
                                            contentDescription = "Next",
                                            tint = Color.LightGray)

                                    }

                                }
                            }
                        }
                    }
                    if (page == 1) {
                        val ctx = LocalContext.current;
                        var items = listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
                            "10", "11", "12", "13", "14", "15", "16","17", "18","19",
                            "20", "21", "22","23","24","25","26","27","28","29",
                            "30", "31", "32","33","34","35","36","37","38","39",
                            "40", "41","42","43","44","45","46","47","48","49","50",
                            "51","52","53","54","55","56","57","58","59","60",
                            "61","62","63","64","65","66","67","68","69","70",
                            "71","72","73","74","75","76","77","78","79","80",
                            "81","82","83","84","85","86","87","88","89","90",
                            "91","92","93","94","95","96","97","98","99")
                        val state = rememberPickerState(items.size)

                        Column(
                            modifier = Modifier.fillMaxSize(),
                            //contentAlignment = Alignment.CenterStart,
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally

                        ) {
                            Spacer(modifier = Modifier.height(20.dp))
                            Picker(
                                state = state,

                                modifier = Modifier.size(220.dp, 70.dp),
                                gradientColor = Color.Green

                            ) {

                                Text(items[it], modifier = Modifier.padding(10.dp))
                            }

                        }
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = 20.dp),
                                fontFamily = raleway,
                                fontWeight = FontWeight.Thin,
                                text =  "Edad"
                            )
                            //Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = 38.dp),
                                fontFamily = raleway,
                                fontWeight = FontWeight.Thin,
                                text = "${items[state.selectedOption]}"
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .height(10.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                var position = state.selectedOption
                                var value = items[position]
                                Button(
                                    onClick = {
                                        Toast.makeText(
                                            ctx,
                                            "Su peso es de: $value kg",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        var editor = sharedPreferences.edit()
                                        editor.putString("EDAD", value)
                                        editor.commit()
                                        //navigation.navigate("detailScreenedad/$value")
                                    }, colors = ButtonDefaults.buttonColors(
                                        backgroundColor = Color.Black,
                                        contentColor = Color.Black
                                    )
                                ) {
                                    Icon(imageVector = Icons.Rounded.Save,
                                        contentDescription = "Next",
                                        tint = Color.LightGray)
                                }

                            }
                        }
                    }
                    if (page == 2) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
                            val ctx = LocalContext.current;
                            var items = listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
                                "10", "11", "12", "13", "14", "15", "16","17", "18","19",
                                "20", "21", "22","23","24","25","26","27","28","29",
                                "30", "31", "32","33","34","35","36","37","38","39",
                                "40", "41","42","43","44","45","46","47","48","49","50",
                                "51","52","53","54","55","56","57","58","59","60",
                                "61","62","63","64","65","66","67","68","69","70",
                                "71","72","73","74","75","76","77","78","79","80",
                                "81","82","83","84","85","86","87","88","89","90",
                                "91","92","93","94","95","96","97","98","99")
                            val state = rememberPickerState(items.size)
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                /*Text(
                                    modifier = Modifier
                                        .align(Alignment.TopCenter)
                                        .padding(top = 10.dp),
                                    text = "selected: ${items[state.selectedOption]}"
                                )*/
                                Picker(
                                    state = state,
                                    modifier = Modifier
                                        .size(150.dp, 100.dp)
                                        .padding(top = 20.dp),
                                    gradientColor = Color.Blue,
                                    gradientRatio = 0.5f,
                                ) {
                                    Text(items[it], modifier = Modifier.padding(10.dp))
                                }

                            }

                            //picker 2
                            var items2 = listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
                                "10", "11", "12", "13", "14", "15", "16","17", "18","19",
                                "20", "21", "22","23","24","25","26","27","28","29",
                                "30", "31", "32","33","34","35","36","37","38","39",
                                "40", "41","42","43","44","45","46","47","48","49","50",
                                "51","52","53","54","55","56","57","58","59","60",
                                "61","62","63","64","65","66","67","68","69","70",
                                "71","72","73","74","75","76","77","78","79","80",
                                "81","82","83","84","85","86","87","88","89","90",
                                "91","92","93","94","95","96","97","98","99")
                            val state2 = rememberPickerState(items.size)
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    modifier = Modifier
                                        .align(Alignment.TopCenter)
                                        .padding(top = 25.dp),
                                    text =  "m",
                                    fontWeight = FontWeight.Thin,
                                    fontFamily = raleway                                    )
                                //Spacer(modifier = Modifier.height(20.dp))
                                Text(
                                    modifier = Modifier
                                        .align(Alignment.TopCenter)
                                        .padding(top = 48.dp),
                                    fontSize = 13.sp,
                                    fontFamily = dancingscript,
                                    text = "${items[state.selectedOption]+"."+items2[state2.selectedOption]}"
                                )
                                //   Spacer(modifier = Modifier.height(20.dp))
                                Picker(

                                    state = state2,
                                    modifier = Modifier
                                        .size(150.dp, 100.dp)
                                        .padding(top = 20.dp),
                                    gradientColor = Color.LightGray,
                                    gradientRatio = 0.5f,

                                    ) {
                                    Text(items2[it], modifier = Modifier.padding(10.dp))
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .height(10.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    var position = state.selectedOption
                                    var position2 = state2.selectedOption
                                    var value = items[position] +"."+ items2[position2]
                                    Button(
                                        onClick = {
                                            Toast.makeText(
                                                ctx,
                                                "Su altura es de: $value cm",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            var editor = sharedPreferences.edit()
                                            editor.putString("ALTURA", value)
                                            editor.commit()
                                            //navigation.navigate("detailScreenpeso/$value")
                                        }, colors = ButtonDefaults.buttonColors(
                                            backgroundColor = Color.Blue,
                                            contentColor = Color.Magenta
                                        )
                                    ) {
                                        Icon(imageVector = Icons.Rounded.Save,
                                            contentDescription = "Next",
                                            tint = Color.LightGray)
                                    }
                                }
                            }
                        }
                    }
                    if (page==3){
                        val ctx = LocalContext.current;
                        var items = listOf("Masculino", "Femenino")
                        val state = rememberPickerState(items.size)
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            //contentAlignment = Alignment.CenterStart,
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(20.dp))
                            Picker(
                                state = state,

                                modifier = Modifier.size(220.dp, 70.dp),
                                gradientColor = Color.Green
                            ) {
                                Text(items[it], modifier = Modifier.padding(10.dp))
                            }
                        }
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = 20.dp),
                                fontFamily = raleway,
                                fontWeight = FontWeight.Thin,
                                text =  "Sexo"
                            )
                            //Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = 38.dp),
                                fontFamily = raleway,
                                fontWeight = FontWeight.Thin,
                                text = "${items[state.selectedOption]}"
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .height(10.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                var position = state.selectedOption
                                var value = items[position]
                                Button(
                                    onClick = {
                                        Toast.makeText(
                                            ctx,
                                            "Su sexo es de: $value ",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        var editor = sharedPreferences.edit()
                                        editor.putString("SEXO", value)
                                        editor.commit()
                                        //navigation.navigate("detailScreenedad/$value")
                                    }, colors = ButtonDefaults.buttonColors(
                                        backgroundColor = Color.Black,
                                        contentColor = Color.Black
                                    )
                                ) {
                                    Icon(imageVector = Icons.Rounded.Save,
                                        contentDescription = "Next",
                                        tint = Color.LightGray)
                                }

                            }
                        }
                    }
                    if (page == 4) {
                        Column(    modifier = Modifier.fillMaxSize(),
                            //contentAlignment = Alignment.CenterStart,
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Button(
                                onClick = { navController.navigate("start"){launchSingleTop=false
                                    popUpTo("start"){
                                        inclusive = false
                                    }
                                }
                                }, colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Color.Black,
                                    contentColor = Color.Black
                                ))
                            {
                                Icon(
                                    imageVector = Icons.Rounded.Undo,
                                    contentDescription = "airplane",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(ButtonDefaults.DefaultButtonSize)
                                        .wrapContentSize(align = Alignment.Center),
                                )
                            }
                            //Spacer(modifier =Modifier.height(.dp))
                            Text(text = "Regresar", color = Color.White)
                        }

                    }
                }
                HorizontalPagerIndicator(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(10.dp),
                    pagerState = pagerState,
                    activeColor = Color.Red,
                    inactiveColor = Color.Blue,
                    indicatorHeight = 7.dp,
                    indicatorWidth = 13.dp,
                )
            }
        }
    }
}


@OptIn(ExperimentalPagerApi::class)
@Composable
fun tabRegistros(navigation: NavController, sharedPreferences: SharedPreferences){
    val navController = rememberSwipeDismissableNavController()
    val pagerState = rememberPagerState(
        initialPage = 0
    )
    SwipeDismissableNavHost(
        navController = navController,
        modifier = Modifier.fillMaxSize(),
        startDestination = "start"
    ) {

        composable(route = "start") {
            val valor="Sin datos"
            var registro=sharedPreferences.getString("REGISTRO", valor)
            val state= rememberScrollState()
            LaunchedEffect(Unit) { state.animateScrollTo(40)}
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background)
                    .verticalScroll(state)
            ) {

                Text(text = "${registro.toString()}", color = Color.White)
                Spacer(modifier = Modifier.height(10.dp))

            }


        }

        composable(route = "second") {
            val state = rememberPagerState()
            val shape = if (LocalConfiguration.current.isScreenRound) CircleShape else null
            Box(Modifier.fillMaxSize()) {
                val paperState = rememberPagerState()
                HorizontalPager(
                    count = 5,
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    if (page == 0) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
                            val ctx = LocalContext.current;
                            var items = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                                "11", "12", "13", "14", "15", "16", "17", "18","19",
                                "20","21", "22","23","24","25","26","27","28","29","30",
                                "31","32","33","34","35","36","37","38","39","40",
                                "41","42","43","44","45","46","47","48","49","50",
                                "51","52","53","54","55","56","57","58","59","60",
                                "61","62","63","64","65","66","67","68","69","70",
                                "71","72","73","74","75","76","77","78","79","80",
                                "81","82","83","84","85","86","87","88","89","90",
                                "91","92","93","94","95","96","97","98","99","100"        )
                            val state = rememberPickerState(items.size)
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                /*Text(
                                    modifier = Modifier
                                        .align(Alignment.TopCenter)
                                        .padding(top = 10.dp),
                                    text = "selected: ${items[state.selectedOption]}"
                                )*/
                                Picker(
                                    state = state,
                                    modifier = Modifier
                                        .size(100.dp, 100.dp)
                                        .padding(top = 20.dp),
                                    gradientColor = Color.Blue,
                                    gradientRatio = 0.5f,
                                ) {
                                    Text(items[it], modifier = Modifier.padding(10.dp))
                                }

                            }

                            //picker 2
                            val items2 =listOf("00","100", "200", "300", "400", "500", "600", "700", "800", "900",
                                "00","100", "200", "300", "400", "500", "600", "700", "800", "900",
                                "00","100", "200", "300", "400", "500", "600", "700", "800", "900",
                                "00","100", "200", "300", "400", "500", "600", "700", "800", "900",
                                "00","100", "200", "300", "400", "500", "600", "700", "800", "900",
                                "00","100", "200", "300", "400", "500", "600", "700", "800", "900",
                                "00","100", "200", "300", "400", "500", "600", "700", "800", "900",
                                "00","100", "200", "300", "400", "500", "600", "700", "800", "900",
                                "00","100", "200", "300", "400", "500", "600", "700", "800", "900",
                                "00","100", "200", "300", "400", "500", "600", "700", "800", "900")
                            val state2 = rememberPickerState(items.size)
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    modifier = Modifier
                                        .align(Alignment.TopCenter)
                                        .padding(top = 25.dp),
                                    text =  "kg",
                                    fontWeight = FontWeight.Thin,
                                    fontFamily = raleway,
                                )
                                //Spacer(modifier = Modifier.height(20.dp))
                                Text(
                                    modifier = Modifier
                                        .align(Alignment.TopCenter)
                                        .padding(top = 48.dp),
                                    fontSize = 13.sp,
                                    fontFamily = dancingscript,
                                    text = "${items[state.selectedOption]+"."+items2[state2.selectedOption]}"
                                )
                                //   Spacer(modifier = Modifier.height(20.dp))
                                Picker(
                                    state = state2,
                                    modifier = Modifier
                                        .size(100.dp, 100.dp)
                                        .padding(top = 20.dp),
                                    gradientColor = Color.LightGray,
                                    gradientRatio = 0.5f,

                                    ) {
                                    Text(items2[it], modifier = Modifier.padding(10.dp))
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .height(10.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    var position = state.selectedOption
                                    var position2 = state2.selectedOption
                                    var value = items[position] +"."+ items2[position2]
                                    Button(
                                        onClick = {
                                            Toast.makeText(
                                                ctx,
                                                "Su peso es de: $value kg",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            var editor = sharedPreferences.edit()
                                            editor.putString("PESO", value)
                                            editor.commit()
//                                            navigation.navigate("detailScreenpeso/$value")
                                        }, colors = ButtonDefaults.buttonColors(
                                            backgroundColor = Color.Blue,
                                            contentColor = Color.Magenta
                                        )
                                    ) {
                                        Icon(imageVector = Icons.Rounded.Save,
                                            contentDescription = "Next",
                                            tint = Color.LightGray)

                                    }

                                }
                            }
                        }
                    }
                    if (page == 1) {
                        val ctx = LocalContext.current;
                        var items = listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
                            "10", "11", "12", "13", "14", "15", "16","17", "18","19",
                            "20", "21", "22","23","24","25","26","27","28","29",
                            "30", "31", "32","33","34","35","36","37","38","39",
                            "40", "41","42","43","44","45","46","47","48","49","50",
                            "51","52","53","54","55","56","57","58","59","60",
                            "61","62","63","64","65","66","67","68","69","70",
                            "71","72","73","74","75","76","77","78","79","80",
                            "81","82","83","84","85","86","87","88","89","90",
                            "91","92","93","94","95","96","97","98","99")
                        val state = rememberPickerState(items.size)

                        Column(
                            modifier = Modifier.fillMaxSize(),
                            //contentAlignment = Alignment.CenterStart,
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally

                        ) {
                            Spacer(modifier = Modifier.height(20.dp))
                            Picker(
                                state = state,

                                modifier = Modifier.size(220.dp, 70.dp),
                                gradientColor = Color.Green

                            ) {

                                Text(items[it], modifier = Modifier.padding(10.dp))
                            }

                        }
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = 20.dp),
                                fontFamily = raleway,
                                fontWeight = FontWeight.Thin,
                                text =  "Edad"
                            )
                            //Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = 38.dp),
                                fontFamily = raleway,
                                fontWeight = FontWeight.Thin,
                                text = "${items[state.selectedOption]}"
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .height(10.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                var position = state.selectedOption
                                var value = items[position]
                                Button(
                                    onClick = {
                                        Toast.makeText(
                                            ctx,
                                            "Su peso es de: $value kg",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        var editor = sharedPreferences.edit()
                                        editor.putString("EDAD", value)
                                        editor.commit()
                                        //navigation.navigate("detailScreenedad/$value")
                                    }, colors = ButtonDefaults.buttonColors(
                                        backgroundColor = Color.Black,
                                        contentColor = Color.Black
                                    )
                                ) {
                                    Icon(imageVector = Icons.Rounded.Save,
                                        contentDescription = "Next",
                                        tint = Color.LightGray)
                                }

                            }
                        }
                    }
                    if (page == 2) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
                            val ctx = LocalContext.current;
                            var items = listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
                                "10", "11", "12", "13", "14", "15", "16","17", "18","19",
                                "20", "21", "22","23","24","25","26","27","28","29",
                                "30", "31", "32","33","34","35","36","37","38","39",
                                "40", "41","42","43","44","45","46","47","48","49","50",
                                "51","52","53","54","55","56","57","58","59","60",
                                "61","62","63","64","65","66","67","68","69","70",
                                "71","72","73","74","75","76","77","78","79","80",
                                "81","82","83","84","85","86","87","88","89","90",
                                "91","92","93","94","95","96","97","98","99")
                            val state = rememberPickerState(items.size)
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                /*Text(
                                    modifier = Modifier
                                        .align(Alignment.TopCenter)
                                        .padding(top = 10.dp),
                                    text = "selected: ${items[state.selectedOption]}"
                                )*/
                                Picker(
                                    state = state,
                                    modifier = Modifier
                                        .size(150.dp, 100.dp)
                                        .padding(top = 20.dp),
                                    gradientColor = Color.Blue,
                                    gradientRatio = 0.5f,
                                ) {
                                    Text(items[it], modifier = Modifier.padding(10.dp))
                                }

                            }

                            //picker 2
                            var items2 = listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
                                "10", "11", "12", "13", "14", "15", "16","17", "18","19",
                                "20", "21", "22","23","24","25","26","27","28","29",
                                "30", "31", "32","33","34","35","36","37","38","39",
                                "40", "41","42","43","44","45","46","47","48","49","50",
                                "51","52","53","54","55","56","57","58","59","60",
                                "61","62","63","64","65","66","67","68","69","70",
                                "71","72","73","74","75","76","77","78","79","80",
                                "81","82","83","84","85","86","87","88","89","90",
                                "91","92","93","94","95","96","97","98","99")
                            val state2 = rememberPickerState(items.size)




                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    modifier = Modifier
                                        .align(Alignment.TopCenter)
                                        .padding(top = 25.dp),
                                    text =  "m",
                                    fontWeight = FontWeight.Thin,
                                    fontFamily = raleway,


                                    )
                                //Spacer(modifier = Modifier.height(20.dp))
                                Text(
                                    modifier = Modifier
                                        .align(Alignment.TopCenter)
                                        .padding(top = 48.dp),
                                    fontSize = 13.sp,
                                    fontFamily = dancingscript,
                                    text = "${items[state.selectedOption]+"."+items2[state2.selectedOption]}"
                                )
                                //   Spacer(modifier = Modifier.height(20.dp))
                                Picker(

                                    state = state2,
                                    modifier = Modifier
                                        .size(150.dp, 100.dp)
                                        .padding(top = 20.dp),
                                    gradientColor = Color.LightGray,
                                    gradientRatio = 0.5f,

                                    ) {
                                    Text(items2[it], modifier = Modifier.padding(10.dp))
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .height(10.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    var position = state.selectedOption
                                    var position2 = state2.selectedOption
                                    var value = items[position] +"."+ items2[position2]
                                    Button(
                                        onClick = {
                                            Toast.makeText(
                                                ctx,
                                                "Su altura es de: $value cm",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            var editor = sharedPreferences.edit()
                                            editor.putString("ALTURA", value)
                                            editor.commit()
                                            //navigation.navigate("detailScreenpeso/$value")
                                        }, colors = ButtonDefaults.buttonColors(
                                            backgroundColor = Color.Blue,
                                            contentColor = Color.Magenta
                                        )
                                    ) {
                                        Icon(imageVector = Icons.Rounded.Save,
                                            contentDescription = "Next",
                                            tint = Color.LightGray)
                                    }
                                }
                            }
                        }
                    }
                    if (page==3){

                        val ctx = LocalContext.current;
                        var items = listOf("Masculino", "Femenino")
                        val state = rememberPickerState(items.size)
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            //contentAlignment = Alignment.CenterStart,
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(20.dp))
                            Picker(
                                state = state,

                                modifier = Modifier.size(220.dp, 70.dp),
                                gradientColor = Color.Green
                            ) {
                                Text(items[it], modifier = Modifier.padding(10.dp))
                            }
                        }
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = 20.dp),
                                fontFamily = raleway,
                                fontWeight = FontWeight.Thin,
                                text =  "Sexo"
                            )
                            //Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = 38.dp),
                                fontFamily = raleway,
                                fontWeight = FontWeight.Thin,
                                text = "${items[state.selectedOption]}"
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .height(10.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                var position = state.selectedOption
                                var value = items[position]
                                Button(
                                    onClick = {
                                        Toast.makeText(
                                            ctx,
                                            "Su sexo es de: $value ",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        var editor = sharedPreferences.edit()
                                        editor.putString("SEXO", value)
                                        editor.commit()
                                        //navigation.navigate("detailScreenedad/$value")
                                    }, colors = ButtonDefaults.buttonColors(
                                        backgroundColor = Color.Black,
                                        contentColor = Color.Black
                                    )
                                ) {
                                    Icon(imageVector = Icons.Rounded.Save,
                                        contentDescription = "Next",
                                        tint = Color.LightGray)
                                }

                            }
                        }
                    }
                    if (page == 4) {
                        Column(    modifier = Modifier.fillMaxSize(),
                            //contentAlignment = Alignment.CenterStart,
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Button(
                                onClick = { navController.navigate("start"){launchSingleTop=false
                                    popUpTo("start"){
                                        inclusive = false
                                    }
                                }
                                }, colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Color.Black
                                )
                            )
                            {
                                Icon(
                                    imageVector = Icons.Rounded.Undo,
                                    contentDescription = "airplane",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(ButtonDefaults.DefaultButtonSize)
                                        .wrapContentSize(align = Alignment.Center),
                                )
                            }
                            Spacer(modifier =Modifier.height(10.dp))
                            Text(text = "Regresar", color = Color.White)
                        }
                    }
                }
                HorizontalPagerIndicator(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(10.dp),
                    pagerState = pagerState,
                    activeColor = Color.Red,
                    inactiveColor = Color.Blue,
                    indicatorHeight = 7.dp,
                    indicatorWidth = 13.dp,
                )
            }
        }
    }
}


@OptIn(ExperimentalPagerApi::class)
@Composable
fun Ejercicio(navigation: NavController, sharedPreferences: SharedPreferences) {
    val navController = rememberSwipeDismissableNavController()
    val pagerState = rememberPagerState(
        initialPage = 0
    )
    SwipeDismissableNavHost(
        navController = navController,
        modifier = Modifier.fillMaxSize(),
        startDestination = "start"
    ) {
        composable(route = "start") {
            val state= rememberScrollState()
            LaunchedEffect(Unit) { state.animateScrollTo(40)}
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background)
                    .verticalScroll(state),

                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(45.dp))
                Chip(
                    label = {
                        Text(
                            fontFamily = amaticsc,
                            fontWeight = FontWeight.Bold,
                            text = "Ejercitar",
                            textAlign = TextAlign.Center,
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = { navController.navigate("second") },
                    colors = ChipDefaults.imageBackgroundChipColors(
                        backgroundImagePainter = painterResource(id = R.drawable.brandon)
                    ),
                )
                Spacer(modifier = Modifier.height(5.dp))
                Chip(
                    label = {
                        Text(
                            fontFamily = amaticsc,
                            fontWeight = FontWeight.Bold,
                            text = "Registros",
                            textAlign = TextAlign.Center,
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = { navigation.navigate(NavRoute.REGISTROS) },
                    colors = ChipDefaults.imageBackgroundChipColors(
                        backgroundImagePainter = painterResource(id = R.drawable.registro)
                    ),
                )
            }
        }

        composable(route = "second") {
            val state = rememberPagerState()
            val shape = if (LocalConfiguration.current.isScreenRound) CircleShape else null
            Box(Modifier.fillMaxSize()) {
                val paperState = rememberPagerState()
                HorizontalPager(
                    count = 2,
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    if (page == 1) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            //contentAlignment = Alignment.CenterStart,
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Button(
                                onClick = {
                                    navController.navigate("start") {
                                        launchSingleTop = false
                                        popUpTo("start") {
                                            inclusive = false
                                        }
                                    }
                                }, colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Color.Black
                                )
                            )
                            {
                                Icon(
                                    imageVector = Icons.Rounded.Undo,
                                    contentDescription = "airplane",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(ButtonDefaults.DefaultButtonSize)
                                        .wrapContentSize(align = Alignment.Center),
                                )
                            }
                            //Spacer(modifier = Modifier.height(10.dp))
                            Text(text = "Regresar", color = Color.White)
                        }
                    }
                    if (page == 0) {
                        val state= rememberScrollState()
                        LaunchedEffect(Unit) { state.animateScrollTo(40)}
                        Box(modifier = Modifier.fillMaxSize()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colors.background)
                                    .verticalScroll(state),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                calorias(sharedPreferences, totalTime = 0L)
                            }
                        }

                    }
                }


                HorizontalPagerIndicator(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(10.dp),
                    pagerState = pagerState,
                    activeColor = Color.Red,
                    inactiveColor = Color.Blue,
                    indicatorHeight = 7.dp,
                    indicatorWidth = 13.dp,
                )

            }
        }

    }
}



val count: MutableState<Float> = mutableStateOf(0f)

////////////SENSORES
@Composable
fun getStepData():String{
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
        mutableStateOf("")
    }
    val stepSensorListener = object: SensorEventListener {
        override fun onSensorChanged(p0: SensorEvent?) {
            p0?:return
            p0.values.firstOrNull()?.let {
                stepvalue.value= p0.values[0].toInt().toString()
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
    return stepvalue.value
}

@Composable
fun getHearRateData():String{
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


@Composable
fun calorias(
    sharedPreferences: SharedPreferences,
// total time of the timer
    totalTime: Long,
    // set initial value to 1
    initialValue: Float = 1f,
) {

    var resettime: Float = count.value
    // create variable for
    // size of the composable
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }
    // create variable for value
    var value by remember {
        mutableStateOf(initialValue)
    }
    // create variable for current time
    var currentTime by remember {
        mutableStateOf(totalTime)
    }
    // create variable for isTimerRunning
    var isTimerRunning by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = currentTime, key2 = isTimerRunning) {
        if(currentTime >= 0 && isTimerRunning) {
            delay(100L)
            currentTime += 100L
            value = currentTime / totalTime.toFloat()
            resettime+=1
        }
    }
    val ctx = LocalContext.current;
    var peso= sharedPreferences.getString("PESO", "defaultPeso")
    var edad= sharedPreferences.getString("EDAD", "defaultEdad")
    var altura= sharedPreferences.getString("ALTURA", "defaultAltura")
    var sexo= sharedPreferences.getString("SEXO", "defaultSexo")

    var calorias= 0.029*(peso?.toDoubleOrNull() ?:1 *2.2)*((currentTime / 1000L)/60)
    var distancia = getStepData().toDoubleOrNull() ?:1  * 0.000762

    // draw the timer
    Column(modifier= Modifier
        .fillMaxSize()
        .background(MaterialTheme.colors.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally){

        /* +(currentTime / 1000L).toString(),*/
        Spacer(modifier =Modifier.height(18.dp))
        Button(
            modifier = Modifier
                .padding(top = 5.dp)
                .size(30.dp, 30.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.White
            ),
            onClick = { /**/ }, // Ação
        ) {
            Icon(
                imageVector = Icons.Rounded.Favorite,
                contentDescription = "airplane",
                tint = Color.Black,
                modifier = Modifier
                    .size(ButtonDefaults.DefaultButtonSize)
                    .wrapContentSize(align = Alignment.Center),
            )
        }
        Text(text = "Calorias quemadas: "+calorias.toString(), fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White)
        Button(
            modifier = Modifier
                .padding(top = 5.dp)
                .size(30.dp, 30.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.White
            ),
            onClick = { /**/ }, // Ação
        ) {
            Icon(
                imageVector = Icons.Rounded.FollowTheSigns,
                contentDescription = "airplane",
                tint = Color.Black,
                modifier = Modifier
                    .size(ButtonDefaults.DefaultButtonSize)
                    .wrapContentSize(align = Alignment.Center),
            )
        }
        Text(text = "Distancia: "+distancia.toString(), fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White)
        Row() {
            Button( modifier = Modifier
                .padding(top = 5.dp)
                .size(70.dp, 30.dp),
                onClick = {
                    if(currentTime <= 0L) {
                        currentTime = totalTime
                        isTimerRunning = true
                    } else {
                        isTimerRunning = !isTimerRunning
                    }
                },

                // change button color
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (!isTimerRunning || currentTime <= 0L) {
                        Color.Green
                    } else {
                        Color.Red
                    }
                )
            ) {
                Text(
                    // change the text of button based on values
                    text = if (isTimerRunning && currentTime >= 0L) "Stop"
                    else if (!isTimerRunning && currentTime >= 0L) "Start"
                    else "Restart"
                )
            }
            Button( modifier = Modifier
                .padding(top = 5.dp)
                .size(70.dp, 30.dp),
                onClick = {


                    isTimerRunning = false
                    currentTime = 0L
                    resettime=0f

                    val sdf = SimpleDateFormat("'Date\n'dd-MM-yyyy '\n\nand\n\nTime\n'HH:mm:ss z")
                    val currentDateAndTime = sdf.format(Date())
                    var registro= "$currentDateAndTime Calorias $calorias Distancia $distancia"
                    var aux= sharedPreferences.getString("REGISTRO", "defaultRegistro")
                    aux= aux +"\n"+registro
                    var editor = sharedPreferences.edit()
                    editor.putString("REGISTRO", aux)
                    editor.commit()
                    Toast.makeText(ctx, "$registro", Toast.LENGTH_SHORT).show()
                },

                // change button color
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Gray

                )
            ) {
                Text(
                    // change the text of button based on values
                    text = "Restart"
                )
            }
        }
        Text(
            text =(currentTime / 1000L).toString() +" seg",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

    }
    // add value of the timer

    // create button to start or stop the timer

}
////////////////////////////CALORIAS/////////////////////////////



@Composable
fun  textScreen(SharedPreferences: SharedPreferences, navigation:NavController){
    Column(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colors.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        TextInput()
    }
}


@Composable
fun TextInput() {
    val label = remember { mutableStateOf("Start") }
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.let { data ->
                val results: Bundle? = RemoteInput.getResultsFromIntent(data)
                val ipAddress: CharSequence? = results?.getCharSequence("ip_address")
                label.value = ipAddress as String
            }
        }
    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
    ){
        Spacer(modifier =Modifier.height(20.dp))
        Chip(
            label = { Text(label.value) },
            onClick = {},
        )
        Spacer(modifier =Modifier.height(20.dp))
        Chip(label={Text("Insert Data")},
            onClick = {
                val intent : Intent = RemoteInputIntentHelper.createActionRemoteInputIntent();
                val remoteInputs:List<RemoteInput> = listOf(
                    RemoteInput.Builder("ip_address")
                        .setLabel("Manual IP Entry").wearableExtender{
                            setEmojisAllowed(false)
                            setInputActionType(EditorInfo.IME_ACTION_DONE)
                        }.build()
                )
                RemoteInputIntentHelper.putRemoteInputsExtra(intent,remoteInputs)
                launcher.launch(intent)
            } )

    }
}



