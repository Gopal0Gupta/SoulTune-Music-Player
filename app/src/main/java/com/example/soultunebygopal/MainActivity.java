package com.example.soultunebygopal;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
ListView listView;
String[] songList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listsong);

        runTimePermission();

    }
    public void runTimePermission(){
        Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                         forDisplaySongs();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }
    public ArrayList<File> findSong(File file){
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();

        for(File everyFile:files){
            if(everyFile.isDirectory() && !everyFile.isHidden()){
                arrayList.addAll(findSong(everyFile));
            }
            else {
                if(everyFile.getName().endsWith(".mp3")||everyFile.getName().endsWith(".wav")){
                    arrayList.add(everyFile);
                }
            }
        }
        return arrayList;
    }
    public void forDisplaySongs(){
        final ArrayList<File> mysongs = findSong(Environment.getExternalStorageDirectory());
        songList = new String[mysongs.size()];
        for(int i=0;i<mysongs.size();i++){
            songList[i] = mysongs.get(i).getName().toString().replace(".mp3","").replace(".wav","");
        }
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,songList);
//        listView.setAdapter(adapter);

        customadapter customadapter = new customadapter();
        listView.setAdapter(customadapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String songname = (String) listView.getItemAtPosition(position);
                startActivity(new Intent(getApplicationContext(),HomeActivity.class)
                        .putExtra("songs",mysongs)
                        .putExtra("songname",songname)
                        .putExtra("pos",position));
            }
        });

    }
    class customadapter extends BaseAdapter{

        @Override
        public int getCount() {
            return songList.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View myView = getLayoutInflater().inflate(R.layout.list_item,null);
            TextView txtsong = myView.findViewById(R.id.txtsongname);
            txtsong.setSelected(true);
            txtsong.setText(songList[position]);

            return myView;
        }
    }
}