package org.sb.simplersyncclient;

import android.os.Bundle;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;

public class GenerateKeyOutputActivity extends BaseOutputActivity<GenerateKeyTask> implements BaseExecTask.Progress{

    public static final String PRIV_KEY_FILE = "priv_key_file";
    public static final String PUB_KEY_FILE = "pub_key_file";

    @Override
    GenerateKeyTask makeTask() {
        return new GenerateKeyTask(getApplicationContext().getFilesDir(), this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String privKeyFile = getIntent().getStringExtra(PRIV_KEY_FILE);
        String pubKeyFile = getIntent().getStringExtra(PUB_KEY_FILE);
        task.execute(new AbstractMap.SimpleImmutableEntry<File, File>(new File(privKeyFile), new File(pubKeyFile)));
    }
}
