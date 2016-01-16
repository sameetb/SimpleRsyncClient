package org.sb.simplersyncclient;

import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by sam on 09-01-2016.
 */
public class GenerateKeyTask extends BaseExecTask<Map.Entry<File, File>> {
    private final StringBuffer osb;
    //private File dssKeyTmpFile;

    public GenerateKeyTask(File baseDir, Progress rsyncOutput) {
        super(baseDir, rsyncOutput);
        osb = new StringBuffer();
    }

    @Override
    protected Integer doInBackground(Map.Entry<File, File>... params) {
        Integer res = super.doInBackground(params);
        if(res == 0)
        {
            for(Map.Entry<File, File> param : params) {
                //dssKeyTmpFile.renameTo(param.getKey());
                for (String str : Pattern.compile("\n").split(osb)) {
                    if (str.startsWith("ssh-dss")) {
                        final File pubKeyFile = param.getValue();
                        Log.d(BuildConfig.APPLICATION_ID, "Found public key line, writing to " + pubKeyFile);
                        try {
                            FileWriter fw = new FileWriter(pubKeyFile);
                            fw.write(str);
                            fw.close();
                            return res;
                        } catch (IOException e) {
                            publishProgress(e.getMessage());
                            e.printStackTrace();
                            return -1;
                        }
                    }
                }
                publishProgress("Did not find public key line in output.");
                return -1;
            }
        }
        return res;
    }

    @Override
    protected List<String> makeCommand(Map.Entry<File, File> keyFiles) {
        ArrayList<String> words = new ArrayList<String>();
        words.add(new File(binDir, "dropbearkey").getAbsolutePath());
        words.add("-t");
        words.add("dss");
        words.add("-f");
/*        try {
            dssKeyTmpFile = File.createTempFile("dssKeyTmpFile", "", binDir.getParentFile());
            dssKeyTmpFile.delete();
            words.add(dssKeyTmpFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();*/
        if(keyFiles.getKey().exists()) keyFiles.getKey().delete();
            words.add(keyFiles.getKey().getAbsolutePath());
        /*}*/
        return words;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        for(String val : values) osb.append(val);
        super.onProgressUpdate(values);
    }

}
