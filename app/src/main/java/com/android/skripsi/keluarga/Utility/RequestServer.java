package com.android.skripsi.keluarga.Utility;


public class RequestServer {
    // ip localhost untuk avd emulator 10.0.2.2
    //private String server_ip = "192.168.1.101";
    private String server_ip = "10.0.2.2";
    private String server_url = "/keluarga/api/";
    private String img_url = "/keluarga/assets/";

    public String getServer_url(){
        return "http://"+this.server_ip+this.server_url;
    }
    public String getImg_url(){
        return "http://"+this.server_ip+this.img_url;
    }

}
