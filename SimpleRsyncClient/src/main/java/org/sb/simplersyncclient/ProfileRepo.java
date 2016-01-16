package org.sb.simplersyncclient;

import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by sam on 21/12/15.
 */
class ProfileRepo {

    private final File profsDir;

    public ProfileRepo(File baseDir) throws IOException {
        profsDir = new File(baseDir, "profiles");
        if(!profsDir.exists()) profsDir.mkdir();
    }

    public Profile getProfile(String profileName) throws IOException
    {
        File prof = new File(profsDir, profileName);
        FileReader in = new FileReader(prof);
        JsonReader reader = new JsonReader(in);
        try
        {
            reader.beginObject();
            String hostName = null, userName = null, idPath = null, options = null, locPath = null, remPath = null;
            int port = 22;
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("port")) {
                    port = reader.nextInt();
                } else if (name.equals("name")) {
                    profileName = reader.nextString();
                } else if (name.equals("hostName")) {
                    hostName = reader.nextString();
                } else if (name.equals("userName")) {
                    userName = reader.nextString();
                } else if (name.equals("idPath")) {
                    idPath = reader.nextString();
                } else if (name.equals("locPath")) {
                    locPath = reader.nextString();
                } else if (name.equals("remPath")) {
                    remPath = reader.nextString();
                } else if (name.equals("options")) {
                    options = reader.nextString();
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
            return new Profile(profileName, hostName, port, userName, idPath, locPath, remPath, options);
        }
        finally
        {
            reader.close();
            in.close();
        }
    }

    public String[] getProfiles()
    {
        return profsDir.list();
    }

    public void saveProfile(Profile prof, boolean isNew)  throws IOException
    {
        File file = new File(profsDir, prof.getName());
        if(isNew && file.exists()) throw new IOException("A profile with name " + prof.getName() + " already exists");
        FileWriter fw = new FileWriter(file);
        JsonWriter wr = new JsonWriter(fw);
        try
        {
            wr.beginObject();
            wr.name("name");
            wr.value(prof.getName());
            wr.name("hostName");
            wr.value(prof.getHostName());
            wr.name("port");
            wr.value(prof.getPort());
            wr.name("userName");
            wr.value(prof.getUserName());
            if(prof.getIdPath() != null && prof.getIdPath().length() > 0) {
                wr.name("idPath");
                wr.value(prof.getIdPath());
            }
            wr.name("locPath");
            wr.value(prof.getLocalPath());
            wr.name("remPath");
            wr.value(prof.getRemotePath());
            wr.name("options");
            wr.value(prof.getRsyncOptions());
            wr.endObject();
        }
        finally
        {
            wr.close();
            fw.close();
        }
    }

    public void removeProfile(String profName)
    {
        File file = new File(profsDir, profName);
        file.delete();
    }
}
