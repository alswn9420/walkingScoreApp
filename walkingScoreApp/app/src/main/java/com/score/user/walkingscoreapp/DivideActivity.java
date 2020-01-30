package com.score.user.walkingscoreapp;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapOverlay;
import com.nhn.android.maps.NMapOverlayItem;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;



import com.nhn.android.maps.nmapmodel.NMapError;

import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;
import com.nhn.android.mapviewer.overlay.NMapResourceProvider;

import java.util.Vector;


public class DivideActivity extends NMapActivity
{
    private NMapView mMapView;// 지도 화면 View
    private final String CLIENT_ID = "8du12wjj8x";// 애플리케이션 클라이언트 아이디 값
    NMapResourceProvider mMapViewerResourceProvider = null;
    NMapOverlayManager mOverlayManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMapView = new NMapView(this);
        setContentView(mMapView);
        mMapViewerResourceProvider = new NMapViewerResourceProvider(this);
        mOverlayManager = new NMapOverlayManager(this, mMapView, mMapViewerResourceProvider);
        int markerId = NMapPOIflagType.PIN;
        String longitude [] = new String [12];
        String latitude [] = new String [12];
        String walkingScore [] = new String [12];
        String address [] = new String [12];
        int userDecideNumberOfMark;
        String nearLatitude,nearLongitude,nearWalkScore,nearAddress;
        WalkingScore userWalkingScore;
        WalkingScore nearWalkingScore;
        Double roundWalkingScore;

        Intent intent = getIntent();
        userDecideNumberOfMark = Integer.parseInt(intent.getStringExtra("userDecideNumberOfMark"));
        userWalkingScore =(WalkingScore)getIntent().getSerializableExtra("userInputLocation");


        nearWalkingScore  =(WalkingScore)getIntent().getSerializableExtra("nearWalkingScore"+0);
        NMapController controller = mMapView.getMapController();
        controller.setMapCenter(new NGeoPoint(nearWalkingScore.getLongitude(), nearWalkingScore.getLatitude()), 14); // 맵 중앙 위치 컨트롤
// set POI data
        NMapPOIdata poiData = new NMapPOIdata(13, mMapViewerResourceProvider);
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
        poiData.endPOIdata();
// create POI data overlay
        NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);

        mMapView.setNcpClientId(CLIENT_ID); // 클라이언트 아이디 값 설정
        mMapView.setClickable(true);
        mMapView.setEnabled(true);
        mMapView.setFocusable(true);
        mMapView.setFocusableInTouchMode(true);
        mMapView.requestFocus();

        mOverlayManager.setOnCalloutOverlayViewListener(onCalloutOverlayViewListener);

    }
    //마커 클릭시 이벤트리스너
        private final NMapOverlayManager.OnCalloutOverlayViewListener onCalloutOverlayViewListener =
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
                };
}
