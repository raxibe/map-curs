package com.example.map2

import android.content.Context
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
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.map2.ui.theme.Map2Theme
import com.google.android.gms.common.api.Response
import com.google.android.play.core.integrity.e
import com.yandex.mapkit.GeoObject
import com.yandex.mapkit.MapKitFactory
import kotlinx.coroutines.launch
import ru.sulgik.mapkit.MapKit
import ru.sulgik.mapkit.compose.Circle
import ru.sulgik.mapkit.compose.MapEffect
import ru.sulgik.mapkit.compose.Placemark
import ru.sulgik.mapkit.compose.PlacemarkState
import ru.sulgik.mapkit.compose.YandexMap
import ru.sulgik.mapkit.compose.YandexMapsComposeExperimentalApi
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
import ru.sulgik.mapkit.map.InputListener
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
    val schedulePizzeria: String,
    val phoneNumber: String,
    val name: String
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

val dodo1 = Iconss(
    R.drawable.mappp,
    placemarkGeometry1,
    "Ноябрьск",
    "Улица Новоселов, 6а",
    true,
    "до 23:00",
    "Ежедневно с 9:00 до 23:00",
    "+79208244575",
    "мфффф"
)
val dodo2 = Iconss(
    R.drawable.map_1,
    placemarkGeometry2,
    "Ноябрьск",
    "На кольце",
    false,
    "до 20:00",
    "Никогда",
    "8 800 555 35 35",
    "мфффф"
)
//val nedodo2 = Iconss(R.drawable.map_1, "ну допустим тут живет чиф киф", placemarkGeometry3)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun MapScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberBottomSheetState(BottomSheetValue.Collapsed)
    )
    var selectedPlace by remember { mutableStateOf<Iconss?>(null) }

    // Инициализация MapKit
    LaunchedEffect(Unit) {
        MapKit.setApiKey("299e664a-6317-4736-9d0b-941426428ecd")
        MapKitFactory.initialize(context)
        MapKitFactory.getInstance().onStart()
    }

    DisposableEffect(Unit) {
        onDispose {
            MapKitFactory.getInstance().onStop()
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            selectedPlace?.let {
                MapCard(icons = it)
            } ?: run {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Выберите объект на карте")
                }
            }
        },
        sheetPeekHeight = 0.dp
    ) { padding ->
        YandexMap(
            cameraPositionState = rememberCameraPositionState { position = startPosition },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Обработка кликов по карте
            MapEffect { map ->
                map.addInputListener(object : InputListener() {
                    override fun onMapClick(point: Point) {
                        handleMapClick(context, point) { geoObject ->
                            selectedPlace = convertToIconss(geoObject)
                            scope.launch {
                                scaffoldState.bottomSheetState.expand()
                            }
                        }
                    }

                    override fun onMapLongClick(point: Point) {}
                })
            }

            // Отображение существующих меток
            listOf(dodo1, dodo2).forEach { icon ->
                Placemark(
                    state = rememberPlacemarkState(geometry = icon.geometry.geometryIcon),
                    icon = ImageProvider.fromResource(
                        context = context,
                        resId = icon.image,
                        isCacheable = true
                    ),
                    iconStyle = IconStyle(scale = 0.05f),
                    onTap = {
                        selectedPlace = icon
                        scope.launch {
                            scaffoldState.bottomSheetState.expand()
                        }
                        true
                    }
                )
            }
        }
    }
}

