package com.example.minemusicmp3player;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
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

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b) mediaPlayer.seekTo(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override   //(implements View.OnClickListener)상속받았으니까 온클릭 오버라이딩 함
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ibPlay :
                if(ibPlay.isActivated()==true){
                    mediaPlayer.pause();
                    ibPlay.setActivated(false);
                }else {
                    mediaPlayer.start();
                    ibPlay.setActivated(true);
                    //시크바를 스레드 방식으로 진행해주는 함수
                    setSeekBarThread();
                }
                break;
            case R.id.ibPrevious :
                mediaPlayer.stop();
                mediaPlayer.reset();
                index = (index == 0)? mainActivity.getMusicDataArrayList().size()-1 : index - 1 ;
                //제일 처음 곡으로 가면 이전곡이 제일 뒤에 곡을 재생해야함

                setPlayerData(index, true); //이전곡, 실행트루

                break;
            case R.id.ibNext :
                mediaPlayer.stop();
                mediaPlayer.reset();
                index = (index == mainActivity.getMusicDataArrayList().size()-1) ? 0 : index + 1 ;
                //제일 처음 곡으로 가면 이전곡이 제일 뒤에 곡을 재생해야함

                setPlayerData(index, true); //이전곡, 실행트루
                break;

            case R.id.ibLike :
                if(ibLike.isActivated() == true){
                    ibLike.setActivated(false);
                    musicData.setLiked(0);
                    Toast.makeText(mainActivity, "좋아요 취소", Toast.LENGTH_SHORT).show();
                }else {
                    ibLike.setActivated(true);
                    musicData.setLiked(1);
                    Toast.makeText(mainActivity, "좋아요", Toast.LENGTH_SHORT).show();
                }
                //데이타베이스에 좋아요와 카운트값이 해당되는 노래에 수정이 완료된다.
                mainActivity.getMusicDBHelper().updateMusicDataToDB(musicData);

                break;
            default: break;
        }
    }

    private void setSeekBarThread() {
        //1. Thread, Runnable
        //2. 임시객체, 임시객체
        //3. Runnable을 람다식으로 구현하는 방법

        Thread thread = new Thread(new Runnable() {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
            @Override
            public void run() {
                while (mediaPlayer.isPlaying()==true){
                    int timeData = mediaPlayer.getCurrentPosition();
                    //미디어플레이어가 재생중이라면
                    seekBar.setProgress(timeData);  //미디어플레이어는 위치를 다 알고 잇음 현재 시각을 가져와서 시크바 진행을 움직여줌
                    //시크바 프로그래스바와 값을 동시에 변경하고 싶다면 이 스레드안에서 바꿀 수 없고 runOnUIThread를 하나 더 써야한다.
                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvCurrentTime.setText(simpleDateFormat.format(timeData));
                        }
                    });
                    SystemClock.sleep(100);
                }//end of while
            }
        });
        thread.start();
    }

