package org.sb.simplersyncclient;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by sam on 09-01-2016.
 */
public class ImportOpenSSHKeyTask extends BaseExecTask<Map.Entry<File, File>> {

    public ImportOpenSSHKeyTask(File baseDir, Progress rsyncOutput) {
        super(baseDir, rsyncOutput);
    }

    @Override
    protected List<String> makeCommand(Map.Entry<File, File> keyFiles) {
        ArrayList<String> words = new ArrayList<String>();
        words.add(new File(binDir, "dropbearconvert").getAbsolutePath());
        words.add("openssh");
        words.add("dropbear");
        words.add(keyFiles.getKey().getAbsolutePath());
        words.add(keyFiles.getValue().getAbsolutePath());
        return words;
    }
}
