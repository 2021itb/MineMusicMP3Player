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
    //3. 인터페이스를 멤버변수로 선언한다.
    private OnItemClickListener onItemClickListener = null;


    //생성자
    public MusicAdapter(Context context) {
        this.context = context;

    }
    //setters
    public void setMusicList(ArrayList<MusicData> musicList) {
        this.musicList = musicList;
    }

    //4. setter 함수를 진행한다.
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {   //임시객체로 만들어져서 들어간 것
        this.onItemClickListener = onItemClickListener;
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
        Bitmap albumImg = Player.getAlbumImg(context, Long.parseLong(musicList.get(position).getAlbumArt()), 200);//i는 앨범 이미지 사이즈
        if(albumImg != null){
            customViewHolder.albumArt.setImageBitmap(albumImg); //밑의 커스텀뷰홀더의 앨범아트에 넣겠다
        }   //앨범 아이디

        // recyclerviewer에 보여줘야할 정보 세팅
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");  //시간폼을 만들고
        customViewHolder.title.setText(musicList.get(position).getTitle());//포지션 타이틀 가져와서 밑 커스텀 뷰홀더에 잇는 타이틀에 넣고
        customViewHolder.artist.setText(musicList.get(position).getArtist());//포지션 타이틀 가져와서 밑 커스텀 뷰홀더에 잇는 아티스트에 넣고
        customViewHolder.duration.setText(simpleDateFormat.format(Integer.parseInt(musicList.get(position).getDuration())));//포지션 타이틀 가져와서 밑 커스텀 뷰홀더에 잇는 타이틀에 넣고


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

//            //여기서 걸어도 됨->오른쪽 화면 클릭하면 그 정보를 주고 닫히게끔 설정하기
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                }
//            });
//
//            itemView.setOnClickListener(v->{
//
//
//            });

            //5. 추상화메소드를 구현한다.  /아이템뷰를 누를 때마다
            itemView.setOnClickListener(view->{
                int position = getAdapterPosition();    //위치를 준다.
                if(position != RecyclerView.NO_POSITION){
                    onItemClickListener.onItemClick(view, position);    //이 뷰는 무엇일까..?
                    //아이템뷰를 누를때마다 이 값을 메인액티비티에서 갖고싶다
                }
            });
        }
    }

    //1. 인터페이스를 구현한다.setOnClickListener => setter함수이게 세터스 세터함수임  onClickListener가 인터페이스 이름이다.  (내부인터페이스가 없기 때문에 직접 만들어줘야함)
    public interface OnItemClickListener{
        //2. 추상화메소드를 선언한다.
        void onItemClick(View view, int position);
    }


}
