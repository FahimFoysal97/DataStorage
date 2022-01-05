package com.example.datastorage;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.datastorage.databinding.ActivityMainBinding;
import com.example.datastorage.utils.Constants;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding activityMainBinding;
    private final String filename = Constants.FILE_NAME;
    private final String filepath = Constants.FILE_PATH;
    private File myExternalFile;
    private String myData = "";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        initUI();
    }

    private void initUI() {
        //External storage
        if (!checkReadPermission()) takeExternalReadPermission();
        else myExternalFile = new File(getExternalFilesDir(filepath), filename);
        activityMainBinding.materialButtonSave.setOnClickListener(v -> saveInExternalStorage());
        activityMainBinding.materialButtonRead.setOnClickListener(v -> loadFromExternal());

        //SharedPreference
        sharedPreferences = getSharedPreferences(Constants.SharedPreference.SHAREDPREFERENCE_NAME, Context.MODE_PRIVATE);
        activityMainBinding.materialButtonSaveUsingSharedpreference.setOnClickListener(v -> saveUsingSharedPreference());
        activityMainBinding.materialButtonLoadFromSharedpreference.setOnClickListener(v -> loadFromSharedPreference());
        activityMainBinding.materialButtonDeleteFromSharedpreference.setOnClickListener(v -> deleteFromSharedPreference());
    }

    //SharedPreference delete data
    private void deleteFromSharedPreference() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    //SharedPreference save data
    private void saveUsingSharedPreference() {
        if (checkIfAllFieldsHaveInputs()) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Constants.SharedPreference.NAME, activityMainBinding.edittextInputName.getText().toString());
            editor.putString(Constants.SharedPreference.EMAIL, activityMainBinding.edittextInputEmail.getText().toString());
            editor.putInt(Constants.SharedPreference.ID, Integer.parseInt(activityMainBinding.edittextInputId.getText().toString()));
            editor.apply();
            activityMainBinding.edittextInputName.setText("");
            activityMainBinding.edittextInputEmail.setText("");
            activityMainBinding.edittextInputId.setText("");
        }
    }

    //SharedPreference input fields check
    private boolean checkIfAllFieldsHaveInputs() {
        if (activityMainBinding.edittextInputName.getText().toString().equals("")) {
            Toast.makeText(this, getResources().getString(R.string.enter_your_name), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (activityMainBinding.edittextInputEmail.getText().toString().equals("")) {
            Toast.makeText(this, getResources().getString(R.string.enter_your_email), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (activityMainBinding.edittextInputId.getText().toString().equals("")) {
            Toast.makeText(this, getResources().getString(R.string.enter_your_id), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    //SharedPreference load data
    private void loadFromSharedPreference() {
        String loadedNameFromSharedPreference = sharedPreferences.getString(Constants.SharedPreference.NAME, getString(R.string.na));
        String loadedEmailFromSharedPreference = sharedPreferences.getString(Constants.SharedPreference.EMAIL, getString(R.string.na));
        int loadedIDFromSharedPreference = sharedPreferences.getInt(Constants.SharedPreference.ID, 0);
        String loadedDataAfterFormation = loadedNameFromSharedPreference
                + "\n" + loadedEmailFromSharedPreference
                + "\n" + loadedIDFromSharedPreference;
        activityMainBinding.textviewLoadedDataFromSharedpreference.setText(loadedDataAfterFormation);
    }

    //External storage save data
    private void saveInExternalStorage() {
        if(activityMainBinding.edittextInput.getText().toString().equals("")){
            Toast.makeText(this, getResources().getString(R.string.enter_any_text), Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(myExternalFile);
            fileOutputStream.write(activityMainBinding.edittextInput.getText().toString().getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        activityMainBinding.edittextInput.setText("");
        Toast.makeText(this, getResources().getString(R.string.saved), Toast.LENGTH_SHORT).show();
    }

    //External storage load data
    private void loadFromExternal() {
        myData = "";
        activityMainBinding.textviewLoadedText.setText("");
        try {
            FileInputStream fileInputStream = new FileInputStream(myExternalFile);
            DataInputStream dataInputStream = new DataInputStream(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(dataInputStream));
            String stringLine;
            while ((stringLine = bufferedReader.readLine()) != null) {
                myData = myData.concat(stringLine);
            }
            dataInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        activityMainBinding.textviewLoadedText.setText(myData);
    }

    //External storage take read permission
    private void takeExternalReadPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        if (checkReadPermission())
            Toast.makeText(this, getResources().getString(R.string.permission_granted), Toast.LENGTH_SHORT).show();
    }

    //External storage check read permission
    private boolean checkReadPermission() {
        int permissionExternalRead = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return permissionExternalRead == PackageManager.PERMISSION_GRANTED;
    }
}