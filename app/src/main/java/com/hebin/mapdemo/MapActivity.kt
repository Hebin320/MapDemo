package com.hebin.mapdemo

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.animation.AnimationUtils
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.LocationSource
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.CameraPosition
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.MyLocationStyle
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.geocoder.GeocodeResult
import com.amap.api.services.geocoder.GeocodeSearch
import com.amap.api.services.geocoder.RegeocodeQuery
import com.amap.api.services.geocoder.RegeocodeResult
import kotlinx.android.synthetic.main.activity_map.*
import org.jetbrains.anko.startActivityForResult

class MapActivity : AppCompatActivity(), LocationSource, AMapLocationListener, AMap.OnMapClickListener, AMap.OnCameraChangeListener
        , GeocodeSearch.OnGeocodeSearchListener {

    var mAMapLocation: AMapLocation? = null
    var mListener: LocationSource.OnLocationChangedListener? = null
    var aMap: AMap? = null//初始化地图控制器对象
    var myLocationStyle: MyLocationStyle? = null
    var type = AMap.MAP_TYPE_SATELLITE // 地图卫星模式
    //定位服务类。此类提供单次定位、持续定位、地理围栏、最后位置相关功能
    var aMapLocationClient: AMapLocationClient? = null
    //定位参数设置
    var aMapLocationClientOption: AMapLocationClientOption? = null
    val RESULT_TO_MAPSEARCH = 9 // 跳转到搜索界面
    var count = 18 // 地图缩放级别


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mapView.onCreate(savedInstanceState)
        init(type)
        setClick()
    }

    override fun onDestroy() {
        super.onDestroy()
        //在activity执行onDestroy时执行mapView.onDestroy()，销毁地图
        mapView.onDestroy()
        //销毁定位客户端
        if (aMapLocationClient != null) {
            aMapLocationClient?.onDestroy()
            aMapLocationClient = null
            aMapLocationClientOption = null
        }
    }

    override fun onResume() {
        super.onResume()
        //在activity执行onResume时执行mapView.onResume ()，重新绘制加载地图
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        //在activity执行onPause时执行mapView.onPause ()，暂停地图的绘制
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //在activity执行onSaveInstanceState时执行mapView.onSaveInstanceState (outState)，保存地图当前的状态
        mapView.onSaveInstanceState(outState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        etAddress.setText(data?.getStringExtra("title"))
    }

    private fun init(type: Int) {
        if (aMap == null) {
            aMap = mapView.map
            aMap?.mapType = type// 卫星地图模式
            myLocationStyle = MyLocationStyle()//初始化定位蓝点样式类
            myLocationStyle?.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_point))//设置小蓝点图标
            myLocationStyle?.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER)//连续定位、蓝点不会移动到地图中心点，并且蓝点会跟随设备移动。
            // 连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
            myLocationStyle?.interval(2000) //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
            myLocationStyle?.strokeColor(Color.parseColor("#00000000")) // 圆圈边框颜色
            myLocationStyle?.radiusFillColor(Color.parseColor("#00000000")) // 圆圈填充颜色
            aMap?.myLocationStyle = myLocationStyle//设置定位蓝点的Style
            aMap?.uiSettings?.isRotateGesturesEnabled = false//关闭手势旋转
            aMap?.uiSettings?.isZoomControlsEnabled = false // 隐藏缩放按钮
            aMap?.setLocationSource(this)//通过aMap对象设置定位数据源的监听
            aMap?.isMyLocationEnabled = true// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
            aMapLocationClient = AMapLocationClient(applicationContext)
            aMapLocationClient?.setLocationListener(this)
            //初始化定位参数
            aMapLocationClientOption = AMapLocationClientOption()
            //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
            aMapLocationClientOption?.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
            //设置是否返回地址信息（默认返回地址信息）
            aMapLocationClientOption?.isNeedAddress = true
            //设置是否只定位一次,默认为false
            aMapLocationClientOption?.isOnceLocation = true
            //设置是否强制刷新WIFI，默认为强制刷新
            aMapLocationClientOption?.isWifiActiveScan = true
            //设置是否允许模拟位置,默认为false，不允许模拟位置
            aMapLocationClientOption?.isMockEnable = false
            //设置定位间隔,单位毫秒,默认为2000ms
            aMapLocationClientOption?.interval = 2000
            //给定位客户端对象设置定位参数
            aMapLocationClient?.setLocationOption(aMapLocationClientOption)
            //启动定位
            aMapLocationClient?.startLocation()
            //点击地图监听事件
            aMap?.setOnMapClickListener(this)
            // 移动地图监听事件
            aMap?.setOnCameraChangeListener(this)
        }
    }

    override fun deactivate() {
    }

    override fun activate(p0: LocationSource.OnLocationChangedListener?) {
        mListener = p0
    }

    override fun onLocationChanged(aMapLocation: AMapLocation?) {
        if (mListener != null && aMapLocation != null) {
            mAMapLocation = aMapLocation
            //设置缩放级别（缩放级别为3-19级）
            aMap?.moveCamera(CameraUpdateFactory.zoomTo(18f))
            //将地图移动到定位点
            aMap?.moveCamera(CameraUpdateFactory.changeLatLng(LatLng(aMapLocation.latitude, aMapLocation.longitude)))
            mListener?.onLocationChanged(aMapLocation)// 显示系统小蓝点
            if (aMapLocation.errorCode == 0) {
                etAddress.setText(aMapLocation.address)
                //定位成功回调信息，设置相关消息
//                aMapLocation.locationType;//获取当前定位结果来源，如网络定位结果，详见定位类型表
//                aMapLocation.latitude;//获取纬度;
//                aMapLocation.longitude;//获取经度
//                aMapLocation.accuracy;//获取精度信息
//                aMapLocation.address;//地址，如果option中设置isNeedAddress为false，则没有此结果
//                aMapLocation.country;//国家信息
//                aMapLocation.province;//省信息
//                aMapLocation.city;//城市信息
//                aMapLocation.district;//城区信息
//                aMapLocation.road;//街道信息
//                aMapLocation.cityCode;//城市编码
//                aMapLocation.adCode;//地区编码
            }
        }
    }

    override fun onMapClick(latLng: LatLng) {
        val aMapLocation = mAMapLocation
        //将地图移动到定位点
        aMapLocation?.longitude = latLng.longitude
        aMapLocation?.latitude = latLng.latitude
        aMap?.moveCamera(CameraUpdateFactory.changeLatLng(LatLng(latLng.latitude, latLng.longitude)))
        mListener?.onLocationChanged(aMapLocation)
    }

    override fun onCameraChangeFinish(cameraPosition: CameraPosition) {
        val aMapLocation = mAMapLocation
        //将地图移动到定位点
        aMapLocation?.longitude = cameraPosition.target.longitude
        aMapLocation?.latitude = cameraPosition.target.latitude
        aMap?.moveCamera(CameraUpdateFactory.changeLatLng(LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude)))
        mListener?.onLocationChanged(aMapLocation)
        val geocoderSearch = GeocodeSearch(this)
        geocoderSearch.setOnGeocodeSearchListener(this)
        // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        val point = LatLonPoint(cameraPosition.target.latitude, cameraPosition.target.longitude)
        val query = RegeocodeQuery(point, 200f, GeocodeSearch.AMAP)
        geocoderSearch.getFromLocationAsyn(query)
    }

    override fun onCameraChange(p0: CameraPosition?) {
        cardBottom.visibility = View.GONE
    }

    override fun onRegeocodeSearched(result: RegeocodeResult?, p1: Int) {
        if (result != null) {
            etAddress.setText(result.regeocodeAddress.formatAddress)
            Handler().postDelayed({
                if (!result.regeocodeAddress.district.isEmpty()) {
                    cardBottom.visibility = View.VISIBLE
                    val animation = AnimationUtils.loadAnimation(this@MapActivity, R.anim.push_bottom_in)
                    cardBottom.startAnimation(animation)
                }
            }, 500)
        }
    }

    override fun onGeocodeSearched(p0: GeocodeResult?, p1: Int) {
    }


    //点击事件
    private fun setClick() {
        // 跳转到搜索地点的界面
        tvSearch.setOnClickListener {
            startActivityForResult<MapSearchActivity>(RESULT_TO_MAPSEARCH)
        }
        //回到我的位置
        ivReset.setOnClickListener {
            aMap = null
            aMapLocationClient = null
            aMapLocationClientOption = null
            init(type)
        }
        //切换到卫星地图
        ivsatellite.setOnClickListener {
            tvSearch.setTextColor(ContextCompat.getColor(this, R.color.white))
            type = AMap.MAP_TYPE_SATELLITE
            aMap = null
            aMapLocationClient = null
            aMapLocationClientOption = null
            init(type)
        }
        //切换到平面地图
        ivNormal.setOnClickListener {
            tvSearch.setTextColor(Color.parseColor("#d2d2d2"))
            type = AMap.MAP_TYPE_NORMAL
            aMap = null
            aMapLocationClient = null
            aMapLocationClientOption = null
            init(type)
        }
        //地图放大
        ivAdd.setOnClickListener {
            if (count != 19) {
                count++
                //设置缩放级别（缩放级别为3-19级）
                aMap?.moveCamera(CameraUpdateFactory.zoomTo(count.toFloat()))
            }
        }
        //地图缩小
        ivReduce.setOnClickListener {
            if (count != 3) {
                count--
                //设置缩放级别（缩放级别为3-19级）
                aMap?.moveCamera(CameraUpdateFactory.zoomTo(count.toFloat()))
            }
        }
    }
}
