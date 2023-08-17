package com.example.iothome;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    // private : 외부에서의 생성 방지
    private DBHelper(Context context)
    {
        super(context, "myDB", null, 1);
    }

    // 싱글톤 구현
    private static DBHelper instance = null;

    public static DBHelper getInstance(Context context)
    {
        if(instance == null) // 인스턴스가 없으면 생성
        {
            instance = new DBHelper(context);
        }
        return instance;
    }

    @Override
    public void onConfigure(SQLiteDatabase db){
        db.setForeignKeyConstraintsEnabled(true);   // FK 기능 활성화
    }

    // 테이블 생성
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql1 = "CREATE TABLE contact (" +
                "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "mobile TEXT NOT NULL, " +
                "age INTEGER, " +
                "res_id INTEGER)";

        String sql2 = "CREATE TABLE todolist (" +
                "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "hour INTEGER NOT NULL, " +
                "minute INTEGER NOT NULL, " +
                "task TEXT NOT NULL, " +
                "date_id INTEGER NOT NULL REFERENCES date (id) ON UPDATE CASCADE ON DELETE CASCADE)";

        String sql3 = "CREATE TABLE date (" +
                "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "month_day TEXT NOT NULL)";

        String sql4 = "CREATE TABLE note (" +
                "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL, " +
