package com.omnicron.avax.helpers;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.auth0.jwt.JWT;

import java.io.Serializable;

public class UserCredential implements Serializable {
    private String uid;
    private String email;
    private String institutionID;
    private String password;
    private String accessKey;
    private String refreshKey;
    private String givenName;
    private String middleName;
    private String familyName;
    private String suffix;
    private String gender;
    private String birthdate;
    private String phoneNumber;
    private String address1;
    private String address2;
    private String city;
    private String vaccintionStatus;
    private String healthStatus;

    public String getVaccintionStatus() {
        return vaccintionStatus;
    }

    public void setVaccintionStatus(String vaccintionStatus) {
        this.vaccintionStatus = vaccintionStatus;
    }

    public String getHealthStatus() {
        return healthStatus;
    }

    public void setHealthStatus(String healthStatus) {
        this.healthStatus = healthStatus;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getRefreshKey() {
        return refreshKey;
    }

    public void setRefreshKey(String refreshKey) {
        this.refreshKey = refreshKey;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getInstitutionID() {
        return institutionID;
    }

    public void setInstitutionID(String institutionID) {
        this.institutionID = institutionID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void reset(){
        this.accessKey = null;
        this.refreshKey = null;
        this.email = null;
        this.institutionID = null;
        this.uid = null;
        this.password = null;
        this.givenName = null;
        this.middleName = null;
        this.familyName = null;
        this.suffix = null;
        this.gender = null;
        this.phoneNumber = null;
        this.address1 = null;
        this.address2 = null;
        this.city = null;
        this.vaccintionStatus = null;
        this.healthStatus = null;
    }
    public static UserCredential initExtra(Activity activity){
        Intent intent = activity.getIntent();
        UserCredential user = (UserCredential) intent.getSerializableExtra("User");
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        String refreshKey = pref.getString("refresh_key", "");
        String accessKey = pref.getString("access_key", user.accessKey);
        user.setAccessKey(accessKey);
        user.setRefreshKey(refreshKey);
        user.setUid(SessionLibrary.parseJWT(refreshKey, "uid"));
        return user;
    }
}