//    //리사이클러뷰에서 아이템을 선택하면 해당된 위치와 좋아요 음악인지 일반 음악인지 선택내용이 온다. 좋아요 음악은 false로 넘어오고
//    //일반음악은 (true)
//    public void setPlyerData(int position, boolean flag) {
//        index = position;
//
//        mediaPlayer.stop();
//        mediaPlayer.reset();
//
//        if(flag == true){   //트루면 일반노래
////            ArrayList<MusicData> arrayList=mainActivity.getMusicDataArrayList();
////            musicData = arrayList.get(index);
//            musicData = mainActivity.getMusicDataArrayList().get(index);
//        }else { //false면 좋아요노래
//            //위와 마찬자지로 좋아요 데이타 리스트 가져오면 됨
//        }
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
//
//        tvTitle.setText(musicData.getTitle());
//        tvArtist.setText(musicData.getArtist());
//        tvPlayCount.setText(String.valueOf(musicData.getPlayCount()));
//        tvDuration.setText(simpleDateFormat.format(Integer.parseInt(musicData.getDuration())));
//
//        if(musicData.getLiked() == 1){
//            ibLike.setActivated(true);
//        }else{
//            ibLike.setActivated(false);
//        }
//
//        // 앨범 이미지 세팅
//        Bitmap albumImg = getAlbumImg(mainActivity, Long.parseLong(musicData.getAlbumArt()), 200);
//        if(albumImg != null){
//            ivAlbum.setImageBitmap(albumImg);
//        }else{
//            ivAlbum.setImageResource(R.drawable.album_default);
//        }
//
//        // 음악 재생
//        Uri musicURI = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,musicData.getId());
//        try {
//            mediaPlayer.setDataSource(mainActivity, musicURI);
//            mediaPlayer.prepare();
//            mediaPlayer.start();
//            seekBar.setProgress(0);
//            seekBar.setMax(Integer.parseInt(musicData.getDuration()));
//            ibPlay.setActivated(true);
//
////            setSeekBarThread();
//
//            // 재생완료 리스너
//            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaPlayer mediaPlayer) {
//                    musicData.setPlayCount(musicData.getPlayCount() + 1);
//                    ibNext.callOnClick();
//                }
//            });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    //앨범사진 아이디와 앨범 사이즈를 부여한다.
//    private Bitmap getAlbumImg(Context context, long albumArt, int imgMaxSize) {
//        BitmapFactory.Options options = new BitmapFactory.Options();    //외부에서 가져오는 이미지는 비트맵을 통해서 가져온다. 옵션은 어떤식으로 가져올 것인지
//
//        /*컨텐트 프로바이더(Content Provider)는 앱 간의 데이터 공유를 위해 사용됨.
//        특정 앱이 다른 앱의 데이터를 직접 접근해서 사용할 수 없기 때문에
//        무조건 컨텐트 프로바이더를 통해 다른 앱의 데이터를 사용해야만 한다.
//        다른 앱의 데이터를 사용하고자 하는 앱에서는 URI를 이용하여 컨텐트 리졸버(Content Resolver)를 통해
//        다른 앱의 컨텐트 프로바이더에게 데이터를 요청하게 되는데
//        요청받은 컨텐트 프로바이더는 URI를 확인하고 내부에서 데이터를 꺼내어 컨텐트 리졸버에게 전달한다.
//        */
//
//        ContentResolver contentResolver = context.getContentResolver(); //외부 이미지는 컨텐트리졸버를 통해서 가져온다.
//
//        // 앨범아트는 uri를 제공하지 않으므로, 별도로 생성.
//        Uri uri = Uri.parse("content://media/external/audio/albumart/" + albumArt); //이미지 경로+이미지 아이디    가져오는 주소는 유알아이
//        if (uri != null){
//            ParcelFileDescriptor fd = null;
//            try{
//                fd = contentResolver.openFileDescriptor(uri, "r");  //이미지 읽어서 fd에다가 넣음 fd에 파일이 들어잇음 /그 값을 읽어온다
//
//                //true면 비트맵객체에 메모리를 할당하지 않아서 비트맵을 반환하지 않음.
//                //다만 options fields는 값이 채워지기 때문에 Load 하려는 이미지의 크기를 포함한 정보들을 얻어올 수 있다.
//
//                options.inJustDecodeBounds = false; //false면 비트맵을 만들고 해당이미지를 가로세로를 중심으로 가져옴. true면 비트맵을 만들지 않고 해당이미지의 가로, 세로, Mime type등의 정보만 가져옴
//                //비트맵으로 가져온다
//
//                Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor(), null, options);  //비트멥팩토리에 그 파일 디스크립터를 주고, 패딩은 안주고,
//
//                if(bitmap != null){
//                    // 정확하게 사이즈를 맞춤
//                    if(options.outWidth != imgMaxSize || options.outHeight != imgMaxSize){  //비트맵이 내가 원하는 사이즈가 아니면 맞혀줌
//                        Bitmap tmp = Bitmap.createScaledBitmap(bitmap, imgMaxSize, imgMaxSize, true);   //내가 원하는 사이즈로 맞혀서
//                        bitmap.recycle();   //비트맵 정리해주라
//                        bitmap = tmp;
//                    }
//                }
//                return bitmap;
//
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }finally {
//                try {
//                    if (fd != null)
//                        fd.close();
//                } catch (IOException e) {
//                }
//            }
//        }
//        return null;
//    }

//    public void setPlayerData(int position, boolean flag) {
//        index = position;
//
//        mediaPlayer.stop();
//        mediaPlayer.reset();
//
//        if(flag == true){
//            musicData=mainActivity.getMusicDataArrayList().get(index);
//        }else{
//
//        }
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
//
//        tvTitle.setText(musicData.getTitle());
//        tvArtist.setText(musicData.getArtist());
//        tvPlayCount.setText(String.valueOf(musicData.getPlayCount()));
//        tvDuration.setText(simpleDateFormat.format(Integer.parseInt(musicData.getDuration())));
//
//        if(musicData.getLiked() == 1){
//            ibLike.setActivated(true);
//        }else{
//            ibLike.setActivated(false);
//        }
//
//        // 앨범 이미지 세팅
//        Bitmap albumImg = getAlbumImg(mainActivity, Integer.parseInt(musicData.getAlbumArt()), 200);
//        if(albumImg != null){
//            ivAlbum.setImageBitmap(albumImg);
//        }else{
//            ivAlbum.setImageResource(R.drawable.album_default);
//        }
//
//        // 음악 재생
//        Uri musicURI = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,musicData.getId());
//        try {
//            mediaPlayer.setDataSource(mainActivity, musicURI);
//            mediaPlayer.prepare();
//            mediaPlayer.start();
//            seekBar.setProgress(0);
//            seekBar.setMax(Integer.parseInt(musicData.getDuration()));
//            ibPlay.setActivated(true);
//
//            //setSeekBarThread();
//
//            // 재생완료 리스너
//            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaPlayer mediaPlayer) {
//                    musicData.setPlayCount(musicData.getPlayCount() + 1);
//                    ibNext.callOnClick();
//                }
//            });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    //앨범사진 아이디와 앨범사이즈를 부여한다.
//    private Bitmap getAlbumImg(Context context, long albumArt, int imgMaxSize) {
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        /*컨텐트 프로바이더(Content Provider)는 앱 간의 데이터 공유를 위해 사용됨.
//        특정 앱이 다른 앱의 데이터를 직접 접근해서 사용할 수 없기 때문에
//        무조건 컨텐트 프로바이더를 통해 다른 앱의 데이터를 사용해야만 한다.
//        다른 앱의 데이터를 사용하고자 하는 앱에서는 URI를 이용하여 컨텐트 리졸버(Content Resolver)를 통해
//        다른 앱의 컨텐트 프로바이더에게 데이터를 요청하게 되는데
//        요청받은 컨텐트 프로바이더는 URI를 확인하고 내부에서 데이터를 꺼내어 컨텐트 리졸버에게 전달한다.
//        */
//        ContentResolver contentResolver = context.getContentResolver();
//
//        // 앨범아트는 uri를 제공하지 않으므로, 별도로 생성.
//        Uri uri = Uri.parse("content://media/external/audio/albumart/"+albumArt);
//        if (uri != null){
//            ParcelFileDescriptor fd = null;
//            try{
//                fd = contentResolver.openFileDescriptor(uri, "r");
//
//                //true면 비트맵객체에 메모리를 할당하지 않아서 비트맵을 반환하지 않음.
//                //다만 options fields는 값이 채워지기 때문에 Load 하려는 이미지의 크기를 포함한 정보들을 얻어올 수 있다.
//                //93번 문항부터 98번까지는 체크안해도 되는 문장임. options.inJustDecodeBounds = false; 앞문장까지
//
//                options.inJustDecodeBounds = false; // false 비트맵을 만들고 해당이미지의 가로, 세로, 중심으로 가져옴
//                Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor(), null, options);
//
//                if(bitmap != null){
//                    // 정확하게 사이즈를 맞춤
//                    if(options.outWidth != imgMaxSize || options.outHeight != imgMaxSize){
//                        Bitmap tmp = Bitmap.createScaledBitmap(bitmap, imgMaxSize, imgMaxSize, true);
//                        bitmap.recycle();
//                        bitmap = tmp;
//                    }
//                }
//                return bitmap;
//
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }finally {
//                try {
//                    if (fd != null)
//                        fd.close();
//                } catch (IOException e) {
//                }
//            }
//        }
//        return null;
//    }

    //리사이클러뷰에서 아이템을 선택하면 해당된 위치와 좋아요 음악인지 일반 음악인지 선택내용이 온다. 좋아요 음악은 false로 넘어오고
    //일반음악은 (true)
    public void setPlayerData(int position, boolean flag) {
        index = position;

        mediaPlayer.stop();
        mediaPlayer.reset();

        if(flag == true){
            //디비에서 저장해서 가져와서 줌. 뮤직데이터에 넘겨줌.
            musicData=mainActivity.getMusicDBHelper().selectMusicTblMusicData(mainActivity.getMusicDataArrayList().get(index));
        }else{

        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");

        tvTitle.setText(musicData.getTitle());
        tvArtist.setText(musicData.getArtist());
        tvPlayCount.setText(String.valueOf(musicData.getPlayCount()));
        tvDuration.setText(simpleDateFormat.format(Integer.parseInt(musicData.getDuration())));

        if(musicData.getLiked() == 1){
            ibLike.setActivated(true);
        }else{
            ibLike.setActivated(false);
        }

        // 앨범 이미지 세팅
        Bitmap albumImg = getAlbumImg(mainActivity, Long.parseLong(musicData.getAlbumArt()), 200);
        if(albumImg != null){
            ivAlbum.setImageBitmap(albumImg);
        }else{
            ivAlbum.setImageResource(R.drawable.album_default);
        }

        // 음악 재생
        Uri musicURI = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,musicData.getId());
        try {
            mediaPlayer.setDataSource(mainActivity, musicURI);  //메인액티비티에다가 뮤직유알아이를 줌
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBar.setProgress(0);
            seekBar.setMax(Integer.parseInt(musicData.getDuration()));
            ibPlay.setActivated(true);

            setSeekBarThread();

            // 한곡의 노래를 완료했을 때 발생하는 이벤트 리스너
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    musicData.setPlayCount(musicData.getPlayCount() + 1);
                    //데이타베이스에 노래듣는 카운트 증가시켜서 저장함. 디비 속에 좋아요와 증가치값이 다 들어감
                    mainActivity.getMusicDBHelper().updateMusicDataToDB(musicData); //이렇게 해주면 카운트값이 포함되서 넣어주는 것이다
                    ibNext.callOnClick();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //앨범사진 아이디와 앨범사이즈를 부여한다.
    public static Bitmap getAlbumImg(Context context, long albumArt, int imgMaxSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        /*컨텐트 프로바이더(Content Provider)는 앱 간의 데이터 공유를 위해 사용됨.
        특정 앱이 다른 앱의 데이터를 직접 접근해서 사용할 수 없기 때문에
        무조건 컨텐트 프로바이더를 통해 다른 앱의 데이터를 사용해야만 한다.
        다른 앱의 데이터를 사용하고자 하는 앱에서는 URI를 이용하여 컨텐트 리졸버(Content Resolver)를 통해
        다른 앱의 컨텐트 프로바이더에게 데이터를 요청하게 되는데
        요청받은 컨텐트 프로바이더는 URI를 확인하고 내부에서 데이터를 꺼내어 컨텐트 리졸버에게 전달한다.
        */
        ContentResolver contentResolver = context.getContentResolver();

        // 앨범아트는 uri를 제공하지 않으므로, 별도로 생성.
        Uri uri = Uri.parse("content://media/external/audio/albumart/"+albumArt);
        if (uri != null){
            ParcelFileDescriptor fd = null;
            try{
                fd = contentResolver.openFileDescriptor(uri, "r");

                //true면 비트맵객체에 메모리를 할당하지 않아서 비트맵을 반환하지 않음.
                //다만 options fields는 값이 채워지기 때문에 Load 하려는 이미지의 크기를 포함한 정보들을 얻어올 수 있다.
                //93번 문항부터 98번까지는 체크안해도 되는 문장임. options.inJustDecodeBounds = false; 앞문장까지

                options.inJustDecodeBounds = false; // false 비트맵을 만들고 해당이미지의 가로, 세로, 중심으로 가져옴
                Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor(), null, options);

                if(bitmap != null){
                    // 정확하게 사이즈를 맞춤
                    if(options.outWidth != imgMaxSize || options.outHeight != imgMaxSize){
                        Bitmap tmp = Bitmap.createScaledBitmap(bitmap, imgMaxSize, imgMaxSize, true);
                        bitmap.recycle();
                        bitmap = tmp;
                    }
                }
                return bitmap;

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }finally {
                try {
                    if (fd != null)
                        fd.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

}
