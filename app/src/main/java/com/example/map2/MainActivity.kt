package com.example.map2

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.traceEventEnd

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.map2.ui.theme.Map2Theme
import kotlinx.coroutines.launch
import ru.sulgik.mapkit.MapKit
import ru.sulgik.mapkit.compose.Circle
import ru.sulgik.mapkit.compose.Placemark
import ru.sulgik.mapkit.compose.PlacemarkState
import ru.sulgik.mapkit.compose.YandexMap
import ru.sulgik.mapkit.compose.bindToLifecycleOwner
import ru.sulgik.mapkit.compose.imageProvider
import ru.sulgik.mapkit.compose.rememberAndInitializeMapKit
import ru.sulgik.mapkit.compose.rememberCameraPositionState
import ru.sulgik.mapkit.compose.rememberCircleState
import ru.sulgik.mapkit.compose.rememberPlacemarkState
import ru.sulgik.mapkit.geometry.Circle
import ru.sulgik.mapkit.geometry.Point
import ru.sulgik.mapkit.map.CameraPosition
import ru.sulgik.mapkit.map.IconStyle
import ru.sulgik.mapkit.map.ImageProvider
import ru.sulgik.mapkit.map.fromResource

data class GeometryIcons(
    val geometryIcon: Point
)

data class Iconss(
    val image: Int,
    val geometry: GeometryIcons,
    val city: String,
    val adres: String,
    val state: Boolean,
    val timeToState: String,
    val imageRange: Int,
    val range: String,
    val delivery: String,
    val scheduleDilivery: String,
    val pizzeria: String,
    val schedulePizzeria: String,
    val phoneNumber: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            enableEdgeToEdge()
            initMapKit()
            MapScreen()
        }
    }
}

val startPosition = CameraPosition(Point(63.201795, 75.450244), 11.0f, 10.0F, 0.0f)

val placemarkGeometry1 = GeometryIcons(Point(63.201436, 75.451114))
val placemarkGeometry2 = GeometryIcons(Point(63.201810, 75.451144))
val placemarkGeometry3 = GeometryIcons(Point(63.201810, 75.551144))

//val dodo1 = Iconss(R.drawable.mappp, "ну допустим тут живет нуф нуф", placemarkGeometry1)
//val nedodo = Iconss(R.drawable.map_1, "ну допустим тут живет ниф ниф", placemarkGeometry2)
//val nedodo2 = Iconss(R.drawable.map_1, "ну допустим тут живет чиф киф", placemarkGeometry3)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MapScreen() {
    val context = LocalContext.current

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberBottomSheetState(BottomSheetValue.Collapsed)
    )

    var selectedIcon by remember { mutableStateOf<Iconss?>(null) }

    val coroutineScope = rememberCoroutineScope()

    rememberAndInitializeMapKit().bindToLifecycleOwner() // if is not called earlier
    val cameraPositionState = rememberCameraPositionState { position = startPosition }

    val icons = remember {
        mutableListOf(dodo1, nedodo,nedodo2)
    }

    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState, sheetContent = {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                selectedIcon?.let { MapCard(it) }
            }
        }, sheetPeekHeight = 0.dp
    ) {

        YandexMap(
            cameraPositionState = cameraPositionState,
            modifier = Modifier.fillMaxSize(),
        ) {
            icons.forEach { icon ->
                Placemark(
                    state = rememberPlacemarkState(icon.geometry.geometryIcon),
                    icon = ImageProvider.fromResource(
                        context = context, icon.image, isCacheable = true
                    ),
                    iconStyle = IconStyle(scale = 0.05f),
                    onTap = {
                        Log.d("CheckViewModel", "HourFragment it: $it")
                        selectedIcon = icon
                        coroutineScope.launch {
                            bottomSheetScaffoldState.bottomSheetState.expand()
                        }
                        true
                    }
                )
            }
        }
    }

//    // Кнопка для добавления новых иконок
//    ExtendedFloatingActionButton(
//        text = { Text("Добавить иконку") },
//        icon = { Icon(Icons.Filled.Add, contentDescription = "Добавить") },
//        onClick = {
//            // Здесь можно добавить логику для добавления новых иконок
//            val newIcon = Iconss(R.drawable.mappp, "Новая иконка", GeometryIcons(Point(63.202436, 75.451114)))
//            icons.add(newIcon)
//        },
//        modifier = Modifier.padding(16.dp)
//    )
}

@Composable
fun MapCard(icons: Iconss) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = icons.name)
    }
}

@Composable
fun initMapKit() {
    MapKit.setApiKey("299e664a-6317-4736-9d0b-941426428ecd")
}




