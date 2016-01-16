package org.sb.simplersyncclient;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by sam on 14/12/15.
 */
class RsyncTask extends BaseExecTask<Profile> {

    public RsyncTask(File baseDir, Progress rsyncOutput)
    {
        super(baseDir, rsyncOutput);
    }


    protected List<String> makeCommand(Profile prof)
    {
        ArrayList<String> words = new ArrayList<String>();
        words.add(new File(binDir, "rsync").getAbsolutePath());
        words.addAll(Arrays.asList(prof.getRsyncOptions().split(" ")));
        /*words.add("-e");
        words.add(new File(binDir, "ssh").getAbsolutePath());
        words.add("-o");
        words.add("StrictHostKeyChecking=no");
        words.add("-y");
        words.add("-p");
        words.add(String.valueOf(prof.getPort()));
        words.add("-i");
        words.add("'" + prof.getIdPath() + "'");*/
        //words.add("--rsh=" + makeSsh(prof));
        words.add(prof.getLocalPath());
        words.add(prof.getUserName() + "@" + prof.getHostName() + ":" + prof.getRemotePath());
        //return makeSSHCommand(prof);
        return words;
    }

    @Override
    protected Map<String, String> makeEnv(Profile prof) {
        return Collections.singletonMap("RSYNC_RSH", makeSsh(prof));
    }

    private String makeSsh(Profile prof)
    {
        StringBuffer ssh = new StringBuffer()
                .append(new File(binDir, "ssh").getAbsolutePath())
                .append(" -y")
                .append(" -p ")
                .append(String.valueOf(prof.getPort()));
        if(prof.getIdPath() != null
                && prof.getIdPath().length() > 0
                && new File(prof.getIdPath()).exists())
            ssh.append(" -i")
                .append(" '")
                .append(prof.getIdPath())
                .append("'");
        else
            publishProgress("No valid identity file '" + prof.getIdPath() + "' found, falling back to password authentication");
        return ssh.toString();
    }

    protected List<String> makeSSHCommand(Profile prof)
    {
        ArrayList<String> words = new ArrayList<String>();
        words.add(new File(binDir, "ssh").getAbsolutePath());
        words.add("-y");
        words.add("-p");
        words.add(String.valueOf(prof.getPort()));
        words.add("-i");
        words.add(prof.getIdPath());
        words.add(prof.getUserName() + "@" + prof.getHostName());
        words.add("/ffp/bin/date");

        Log.d(BuildConfig.APPLICATION_ID, "Executing command: " + words);
        return words;
    }
}
