package com.capstone.studenttracking;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBClass extends SQLiteOpenHelper {

    public static String dbname = "student_tracking";

    public static String url = "http://192.168.157.116:8082/student_tracking/api/";
    public static String urlLogin = url + "login.php";

//    admin
    public static String urlgetStudents = url + "admin/students.php";
    public static String urlgetStaffs = url + "admin/staffs.php";
    public static String urlAddStaff = url + "admin/add_staff.php";
    public static String urlDeleteStaff = url + "admin/delete_staff.php";
    public static String urlNotifications = url + "admin/notifications.php";
    public static String urlUpdateStudent = url + "admin/update_student.php";
    public static String urlApproveStudent = url + "admin/approve_student.php";
    public static String urlStaffProfile = url + "staff_profile.php";
    public static String urlGetAllStudents = url + "get_students.php";
    public static String urlUploadSchedule = url + "upload_schedule.php";
    public static String urlGetSchedule = url + "get_schedules.php";
    public static String urlGetAllStaff = url + "get_staff.php";
    public static String urlUpdateLocation = url + "updatelocation.php";
    public static String urlAddAnnouncement = url + "add_announcement.php";
    public static String urlGetAnnoucement = url + "get_announcements.php";
    public static String urlAddLostFound = url + "add_lost_found.php";
    public static String urlForgotPassword = url + "forgot_password.php";

    public static String urlRegistration = url + "register.php";
    public static String urlUsers = url + "users.php";
    public static String urlProfile = url + "profile.php";
    public static String urlUpdateProfile = url + "updateprofile.php";


    public static SQLiteDatabase database;


    public DBClass(Context context){

        super(context, DBClass.dbname, null, 1);
    }

    public void onCreate(SQLiteDatabase arg) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    public static void execNonQuery(String query){
        //Execute Insert, Update, Delete, Create table queries
        //Log.e("Quesry", query);
        database.execSQL(query);
    }

    public static Cursor getCursorData(String query){
        //Log.d("SQuery", query);
        Cursor res =  database.rawQuery(query, null);
        return res;
    }

    public static String getSingleValue(String query) {
        try {
            Cursor res = getCursorData(query);
            String value = "";
            if (res.moveToNext()) {
                return res.getString(0);
            }
            return value;
        }
        catch (Exception ex)
        {
            return "";
        }
    }

    public static int getNoOfRows(String query){
        try {
            Cursor res = database.rawQuery(query, null);
            return res.getCount();
        }catch (Exception ex)
        {
            return 0;
        }
    }

    public static boolean checkIfRecordExist(String query){
        //Log.e("CheckQuery", query);
        Cursor res =  database.rawQuery(query, null );
        if(res.getCount() > 0)
            return true;
        else
            return false;
    }


    public static boolean doesTableExists(String tableName)
    {
        try{
            Cursor cursor = getCursorData("SELECT * FROM " + tableName);
            return true;
        }
        catch (Exception ex)
        {
            return  false;
        }
    }

    public static boolean doesFieldExist(String tableName, String fieldName)
    {
        try {
            String query = "SELECT " + fieldName + " FROM " + tableName;
            Cursor cursor = getCursorData(query);
            return  true;
        }
        catch (Exception ex)
        {
            return  false;
        }
    }


}
