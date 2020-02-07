package com.score.user.walkingscoreapp;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.view.View;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapOptions;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapOverlay;
import com.nhn.android.maps.NMapOverlayItem;
//import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;


import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;
import com.nhn.android.mapviewer.overlay.NMapResourceProvider;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;


public class DivideActivity extends FragmentActivity implements OnMapReadyCallback
{
    //private NMapView mMapView;// 지도 화면 View
    private final String CLIENT_ID = "8du12wjj8x";// 애플리케이션 클라이언트 아이디 값 //
    //NMapResourceProvider mMapViewerResourceProvider = null;
    //NMapOverlayManager mOverlayManager;
    String nearLatitude,nearLongitude,nearWalkScore,nearAddress;
    WalkingScore userWalkingScore;
    WalkingScore nearWalkingScore;
    Double roundWalkingScore;
    int userDecideNumberOfMark;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mMapView = new NMapView(this);
        setContentView(R.layout.activity_divide);

        //사용 불필요 mMapViewerResourceProvider = new NMapViewerResourceProvider(this);
        //mOverlayManager = new NMapOverlayManager(this, mMapView, mMapViewerResourceProvider);
        int markerId = NMapPOIflagType.PIN;
        String longitude [] = new String [12];
        String latitude [] = new String [12];
        String walkingScore [] = new String [12];
        String address [] = new String [12];


        Intent intent = getIntent();
        userDecideNumberOfMark = Integer.parseInt(intent.getStringExtra("userDecideNumberOfMark"));
        userWalkingScore =(WalkingScore)getIntent().getSerializableExtra("userInputLocation");


        NaverMapOptions options = new NaverMapOptions();

        nearWalkingScore  =(WalkingScore)getIntent().getSerializableExtra("nearWalkingScore"+0);
        //초기 카메라 위치지정은 navermapoptions내부의 camera에 대해서 적용
        options.camera(new CameraPosition(new LatLng(nearWalkingScore.getLatitude(),nearWalkingScore.getLongitude()),18)).mapType(NaverMap.MapType.Basic);

        //nmapview 대신 mapfragment 사용해서 뷰 띄워주기
       FragmentManager fm = this.getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.map_fragment);

        if(mapFragment == null){
            mapFragment = MapFragment.newInstance(options);
            fm.beginTransaction().add(R.id.map_fragment,mapFragment).commit();
        }
        mapFragment.getMapAsync(this);







        //controller.setMapCenter(new NGeoPoint(nearWalkingScore.getLongitude(), nearWalkingScore.getLatitude()), 14); // 맵 중앙 위치 컨트롤
// set POI data
       /* NMapPOIdata poiData = new NMapPOIdata(13, mMapViewerResourceProvider);
//  사용자 입력 주소 마커 아이템 생성
        poiData.addPOIitem(userWalkingScore.getLongitude(), userWalkingScore.getLatitude(),
                "Address: "+userWalkingScore.getAddress()+"latitude: "+userWalkingScore.getLatitude()+"longitude: "+userWalkingScore.getLongitude(), NMapPOIflagType.SPOT, 0);
        for(int i=0;i<userDecideNumberOfMark;i++) {
            nearWalkingScore  =(WalkingScore)getIntent().getSerializableExtra("nearWalkingScore"+i);
            nearLatitude = Double.toString(nearWalkingScore.getLatitude());
            nearLongitude =  Double.toString(nearWalkingScore.getLongitude());
            roundWalkingScore = Math.round(nearWalkingScore.getWalkingScore()*100)/100.0;

            nearWalkScore = Double.toString(roundWalkingScore);
            //사용자 입력 주소 근처 저장된 주소 마커 아이템 생성
            poiData.addPOIitem(nearWalkingScore.getLongitude(),nearWalkingScore.getLatitude(),
                    "WalkingScore: " + nearWalkScore + "\naddress: " + nearWalkingScore.getAddress(), markerId, 0);
        }
        poiData.endPOIdata();*/
