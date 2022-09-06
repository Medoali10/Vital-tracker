package com.example.pdapplication.mapActivities

import android.Manifest
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.view.Menu
import android.view.MenuInflater
import android.view.MotionEvent
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment
import com.esri.arcgisruntime.concurrent.ListenableFuture
import com.esri.arcgisruntime.data.ServiceFeatureTable
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.layers.FeatureLayer
import com.esri.arcgisruntime.loadable.LoadStatus
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.Basemap
import com.esri.arcgisruntime.mapping.view.*
import com.esri.arcgisruntime.mapping.view.LocationDisplay.DataSourceStatusChangedEvent
import com.esri.arcgisruntime.portal.Portal
import com.esri.arcgisruntime.portal.PortalItem
import com.esri.arcgisruntime.symbology.SimpleLineSymbol
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol
import com.esri.arcgisruntime.symbology.TextSymbol
import com.esri.arcgisruntime.tasks.geocode.GeocodeParameters
import com.esri.arcgisruntime.tasks.geocode.GeocodeResult
import com.esri.arcgisruntime.tasks.geocode.LocatorTask
import com.esri.arcgisruntime.util.ListenableList
import com.example.pdapplication.R
import java.util.concurrent.ExecutionException


class MapActivity : AppCompatActivity() {

