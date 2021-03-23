package com.example.minemusicmp3player;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.FrameLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    //activity_main id에 있는 구조다
    private DrawerLayout drawerLayout;
    private FrameLayout frameLayout;    //이 프레임 레이아웃에 프래그먼트를 넣어야함
    private RecyclerView recyclerView;
    private RecyclerView recyclerLike;
    //musicDataArrayList
    ArrayList<MusicData> musicDataArrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //find id activty_main에서 아이디 찾기

        // View 아이디 연결
        findViewByIdFunc();

        //외부접근권한 설정
        requestPermissionsFunc();

        //어뎁터생성
        MusicAdapter musicAdapter = new MusicAdapter(getApplicationContext());  //뮤직 어뎁터를 생성하고

        //리사이클러뷰에는 리니어레이아웃메니저를 적용시켜야된다.
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        //리사이클러뷰에다가 리니어레이아웃매니저를 적용
        recyclerView.setAdapter(musicAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);

        //ArrayList<musicData>를 가져와서 musicAdapter에 적용시켜야된다.
        musicDataArrayList = findMusic();
        //가져온 걸 뮤직어뎁터의 musicList에 제공해서 작동시키면 됨
        musicAdapter.setMusicList(musicDataArrayList); //뮤직어뎁터 클래스의 뮤직리스트에다가 값을 전달달
        musicAdapter.notifyDataSetChanged();  //변화됐다고 알려주면 뿌려짐


        //프레그먼트 지정  프레임레이아웃에다가 내가만든 프레그먼트 지정
        //현재 액티비티에 있는 프레임레이아웃에 프레그먼트 지정
        replaceFrag();

    }

    // sdCard 안의 음악을 검색한다
    public ArrayList<MusicData> findMusic() {
        ArrayList<MusicData> sdCardList = new ArrayList<>();

        String[] data = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DURATION};

        // 특정 폴더에서 음악 가져오기
//        String selection = MediaStore.Audio.Media.DATA + " like ? ";
//        String selectionArqs = new String[]{"%MusicList%"}

        // 전체 영역에서 음악 가져오기
        Cursor cursor = getApplicationContext().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                data, null, null, data[2] + " ASC");//타이틀로 정렬시킨[2] 그 정보가 커서에 다 들어감

        if (cursor != null) {
            while (cursor.moveToNext()) {

                // 음악 데이터 가져오기
                String id = cursor.getString(cursor.getColumnIndex(data[0]));
                String artist = cursor.getString(cursor.getColumnIndex(data[1]));
                String title = cursor.getString(cursor.getColumnIndex(data[2]));
                String albumArt = cursor.getString(cursor.getColumnIndex(data[3]));
                String duration = cursor.getString(cursor.getColumnIndex(data[4]));

                MusicData mData = new MusicData(id, artist, title, albumArt, duration, 0, 0);

                sdCardList.add(mData);
            }
        }

        return sdCardList;
    }


    //외부파일 접근하려하는데 허용하시겠습니까
    private void requestPermissionsFunc() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                MODE_PRIVATE);  //허용해주는 기능
    }

    //현재 액티비티에 있는 프레임레이아웃에 프레그먼트 지정
    private void replaceFrag() {   //매개변수로(int position)주면->대체할 프레그먼트가 많을 경우 이렇게 해주면 들어오는 값에 따라 화면 바꿀 수 있다.
        //프레그먼트 생성
        Player player = new Player();   //플레이어클래스의 디폴트 생성자를 부름

        FragmentTransaction ft =getSupportFragmentManager().beginTransaction(); //객체 만들고
        ft.replace(R.id.frameLayout, player);   //만든 객체의 멤버함수인 리플레이스
        ft.commit();    //이렇게 하면 달라붙음
    }


    // View 아이디 연결
    private void findViewByIdFunc() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);  //형변환은 안써줘도 됨
        frameLayout = (FrameLayout) findViewById(R.id.frameLayout); //이 프레임 레이아웃에 프래그먼트를 넣어야함
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerLike = (RecyclerView) findViewById(R.id.recyclerLike);
    }

}
