package com.example.map2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.traceEventEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.map2.ui.theme.Map2Theme
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

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

val startPosition = CameraPosition(Point(63.723768, 75.961573), 11.0f, 10.0F, 0.0f)

val placemarkGeometry = Point(63.201436, 75.451114)

@Composable
fun MapScreen() {

    val context = LocalContext.current

    rememberAndInitializeMapKit().bindToLifecycleOwner() // if is not called earlier
    val cameraPositionState = rememberCameraPositionState { position = startPosition }
    YandexMap(
        cameraPositionState = cameraPositionState,
        modifier = Modifier.fillMaxSize(),
    ) {






        Placemark(


            state = rememberPlacemarkState(placemarkGeometry),
            icon = ImageProvider.fromResource(
                context = context,
                R.drawable.mappp,
                isCacheable = true
            ),
            iconStyle = IconStyle(scale = 0.05f)

            )



    }


    var clicksCount by remember { mutableStateOf(1) }
  //  val density = LocalDensity.current
   // val contentSize = with(density) { DpSize(75.dp, 10.dp + 12.sp.toDp()) }
    val clicksImageProvider = imageProvider(size = DpSize(width = 30.dp , height = 30.dp), clicksCount) {
        Box(
            modifier = Modifier
                .background(Color.LightGray, MaterialTheme.shapes.medium)
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outline,
                    MaterialTheme.shapes.medium
                )
                .padding(vertical = 5.dp, horizontal = 10.dp)
        ) {
            Text("clicks: $clicksCount", fontSize = 12.sp)
        }
    }

    Placemark(
        icon = clicksImageProvider,
        state = rememberPlacemarkState(placemarkGeometry),
        onTap = {
            clicksCount++
            true
        }
    )


}





@Composable
fun initMapKit() {
    MapKit.setApiKey("299e664a-6317-4736-9d0b-941426428ecd")
}