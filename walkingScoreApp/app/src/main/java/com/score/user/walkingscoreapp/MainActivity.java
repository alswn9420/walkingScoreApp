package com.score.user.walkingscoreapp;

        import android.Manifest;
        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.content.pm.PackageManager;
        import android.net.ConnectivityManager;
        import android.net.NetworkInfo;
        import android.os.Bundle;
        import android.os.Environment;
        import android.os.SystemClock;
        import android.preference.PreferenceManager;
        import android.support.v4.app.ActivityCompat;
        import android.support.v4.app.FragmentManager;
        import android.support.v4.content.ContextCompat;
        import android.support.v7.app.AppCompatActivity;
        import android.support.v7.widget.VectorEnabledTintResources;
        import android.util.Log;
        import android.view.View;
        import android.view.WindowManager;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.LinearLayout;
        import android.widget.Spinner;
        import android.widget.TextView;
        import android.widget.Toast;

        import org.w3c.dom.Text;

        import java.io.BufferedReader;
        import java.io.BufferedWriter;
        import java.io.ByteArrayOutputStream;
        import java.io.File;
        import java.io.FileReader;
        import java.io.FileWriter;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.io.OutputStreamWriter;
        import java.util.Iterator;
        import java.util.StringTokenizer;
        import java.util.Vector;

        import com.naver.maps.geometry.LatLng;
        import com.naver.maps.geometry.Utmk;
        import com.naver.maps.map.MapFragment;

public class MainActivity extends AppCompatActivity {
    public final int REQUEST_CODE = 101;
    public final int REQUEST_CODE2 = 103;
    public final int REQUEST_SAVE_RESULT = 105;
    public final int REQUEST_NAVERMAP_CODE = 102;
    Button insertButton;
    Spinner spinnerView;
    Location location;
    WalkingScore inputWalkingScore;
    WalkingScore userInputLocation;
    private Vector<WalkingScore> walkingScorePool = new Vector<>();
    Vector<WalkingScore> nearWalkingScore = new Vector<>();
    SharedPreferences setting;

    String addressValue;
    String[] addressItems;


    boolean pointSearchCheck = false;  // 밑에 위경도 검색일때만 ture로 바뀌는 변수 주소검색과 위도검색의 searchSuccess 조건이 다름을 이용해 사용하는 변수


    int userDecideNumberOfMark = 12; // 사용자가 정하는 마커 개수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE); // 키보드 올라오면 화면 크기 재조정

