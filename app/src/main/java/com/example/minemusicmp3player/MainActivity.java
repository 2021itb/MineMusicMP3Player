package com.example.minemusicmp3player;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {
    //activity_main id에 있는 구조다
    private DrawerLayout drawerLayout;
    private FrameLayout frameLayout;    //이 프레임 레이아웃에 프래그먼트를 넣어야함
    private RecyclerView recyclerView;
    private RecyclerView recyclerLike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //find id activty_main에서 아이디 찾기

        // View 아이디 연결
        findViewByIdFunc();

        //프레그먼트 지정  프레임레이아웃에다가 내가만든 프레그먼트 지정
        //현재 액티비티에 있는 프레임레이아웃에 프레그먼트 지정
        replaceFrag();

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
