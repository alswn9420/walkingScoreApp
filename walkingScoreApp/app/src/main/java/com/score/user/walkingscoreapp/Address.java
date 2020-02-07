package com.score.user.walkingscoreapp;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Vector;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Address {
    private int currentPage;
    private int countPerPage;
    private String resultType;
    private String confmKey;

    public Address() {
        currentPage = 1;
        countPerPage = 20;
        resultType = "json";
       confmKey = "U01TX0FVVEgyMDE4MDYzMDIzMjgwMTEwNzk3NDk=";
      // confmKey = "U01TX0FVVEgyMDE4MDgxMzE0NDgxNjEwODA3Mzk=";
    }

    public Address(int currentPage, int countPerPage, String resultType, String confmKey) {
        super();
        this.currentPage = currentPage;
        this.countPerPage = countPerPage;
        this.resultType = resultType;
        this.confmKey = confmKey;
    }

    // 아거 제이슨 데이터 통째로 스트링으로 바꾼거
    public Vector<String> getAddressVector(String keyword) {
        Vector<String> addressVector = null;

        String jsonAddress = getJSONAddressFromJuso(keyword);

        addressVector = parseJSONAddressVector(jsonAddress);

        return addressVector;
    }

    // 아거 제이슨 데이터 통째로 스트링으로 바꾼거
    public String getFullAddress(String keyword) {
        String fullAddress = null;

        String jsonAddress = getJSONAddressFromJuso(keyword);

        fullAddress = parseJSONAddress(jsonAddress);

        return fullAddress;
    }

    public String getJSONAddressFromJuso(final String keyword) {
        final StringBuffer response = new StringBuffer();

        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
                {
        try {

            String apiURL = "http://www.juso.go.kr/addrlink/addrLinkApi.do?currentPage=" + currentPage;
            //String apiURL = "http://www.juso.go.kr/addrlink/addrMobileLinkUrl.do?currentPage=" + currentPage;
            apiURL += "&countPerPage="+countPerPage;
            apiURL += "&keyword="+URLEncoder.encode(keyword,"UTF-8");
            apiURL += "&confmKey="+confmKey;
            apiURL += "&resultType="+resultType;

            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();


            BufferedReader br;
            if(responseCode==200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
            } else {  // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream(), "utf-8"));

            }

            String inputLine;

            //왜 while썻어요?

            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
                Log.d("Address_while",response.toString());
            }
            /*response.append(br.readLine());
            Log.d("Address_while",response.toString());*/



            br.close();
            con.disconnect();

        } catch (Exception e) {
            System.out.println(e);
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
        Log.d("Address",response.toString());
        return response.toString();
    }

    public String parseJSONAddress(String jsonAddress) {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = null;
        JSONObject results;
        JSONArray juso;
        JSONObject item;
        String fullAddress = null;

        try {
            jsonObject = (JSONObject)parser.parse(jsonAddress);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if(jsonObject.get("results") != null) {
            results = (JSONObject)jsonObject.get("results");
            juso = (JSONArray)results.get("juso");

            if(juso != null) {
                item = (JSONObject) juso.get(0);
                if(item.get("roadAddrPart1") != null){
                    fullAddress = (String)item.get("roadAddrPart1").toString();

                }
            }else{
                Log.d("여기다","잭팟");
            }

        }
        return fullAddress;
    }
    //지번주소를 파싱하는 메소드
    public String parseJSONJibunAddress(String jsonAddress) {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = null;
        JSONObject results;
        JSONArray juso;
        JSONObject item;
        String fullAddress = null;

        try {
            jsonObject = (JSONObject)parser.parse(jsonAddress);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if(jsonObject.get("results") != null) {
            results = (JSONObject)jsonObject.get("results");
            juso = (JSONArray)results.get("juso");

            if(juso != null) {
                item = (JSONObject) juso.get(0);
                if(item.get("jibunAddr") != null){
                    fullAddress = (String)item.get("jibunAddr").toString();
                    Log.d("여기라고?","fullAddress");
                }
            }
        }
        return fullAddress;
    }

    public Vector<String> parseJSONAddressVector(String jsonAddress) {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = null;
        JSONObject results;
        JSONArray juso;
        JSONObject item;
        String address = null;
        Vector<String> addressVector = new Vector<String>();

        try {
            jsonObject = (JSONObject)parser.parse(jsonAddress);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if(jsonObject.get("results") != null) {
            results = (JSONObject)jsonObject.get("results");
            juso = (JSONArray)results.get("juso");

            if(juso != null) {
                for(int i = 0; i < juso.size(); i++) {
                    item = (JSONObject) juso.get(i);
                    if(item.get("roadAddrPart1") != null){
                        address = (String)item.get("roadAddrPart1").toString();
                        addressVector.add(address);
                    }
                }
            }
        }
        return addressVector;
    }
}