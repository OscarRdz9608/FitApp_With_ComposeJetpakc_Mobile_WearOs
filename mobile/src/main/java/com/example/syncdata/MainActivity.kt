package com.example.syncdata

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState


import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.google.accompanist.pager.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.time.format.TextStyle

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPagerApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScreenMain()
            //TabLayout()
        }
    }
}
/*
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
*/

@Composable
fun WearApp() {
 //   val viewModel = workOutviewmodel()
    //workOutScreen(viewModel)
    //Home()

}

/*
@Composable
fun workOutList(workoutData: List<workoutData>) {

    Column {
        workoutData.forEachIndexed { index, workoutData ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = workoutData.name,
                    modifier = Modifier.weight(1f),
                    color = Color.Magenta,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = workoutData.reps.toString(),
                    modifier = Modifier.weight(1f),
                    color = Color.Magenta,
                    textAlign = TextAlign.Center
                )
                /*Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.White
                    )
                }
            }
        }
    }
}
*/

@Composable
fun workOutScreen(viewModel: workOutviewmodel ) {
    viewModel.getData()
    val index = viewModel.workoutData.value.size
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                viewModel.writeToDB(workoutData("Pushups", 10), index)
            }) {
                Text(text = "Pushups")
            }
            Button(onClick = {
                viewModel.writeToDB(workoutData("Situps", 10), index)
            }) {
                Text(text = "Situps")
            }
            Button(onClick = {
                viewModel.writeToDB(workoutData("Squats", 10), index)
            }) {
                Text(text = "Squats")
            }
            //Boton para refrescar la lista
            Button(onClick = {
                viewModel.getData()
            }) {
                Text(text = "Refresh")
            }
            //Boton para borrar la lista
            Button(onClick = {
                viewModel.writeToDB(workoutData(), 0)
            }) {
                Text(text = "Clear")
            }
        }
        workOutList(viewModel.workoutData.value)
    }

}

*/


@Composable
fun Home(){
    Column(    modifier = Modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {

        Card(
            shape = RoundedCornerShape(8.dp),
            backgroundColor = Color.Blue,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text(text = "App de Ejercicio",
                    style = MaterialTheme.typography.h4)
            }
        }
        Card(
            shape = RoundedCornerShape(8.dp),
            backgroundColor = MaterialTheme.colors.surface,
        ) {
            Column(
                modifier = Modifier
                    .height(200.dp)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text(text = "Sensores",
                    style = MaterialTheme.typography.h4)
                Button( modifier = Modifier,
                    onClick = { /*TODO*/ }) {
                    Text(text = "BUTTON")
                }
            }
        }

        Card(
            shape = RoundedCornerShape(8.dp),
            backgroundColor = MaterialTheme.colors.surface,
        ) {
            Column(
                modifier = Modifier
                    .height(200.dp)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text(text = "Tus datos",
                    style = MaterialTheme.typography.h4)
                Button( modifier = Modifier,
                    onClick = { /*TODO*/ }) {
                    Text(text = "BUTTON")
                }
            }
        }
        Card(
            shape = RoundedCornerShape(8.dp),
            backgroundColor = MaterialTheme.colors.surface,
        ) {
            Column(
                modifier = Modifier
                    .height(200.dp)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text(text = "Registro",
                    style = MaterialTheme.typography.h4)
                Button( modifier = Modifier,
                    onClick = { /*TODO*/ }) {
                    Text(text = "BUTTON")
                }
            }
        }
    }
}