//                "content TEXT)" +
                "content TEXT, " +
                "date_id INTEGER NOT NULL REFERENCES date (id) ON UPDATE CASCADE ON DELETE CASCADE)";

        db.execSQL(sql1);
        db.execSQL(sql2);
        db.execSQL(sql3);
        db.execSQL(sql4);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql1 = "DROP TABLE if exists contact";
        String sql2 = "DROP TABLE if exists todolist";
        String sql3 = "DROP TABLE if exists date";
        String sql4 = "DROP TABLE if exists note";

        db.execSQL(sql1);
        db.execSQL(sql2);
        db.execSQL(sql3);
        db.execSQL(sql4);
        onCreate(db);
    }
    
    // -------------------------------- 삽입 --------------------------------

    // 연락처 삽입
    public void insert_contact(String name, String mobile, int age, int res_id) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("insert into contact(name, mobile, age, res_id) values ('" + name + "', '" + mobile + "', " + age + ", " + res_id + ")");
        db.close();
    }
    
    // 특정 날짜에 대한 todolist 삽입
    public void insert_todolist(int hour, int minute, String task, int date_id) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("insert into todolist(hour, minute, task, date_id) values (" + hour + ", " + minute + ", '" + task + "', " + date_id + ")");
        db.close();
    }

    // 특정 날짜의 date에 대한 record 삽입
    public void insert_date(String month_day) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("insert into date(month_day) values ('" + month_day + "')");
        db.close();
    }

    // note 삽입
    public void insert_note(String title, String content, int date_id) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("insert into note(title, content, date_id) values ('" + title + "', '" + content + "', " + date_id + ")");
        db.close();
    }

    // -------------------------------- 조회 --------------------------------

    // date에서 특정 날짜를 갖는 record의 id 조회
    public int select_date_with_month_day(String month_day){
        int id = 0;
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT id FROM date WHERE month_day = '" + month_day + "'";
        Cursor cursor = db.rawQuery(sql, null);

        if(cursor != null && cursor.moveToFirst()){
            id = cursor.getInt(0);
            cursor.close();
        }
        db.close();
        return id;
    }

    // date에서 특정 id를 갖는 record의 month_day 조회
    public String select_month_day_with_id(int id){
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT month_day FROM date WHERE id = " + id;
        Cursor cursor = db.rawQuery(sql, null);

        cursor.moveToFirst();
        String month_day = cursor.getString(0);
        cursor.close();
        db.close();

        return month_day;
    }

    // 특정 date_id를 갖는 todolist 목록 조회
    public ArrayList<String> select_todolist_with_date_id(int date_id){
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT hour, minute, task FROM todolist WHERE date_id = " + date_id;
        Cursor cursor = db.rawQuery(sql, null);
        ArrayList<String> itemList = new ArrayList<>();

        while(cursor.moveToNext()){
            int hour = cursor.getInt(0);
            int minute = cursor.getInt(1);
            String task = cursor.getString(2);
            if (hour < 10){
                itemList.add("0" + Integer.toString(hour) + ":" + Integer.toString(minute) + " " + task);
            }else{
                itemList.add(Integer.toString(hour) + ":" + Integer.toString(minute) + " " + task);
            }
        }
        cursor.close();
        db.close();

        return itemList;
    }

    // 특정 정보를 갖는 todolist의 id 얻기 (수정을 위해 사용)
    public int select_id_from_todolist(int date_id, int hour, int minute, String task){
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT id FROM todolist WHERE date_id = " + date_id + " AND hour = " + hour + " AND minute = " + minute + " AND task = '" + task + "'";
        Cursor cursor = db.rawQuery(sql, null);

        cursor.moveToFirst();
        int id = cursor.getInt(0);
        cursor.close();
        db.close();

        return id;
    }

    // 전체 contact 목록 조회
    public ArrayList<ArrayList<String>> select_contact_list(){
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT name, mobile FROM contact";
        Cursor cursor = db.rawQuery(sql, null);

        ArrayList<ArrayList<String>> itemList = new ArrayList<ArrayList<String>>();
        ArrayList<String> nameList = new ArrayList<>();
        ArrayList<String> mobileList = new ArrayList<>();
        itemList.add(nameList);
        itemList.add(mobileList);

        while(cursor.moveToNext()){
            String name = cursor.getString(0);
            String mobile = cursor.getString(1);

            itemList.get(0).add(name);
            itemList.get(1).add(mobile);
        }
        cursor.close();
        db.close();
        return itemList;
    }

    // 특정 name을 가진 contact의 id 조회
    public int select_id_from_contact(String targetName){
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT id FROM contact WHERE name = '" + targetName + "'";
        Cursor cursor = db.rawQuery(sql, null);

        int id = cursor.getInt(0);
        cursor.close();
        db.close();
        return id;
    }

    // 특정 id을 가진 contact의 정보 조회
    public List select_contact_with_id(int targetId){
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT mobile, age FROM contact WHERE id = " + targetId;
        Cursor cursor = db.rawQuery(sql, null);

        String mobile = cursor.getString(0);
        int age = cursor.getInt(1);
        cursor.close();
        db.close();

        return Arrays.asList(mobile, age);
    }

    // 전체 note 목록 조회
    public ArrayList<ArrayList<String>> select_note_list(){
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT title, date_id FROM note";
        Cursor cursor = db.rawQuery(sql, null);

        ArrayList<ArrayList<String>> itemList = new ArrayList<ArrayList<String>>();
        ArrayList<String> titleList = new ArrayList<>();
        ArrayList<String> dateList = new ArrayList<>();
        itemList.add(titleList);
        itemList.add(dateList);

        while(cursor.moveToNext()){
            String title = cursor.getString(0);
            int dateId = cursor.getInt(1);

            String date = select_month_day_with_id(dateId);

            itemList.get(0).add(title);
            itemList.get(1).add(date);
        }
        cursor.close();
        db.close();
        return itemList;
    }

    // 특정 title의 note 조회
    public String select_note_with_title(String targetTitle){
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT content FROM note WHERE title = '" + targetTitle + "'";
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.getCount() == 0){
            return null;
        }

        cursor.moveToFirst();
        String content = cursor.getString(0);
        cursor.close();
        db.close();
        return content;
    }


    // -------------------------------- 검색 --------------------------------

    // 검색창에 task 입력하여 todolist 검색
    public ArrayList<String> select_todolist_with_task(String targetTask){
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT date_id, hour, minute, task FROM todolist WHERE task like '%" + targetTask + "%'";
        Cursor cursor = db.rawQuery(sql, null);

        ArrayList<String> itemList = new ArrayList<>();

        if(cursor.getCount() == 0) return null;

        while(cursor.moveToNext()){
            int dateId = cursor.getInt(0);

            int hour = cursor.getInt(1);
            int minute = cursor.getInt(2);
            String task = cursor.getString(3);

            String date = select_month_day_with_id(dateId);

            if (hour < 10){
                itemList.add(date + "    0" + Integer.toString(hour) + ":" + Integer.toString(minute) + " " + task);
            }else{
                itemList.add(date + "    " + Integer.toString(hour) + ":" + Integer.toString(minute) + " " + task);
            }

        }
        cursor.close();
        db.close();

        return itemList;
    }

    // 검색창에서 name 입력하여 contact 검색
    public ArrayList<ArrayList<String>> select_contact_with_name(String targetName){
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT name, mobile FROM contact WHERE name like '%" + targetName + "%'";
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.getCount() == 0){
            return null;
        }else{
            ArrayList<ArrayList<String>> itemList = new ArrayList<ArrayList<String>>();
            ArrayList<String> nameList = new ArrayList<>();
            ArrayList<String> mobileList = new ArrayList<>();
            itemList.add(nameList);
            itemList.add(mobileList);

            while(cursor.moveToNext()){
                String name = cursor.getString(0);
                String mobile = cursor.getString(1);

                itemList.get(0).add(name);
                itemList.get(1).add(mobile);
            }
            cursor.close();
            db.close();
            return itemList;
        }

    }

    // -------------------------------- 수정 --------------------------------

    // 특정 id의 todolist 수정
    public void update_todolist(int targetId, int hour, int minute, String task){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE todolist SET hour = " + hour + ", " + "minute = " + minute + ", " + "task = '" + task + "' WHERE id = " + targetId);
        db.close();
    }

    // 특정 name의 연락처 수정
    public void update_contact(String targetName, String name, String mobile){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE contact SET name = '" + name + "', " + "mobile = '" + mobile + "' WHERE name = '" + targetName + "'");
        db.close();
    }

    // 특정 title의 note 수정
    public void update_note(String targetTitle, String title, String content){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE note SET title = '" + title + "', content = '" + content + "' WHERE title = '" + targetTitle + "'");
        db.close();
    }

    // -------------------------------- 삭제 --------------------------------

    // 특정 date_id의 특정 todolist 삭제
    public void delete_todolist(int hour, int minute, String task, int date_id){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM todolist WHERE hour = " + hour + " AND " + "minute = " + minute + " AND " + "task = '" + task + "' AND " + "date_id = " + date_id);
        db.close();
    }

//    // 특정 id의 contact 삭제
//    public void delete_contact(int id){
//        SQLiteDatabase db = getWritableDatabase();
//        db.execSQL("DELETE FROM contact WHERE id = " + id);
//        db.close();
//    }

    // 특정 name의 contact 삭제
    public void delete_contact(String name){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM contact WHERE name = '" + name + "'");
        db.close();
    }

    // 특정 title의 note 삭제
    public void delete_note(String title){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM note WHERE title = '" + title + "'");
        db.close();
    }

}
