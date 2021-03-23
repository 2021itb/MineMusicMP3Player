package com.example.minemusicmp3player;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

//뷰홀더를 만든다 이름은 커스텀으로 지어줬다
public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.CustomViewHolder> {
    private Context context;
    private ArrayList<MusicData> musicList;

    //생성자
    public MusicAdapter(Context context) {
        this.context = context;

    }
    //setters
    public void setMusicList(ArrayList<MusicData> musicList) {
        this.musicList = musicList;
    }



    //리사이클러뷰에 들어갈 항목 뷰를 inflater 한다.  View홀더를 통해서 항목객체를 관리한다.
    @NonNull
    @Override   //여기가 화면임
    public MusicAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_item, viewGroup, false);//리사이클러아이팀을 뷰그룹에 붙일거야
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;//뷰홀더가 관리해서 주겠다.
    }

    @Override
    public void onBindViewHolder(@NonNull MusicAdapter.CustomViewHolder customViewHolder, int position) {
        //앨범 이미지를 비트맵으로 만들기
        Bitmap albumImg = getAlbumImg(context, Long.parseLong(musicList.get(position).getAlbumArt()), 200);//i는 앨범 이미지 사이즈
        if(albumImg != null){
            customViewHolder.albumArt.setImageBitmap(albumImg); //밑의 커스텀뷰홀더의 앨범아트에 넣겠다
        }   //앨범 아이디

        // recyclerviewer에 보여줘야할 정보 세팅
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");  //시간폼을 만들고
        customViewHolder.title.setText(musicList.get(position).getTitle());//포지션 타이틀 가져와서 밑 커스텀 뷰홀더에 잇는 타이틀에 넣고
        customViewHolder.artist.setText(musicList.get(position).getArtist());//포지션 타이틀 가져와서 밑 커스텀 뷰홀더에 잇는 아티스트에 넣고
        customViewHolder.duration.setText(simpleDateFormat.format(Integer.parseInt(musicList.get(position).getDuration())));//포지션 타이틀 가져와서 밑 커스텀 뷰홀더에 잇는 타이틀에 넣고


    }
    //앨범사진 아이디와 앨범 사이즈를 부여한다.
    private Bitmap getAlbumImg(Context context, long albumArt, int imgMaxSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();    //외부에서 가져오는 이미지는 비트맵을 통해서 가져온다. 옵션은 어떤식으로 가져올 것인지

        /*컨텐트 프로바이더(Content Provider)는 앱 간의 데이터 공유를 위해 사용됨.
        특정 앱이 다른 앱의 데이터를 직접 접근해서 사용할 수 없기 때문에
        무조건 컨텐트 프로바이더를 통해 다른 앱의 데이터를 사용해야만 한다.
        다른 앱의 데이터를 사용하고자 하는 앱에서는 URI를 이용하여 컨텐트 리졸버(Content Resolver)를 통해
        다른 앱의 컨텐트 프로바이더에게 데이터를 요청하게 되는데
        요청받은 컨텐트 프로바이더는 URI를 확인하고 내부에서 데이터를 꺼내어 컨텐트 리졸버에게 전달한다.
        */

        ContentResolver contentResolver = context.getContentResolver(); //외부 이미지는 컨텐트리졸버를 통해서 가져온다.

        // 앨범아트는 uri를 제공하지 않으므로, 별도로 생성.
        Uri uri = Uri.parse("content://media/external/audio/albumart/" + albumArt); //이미지 경로+이미지 아이디    가져오는 주소는 유알아이
        if (uri != null){
            ParcelFileDescriptor fd = null;
            try{
                fd = contentResolver.openFileDescriptor(uri, "r");  //이미지 읽어서 fd에다가 넣음 fd에 파일이 들어잇음 /그 값을 읽어온다

                //true면 비트맵객체에 메모리를 할당하지 않아서 비트맵을 반환하지 않음.
                //다만 options fields는 값이 채워지기 때문에 Load 하려는 이미지의 크기를 포함한 정보들을 얻어올 수 있다.

                options.inJustDecodeBounds = false; //false면 비트맵을 만들고 해당이미지를 가로세로를 중심으로 가져옴. true면 비트맵을 만들지 않고 해당이미지의 가로, 세로, Mime type등의 정보만 가져옴
                //비트맵으로 가져온다

                Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor(), null, options);  //비트멥팩토리에 그 파일 디스크립터를 주고, 패딩은 안주고,

                if(bitmap != null){
                    // 정확하게 사이즈를 맞춤
                    if(options.outWidth != imgMaxSize || options.outHeight != imgMaxSize){  //비트맵이 내가 원하는 사이즈가 아니면 맞혀줌
                        Bitmap tmp = Bitmap.createScaledBitmap(bitmap, imgMaxSize, imgMaxSize, true);   //내가 원하는 사이즈로 맞혀서
                        bitmap.recycle();   //비트맵 정리해주라
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

    @Override
    public int getItemCount() {
        return (musicList != null) ?  musicList.size() : 0 ;    //뿌려줘야할 개수를 매치시켜줌
    }



    public class CustomViewHolder extends RecyclerView.ViewHolder{
        //inflater한 데이타항목을 찾아온다.    이미지뷰 타이트 뮤직 듀레이션 가져와서 뿌려줘야함
        ImageView albumArt;
        TextView title;
        TextView artist;
        TextView duration;

        //부를때마다 뷰홀더에서 관리하니까 매치시켜줘야함

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            //인플레터는 여기서 찾으면 됨.
            this.albumArt = itemView.findViewById(R.id.d_ivAlbum);
            this.title = itemView.findViewById(R.id.d_tvTitle);
            this.artist = itemView.findViewById(R.id.d_tvArtist);
            this.duration = itemView.findViewById(R.id.d_tvDuration);   //값들을 다 붙여줌

        }
    }
}