    private var mFeatureLayer: FeatureLayer? = null
    lateinit var mMapView: MapView
    private var graphicsOverlay: GraphicsOverlay? = null
    private val locator = LocatorTask("http://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer")
    private var spinner: Spinner? = null
    lateinit var mLocationDisplay:LocationDisplay
    private var mSearchView: SearchView? = null
    private var mGraphicsOverlay: GraphicsOverlay? = null
    private var mLocatorTask: LocatorTask? = null
    private var mGeocodeParameters: GeocodeParameters? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        mMapView = findViewById(R.id.mapView);
        setupMap();

//        location method
        setupLocationDisplay();
//        Add a layer to the map
        addTrailheadsLayer();
//        search address
        setupLocator();
    }


    private fun setupMap() {
        if (mMapView != null) {
            ArcGISRuntimeEnvironment.setLicense(getResources().getString(R.string.arcgis_license_key));

            val itemId = "41281c51f9de45edaf1c8ed44bb10e30"
            val portal = Portal("https://www.arcgis.com", false)
            val portalItem = PortalItem(portal, itemId)
            val map = ArcGISMap(portalItem)
            val basemapType: Basemap.Type = Basemap.Type.TOPOGRAPHIC

            mMapView.map = map
//            add feature layer
            addLayer(map)
            mMapView.addViewpointChangedListener(object : ViewpointChangedListener {
                override fun viewpointChanged(viewpointChangedEvent: ViewpointChangedEvent) {
                    if (graphicsOverlay == null) {
                        graphicsOverlay = GraphicsOverlay()
                        mMapView.graphicsOverlays.add(graphicsOverlay)
                        setupSpinner()
                        setupPlaceTouchListener()
                        setupNavigationChangedListener()
                        mMapView.removeViewpointChangedListener(this)
                    }
                }
            })

        }

    }
    // search by address part
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)
        val searchMenuItem = menu.findItem(R.id.search)
        if (searchMenuItem != null) {
            mSearchView = searchMenuItem.getActionView() as SearchView?
            if (mSearchView != null) {
                val searchManager: SearchManager =
                    getSystemService(Context.SEARCH_SERVICE) as SearchManager
                mSearchView!!.setSearchableInfo(searchManager.getSearchableInfo(componentName))
                mSearchView!!.isIconifiedByDefault = false
            }
        }
        return true
    }
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            queryLocator(intent.getStringExtra(SearchManager.QUERY))
        }
    }
    private fun queryLocator(query: String?) {
        if (query != null && query.length > 0) {
            mLocatorTask!!.cancelLoad()
            val geocodeFuture: ListenableFuture<List<GeocodeResult>> =
                mLocatorTask!!.geocodeAsync(query, mGeocodeParameters)
            geocodeFuture.addDoneListener(object : Runnable {
                override fun run() {
                    try {
                        val geocodeResults: List<GeocodeResult> = geocodeFuture.get()
                        if (geocodeResults.size > 0) {
                            displaySearchResult(geocodeResults[0])
                        } else {
                            Toast.makeText(
                                applicationContext,
                                getString(R.string.nothing_found) + " " + query,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } catch (e: InterruptedException) {
                        // ... determine how you want to handle an error
                    } catch (e: ExecutionException) {
                    }
                    geocodeFuture.removeDoneListener(this) // Done searching, remove the listener.
                }
            })
        }
    }

    private fun displaySearchResult(geocodedLocation: GeocodeResult) {
        val displayLabel = geocodedLocation.label
        val textLabel = TextSymbol(
            18f,
            displayLabel,
            Color.rgb(192, 32, 32),
            TextSymbol.HorizontalAlignment.CENTER,
            TextSymbol.VerticalAlignment.BOTTOM
        )
        val textGraphic = Graphic(geocodedLocation.displayLocation, textLabel)
        val mapMarker = Graphic(
            geocodedLocation.displayLocation, geocodedLocation.attributes,
            SimpleMarkerSymbol(
                SimpleMarkerSymbol.Style.SQUARE,
                Color.rgb(255, 0, 0),
                12.0f
            )
        )
        val allGraphics: ListenableList<Graphic> = mGraphicsOverlay!!.graphics
        allGraphics.clear()
        allGraphics.add(mapMarker)
        allGraphics.add(textGraphic)
        mMapView.setViewpointCenterAsync(geocodedLocation.displayLocation)
    }

    private fun setupLocator() {
        val locatorService =
            "https://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer"
        mLocatorTask = LocatorTask(locatorService)
        mLocatorTask!!.addDoneLoadingListener {
            if (mLocatorTask!!.loadStatus == LoadStatus.LOADED) {
                mGeocodeParameters = GeocodeParameters()
                mGeocodeParameters!!.resultAttributeNames.add("*")
                mGeocodeParameters!!.maxResults = 1
                mGraphicsOverlay = GraphicsOverlay()
                mMapView.graphicsOverlays.add(mGraphicsOverlay)
            } else if (mSearchView != null) {
                mSearchView!!.isEnabled = false
            }
        }
        mLocatorTask!!.loadAsync()
    }
//add feature layer to the activity
private  fun addLayer(map: ArcGISMap): Unit {
    var itemID = "fbca9c87feb94ba5b00411b3a00809f3"
    val portal = Portal("http://www.arcgis.com")
    val portalItem = PortalItem(portal, itemID)

//    create the layer
    mFeatureLayer = FeatureLayer(portalItem, 0)
    mFeatureLayer!!.addDoneLoadingListener(object : Runnable {
        override fun run() {
            if (mFeatureLayer!!.getLoadStatus() == LoadStatus.LOADED) {
                map.getOperationalLayers().add(mFeatureLayer);
            }
        }
    })
    mFeatureLayer!!.loadAsync()
}
//Add a layer to the map
private fun addTrailheadsLayer(): Unit {
    val url =
        "https://services3.arcgis.com/GVgbJbqm8hXASVYi/arcgis/rest/services/Trailheads/FeatureServer/0"
    val serviceFeatureTable = ServiceFeatureTable(url)
    val featureLayer = FeatureLayer(serviceFeatureTable)
    val map = mMapView.map
    map.operationalLayers.add(featureLayer)
}
    private fun setupSpinner() {
        spinner = findViewById(R.id.spinner)
        spinner!!.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>,
                view: View?,
                i: Int,
                l: Long
            ) {
                findPlaces(adapterView.getItemAtPosition(i).toString())
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        })
        findPlaces(spinner!!.getSelectedItem().toString())
    }
    private fun setupNavigationChangedListener() {
        mMapView.addNavigationChangedListener { navigationChangedEvent: NavigationChangedEvent ->
            if (!navigationChangedEvent.isNavigating) {
                mMapView.callout.dismiss()
                findPlaces(spinner!!.selectedItem.toString())
            }
        }
    }
    private fun findPlaces(placeCategory: String) {
        val parameters = GeocodeParameters()
        val searchPoint: Point
        if (mMapView.getVisibleArea() != null) {
            searchPoint = mMapView.getVisibleArea().getExtent().getCenter()
            if (searchPoint == null) {
                return
            }
        } else {
            return
        }
        parameters.preferredSearchLocation = searchPoint
        parameters.maxResults = 25
        val outputAttributes =
            parameters.resultAttributeNames
        outputAttributes.add("Place_addr")
        outputAttributes.add("PlaceName")


        val results = locator.geocodeAsync(placeCategory, parameters)
        results.addDoneListener { try {
            val graphics = graphicsOverlay?.getGraphics()
            graphics?.clear()
            val places = results.get()
            for (result in places) {
                // Add a graphic representing each location with a simple marker symbol.
                val placeSymbol = SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.GREEN, 10f)
                placeSymbol.setOutline(SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.WHITE, 2f))
                val graphic = Graphic(result.getDisplayLocation(), placeSymbol)
                val attributes = result.getAttributes()
                // Store the location attributes with the graphic for later recall when this location is identified.
                for (key in attributes.keys) {
                    val value = attributes.get(key).toString()
                    graphic.getAttributes().put(key, value)
                }
                graphics!!.add(graphic)
            }
        } catch (exception:InterruptedException) {
            exception.printStackTrace()
        } catch (exception: ExecutionException) {
            exception.printStackTrace()
        }
        }
    }

    private fun showCalloutAtLocation(
        graphic: Graphic,
        mapPoint: Point
    ) {
        val callout = mMapView.callout
        val calloutContent = TextView(applicationContext)
        callout.location = graphic.computeCalloutLocation(mapPoint, mMapView)
        calloutContent.setTextColor(Color.BLACK)
        calloutContent.text = Html.fromHtml(
            "<b>" + graphic.attributes["PlaceName"]
                .toString() + "</b><br>" + graphic.attributes["Place_addr"].toString()
        )
        callout.content = calloutContent
        callout.show()
    }


    private fun setupPlaceTouchListener() {
        mMapView.setOnTouchListener(object : DefaultMapViewOnTouchListener(this, mMapView) {
            override fun onSingleTapConfirmed(motionEvent: MotionEvent): Boolean {

                // Dismiss a prior callout.
                mMapView.callout.dismiss()

                // get the screen point where user tapped
                val screenPoint = android.graphics.Point(
                    Math.round(motionEvent.getX()).toInt(),
                    Math.round(motionEvent.getY()).toInt()
                )

                // identify graphics on the graphics overlay
                val identifyGraphic: ListenableFuture<IdentifyGraphicsOverlayResult> =
                    mMapView.identifyGraphicsOverlayAsync(
                        graphicsOverlay,
                        screenPoint,
                        10.0,
                        false,
                        2
                    )
                identifyGraphic.addDoneListener({
                    try {
                        val graphicsResult: IdentifyGraphicsOverlayResult = identifyGraphic.get()
                        // get the list of graphics returned by identify graphic overlay
                        val graphicList: List<Graphic> = graphicsResult.getGraphics()

                        // get the first graphic selected and show its attributes with a callout
                        if (!graphicList.isEmpty()) {
                            showCalloutAtLocation(
                                graphicList[0],
                                mMapView.screenToLocation(screenPoint)
                            )
                        }
                    } catch (exception: InterruptedException) {
                        exception.printStackTrace()
                    } catch (exception: ExecutionException) {
                        exception.printStackTrace()
                    }
                })
                return super.onSingleTapConfirmed(motionEvent)
            }
        })
    }