// create POI data overlay
        //NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);

        /*mMapView.setNcpClientId(CLIENT_ID); // 클라이언트 아이디 값 설정
        mMapView.setClickable(true);
        mMapView.setEnabled(true);
        mMapView.setFocusable(true);
        mMapView.setFocusableInTouchMode(true);
        mMapView.requestFocus();*/

        //mOverlayManager.setOnCalloutOverlayViewListener(onCalloutOverlayViewListener);

    }
    //마커 클릭시 이벤트리스너
       /* private final NMapOverlayManager.OnCalloutOverlayViewListener onCalloutOverlayViewListener =
                new NMapOverlayManager.OnCalloutOverlayViewListener() {
                    @Override
                    public View onCreateCalloutOverlayView(NMapOverlay itemOverlay,
                                                           NMapOverlayItem overlayItem, Rect itemBounds) {
                        if (overlayItem != null) {
                            // [TEST] 말풍선 오버레이를 뷰로 설정함
                            String title = overlayItem.getTitle();
                            if (title != null) {
                                return new NMapCalloutCustomOverlayView(DivideActivity.this,
                                        itemOverlay, overlayItem, itemBounds);
                            }
                        }
                        // null을 반환하면 말풍선 오버레이를 표시하지 않음
                        return null;
                    }
                };*/

    //mapfragment에서 비동기식으로 navermap 호출시 onmapready 호출! --> 여기서 맵 설정 바꿔야 할듯
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        //마커 클릭시 나오는 정보창
        final InfoWindow infoWindow = new InfoWindow();
        infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(this) {
            @NonNull
            @Override
            public CharSequence getText(@NonNull InfoWindow infoWindow) {
                return (CharSequence)infoWindow.getMarker().getTag();
            }
        });
        infoWindow.setOnClickListener(new Overlay.OnClickListener() {
            @Override
            public boolean onClick(@NonNull Overlay overlay) {
                return true;
            }
        });

        //마커 생성 (배열 통해서 생성)
        final Marker marker[] = new Marker[13];
        for(int i =0;i<marker.length;i++){
            marker[i]=new Marker();
        }
        marker[0].setPosition(new LatLng(userWalkingScore.getLatitude(),userWalkingScore.getLongitude()));
        marker[0].setTag("Address: "+userWalkingScore.getAddress()+"latitude: "+userWalkingScore.getLatitude()+"longitude: "+userWalkingScore.getLongitude());
        marker[0].setMap(naverMap);
        //마커 클릭리스너 붙이기
        for(int i =0;i<13;i++){
            marker[i].setOnClickListener(new Overlay.OnClickListener() {
                @Override
                public boolean onClick(@NonNull Overlay overlay) {
                    infoWindow.open((Marker)overlay);
                    return true;
                }
            });

        }
        for(int i=1;i<=userDecideNumberOfMark;i++){

            nearLatitude = Double.toString(nearWalkingScore.getLatitude());
            nearLongitude =  Double.toString(nearWalkingScore.getLongitude());
            roundWalkingScore = Math.round(nearWalkingScore.getWalkingScore()*100)/100.0;

            nearWalkScore = Double.toString(roundWalkingScore);
            //마커 위치 저쟝,
            marker[i].setPosition(new LatLng(nearWalkingScore.getLatitude(),nearWalkingScore.getLongitude()));
            marker[i].setTag("WalkingScore: " + nearWalkScore + "\naddress: " + nearWalkingScore.getAddress());
            marker[i].setMap(naverMap);
        }

        //위치 변경 (원래 options 사용해서 변경 가능하나,  xml파일에서 mapfragment받아오기때문에 navermap 호출후 위치 변경필요

        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(nearWalkingScore.getLatitude(),nearWalkingScore.getLongitude()));
        naverMap.moveCamera(cameraUpdate);
    }
}
