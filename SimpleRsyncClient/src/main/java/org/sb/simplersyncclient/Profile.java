package org.sb.simplersyncclient;

import java.io.Serializable;

/**
 * Created by sam on 14/12/15.
 */
public class Profile implements Serializable{

    private String name;
    private String hostName;
    private int port;
    private String userName;
    private String idPath;
    private String rsyncOptions;
    private String localPath;
    private String remotePath;

    public Profile(String name, String hostName, int port, String userName,
                   String idPath, String localPath, String remotePath, String rsyncOptions)
    {
        this.name = name;
        this.hostName = hostName;
        this.port = port;
        this.userName = userName;
        this.idPath = idPath;
        this.localPath = localPath;
        this.remotePath = remotePath;
        this.rsyncOptions = rsyncOptions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIdPath() {
        return idPath;
    }

    public void setIdPath(String idPath) {
        this.idPath = idPath;
    }

    public String getRsyncOptions() {
        return rsyncOptions;
    }

    public void setRsyncOptions(String rsyncOptions) {
        this.rsyncOptions = rsyncOptions;
    }

    public String getLocalPath() {
        return localPath;
    }

    public String getRemotePath() {
        return remotePath;
    }

    public boolean promptPassword()
    {
        return false;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "name='" + name + '\'' +
                ", hostName='" + hostName + '\'' +
                ", port=" + port +
                ", userName='" + userName + '\'' +
                ", idPath='" + idPath + '\'' +
                ", rsyncOptions='" + rsyncOptions + '\'' +
                ", localPath='" + localPath + '\'' +
                ", remotePath='" + remotePath + '\'' +
                '}';
    }
}
