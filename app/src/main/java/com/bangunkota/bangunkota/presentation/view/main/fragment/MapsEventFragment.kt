package com.bangunkota.bangunkota.presentation.view.main.fragment

import android.animation.Animator
import android.annotation.SuppressLint
import android.graphics.Color.rgb
import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnticipateOvershootInterpolator
import android.widget.Toast
import com.bangunkota.bangunkota.R
import com.bangunkota.bangunkota.databinding.FragmentMapsEventBinding
import com.bangunkota.bangunkota.utils.MyLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mapbox.geojson.Geometry
import com.mapbox.geojson.Point
import com.mapbox.maps.*
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.style.atmosphere.generated.atmosphere
import com.mapbox.maps.extension.style.expressions.generated.Expression.Companion.color
import com.mapbox.maps.extension.style.layers.properties.generated.ProjectionName
import com.mapbox.maps.extension.style.projection.generated.projection
import com.mapbox.maps.extension.style.sources.generated.rasterDemSource
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.extension.style.terrain.generated.terrain
import com.mapbox.maps.plugin.animation.CameraAnimatorOptions.Companion.cameraAnimatorOptions
import com.mapbox.maps.plugin.animation.MapAnimationOptions.Companion.mapAnimationOptions
import com.mapbox.maps.plugin.animation.animator.CameraAnimator
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.OnPointAnnotationClickListener
import com.mapbox.maps.plugin.annotation.generated.createCircleAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.OnMapClickListener
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import java.util.*

@SuppressLint("Lifecycle")
class MapsEventFragment : Fragment(), OnMapClickListener {

    private lateinit var binding: FragmentMapsEventBinding

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geocoder: Geocoder
    private lateinit var myLocation: MyLocation

    private var mapView: MapView? = null
    private var mapboxMap: MapboxMap? = null
    private var isAtStart = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMapsEventBinding.inflate(layoutInflater, container, false)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        geocoder = Geocoder(requireActivity(), Locale.getDefault())
        myLocation = MyLocation(requireActivity(), fusedLocationClient, geocoder)

        mapView = binding.mapView
        mapboxMap = mapView?.getMapboxMap()
        mapboxMap?.loadStyle(
            style(Style.MAPBOX_STREETS) {
                +projection(ProjectionName.GLOBE)
                +atmosphere {
                    color(rgb(220, 159, 159)) // Pink fog / lower atmosphere
                    highColor(rgb(220, 159, 159)) // Blue sky / upper atmosphere
                    horizonBlend(0.4) // Exaggerate atmosphere (default is .1)
                }
                +rasterDemSource("raster-dem") {
                    url("mapbox://mapbox.terrain-rgb")
                }
                +terrain("raster-dem") {
                    // camera seems to be a bit jumping on high zoom level - check with gl-native
                    exaggeration(1.5)
                }
            }
        ) {
            // Toast instructing user to tap on the map
            Toast.makeText(
                requireActivity(),
                getString(R.string.tap_on_map_instruction),
                Toast.LENGTH_LONG
            ).show()
            mapboxMap?.addOnMapClickListener(this)
        }



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun addAnnotationMap(mapView: MapView?, lat: Double, long: Double) {

        // Create an instance of the Annotation API and get the CircleAnnotationManager.
        val annotationApi = mapView?.annotations
        val circleAnnotationManager = annotationApi?.createCircleAnnotationManager(mapView)
        // Set options for the resulting circle layer.
        val circleAnnotationOptions: CircleAnnotationOptions = CircleAnnotationOptions()
            // Define a geographic coordinate.
            .withPoint(Point.fromLngLat(long, lat))
            // Style the circle that will be added to the map.
            .withCircleRadius(8.0).withCircleColor("# 2F80ED").withCircleStrokeWidth(2.0)
            .withCircleStrokeColor("#ffffff")
        // Add the resulting circle to the map.
        circleAnnotationManager?.create(circleAnnotationOptions)
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()

        myLocation.getLastLocation({
            if (it != null) {

                binding.animationView.visibility = View.GONE
                binding.tvLoadingMap.visibility = View.GONE

                addAnnotationMap(mapView, it.latitude, it.longitude)

                val cameraOpt = cameraOptions {
                    center(Point.fromLngLat(it.longitude, it.latitude))
                    zoom(12.5)
                    pitch(75.0)
                    bearing(100.0)
                }

                mapboxMap?.flyTo(
                    cameraOpt,
                    mapAnimationOptions {
                        duration(12_000)
                    }
                )
            } else {
                Toast.makeText(requireActivity(), "Tungguin, Lagi nyari lokasi!", Toast.LENGTH_LONG).show()
            }
        }, {
            Toast.makeText(requireActivity(), "Lokasi Error ${it.localizedMessage}!", Toast.LENGTH_LONG).show()
        })
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onMapClick(point: Point): Boolean {
        val target = if (isAtStart) CAMERA_END else CAMERA_START
        isAtStart = !isAtStart
        mapboxMap?.flyTo(
            target,
            mapAnimationOptions {
                duration(12_000)
            }
        )
        return true
    }

    private companion object {
        private val CAMERA_START = cameraOptions {
            center(Point.fromLngLat(80.0, 36.0))
            zoom(1.0)
            pitch(0.0)
            bearing(0.0)
        }
        private val CAMERA_END = cameraOptions {
            center(Point.fromLngLat(106.992416, -6.241586))
            zoom(12.5)
            pitch(75.0)
            bearing(130.0)
        }
    }
}