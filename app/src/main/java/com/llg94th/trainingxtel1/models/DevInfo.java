package com.llg94th.trainingxtel1.models;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

public class DevInfo {
    private String deviceid;
    private String os_name;
    private String os_version;
    private int type;
    private String vendor;
    private String other;

    public DevInfo() {
    }

    public DevInfo(String deviceid, String os_name, String os_version, int type, String vendor, String other) {
        this.deviceid = deviceid;
        this.os_name = os_name;
        this.os_version = os_version;
        this.type = type;
        this.vendor = vendor;
        this.other = other;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public String getOs_name() {
        return os_name;
    }

    public void setOs_name(String os_name) {
        this.os_name = os_name;
    }

    public String getOs_version() {
        return os_version;
    }

    public void setOs_version(String os_version) {
        this.os_version = os_version;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public static DevInfo getInstant(Context context){
        DevInfo devInfo = new DevInfo();
        devInfo.type = 1;
        devInfo.os_name = "android";
        devInfo.os_version = String.valueOf(Build.VERSION.SDK_INT);
        devInfo.vendor = Build.MANUFACTURER;
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        devInfo.deviceid = telephonyManager.getDeviceId();
        return devInfo;
    }
}
