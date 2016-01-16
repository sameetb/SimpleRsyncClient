package org.sb.simplersyncclient;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

public class GenerateKeyActivity extends Activity {

    private static final int SSH_KEY_FILE_SELECT_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_key);

        File sdDir = Environment.getExternalStorageDirectory();
        ((EditText) findViewById(R.id.genPrivateKeyFileText)).setText(new File(sdDir, "dss_key").getAbsolutePath());
        ((EditText) findViewById(R.id.genPublicKeyFileText)).setText(new File(sdDir, "id_dsa.pub").getAbsolutePath());

        ((Button)findViewById(R.id.chooseSSHPrivateKeyButton)).setOnClickListener(new FileChooserListener(this, SSH_KEY_FILE_SELECT_CODE));

        ((Button)findViewById(R.id.generatePrivateKeyButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String privKeyFile = ((EditText) findViewById(R.id.genPrivateKeyFileText)).getText().toString();
                String pubKeyFile = ((EditText) findViewById(R.id.genPublicKeyFileText)).getText().toString();
                if(privKeyFile.length() == 0 || pubKeyFile.length() == 0) {
                    Toast.makeText(GenerateKeyActivity.this, "Key file path cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(GenerateKeyActivity.this, GenerateKeyOutputActivity.class);
                intent.putExtra(GenerateKeyOutputActivity.PRIV_KEY_FILE, privKeyFile);
                intent.putExtra(GenerateKeyOutputActivity.PUB_KEY_FILE, pubKeyFile);
                startActivity(intent);
            }
        });

        ((Button)findViewById(R.id.importPrivateKeyButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String openSshPrivKeyFile = ((EditText) findViewById(R.id.sshPrivateKeyFileText)).getText().toString();
                String dssPrivKeyFile = ((EditText) findViewById(R.id.importedPrivateKeyFileText)).getText().toString();
                if(openSshPrivKeyFile.length() == 0 || dssPrivKeyFile.length() == 0) {
                    Toast.makeText(GenerateKeyActivity.this, "Key file path cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(GenerateKeyActivity.this, ImportKeyOutputActivity.class);
                intent.putExtra(GenerateKeyOutputActivity.PRIV_KEY_FILE, openSshPrivKeyFile);
                intent.putExtra(GenerateKeyOutputActivity.PUB_KEY_FILE, dssPrivKeyFile);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SSH_KEY_FILE_SELECT_CODE)
            {
                Uri uri = data.getData();
                Log.d(getPackageName(), "selected uri: " + uri);
                if("file".equals(uri.getScheme()))
                {
                    EditText viewById = (EditText) findViewById(R.id.sshPrivateKeyFileText);
                    viewById.setText(uri.getPath());
                }
                else
                    Toast.makeText(this, "Unsupported content selected, must be a file.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
