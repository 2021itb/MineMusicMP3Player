package com.example.minemusicmp3player;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

//왜 뷰 온클릭을 썼을까 implements View.OnClickListener
public class Player extends Fragment implements View.OnClickListener{  //얘는 뷰가 아니므로 뷰를 상속받고 구현하려면 implements View.OnClickListener


    private ImageView ivAlbum;
    private TextView tvPlayCount, tvArtist, tvTitle, tvCurrentTime, tvDuration;
    private SeekBar seekBar;
    private ImageButton ibPlay,ibPrevious, ibNext, ibLike;

    //프레그먼트에서 장착된 액티비티를 가져올 수가 있는데 그게 겟액티비티이다. getAcivity 하면 그 클래스의 모든 멤버를 가져올 수 있다.
    private MainActivity mainActivity;
    //노래를 등록하기 위해서 선언한 객체변수
    private MediaPlayer mediaPlayer = new MediaPlayer();

    private int index;  //노래 들을 위치 지정
    //데이타를 전체 5개를 가져오려고 함
    private MusicData musicData = new MusicData();

    //왜 리스트 중에 라이크 얼레이리스트만 썼을까 //왜 좋아요리스트만 가져왓을까?
    private ArrayList<MusicData> likeArrayList = new ArrayList<>(); //이거를 메인에서 가져오려고 함
    //왜 뮤직 어뎁터를 가져왔을까?
//    private MusicAdapter musicAdapter;  //리사이클러뷰의 뮤직어뎁터 가져옴 리사이클러뷰에 항목들을 제공해주려고 하니까 어뎁터가 필요함

    //프레그먼트는 자기가 붙어있는 화면 정보를 가져올 수 있다.


    //Context가 뭡니까? Context: (화면 + 컨트롤러 클래스)의 정보를 다 가지고 있는 것
    @Override
    public void onAttach(@NonNull Context context) {    //메인에 달라붙었을 때 온테치하고 컨텍스트를 줌
        super.onAttach(context);
        this.mainActivity = (MainActivity)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.mainActivity = null;   //
    }

    @Nullable
    @Override//여기서 화면설계한다./온크리에이트 뷰를 가져오는 때는 프레그먼트 화면이
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.player, container, false);  //플레이어를 가져와서, 컨테이너에 넣어서 , 지금 붙일 건 아니야.
        //인플레터 시키는 순간 플레이어가 메모리에 올라옴




        //메모리에 올라왔으니까 아이디 찾을 수 있음
        findViewByIdFunc(view); //뷰를 통해서 아이디를 찾아야함
        return view;
    }

    private void findViewByIdFunc(View view) {
        ivAlbum = view.findViewById(R.id.ivAlbum);
        tvPlayCount = view.findViewById(R.id.tvPlayCount);
        tvArtist = view.findViewById(R.id.tvArtist);
        tvTitle = view.findViewById(R.id.tvTitle);
        tvCurrentTime = view.findViewById(R.id.tvCurrentTime);
        tvDuration = view.findViewById(R.id.tvDuration);
        seekBar = view.findViewById(R.id.seekBar);
        ibPlay = view.findViewById(R.id.ibPlay);
        ibPrevious = view.findViewById(R.id.ibPrevious);
        ibNext = view.findViewById(R.id.ibNext);
        ibLike = view.findViewById(R.id.ibLike);

//        ibPlay.setOnClickListener(v->{});
        // 이벤트처리하는 또다른 방법: 이벤트끼리 모으려고
        ibPlay.setOnClickListener(this);    //이걸 누르면 누른 거에 해당하는 아이디가 밑의 OnClickListener로 감. (implements View.OnClickListener)상속받았으니까
        ibPrevious.setOnClickListener(this);
        ibNext.setOnClickListener(this);
        ibLike.setOnClickListener(this);
    }

    @Override   //(implements View.OnClickListener)상속받았으니까 온클릭 오버라이딩 함
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ibPlay : break;
            case R.id.ibPrevious : break;
            case R.id.ibNext : break;
            case R.id.ibLike : break;
            default: break;
        }
    }
}