//    track your location method
private fun setupLocationDisplay(): Unit {
    mLocationDisplay = mMapView.locationDisplay
    mLocationDisplay.addDataSourceStatusChangedListener { dataSourceStatusChangedEvent: DataSourceStatusChangedEvent ->
        if (dataSourceStatusChangedEvent.isStarted || dataSourceStatusChangedEvent.error == null) {
            return@addDataSourceStatusChangedListener
        }
        val requestPermissionsCode = 2
        val requestPermissions = arrayOf<String>(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (!(ContextCompat.checkSelfPermission(
                this@MapActivity,
                requestPermissions[0]
            ) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(
                this@MapActivity,
                requestPermissions[1]
            ) == PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(
                this@MapActivity,
                requestPermissions,
                requestPermissionsCode
            )
        } else {
            val message = String.format(
                "Error in DataSourceStatusChangedListener: %s",
                dataSourceStatusChangedEvent.source.locationDataSource.error.message
            )
            Toast.makeText(this@MapActivity, message, Toast.LENGTH_LONG).show()
        }
    }
    mLocationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.COMPASS_NAVIGATION);
    mLocationDisplay.startAsync();
}
     override fun onRequestPermissionsResult(
         requestCode: Int,
         permissions: Array<out String>,
         grantResults: IntArray
     ) {
        if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mLocationDisplay.startAsync()
        } else {
            Toast.makeText(
                this@MapActivity,
                "location premission denied",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onPause() {
        super.onPause()
        if (mMapView != null) {
            mMapView.pause()
        }
    }
    override fun onResume() {
        super.onResume()
        if (mMapView != null) {
            mMapView.resume()
        }
    }

    override fun onDestroy() {
        if (mMapView != null) {
            mMapView.dispose()
        }
        super.onDestroy()
    }

}