private fun handleMapClick(
    context: Context,
    point: Point,
    onSuccess: (GeoObject) -> Unit
) {
    val searchManager = SearchFactory.getInstance().createSearchManager()
    searchManager.submit(
        point,
        SearchOptions().apply {
            searchTypes = SearchType.GEO.value
            resultPageSize = 1
        },
        object : SearchListener {
            override fun onSearchResponse(response: Response) {
                response.collection.children.firstOrNull()?.obj?.let { geoObject ->
                    if ((geoObject.metadata["precision"] as? String) == "exact") {
                        onSuccess(geoObject)
                    } else {
                        Toast.makeText(context, "Точный адрес не найден", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onSearchError(error: Error) {
                Toast.makeText(context, "Ошибка поиска: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }
    )
}

private fun convertToIconss(geoObject: GeoObject): Iconss {
    val point = (geoObject.geometry as? PointGeometry)?.point ?: Point(0.0, 0.0)
    return Iconss(
        image = R.drawable.mappp,
        geometry = GeometryIcons(point),
        city = geoObject.name ?: "Неизвестный город",
        adres = geoObject.descriptionText ?: "Адрес не указан",
        state = true,
        timeToState = "До 23:00",
        schedulePizzeria = "Ежедневно с 10:00 до 22:00",
        phoneNumber = "+7 900 123-45-67",
        name = geoObject.name ?: "Неизвестный объект"
    )
}


@Composable
fun MapCard(icons: Iconss) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxSize()
        ) {
            Text(text = icons.city)
            Spacer(modifier = Modifier.padding(top = 10.dp))
            Text(text = icons.adres)
            Spacer(modifier = Modifier.padding(top = 10.dp))
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                initState(icons)
                Spacer(modifier = Modifier.padding(start = 10.dp))
                Text(text = icons.timeToState)
            }
            Spacer(modifier = Modifier.padding(top = 10.dp))
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(0.3f)
                ) {
                    Text(text = "Время работы")
                    Spacer(modifier = Modifier.padding(top = 5.dp))
                    Text(text = "Телефон")
                }
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = icons.schedulePizzeria)
                    Spacer(modifier = Modifier.padding(top = 5.dp))
                    Text(text = icons.phoneNumber)
                }
            }


        }
    }
}


@Composable
fun initMapKit() {
    MapKit.setApiKey("299e664a-6317-4736-9d0b-941426428ecd")
}

@Composable
fun initState(icons: Iconss) {
    if (icons.state == true) {
        Text(text = "открыто")
    } else {
        Text(text = "закрыто")
    }
}


//@Composable
//fun MapScreen() {
//    val cameraPositionState = rememberCameraPositionState {
//        position = CameraPosition(Point(55.751244, 37.618423), 12.0f, 0.0f, 0.0f)
//    }
//
//    YandexMap(
//        cameraPositionState = cameraPositionState,
//        modifier = Modifier.fillMaxSize()
//    ) {
//        MapEffect { map ->
//            map.addInputListener(object : InputListener {
//                override fun onMapClick(point: Point) {
//                    handleMapClick(point, map)
//                }
//
//                override fun onMapLongClick(point: Point) {}
//            })
//        }
//    }
//}
//
//private fun handleMapClick(point: Point, map: Map) {
//    val searchManager = SearchFactory.getInstance().createSearchManager()
//    val searchSession = searchManager.submit(
//        point,
//        SearchOptions().apply { searchTypes = SearchType.GEO.value },
//        object : SearchListener {
//            override fun onSearchResponse(response: Response) {
//                response.collection.children.firstOrNull()?.obj?.let { geoObject ->
//                    showBalloon(geoObject, point, map)
//                }
//            }
//
//            override fun onSearchError(error: Error) {
//                // Обработка ошибок
//            }
//        })
//}
//
//private fun showBalloon(geoObject: GeoObject, point: Point, map: Map) {
//    map.mapObjects.addPlacemark(
//        point,
//        ImageProvider.fromResource(context, R.drawable.ic_pin),
//        balloonContent = geoObject.name ?: "Неизвестный объект"
//    ).apply {
//        addTapListener { _, _ ->
//            showDetailedInfo(geoObject)
//            true
//        }
//    }
//}
//
//kotlin {
//    sourceSets {
//        commonMain.dependencies {
//            implementation("ru.sulgik.mapkit:yandex-mapkit-kmp:0.1.1")
//            implementation("ru.sulgik.mapkit:yandex-mapkit-kmp-compose:0.1.1")
//        }
//    }
//}
//
//cocoapods {
//    pod("YandexMapsMobile") {
//        version = "4.8.1-lite"
//    }
//}


