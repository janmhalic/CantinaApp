package com.android.schoolstore;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    //Initialize variable
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    //Create constructor
    public SessionManager(Context context){
        sharedPreferences = context.getSharedPreferences("Appkey",0);
        editor = sharedPreferences.edit();
        editor.apply();
    }
    //create set login method
    public void setLogin(boolean login){
        editor.putBoolean("KEY_LOGIN",login);
        editor.commit();
    }
    //create get login method
    public boolean getLogin(){
        return sharedPreferences.getBoolean("KEY_LOGIN", false);
    }

    //Create set username method
    public void setUsername(String username){
        editor.putString("KEY_USERNAME",username);
        editor.commit();
    }
    //create get username method
    public String getUsername(){
        return sharedPreferences.getString("KEY_USERNAME", "");
    }

    //Create set Fullname method
    public void setFullname(String Fullname){
        editor.putString("KEY_FULLNAME",Fullname);
        editor.commit();
    }

    //create get Fullname method
    public String getFullname(){
        return sharedPreferences.getString("KEY_FULLNAME", "");
    }

    //Create set access method
    public void setAccess(String access1){
        editor.putString("KEY_ACCESS",access1);
        editor.commit();
    }

    //create get access method
    public String getAccess(){
        return sharedPreferences.getString("KEY_ACCESS", "");
    }

    //Create set contact method
    public void setContact(String contact1){
        editor.putString("KEY_CONTACT",contact1);
        editor.commit();
    }

    //create get contact method
    public String getContact(){
        return sharedPreferences.getString("KEY_CONTACT", "");
    }


    //Create set id method
    public void setId(String user_id){
        editor.putString("KEY_ID",user_id);
        editor.commit();
    }

    //create get contact method
    public String getId(){
        return sharedPreferences.getString("KEY_ID", "");
    }





}
