package com.score.user.walkingscoreapp;

import android.os.Environment;
import android.util.Log;

        import org.json.simple.JSONArray;
        import org.json.simple.JSONObject;
        import org.json.simple.parser.JSONParser;
        import org.json.simple.parser.ParseException;

        import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
        import java.net.HttpURLConnection;
        import java.net.MalformedURLException;
        import java.net.ProtocolException;
        import java.net.URL;
        import java.net.URLEncoder;

public class Coordinate
{
    Location location;

    JSONObject jsonObject;
    JSONParser parser = new JSONParser();

    JSONObject itemValue;
    JSONObject center;
    JSONArray results;
    JSONArray addresses;

    String longitude = "";
    String latitude = "";
    String addresstemp="";
    BufferedReader bufferedReader;
    String response;
    String inputLine;

    int responseCode;
    String clientId = "8du12wjj8x";//애플리케이션 클라이언트 아이디값";
    String clientSecret = "DI0wvlCs8Zsbtk5WEHSQxOLTXxFL881qmatgoSrX";//애플리케이션 클라이언트 시크릿값";

    public Location convertAddrtoCoord(final String address)
    {
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try {

                    String addr = URLEncoder.encode(address, "UTF-8");
                    String apiURL = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query=" + addr; //json
                    URL url = new URL(apiURL);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
                    con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);

                    responseCode = con.getResponseCode();

                    String code = Integer.toString(responseCode);

                    response = readURLStream(responseCode, con);
                    Log.d("결과0 좌표 ->주소", response);

                    location = addrToCoord_extraction(response);
                        //  location = new Location(Double.parseDouble(latitude), Double.parseDouble(longitude),addresstemp);
                    Log.d("로케이션", Double.toString(location.getLongitude()) + "  " + Double.toString(location.getLongitude()) + "   " + addresstemp);

                    bufferedReader.close();
                    con.disconnect();

                }
                catch (Exception e)
                {
                    System.out.println(e);
                    Log.d("익섹셥","체크됬나?");

                }
            }
        });
        thread.start();
        try
        {
            thread.join();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return location;
    }
    public Location convertCoordtoAddr(final WalkingScore walkingScore){
        Thread thread2  = new Thread(new Runnable() {

            public void run() {
                try {

                    String walkLatitude = Double.toString(walkingScore.getLatitude());
                    String walkLongitude = Double.toString(walkingScore.getLongitude());
                    String apiURL2 = "https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc?coords="+walkLongitude+","+walkLatitude+"&sourcecrs=epsg:4326&output=json"; //json

                    URL url = new URL(apiURL2);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
                    con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);
                    responseCode = con.getResponseCode();
                    response =readURLStream(responseCode,con);
                    Log.d("결과2 좌표 ->주소", response);

                    location = coordToAddr_extraction2(response);
                    Log.d("로케이션2",addresstemp );


                    bufferedReader.close();
                    con.disconnect();
                } catch (Exception e) {
                    Log.d("익셉션", "??");
                    System.out.println(e);

                    //주소를 숫자로 치거나 엉뚱한 값을 넣어서 앱이 꺼지니까
                    //앱이 꺼지지 않도록 익셉션에서 리턴값을 null로 받는다
                    //만약 값이 널을 리턴받으면 메인문에서 주소를 제대로 입력해달라고
                    //토스트 메세지를 띄우도록 코드를 짜보자

                }
            }
        });
        thread2.start();
        try
        {
            thread2.join();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return location;
    }

    public void staticMapping(){

        Thread thread3  = new Thread(new Runnable() {

            public void run() {
                try {
                    // String walkLatitude = Double.toString(walkingScore.getLatitude());
                    //String walkLongitude = Double.toString(walkingScore.getLongitude());
                    String apiURL3 = "https://naveropenapi.apigw.ntruss.com/map-static/v2/raster?w=300&h=300&center=127.1054221,37.3591614&level=11&maptype=basic&format=jpg"; //json


                    URL url = new URL(apiURL3);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
                    con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);
                    responseCode = con.getResponseCode();


                    String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                    InputStream is = con.getInputStream();

                    File imgFile = new File(rootPath+ "/" + "collect.jpg");
                    File outFile = new File(rootPath+ "/" + "out.jpg");

                    FileOutputStream outStream = new FileOutputStream(outFile);

                    //받아온 바이트스트림을 file객체에 쓰는 코드  한바이트 단위로 반복문이 돌아가기에 비효율적적
                    byte[] buf = new byte[con.getContentLength()];
                    int len = 0;

                    while ((len = is.read(buf)) > 0) {
                        outStream.write(buf, 0, len);
                    }
                    outStream.close();
                    is.close();

                    con.disconnect();
                } catch (Exception e) {
                    Log.d("익셉션", "??");
                    System.out.println(e);
                }
            }
        });
        thread3.start();
        try
        {
            thread3.join();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        // return location;
    }

    //이제 날아오는 json 데이터가 하나가 아니라 좌표에 근접한 모든 주소를 불러오는 모양인데
    //일단은 가장 맨위의 데이터만 받아와보겠다
   public Location coordToAddr_extraction2(String response)  {
        JSONObject area1;
        JSONObject area2;
        JSONObject area3;
        JSONObject region;
        JSONObject coords;
        try{
            jsonObject = (JSONObject) parser.parse(response);
            results = (JSONArray) jsonObject.get("results");
            Log.d("결과 ex2", response.toString());

           for (int i = 0; i < 1; i++) {

               itemValue = (JSONObject) results.get(i);
               Log.d("한번에 받는", results.get(i).toString());
               if (itemValue.get("region") != null) {
                   region= (JSONObject) itemValue.get("region");
                   if (region.get("area1") != null){
                        area1 = (JSONObject) region.get("area1");
                        addresstemp = (String) area1.get("name").toString();
                        Log.d("받은 주소1", area1.get("name").toString());
                    }

                    if (region.get("area2") != null){
                        area2 = (JSONObject) region.get("area2");
                        addresstemp = addresstemp +" "+(String) area2.get("name").toString();
                        Log.d("받은 주소2", addresstemp);
                    }

                    if (region.get("area3") != null) {
                        area3 = (JSONObject) region.get("area3");
                        addresstemp = addresstemp +" "+(String) area3.get("name").toString();
                        coords = (JSONObject) area3.get("coords");
                        center = (JSONObject) coords.get("center");
                        longitude = (String) center.get("x").toString();
                        latitude = (String) center.get("y").toString();
                    }
                }
            }
            Log.d("받은 주소", addresstemp);
            Log.d("받은 위도 경도ㅇㅇ", longitude + " " + latitude);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        location = new Location(Double.parseDouble(latitude), Double.parseDouble(longitude),addresstemp);
            return location;
    }

    public Location addrToCoord_extraction(String response)  {

        try{
            jsonObject = (JSONObject) parser.parse(response);
            addresses = (JSONArray) jsonObject.get("addresses");
            Log.d("결과 extraction", response.toString());

            for (int i = 0; i < addresses.size(); i++) {
                itemValue = (JSONObject) addresses.get(i);

                addresstemp = (String)itemValue.get("jibunAddress").toString();
                Log.d("json 주소",addresstemp);

                longitude = (String) itemValue.get("x").toString();
                Log.d("json x",longitude);

                latitude = (String) itemValue.get("y").toString();
                Log.d("json y",latitude);
            }
            Log.d("받은 위도 경도",longitude+" "+latitude);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        location = new Location(Double.parseDouble(latitude), Double.parseDouble(longitude),addresstemp);
        return location;
    }
    //read StringBuffer
    public String readURLStream(int responseCode,HttpURLConnection con) {
        StringBuilder response = new StringBuilder();
        try {
            if (responseCode == 200) { // 정상 호출
                bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {  // 에러 발생
                bufferedReader = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            while ((inputLine = bufferedReader.readLine()) != null) {
                response.append(inputLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response.toString();
    }
}

