package com.neo.ftp;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * @Auther: 13965
 * @Date: 2018/11/1 16:13
 * @Description:
 * @Version: 1.0
 */
public class FTPPoolConfig extends GenericObjectPoolConfig{
    private String host;
    private int port=21;
    private String username;
    private String password;
    private int  connectTimeOut=5000;//ftp 连接超时时间 毫秒
    private String  controlEncoding="utf-8";
    private int  bufferSize  =1024;//缓冲区大小
    private  int  fileType =2  ;//  传输数据格式   2表binary二进制数据
    private  int  dataTimeout=  120000;
    private   boolean  useEPSVwithIPv4 =false;
    private  boolean  passiveMode =true;//是否启用被动模式

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getConnectTimeOut() {
        return connectTimeOut;
    }

    public void setConnectTimeOut(int connectTimeOut) {
        this.connectTimeOut = connectTimeOut;
    }

    public String getControlEncoding() {
        return controlEncoding;
    }

    public void setControlEncoding(String controlEncoding) {
        this.controlEncoding = controlEncoding;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public int getDataTimeout() {
        return dataTimeout;
    }

    public void setDataTimeout(int dataTimeout) {
        this.dataTimeout = dataTimeout;
    }

    public boolean isUseEPSVwithIPv4() {
        return useEPSVwithIPv4;
    }

    public void setUseEPSVwithIPv4(boolean useEPSVwithIPv4) {
        this.useEPSVwithIPv4 = useEPSVwithIPv4;
    }

    public boolean isPassiveMode() {
        return passiveMode;
    }

    public void setPassiveMode(boolean passiveMode) {
        this.passiveMode = passiveMode;
    }
}