// on below line we are creating a
// composable function for our tab layout
@OptIn(ExperimentalUnitApi::class)
@ExperimentalPagerApi
@Composable
fun TabLayout() {
    val colorcito = Color(0xFF6E53C6)
    // on below line we are creating variable for pager state.
    val pagerState = rememberPagerState(pageCount = 4)


    // on below line we are creating a column for our widgets.
    Column(
        // for column we are specifying modifier on below line.
        modifier = Modifier.background(Color.White)
    ) {
        // on the below line we are specifying the top app bar
        // and specifying background color for it.
        TopAppBar(backgroundColor = colorcito) {
            // on below line we are specifying a column
            // for our text view to display a text
            // in our top app bar.
            Column(
                modifier = Modifier.fillMaxSize(),
                // on below line we are providing alignment for our
                // column to center of our top app bar.
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // on below line we are specifying a text and
                // specifying a text as "Tab Layout Example"
                Text(
                    text = "App Fitness",
                    style = androidx.compose.ui.text.TextStyle(color = Color.White),
                    fontWeight = FontWeight.Bold,
                    fontSize = TextUnit(
                        20F,
                        TextUnitType.Sp
                    ),
                    // on below line we are specifying a modifier
                    // to our text and adding passing from all sides.
                    modifier = Modifier.padding(all = Dp(5F)),
                    // on below line we are aligning
                    // our text to center.
                    textAlign = TextAlign.Center
                )
            }
        }
        // on below line we are calling tabs
        Tabs(pagerState = pagerState)
        // on below line we are calling tabs content
        // for displaying our page for each tab layout
        TabsContent(pagerState = pagerState)
    }
}

// on below line we are
// creating a function for tabs
@ExperimentalPagerApi
@Composable
fun Tabs(pagerState: PagerState) {
    val colorcito = Color(0xFF6E53C6)
    val list = listOf(
        "Walking" to Icons.Default.DirectionsWalk,
        "Running" to Icons.Default.DirectionsRun,
        "Squats" to Icons.Default.SelfImprovement,
        //"Sentadillas" to Icons.Default.AccessibilityNew,
        "Data" to Icons.Default.Person,
    )
    val scope = rememberCoroutineScope()
    TabRow(
        selectedTabIndex = pagerState.currentPage,
        backgroundColor = colorcito,
        contentColor = Color.White,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                height = 2.dp,
                color = Color.White
            )
        }
    ) {
        list.forEachIndexed { index, _ ->
            Tab(
                icon = {
                    Icon(imageVector = list[index].second, contentDescription = null)
                },
                text = {
                    Text(
                        list[index].first,
                        color = if (pagerState.currentPage == index) Color.White else Color.LightGray
                    )
                },
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )
        }
    }
}

