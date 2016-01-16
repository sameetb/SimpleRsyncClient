package org.sb.simplersyncclient;

import android.os.Bundle;
import android.widget.Toast;

import java.io.File;
import java.util.AbstractMap;

public class ImportKeyOutputActivity extends BaseOutputActivity<ImportOpenSSHKeyTask> implements BaseExecTask.Progress{

    public static final String OPEN_SSH_PRIV_KEY_FILE = "openssh_priv_key_file";
    public static final String DSS_KEY_FILE = "dss_key_file";

    @Override
    ImportOpenSSHKeyTask makeTask() {
        return new ImportOpenSSHKeyTask(getApplicationContext().getFilesDir(), this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String openSshPrivKeyFile = getIntent().getStringExtra(OPEN_SSH_PRIV_KEY_FILE);
        String dssPrivKeyFile = getIntent().getStringExtra(DSS_KEY_FILE);
        task.execute(new AbstractMap.SimpleImmutableEntry<File, File>(new File(openSshPrivKeyFile), new File(dssPrivKeyFile)));
    }
}