        // 파일 외부저장소를 읽는 권할을 부여하는 코드
        /*ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "권한 있음", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this,"권한 없음",Toast.LENGTH_LONG).show();
            if(ActivityCompat.shouldShowRequestPermissionRationale(
                    this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(this,"권한 설명 필요",Toast.LENGTH_LONG).show();
            }else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }
        }*/
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
        int permissionCheck2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck2 == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, " 권한 있음", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "권한 없음", Toast.LENGTH_LONG).show();
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "권한 설명 필요", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
            }
        }


        setting = getPreferences(0);
        dataInsert(setting);

        int loadNumber = 1;
        String line;
        while (!((line = (setting.getString("location" + loadNumber, "empty"))).equals("empty"))) {
            StringTokenizer stringTokenizer = new StringTokenizer(line, "\t");
            double longitude = Double.parseDouble(stringTokenizer.nextToken());
            double latitude = Double.parseDouble(stringTokenizer.nextToken());
            double walkingScore = Double.parseDouble(stringTokenizer.nextToken());
            walkingScorePool.add(new WalkingScore(latitude, longitude, walkingScore));
            loadNumber++;
        }
        //dataFirstInsert();
        final String[] markSpinnerItem = new String[12];
        for (int index = 0; index < 12; index++)
            markSpinnerItem[index] = Integer.toString(index + 1);
        Spinner markSpinner = (Spinner) findViewById(R.id.markSpinner);

        ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, markSpinnerItem);
        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        markSpinner.setAdapter(arrayAdapter2);

        markSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                userDecideNumberOfMark = Integer.parseInt(markSpinnerItem[position]);
                if (walkingScorePool.size() < userDecideNumberOfMark) {
                    userDecideNumberOfMark = walkingScorePool.size();
                }
                Log.d("userdecideMark",Integer.toString(userDecideNumberOfMark));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //dataFirstInsert(); // walk_scor_data에서 파일읽어오는 메소드
        Log.d("저장", "" + walkingScorePool.size());

        //맨위에 검색 버튼
        Button searchButton = (Button) findViewById(R.id.button);
        searchButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {

                Location location = null;
                String editAddr = null;

                EditText addrEdit = (EditText) findViewById(R.id.edit);
                if (addrEdit.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "주소가 입력되지 않았습니다", Toast.LENGTH_LONG).show();
                } else if (!isNetWork()) {
                    Toast.makeText(getApplicationContext(), "네트워크 연결 불량", Toast.LENGTH_LONG).show();
                } else {
                    Coordinate coordinate = new Coordinate();
                    editAddr = addrEdit.getText().toString();
                    editAddr = exceptUnderground(editAddr);  // 지하나 산이 있으면 주소를 지하와 산을 뺀 값을 리턴
                    addrEdit.setText(null);

                    Spinner spinnerView = (Spinner) findViewById(R.id.spinnerView);
                    Address getAddress = new Address();

                    spinnerView = createItems(editAddr);  // 스피너뷰 아이템들 생성

                    if (location != null) {

                        inputWalkingScore = new WalkingScore(location.getLatitude(), location.getLongitude());
                        userInputLocation = inputWalkingScore;

                        userInputLocation.setAddress(coordinate.convertCoordtoAddr(inputWalkingScore).getAddress()); //사용자가 입력한 주소값

                        nearWalkingScore = nearestPosition(inputWalkingScore); // 이 메소드에서 근사값을 가진 워킹스코어로 변환
                        inputWalkingScore = nearWalkingScore.get(0);
                        addressValue = coordinate.convertCoordtoAddr(inputWalkingScore).getAddress();//리턴타입 로케이션
                        visibleText(inputWalkingScore, addressValue);
                    } else {
                        Log.d("로케이션 에러", "로케이션이 없습니다.");
                    }
                }
            }
        });

        spinnerView = (Spinner) findViewById(R.id.spinnerView);
        spinnerView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            EditText addrEdit = (EditText) findViewById(R.id.edit);

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int index, long id) {

                String selectedAddress;
                Coordinate coordinate = new Coordinate();
                selectedAddress = exceptUnderground(spinnerView.getSelectedItem().toString());

                location = coordinate.convertAddrtoCoord(selectedAddress);

                if (location == null) {
                    Address address = new Address();
                    String jsonAddress = address.getJSONAddressFromJuso(spinnerView.getSelectedItem().toString());
                    String jibunAddress = address.parseJSONJibunAddress(jsonAddress);
                    location = coordinate.convertAddrtoCoord(jibunAddress);
                }

                    inputWalkingScore = new WalkingScore(location.getLatitude(), location.getLongitude());
                userInputLocation = inputWalkingScore;
                //userInputLocation.setAddress(selectedAddress);
                userInputLocation.setAddress(coordinate.convertCoordtoAddr(inputWalkingScore).getAddress()); //네이버 주소를 유저인풋로케이션에 저장

                nearWalkingScore = nearestPosition(inputWalkingScore);
                inputWalkingScore = nearWalkingScore.get(0);
                addressValue = coordinate.convertCoordtoAddr(inputWalkingScore).getAddress();//네이버에서 받아오는 주소
                visibleText(inputWalkingScore, addressValue);

                appearNaverMap(userInputLocation, userDecideNumberOfMark);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        // 위도 경도 스코어 데이터 삽입
        insertButton = (Button) findViewById(R.id.insertButton);
        insertButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {

                inputWalkingScore = readEditText();
                if (inputWalkingScore == null) {
                    Toast.makeText(getApplicationContext(), "위도와 경도 모두 입력해주세요", Toast.LENGTH_LONG).show();
                } else if (inputWalkingScore.getLatitude() >= 33 && inputWalkingScore.getLatitude() <= 38
                        && inputWalkingScore.getLongitude() >= 124 && inputWalkingScore.getLongitude() <= 132) {
                    String puts = inputWalkingScore.toString();

                    SharedPreferences.Editor editor;
                    editor = setting.edit();
                    editor.putString("location" + walkingScorePool.size(), puts);
                    editor.commit();

                    walkingScorePool.add(inputWalkingScore);

                    Toast.makeText(MainActivity.this, "데이터 삽입 완료", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "데이터 삽입 실패, 대한민국 영토 내 검색", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button loadButton = (Button) findViewById(R.id.loadButton);
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent listIntent = new Intent(getApplicationContext(), LoadActivity.class);
                startActivityForResult(listIntent, REQUEST_CODE);

            }
        });
        Button saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent listIntent = new Intent(getApplicationContext(), ListActivity.class);
                startActivityForResult(listIntent, REQUEST_CODE2);
            }
        });
        Button collectButton = (Button) findViewById(R.id.collectButton);
        collectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Iterator<WalkingScore> it = walkingScorePool.iterator();

                WalkingScore latlng = new WalkingScore();

                try {
                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/utmk.txt");

                    BufferedWriter out = new BufferedWriter(new FileWriter(file, false));
                    out.write("좌표 x  y\n");
                    while (it.hasNext()) {
                        latlng = it.next();
                        LatLng latLng = new LatLng(latlng.getLatitude(), latlng.getLongitude()); // 워킹스코어 풀을 하나씩 읽으면서 Latlng 객체에 위도 경도를 저장

                        Utmk utmk = Utmk.valueOf(latLng);  // 위경도를 utmk좌표계로 변환


                        out.write(utmk.toString() + "\r\n");
                    }
                    out.flush();
                    out.close();
                }catch (IOException e) {
                    Log.d("ex", "dfd");
                }
                Toast.makeText(getApplicationContext(), "덮어쓰기 완료", Toast.LENGTH_LONG).show();


                //Coordinate coord = new Coordinate();
                //coord.staticMapping();
            }
        });
        // 위도 경도로 숫자로 검색하기
        Button pointSearchButton = (Button) findViewById(R.id.pointSearchButton);
        pointSearchButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {

                inputWalkingScore = readEditText();
                pointSearchCheck= true;

                if (inputWalkingScore == null) {
                    Toast.makeText(getApplicationContext(), "위도와 경도 모두 입력해주세요", Toast.LENGTH_LONG).show();
                }else if(!isNetWork()){
                    Toast.makeText(getApplicationContext(), "네트워크 상태 불량", Toast.LENGTH_LONG).show();
                }
                else if (inputWalkingScore.getLatitude() >= 33 && inputWalkingScore.getLatitude() <= 38
                        && inputWalkingScore.getLongitude() >= 124 && inputWalkingScore.getLongitude() <= 132) {
                    Coordinate coordinate = new Coordinate();
                    userInputLocation = inputWalkingScore;
                    userInputLocation.setAddress(coordinate.convertCoordtoAddr(inputWalkingScore).getAddress()); // 사용자가 검색한 주소
                    nearWalkingScore = nearestPosition(inputWalkingScore);
                    inputWalkingScore = nearWalkingScore.get(0);  //가장 가까운 주소

                    addressValue = coordinate.convertCoordtoAddr(inputWalkingScore).getAddress();

                    visibleText(inputWalkingScore, addressValue);


                    appearNaverMap(userInputLocation, userDecideNumberOfMark); // 오버라이딩으로 appearNaverMap메소드 두개만들어서 포인ㅌ서치랑 어드레스 서치 구분 하든가
                } else {
                    Toast.makeText(MainActivity.this, "데이터 삽입 실패, 대한민국 영토 내 검색", Toast.LENGTH_LONG).show();
                }
            }
        });
        Button mapViewButton = (Button) findViewById(R.id.MapViewButton);
        mapViewButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {


                if(!isNetWork()){
                    Toast.makeText(MainActivity.this, "네트워크 상태 불량", Toast.LENGTH_LONG).show();
                }
                else appearNaverMap(userInputLocation, userDecideNumberOfMark);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 불러오기 버튼 누를시 나오는 액티비티 에서 가져온 경로 처리
        if (requestCode == REQUEST_CODE) {

            if (resultCode == RESULT_OK) {
                String path = data.getStringExtra("path");
                String line = new String();
                StringTokenizer stringTokenizer;
                try {
                    BufferedReader in = new BufferedReader(new FileReader(path));
                    walkingScorePool.clear();
                    in.readLine();

                    while (true) {
                        if ((line = in.readLine()) == null) break;

                        stringTokenizer = new StringTokenizer(line, "\t");

                        double longitude = Double.parseDouble(stringTokenizer.nextToken());
                        double latitude = Double.parseDouble(stringTokenizer.nextToken());
                        double walkingScore = Double.parseDouble(stringTokenizer.nextToken());
                        walkingScorePool.add(new WalkingScore(latitude, longitude, walkingScore));
                    }
                } catch (IOException e) {

                }
                dataNewInsert(setting);  // 데이터 싹비우고 로드된 데이터 삽입
                if (walkingScorePool.size() < userDecideNumberOfMark) {
                    userDecideNumberOfMark = walkingScorePool.size();
                }

                Toast.makeText(getApplicationContext(), "불러오기 완료", Toast.LENGTH_LONG).show();
            }
        }
        // 세이브 버튼시 여는 리스트액티비티에서 가져오는 데이터 처리
        if (requestCode == REQUEST_CODE2) { //파일 덮어쓰기시 조건문
            if (resultCode == RESULT_OK) {
                String path = data.getStringExtra("path");
                Log.d("path", path);
                String line = new String();
                StringTokenizer stringTokenizer;
                try {

                    File file = new File(path);

                    BufferedWriter out = new BufferedWriter(new FileWriter(file, false));

                    out.write("Longitude" + "\t" + "latitude" + "\t" + "WalkingSore" + "\r\n");

                    for (int i = 0; i < walkingScorePool.size(); i++) {
                        out.write(walkingScorePool.get(i).toString() + "\r\n");
                        //out.newLine();
                    }
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    Log.d("ex", "dfd");
                }
                Toast.makeText(getApplicationContext(), "덮어쓰기 완료", Toast.LENGTH_LONG).show();
            } else if (resultCode == REQUEST_SAVE_RESULT) { //파일생성시 조건문

                String fileName = data.getStringExtra("fileName");
                String path = data.getStringExtra("path");
                String line = new String();
                StringTokenizer stringTokenizer;
                Log.d("도착", path);
                Log.d("파일이름도 도착", fileName);
                try {

                    File saveFile = new File(path + "/" + fileName + ".txt");

                    if (saveFile.exists()) { //이미 같은 파일이 존재한다면 덮어쓰기
                        BufferedWriter out = new BufferedWriter(new FileWriter(path, false));

                        out.write("Longitude" + "\t" + "latitude" + "\t" + "WalkingSore" + "\r\n");

                        for (int i = 0; i < walkingScorePool.size(); i++) {
                            out.write(walkingScorePool.get(i).toString() + "\r\n");
                            //out.newLine();
                        }
                        out.flush();
                        out.close();
                    } else {
                        Log.d("세이브파일 경로", saveFile.getPath());
                        BufferedWriter out = new BufferedWriter(new FileWriter(saveFile, false));

                        out.write("Longitude" + "\t" + "latitude" + "\t" + "WalkingSore" + "\r\n");
                        for (int i = 0; i < walkingScorePool.size(); i++) {
                            out.write(walkingScorePool.get(i).toString() + "\r\n");

                        }
                        out.flush();
                        out.close();
                    }

                } catch (IOException e) {
                    Log.d("catch", "잡혔다");
                }
                Toast.makeText(getApplicationContext(), "파일생성 완료", Toast.LENGTH_LONG).show();
            }
        }
    }

    public boolean dataEmpty(SharedPreferences setting) {
        if (!setting.getBoolean("dataExist", false))
            return true;
        return false;
    }

    public void dataInsert(SharedPreferences setting) {
        if (dataEmpty(setting)) {

            try {
                String line;
                SharedPreferences.Editor editor;
                editor = setting.edit();
                int locationNumber = 0;
                BufferedReader in = new BufferedReader(new InputStreamReader(getResources().openRawResource(com.score.user.walkingscoreapp.R.raw.walk_score_data)));
                //line = in.readLine();
                while ((line = in.readLine()) != null) {
                    editor.putString("location" + locationNumber, line);
                    locationNumber++;
                }
                if (locationNumber != 0) {
                    editor.putBoolean("dataExist", true);
                }
                editor.commit();
                in.close();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "file error!!", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void dataNewInsert(SharedPreferences setting) {
        SharedPreferences.Editor editor;
        editor = setting.edit();
        editor.clear();
        int locationNumber = 0;
        for (int i = 0; i < walkingScorePool.size(); i++) {
            editor.putString("location" + locationNumber, walkingScorePool.get(i).toString());
            locationNumber++;
        }
        if (locationNumber != 0) {
            editor.putBoolean("dataExist", true);
        }
        editor.commit();
    }

    /*public void dataFirstInsert(){
        try{
            String line;
            int locationNumber = 1;
            BufferedReader in = new BufferedReader(new InputStreamReader(getResources().openRawResource(com.score.user.walkingscoreapp.R.raw.walk_score_data)));
            in.readLine();

            while ((line = in.readLine()) != null) {
                StringTokenizer stringTokenizer = new StringTokenizer(line, "\t");
                double longitude = Double.parseDouble(stringTokenizer.nextToken());
                double latitude = Double.parseDouble(stringTokenizer.nextToken());
                double walkingScore = Double.parseDouble(stringTokenizer.nextToken());
                walkingScorePool.add(new WalkingScore(latitude, longitude, walkingScore));
                locationNumber++;
            }

            in.close();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "file error!!", Toast.LENGTH_LONG).show();
        }
    }
*/

    public WalkingScore readEditText() {
        EditText editLatitude = (EditText) findViewById(R.id.editLatitude);
        EditText editLongitude = (EditText) findViewById(R.id.editLongitude);
        EditText editWalkScore = (EditText) findViewById(R.id.editWalkScore);

        if (editLatitude.getText().toString().equals(".") || editLongitude.getText().toString().equals(".")
                || editWalkScore.getText().toString().equals(".")) {
            return null;
        }

        String latitudeData = editLatitude.getText().toString();
        String longitudeData = editLongitude.getText().toString();
        String walkScoreData = editWalkScore.getText().toString();
        if (latitudeData.length() == 0 || longitudeData.length() == 0) {
            Toast.makeText(getApplicationContext(), "위도와 경도 모두 입력해 주세요", Toast.LENGTH_LONG).show();
            return null;
        } else if (walkScoreData.length() == 0) {
            WalkingScore walkingScore = new WalkingScore(Double.parseDouble(latitudeData), Double.parseDouble(longitudeData), 0);
            initalizeText();
            return walkingScore;
        } else {
            WalkingScore walkingScore = new WalkingScore(Double.parseDouble(latitudeData), Double.parseDouble(longitudeData), Double.parseDouble(walkScoreData));
            initalizeText();
            return walkingScore;
        }
    }

    public void initalizeText() {
        EditText editLatitude = (EditText) findViewById(R.id.editLatitude);
        EditText editLongitude = (EditText) findViewById(R.id.editLongitude);
        EditText editWalkScore = (EditText) findViewById(R.id.editWalkScore);

        editLatitude.setText("");
        editLongitude.setText("");
        editWalkScore.setText("");
    }

    //제일 가까운 위치 찾고 동시에 검색한 위치에서 가장 가까운 위치 n개 서치
    public Vector<WalkingScore> nearestPosition(WalkingScore walkingScore) {
        nearWalkingScore.clear();

        boolean searchSuccess = false;
        double nearestScore = 0;  // sqrt한 거리 값
        Iterator<WalkingScore> it = walkingScorePool.iterator();

        int nearestNumber = 0;  // 더 작은 값을 만날때마다 저장되는 공간
        int rear=0;
        double vectorNearestScore; //노 가장 가까운 값은 nearestScore에 저장될것이고 가장 가까운값보다 작은지 일일이 비교하기 위해 사용되는 변수
        WalkingScore walkingScorethem = new WalkingScore();

        walkingScorethem = it.next();
        nearestScore=walkingScorethem.calculateDistance(walkingScore.getLatitude(), walkingScore.getLongitude());
        nearWalkingScore.add(rear, walkingScorethem);
        nearWalkingScore.get(rear).setNearestScore(nearestScore);

        while (it.hasNext()) {

            rear = 0;
            walkingScorethem = it.next();
            vectorNearestScore = walkingScorethem.calculateDistance(walkingScore.getLatitude(), walkingScore.getLongitude());

            if (vectorNearestScore < nearestScore) {
                nearestScore = walkingScorethem.calculateDistance(walkingScore.getLatitude(), walkingScore.getLongitude());
                nearWalkingScore.add(rear, walkingScorethem);
                nearWalkingScore.get(rear).setNearestScore(nearestScore);
            } //else if (vectorNearestScore == 0) { // 이부분을 거리고 계산하지 말고 입력값이랑 동일한지 비교하는 조건문을 사용
            else if (walkingScorethem.getLongitude() == walkingScore.getLongitude() && walkingScorethem.getLatitude() == walkingScore.getLatitude()) {
                searchSuccess = true;
                nearestScore = walkingScorethem.calculateDistance(walkingScore.getLatitude(), walkingScore.getLongitude()); //0이다
                nearWalkingScore.add(rear, walkingScorethem);
                nearWalkingScore.get(rear).setNearestScore(nearestScore);
            } else { // 첫번째 값보다 작은경우

                    for (rear = 1; rear < userDecideNumberOfMark; rear++) {    // 맨앞 12개 값보다 큰 값들 전부 버림  // 만약에 rear1~12값이 저장 안되어 있으면 저장
                        //즉 이 코드로 인해 맨앞 12개만 정렬 하고 그 이후에 값들은 그냥 12번째 인덱스에 마구잡이로 정렬안된채로 저장됨
                        if (nearWalkingScore.size() < userDecideNumberOfMark) {
                            nearWalkingScore.add(nearWalkingScore.size(), walkingScorethem);
                            nearWalkingScore.get(nearWalkingScore.size() - 1).setNearestScore(vectorNearestScore);
                            break;
                        }
                        if (vectorNearestScore < nearWalkingScore.get(rear).getNearestScore()) {
                            nearWalkingScore.add(rear, walkingScorethem);
                            nearWalkingScore.get(rear).setNearestScore(vectorNearestScore);
                            break;
                        }
                }
            }
            nearestNumber++;
        }

        if (nearWalkingScore.get(0).getLongitude() == walkingScore.getLongitude() && nearWalkingScore.get(0).getLatitude() == walkingScore.getLatitude()){
            searchSuccess=true;
        }
        if(pointSearchCheck){
            searchSuccessMethod(searchSuccess);
        }


        return nearWalkingScore;
    }

    // 네이버 지도화면 버튼과 검색한 위도 경도 주소 가시화
    public void visibleText(WalkingScore walkingScore, String addressValue) {
        TextView tv = (TextView) findViewById(R.id.locationView);
        Button mapViewButton = (Button) findViewById(R.id.MapViewButton);

        tv.setText("");
        tv.append("Latitude : " + inputWalkingScore.getLatitude() + "\n");
        tv.append("Longitude : " + inputWalkingScore.getLongitude() + "\n");
        tv.append("WalkingScore : " + inputWalkingScore.getWalkingScore() + "\n");
        tv.append("Address : " + addressValue + "\n");
        tv.setVisibility(View.VISIBLE);
        mapViewButton.setVisibility(View.VISIBLE);
    }

    public Spinner createItems(String address) {
        Spinner spinnerView = (Spinner) findViewById(R.id.spinnerView);
        Address getAddress = new Address();

        if (address.length() <= 1) {
            Toast.makeText(getApplicationContext(), "최소 두 글자 이상 입력해주세요", Toast.LENGTH_LONG).show();
        } else {
            Vector<String> addressVector = getAddress.getAddressVector(address);

            int vectorSize = addressVector.size();
            if (vectorSize == 0) {
                Toast.makeText(getApplicationContext(), "주소를 찾지 못하였습니다", Toast.LENGTH_LONG).show();
            }
            addressItems = new String[vectorSize];
            for (int i = 0; i < vectorSize; i++) {
                addressItems[i] = addressVector.get(i);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, addressItems);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerView.setAdapter(adapter);

        }

        return spinnerView;
    }

    // 네이버 지도화면 액티비티 띄우기
    public void appearNaverMap(WalkingScore userInputLocation, int userDecideNumberOfMark) {

        if(userDecideNumberOfMark>nearWalkingScore.size()){
            nearWalkingScore=nearestPosition(inputWalkingScore);
        }

        String addressArray[] = new String[userDecideNumberOfMark];
        Coordinate coordinate = new Coordinate();
        Intent intent = new Intent(getApplicationContext(), DivideActivity.class);
        intent.putExtra("userDecideNumberOfMark", Integer.toString(userDecideNumberOfMark));
        intent.putExtra("userInputLocation", userInputLocation);

        for (int sequence = 0; sequence < userDecideNumberOfMark; sequence++) {
            addressArray[sequence] = coordinate.convertCoordtoAddr(nearWalkingScore.get(sequence)).getAddress();
            nearWalkingScore.get(sequence).setAddress(addressArray[sequence]);
            intent.putExtra("nearWalkingScore" + sequence, nearWalkingScore.get(sequence));
        }
        if(!pointSearchCheck){
            searchSuccessMethod();
        }
        pointSearchCheck=false;
        startActivity(intent);
    }

    // 검색어에서 지하랑 산 제외하기
    public String exceptUnderground(String editAddr) {
        String tokenLine;
        StringTokenizer stringTokenizer;
        StringBuilder stringBuilder = new StringBuilder();

        stringTokenizer = new StringTokenizer(editAddr);
        while (stringTokenizer.hasMoreTokens()) {
            tokenLine = stringTokenizer.nextToken();
            if ((!tokenLine.equals("지하")) && (!tokenLine.equals("산"))) {
                stringBuilder.append(tokenLine + " ");
            } else {
            }
        }
        editAddr = stringBuilder.toString();
        return editAddr;
    }

    // 가장 가까운 값을 찾는 메소드에서 검색 성공여부를 알려줄 토스트를 실행할 조건문을 판별하기 위한 메소드
    //사용 이유는 딜레이를 주기 위해서
    public void searchSuccessMethod() {
        Coordinate coordinate = new Coordinate();
        //userInputLocation.setAddress(coordinate.convertCoordtoAddr(inputWalkingScore).getAddress());
        Log.d("입력값주소", userInputLocation.getAddress());
        Log.d("근사값주소", nearWalkingScore.get(0).getAddress());

        for (int i = 0; i < userDecideNumberOfMark; i++) {
            if (userInputLocation.getAddress().equals(nearWalkingScore.get(i).getAddress())) {
                Toast.makeText(MainActivity.this, "매칭 성공!", Toast.LENGTH_LONG).show();
                return;
            }
        }
        Toast.makeText(MainActivity.this, "매칭 실패! 저장된 가까운 값 불러오기 완료", Toast.LENGTH_LONG).show();

    }
    public void searchSuccessMethod(boolean searchSucess){

        if(searchSucess){
            Toast.makeText(MainActivity.this, "매칭 성공!", Toast.LENGTH_LONG).show();
        }else
            Toast.makeText(MainActivity.this, "매칭 실패! 저장된 가까운 값 불러오기 완료", Toast.LENGTH_LONG).show();

    }

    Boolean isNetWork() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null) {
            if (networkInfo.isConnected()) {
                return true;
            }else return false;

        } else {
            return false;
        }
    }
    public abstract class OnSingleClickListener implements View.OnClickListener {
        // 중복 클릭 방지 시간 설정
        private static final long MIN_CLICK_INTERVAL=3000;   //중복클릭 방지 시간을 재기위한 롱타입변수

        private long mLastClickTime;

        public abstract void onSingleClick(View v);

        @Override
        public final void onClick(View v) {
            long currentClickTime= SystemClock.uptimeMillis();
            long elapsedTime=currentClickTime-mLastClickTime;
            mLastClickTime=currentClickTime;

            // 중복 클릭인 경우
            if(elapsedTime<=MIN_CLICK_INTERVAL){

                return;
            }
            // 중복 클릭아 아니라면 추상함수 호출
            onSingleClick(v);
        }

    }

}
//최대최저 위도 경도 구하기
// 우리나라 위도 33~38  경도 124~132