@ExperimentalPagerApi
@Composable
fun TabsContent(pagerState: PagerState) {
    HorizontalPager(state = pagerState) {
            page ->
        when (page) {
            0 -> Caminar(viewModel= correrView())
            1 -> Correr(viewModel= correrView())
            2 -> Sentadillas(viewModel= sentadillasView())
            3-> Datos(viewModel = datosView())
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
fun Caminar(viewModel: correrView){
    val colorcito = Color(0xFF6E53C6)
    viewModel.getData()
    val index = viewModel.Datos_correr.value.size
    val calorias_quemadas =""
    val distancia = ""
    val pasos = ""
    val ritmo_cardiaco =""
    println("pasos: $pasos"+ "ritmo cardiaco: $ritmo_cardiaco"+ "calorias quemadas: $calorias_quemadas"+ "distancia: $distancia")

    LazyColumn() {
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
                Spacer(modifier = Modifier.height(25.dp))
                Text(color=colorcito, text="Registro"  , fontSize=30.sp)
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Calorias quemadas: " + workout.calorias_quemadas.toString(),
                    textAlign = TextAlign.Center,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = "Distancia: " + workout.distancia.toString() + " m",
                    textAlign = TextAlign.Center,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = "Pasos: " + workout.pasos.toString(),
                    textAlign = TextAlign.Center,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = "Ritmo cardiaco: " + workout.ritmo_cardiaco.toString(),
                    textAlign = TextAlign.Center,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

    }
    }


@Composable
fun Correr(viewModel: correrView) {
    val colorcito = Color(0xFF6E53C6)
    viewModel.getData()
    val index = viewModel.Datos_correr.value.size
    val calorias_quemadas = ""
    val distancia = ""
    val pasos = ""
    val ritmo_cardiaco = ""
    println("pasos: $pasos" + "ritmo cardiaco: $ritmo_cardiaco" + "calorias quemadas: $calorias_quemadas" + "distancia: $distancia")


    LazyColumn() {
        items(viewModel.Datos_correr.value) { workout ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        MaterialTheme.colors.background
                    ),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(25.dp))
                Text(color=colorcito, text="Registro" , fontSize=30.sp )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Calorias quemadas: " + workout.calorias_quemadas.toString(),
                    textAlign = TextAlign.Center,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = "Distancia: " + workout.distancia.toString() + " m",
                    textAlign = TextAlign.Center,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = "Pasos: " + workout.pasos.toString(),
                    textAlign = TextAlign.Center,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = "Ritmo cardiaco: " + workout.ritmo_cardiaco.toString(),
                    textAlign = TextAlign.Center,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
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
fun Datos(viewModel: datosView) {
    val colorcito = Color(0xFF6E53C6)
    viewModel.getData()
    val state = rememberScrollState()
    LaunchedEffect(Unit) { state.animateScrollTo(100) }
    LazyColumn() {
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
                Spacer(modifier = Modifier.height(25.dp))
                Text(color=colorcito, text="Datos" , fontSize=30.sp )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Nombre: ${workout.nombre}" , color=Color.Black
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
fun Sentadillas(viewModel: sentadillasView) {
    val colorcito = Color(0xFF6E53C6)
    viewModel.getData()
    val index = viewModel.Datos_sentadillas.value.size
    val calorias_quemadas = ""
    val repeticiones = ""
    val ritmo_cardiaco = ""
    val acelerometro = ""
    println("Acelerometro:--------------------------- $acelerometro----------------------------")
    println("repeticiones:-------------- $repeticiones"+ "ritmo cardiaco: $ritmo_cardiaco"+ "calorias quemadas: $calorias_quemadas")

    LazyColumn() {
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

                Spacer(modifier = Modifier.height(25.dp))
                Text(color=colorcito, text="Registro" , fontSize=30.sp)
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Calorias quemadas: " + workout.calorias_quemadas.toString(),
                    textAlign = TextAlign.Center,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Repeticiones: " + workout.repeticiones.toString(),
                    textAlign = TextAlign.Center,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Ritmo cardiaco: " + workout.ritmo_cardiaco.toString(),
                    textAlign = TextAlign.Center,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
            }
}


@Composable
fun Login(navController: NavController){
    var usuario by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = "Bienvenido", fontSize = 30.sp)
Spacer(modifier = Modifier.height(15.dp) )
        Text(text = "App Ejercicio", fontSize = 22.sp)
        Spacer(modifier = Modifier.height(35.dp) )
        AsyncImage(
            model = "https://loremflickr.com/400/400/cat?lock=1",
            contentDescription = null,
            modifier = Modifier.size(200.dp , 200.dp),
        )
        Spacer(modifier = Modifier.height(35.dp) )
      TextField(value = usuario, onValueChange = {usuario=it},
          leadingIcon = {Icon(imageVector = Icons.Default.Person, contentDescription = "UserIcon")},
      label = { Text(text = "Usuario")})
        Spacer(modifier = Modifier.height(20.dp))
        TextField(value = password, onValueChange = {password=it},
            leadingIcon = {Icon(imageVector = Icons.Default.Phone, contentDescription = "PasswordIcon")},
        label = { Text(text = "Contraseña")},
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = { if (usuario == "admin" && password == "admin"){
                navController.navigate(Routes.Home.route)
            }
            else{
                Toast.makeText( context,"Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
             }},
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
            modifier = Modifier
                .height(80.dp)
                .width(180.dp),

            // Uses ButtonDefaults.ContentPadding by default
            contentPadding = PaddingValues(
                start = 20.dp,
                top = 12.dp,
                end = 20.dp,
                bottom = 12.dp
            )
        ) {
            // Inner content including an icon and a text label
            Icon(
                Icons.Filled.Favorite,
                contentDescription = "Favorite",
                tint = Color(0xFF26C6DA), //5
                modifier = Modifier.size(24.dp)
            )

            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text("Ingresar")
        }


    }

}

sealed class Routes(val route: String) {
    object Login : Routes("Login")
    object  Home : Routes("Home")
}
@OptIn(ExperimentalPagerApi::class)
@Composable
fun ScreenMain(){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.Login.route) {

        composable(Routes.Login.route) {
            Login(navController = navController)
        }
        composable(Routes.Home.route){
            TabLayout()
        }
    }
}


