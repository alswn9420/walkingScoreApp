package com.score.user.walkingscoreapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class LoadActivity extends AppCompatActivity {
    private final int Three=3;

    private TextView textView;
    private ListView listView;
    private ArrayAdapter<String> listAdapter;
    private ArrayList<String> items;
    private String rootPath = "";  //안드로이드 저장소 최상단 파일경로 저장
    private String nextPath = "";  // 선태간 다음 파일 경로가 존재하는지 체크하기위해 저장
    private String prevPath = "";  // 전 디렉토리 경로를 저장
    private String currentPath = ""; //현재 디렉토리 경로

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        textView = (TextView) findViewById(R.id.tvPath);
        listView = (ListView) findViewById(R.id.lvFileControl);

        items = new ArrayList<>();
        listAdapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1, items);

        // 루트 경로 가져오기
        rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        boolean result = Init(rootPath);

        if (result == false)
            return;

        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                currentPath = textView.getText().toString();
                String extension="";
                String path = items.get(position).toString();
                if(path.length()>4)
                    extension=path.substring(path.length()-Three,path.length());

                //최상위 폴더에서 이전을 누르면 액티비티 종료
                if(currentPath.equals(rootPath)&&path.equals("..")){
                    finish();
                }
                else if (path.equals("..")&&!currentPath.equals(rootPath)) {

                    prevPath(path);
                }else if(extension.equals("txt")){
                    String intentPath = currentPath.concat("/"+path);
                    Intent respondIntent = new Intent();
                    respondIntent.putExtra("path",intentPath);
                    setResult(RESULT_OK,respondIntent);

                    finish();
                }
                else {
                    nextPath(path);
                }
            }
        });


    }

    public boolean Init(String rootPath) {
        // 파일 객체 생성

        File fileRoot = new File(rootPath);
        if (fileRoot.isDirectory() == false) {
            Toast.makeText(getApplicationContext(), "Not Directory", Toast.LENGTH_SHORT).show();
            return false;
        }

        textView.setText(rootPath);

        // 파일 리스트 가져오기
        String[] fileList = fileRoot.list();
        if (fileList == null) {
            Toast.makeText(getApplicationContext(), "Could not find List", Toast.LENGTH_SHORT).show();
            return false;
        }

        // 아이템 리스트 전부 삭제
        items.clear();

        // 리스트의 첫 항목은 뒤로가기 위해 ".." 세팅
        items.add("..");
        for (int i = 0; i < fileList.length; i++) {
            items.add(fileList[i]);
        }

        // 리스트 뷰에 적용
        listAdapter.notifyDataSetChanged();
        return true;
    }

    public void nextPath(String str) {
        prevPath = currentPath;

        // 현재 경로에서 / 와 다음 경로 붙이기
        nextPath = currentPath + "/" + str;
        File file = new File(nextPath);
        if (file.isDirectory() == false) {
            Toast.makeText(getApplicationContext(), "Not Directory", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] fileList = file.list();
        items.clear();
        items.add("..");

        for (int i = 0; i < fileList.length; i++) {
            items.add(fileList[i]);
        }

        textView.setText(nextPath);
        listAdapter.notifyDataSetChanged();

    }

    public void prevPath(String str) {
        nextPath = currentPath;
        prevPath = currentPath;


        // 마지막 / 의 위치 찾기
        int lastSlashPosition = prevPath.lastIndexOf("/");

        // 처음부터 마지막 / 까지의 문자열 가져오기
        prevPath = prevPath.substring(0, lastSlashPosition);
        File file = new File(prevPath);

        if (file.isDirectory() == false) {
            Toast.makeText(getApplicationContext(), "Not Directory", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] fileList = file.list();
        items.clear();
        items.add("..");

        for (int i = 0; i < fileList.length; i++) {
            items.add(fileList[i]);
        }

        textView.setText(prevPath);
        listAdapter.notifyDataSetChanged();
    }

}