package com.example.lin1wang.bdmapdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    //引入百度地图
    private MapView mMapView = null;
    private BaiduMap mBaiduMap = null;

    //定位相关
    private LocationClient mLocationClient; //定位客户端
    private LocationClientOption option;  //定位选项
    private BitmapDescriptor mCurrentMarker; //自定义定位图标
    private MyLocationConfiguration.LocationMode mLocationMode; //定位模式 Normal/Following/Compass
    private double mLatitude; //纬度
    private double mLongitude; //经度
    private float mCurrentX; //当前方向
    private MyOrientationListener myOrientationListener; //方向监听
    private boolean isFirstLoc = true; //第一次定位

    //覆盖物相关
    private BitmapDescriptor mMarker;
    private RelativeLayout mMarkerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //在使用SDK各组件之前初始化context信息，传入ApplicationContext  
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        initView(); //初始化控件
        initLocation(); //初始化定位
        initMarker(); //初始化覆盖物

        //覆盖物点击事件监听
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                Bundle extraInfo = marker.getExtraInfo();
                Info info = (Info) extraInfo.getSerializable("info");

                ImageView iv = (ImageView) mMarkerLayout.findViewById(R.id.id_info_img);
                TextView distance = (TextView) mMarkerLayout.findViewById(R.id.id_info_distance);
                TextView name = (TextView) mMarkerLayout.findViewById(R.id.id_info_name);
                TextView zan = (TextView) mMarkerLayout.findViewById(R.id.id_info_zan);

                iv.setImageResource(info.getImgId());
                distance.setText(info.getDistance());
                name.setText(info.getName());
                zan.setText(info.getZan() + "");

                final InfoWindow infoWindow;
                TextView tv = new TextView(MainActivity.this);
                tv.setBackgroundResource(R.drawable.location_tips);
                tv.setPadding(30, 50, 30, 50);
                tv.setText(info.getName());
                tv.setTextColor(Color.parseColor("#ffffff"));

                final LatLng latLng = marker.getPosition();
                Point p = mBaiduMap.getProjection().toScreenLocation(latLng);
                p.y -= 47;
                LatLng ll = mBaiduMap.getProjection().fromScreenLocation(p);

                InfoWindow.OnInfoWindowClickListener listener = null;
                listener = new InfoWindow.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick() {
                        mBaiduMap.hideInfoWindow();
                    }
                };

                infoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(tv), ll, -47, listener);

                mBaiduMap.showInfoWindow(infoWindow);

                mMarkerLayout.setVisibility(View.VISIBLE);

                return true;
            }
        });

        //地图点击事件监听
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMarkerLayout.setVisibility(View.GONE);
                mBaiduMap.hideInfoWindow();
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });

        //权限申请
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission
                .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission
                .READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission
                .WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        } else {
            requestLocation();
        }
    }

    private void initView() {
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(16f));
        mBaiduMap.setMyLocationEnabled(true);
    }

    private void requestLocation() {
        mLocationClient.start(); //开启定位
        myOrientationListener.start(); //开启方向监听
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(MainActivity.this, "必须同意所有权限才能使用本程序",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                } else {
                    Toast.makeText(MainActivity.this, "发生未知错误", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private void initLocation() {
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());

        mLocationMode = MyLocationConfiguration.LocationMode.NORMAL;

        option = new LocationClientOption();
        //可选，设置定位模式，默认高精度
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，设置发起定位请求的间隔，int类型，单位ms
        option.setScanSpan(5000);
        //可选，是否需要地址信息，默认为不需要，即参数为false
        option.setIsNeedAddress(true);
        //可选，是否需要位置描述信息，默认为不需要，即参数为false
        option.setIsNeedLocationDescribe(true);
        //可选，是否需要周边POI信息，默认为不需要，即参数为false
        option.setIsNeedLocationPoiList(true);

        mLocationClient.setLocOption(option);

        myOrientationListener = new MyOrientationListener(this);
        myOrientationListener.setmOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
            @Override
            public void onOrientationChanged(float x) {
                mCurrentX = x;
            }
        });
    }

    private void initMarker() {
        mMarker = BitmapDescriptorFactory.fromResource(R.drawable.marker_poi);
        mMarkerLayout = (RelativeLayout) findViewById(R.id.maker_layout);
    }


    public class MyLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            double latitude = bdLocation.getLatitude();    //获取纬度信息
            double longitude = bdLocation.getLongitude();    //获取经度信息
            float radius = bdLocation.getRadius();    //获取定位精度，默认值为0.0f

            String addr = bdLocation.getAddrStr();    //获取详细地址信息
            String country = bdLocation.getCountry();    //获取国家
            String province = bdLocation.getProvince();    //获取省份
            String city = bdLocation.getCity();    //获取城市
            String district = bdLocation.getDistrict();    //获取区县
            String street = bdLocation.getStreet();    //获取街道信息
            String locationDescribe = bdLocation.getLocationDescribe();    //获取位置描述信息

            //获取周边POI信息
            List<Poi> poiList = bdLocation.getPoiList();

            Log.d(TAG, "onReceiveLocation: 维度==" + latitude);
            Log.d(TAG, "onReceiveLocation: 经度==" + longitude);
            Log.d(TAG, "onReceiveLocation: 精度==" + radius);
            Log.d(TAG, "onReceiveLocation: 详细地址==" + addr);
            Log.d(TAG, "onReceiveLocation: 国家==" + country);
            Log.d(TAG, "onReceiveLocation: 省份==" + province);
            Log.d(TAG, "onReceiveLocation: 城市==" + city);
            Log.d(TAG, "onReceiveLocation: 区县==" + district);
            Log.d(TAG, "onReceiveLocation: 街道==" + street);
            Log.d(TAG, "onReceiveLocation: 位置描述==" + locationDescribe);

            //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准
            String coorType = bdLocation.getCoorType();
            //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
            int errorCode = bdLocation.getLocType();

            Log.d(TAG, "onReceiveLocation: 坐标类型==" + coorType);
            Log.d(TAG, "onReceiveLocation: 错误码==" + errorCode);

            mLatitude = bdLocation.getLatitude();
            mLongitude = bdLocation.getLongitude();

            Log.d(TAG, "onReceiveLocation: isFisrtLoc==" + isFirstLoc);
            if (isFirstLoc) {
                LatLng ll = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
                MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
                mBaiduMap.animateMapStatus(update); // 动画的方式到中间
                if (mBaiduMap.getLocationData() != null) {
                    if (mBaiduMap.getLocationData().latitude == bdLocation.getLatitude()
                            && mBaiduMap.getLocationData().longitude == bdLocation.getLongitude()) {
                        Toast.makeText(MainActivity.this, bdLocation.getAddrStr(), Toast.LENGTH_SHORT).show();
                        isFirstLoc = false;
                    }
                }

            }

            // 构造定位数据
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(bdLocation.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentX)
                    .latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude())
                    .build();

            // 设置定位数据
            mBaiduMap.setMyLocationData(locData);

            // 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
            mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.map_poi);
            MyLocationConfiguration config = new MyLocationConfiguration(mLocationMode, true, null);
            mBaiduMap.setMyLocationConfiguration(config);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.id_map_normal:
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                break;
            case R.id.id_map_sitellate:
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.id_map_traffic:
                if (mBaiduMap.isTrafficEnabled()) {
                    mBaiduMap.setTrafficEnabled(false);
                    item.setTitle("实时交通(off)");
                } else {
                    mBaiduMap.setTrafficEnabled(true);
                    item.setTitle("实时交通(on)");
                }
                break;
            case R.id.id_map_location:
                LatLng ll = new LatLng(mLatitude, mLongitude);
                MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
                mBaiduMap.animateMapStatus(update); // 动画的方式到中间
                break;
            case R.id.id_map_mode_normal:
                mLocationMode = MyLocationConfiguration.LocationMode.NORMAL;
                break;
            case R.id.id_map_mode_following:
                mLocationMode = MyLocationConfiguration.LocationMode.FOLLOWING;
                break;
            case R.id.id_map_mode_compass:
                mLocationMode = MyLocationConfiguration.LocationMode.COMPASS;
                break;
            case R.id.id_map_add_overlay:
                addOverlays(Info.infos);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 添加覆盖物
     *
     * @param infos
     */
    private void addOverlays(List<Info> infos) {
        mBaiduMap.clear();
        LatLng latLng = null;
        Marker marker = null;
        OverlayOptions options;

        for (Info info : infos) {
            //经纬度
            latLng = new LatLng(info.getLatitude(), info.getLongitude());
            //图标
            options = new MarkerOptions().position(latLng).icon(mMarker).zIndex(5);
            marker = (Marker) mBaiduMap.addOverlay(options);
            Bundle arg0 = new Bundle();
            arg0.putSerializable("info", info);
            marker.setExtraInfo(arg0);
        }

        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.setMapStatus(msu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBaiduMap.setMyLocationEnabled(false);
        mLocationClient.stop();
        myOrientationListener.stop();
        mMapView.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
        Log.d(TAG, "onPause: ");
    }

}
