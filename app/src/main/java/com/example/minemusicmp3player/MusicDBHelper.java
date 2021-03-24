package com.example.minemusicmp3player;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.MediaStore;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class MusicDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "musicDB";
    private static final int VERSION = 1;
    private Context context;
    //2. 싱글톤 자신의 객체멤버를 갖는다.
    private static MusicDBHelper musicDBHelper;

    //데이타베이스를 만든다.  //생성자가 데이타베이스를 만든다.name이 테이블 이름임
    private MusicDBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, VERSION);
        this.context = context;
    }

    //1. 싱글톤 만들기 1단계 자기가 리턴갑임 겟인스턴스를 해야함
    public static MusicDBHelper getInstance(Context context){
        if(musicDBHelper == null){
            musicDBHelper = new MusicDBHelper(context); //한 번만 만들어야함으로 없으면 만들고
        }
        return musicDBHelper;   //있으면 있는 걸 리턴해줌
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
                "CREATE TABLE musicTBL(" +
                        "id VARCHAR(15) PRIMARY KEY," +
                        "artist VARCHAR(15)," +
                        "title VARCHAR(15)," +
                        "albumArt VARCHAR(15)," +
                        "duration VARCHAR(15)," +
                        "liked INTEGER );");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists musicTBL");
        onCreate(sqLiteDatabase);
    }

    // DB Select
    public ArrayList<MusicData> selectMusicTbl() {

        ArrayList<MusicData> musicDBArrayList = new ArrayList<>();

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        // 쿼리문 입력하고 커서 리턴 받음
        Cursor cursor = sqLiteDatabase.rawQuery("select * from musicTBL;", null);

        while (cursor.moveToNext()) {
            MusicData musicData = new MusicData(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getInt(5),
                    cursor.getInt(6));

            musicDBArrayList.add(musicData);
        }

        cursor.close();
//        sqLiteDatabase.close();

        return musicDBArrayList;
    }

    // DB Select
    public MusicData selectMusicTblMusicData(MusicData data) {

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        // 쿼리문 입력하고 커서 리턴 받음
        Cursor cursor = sqLiteDatabase.rawQuery("select * from musicTBL WHERE id ='" +data.getId() + "';", null);

        MusicData musicData = null;
        while (cursor.moveToNext()) {
            musicData = new MusicData(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getInt(5),
                    cursor.getInt(6));
        }

        cursor.close();
//        sqLiteDatabase.close();

        return musicData;
    }


    // DB 삽입
    public boolean insertMusicDataToDB(ArrayList<MusicData> arrayList) {

        boolean returnValue = false;

        //insert를 하기 위해서 sqlLiteDatabase를 가져와야한다.
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        try {
            for (MusicData data : arrayList) {

                // db에서 리스트 가져오기
                ArrayList<MusicData> dbList = selectMusicTbl();

                // db에 속해있는 요소인지 확인
                if (!dbList.contains(data)) {
                    String title1 = data.getTitle().replace("'","_");
                    data.setTitle(title1);
                    String query = "insert into musicTBL values('" + data.getId() + "','"+ data.getArtist() + "','"+data.getTitle() + "','" + data.getAlbumArt() + "','"+ data.getDuration() + "',"+ data.getPlayCount() + "," + data.getLiked() + ");";

                    // 쿼리문 작성해서 넘김
                    // 예외발생시 SQLException
                    sqLiteDatabase.execSQL(query);
                }
            }
            returnValue = true;
        } catch (Exception e) {
            returnValue = false;
        }

        return returnValue;
    }

    // DB 업데이트
    public boolean updateMusicDataToDB(ArrayList<MusicData> arrayList) {
        boolean returnValue = false;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        try {
            for (MusicData data : arrayList) {

                String query = "UPDATE musicTBL SET liked = " + data.getLiked() + " WHERE id = '" + data.getId() + "';";
                sqLiteDatabase.execSQL(query);
            }

            returnValue = true;
        } catch (Exception e) {
            return false;
        }

//        sqLiteDatabase.close();

        return returnValue;
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
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                data, null, null, data[2] + " ASC");

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

    // 좋아요 리스트 저장
    public ArrayList<MusicData> saveLikeList() {

        ArrayList<MusicData> musicDBArrayList = new ArrayList<>();

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        // 쿼리문 입력하고 커서 리턴 받음
        Cursor cursor = sqLiteDatabase.rawQuery("select * from musicTBL where liked = 1;", null);

        while (cursor.moveToNext()) {
            MusicData musicData = new MusicData(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getInt(5),
                    cursor.getInt(6));

            musicDBArrayList.add(musicData);
        }

//        cursor.close();
//        sqLiteDatabase.close();

        return musicDBArrayList;
    }


    // sdcard에서 검색한 음악과 DB를 비교해서 중복되지 않은 플레이리스트를 리턴
    public ArrayList<MusicData> compareArrayList() {
        ArrayList<MusicData> sdCardList = findMusic();  //sdcard에서 가져옴.
        ArrayList<MusicData> dbList = selectMusicTbl(); //database에서 가져옴.

        // DB가 비었다면 sdcard리스트 리턴
        if (dbList.isEmpty()) {
            return sdCardList;
        }

        // DB가 이미 sdcard 정보를 가지고 있다면 DB리스트를 리턴
        // MusicData에 equals 오버라이딩 필수
        if (dbList.containsAll(sdCardList)) {
            return dbList;
        }

        // 두 리스트를 비교후 중복되지 않은 값을 DB리스트에 추가후 리턴
        int size = dbList.size();
        //int size = sdCardList.size(); kdj -> 이것으로 처리할것
        for (int i = 0; i < size; ++i) {
            if (dbList.contains(sdCardList.get(i))) {
                continue;
            }
            dbList.add(sdCardList.get(i));
            ++size;
        }

        return dbList;
    }

    public boolean updateMusicDataToDB(MusicData data) {
        boolean returnValue = false;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        try{
            if(data != null){
                String query = "UPDATE musicTBL SET click = " + data.getPlayCount() + ", liked = " + data.getLiked() + " WHERE id = '" + data.getId() + "';";
                sqLiteDatabase.execSQL(query);
            }
            returnValue = true;

        }catch (Exception e){
            return false;
        }

        sqLiteDatabase.close();

        return returnValue;
    }

}
