package org.sb.simplersyncclient;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class EditProfileActivity extends Activity {
    public static final String PROFILE_NAME_MSG = "profile_name_message";
    public static final int ADD_PROFILE_REQUEST = 1;
    private static final int PRIVATE_KEY_FILE_SELECT_CODE = 0;
    private static final int LOCAL_PATH_SELECT_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ((Button)findViewById(R.id.okButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    saveProfile(getIntent().getStringExtra(PROFILE_NAME_MSG) == null);
                    finish();
                } catch (IllegalArgumentException e) {
                    Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } catch (IOException e) {
                    Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
        ((Button)findViewById(R.id.cancelButton)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        ((Button)findViewById(R.id.chooseFileButton)).setOnClickListener(new FileChooserListener(this, PRIVATE_KEY_FILE_SELECT_CODE));
        ((Button)findViewById(R.id.chooseLocalPathButton)).setOnClickListener(
                    new FileChooserListener(FileChooserListener.TYPE.FOLDER, this, LOCAL_PATH_SELECT_CODE));
        ((Button)findViewById(R.id.chooseLocalPathButton)).setOnLongClickListener(new FileChooserListener(FileChooserListener.TYPE.FILE, this, LOCAL_PATH_SELECT_CODE));

        try {
            final ProfileRepo repo = new ProfileRepo(getApplicationContext().getFilesDir());

            String profileName = getIntent().getStringExtra(PROFILE_NAME_MSG);
            if (profileName != null)
            {
                Profile prof = repo.getProfile(profileName);
                ((EditText)findViewById(R.id.profileNameText)).setText(prof.getName());
                ((EditText)findViewById(R.id.profileNameText)).setEnabled(false);
                ((EditText)findViewById(R.id.ipAddressText)).setText(prof.getHostName());
                ((EditText)findViewById(R.id.portText)).setText(String.valueOf(prof.getPort()));
                ((EditText)findViewById(R.id.localPathText)).setText(prof.getLocalPath());
                ((EditText)findViewById(R.id.remotePathText)).setText(prof.getRemotePath());
                ((EditText)findViewById(R.id.userNameText)).setText(prof.getUserName());
                ((EditText)findViewById(R.id.privateKeyFileText)).setText(prof.getIdPath());
                ((EditText)findViewById(R.id.optionsText)).setText(prof.getRsyncOptions());
            }
            else
            {
                File sdDir = Environment.getExternalStorageDirectory();
                ((EditText) findViewById(R.id.profileNameText)).setEnabled(true);
                ((EditText)findViewById(R.id.portText)).setText("22");
                ((EditText)findViewById(R.id.localPathText)).setText(new File(sdDir, "DCIM").getAbsolutePath());
                ((EditText)findViewById(R.id.remotePathText)).setText("~/pics");
                ((EditText)findViewById(R.id.privateKeyFileText)).setText(new File(sdDir, "dss_key").getAbsolutePath());
                ((EditText)findViewById(R.id.optionsText)).setText("-vrltDzh --no-perms --chmod=Du=rwx,Dgo=rx,Fu=rw,Fgo=r");
            }
        }
        catch (IOException e)
        {
            Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode == RESULT_OK)
        {
            if (requestCode == PRIVATE_KEY_FILE_SELECT_CODE || requestCode == LOCAL_PATH_SELECT_CODE)
            {
                Uri uri = data.getData();
                Log.d(getPackageName(), "selected uri: " + uri);
                if("file".equals(uri.getScheme()))
                {
                    EditText viewById = (EditText) findViewById(requestCode == PRIVATE_KEY_FILE_SELECT_CODE
                            ? R.id.privateKeyFileText : R.id.localPathText);
                    viewById.setText(uri.getPath());
                }
                else
                    Toast.makeText(EditProfileActivity.this, "Unsupported content selected, must be a file.", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void saveProfile(boolean isNew) throws IllegalArgumentException, IOException
    {
        final String name = ((EditText) findViewById(R.id.profileNameText)).getText().toString();
        if(name.length() == 0) throw new IllegalArgumentException("Profile Name cannot be empty");
        final String hostName = ((EditText) findViewById(R.id.ipAddressText)).getText().toString();
        if(hostName.length() == 0) throw new IllegalArgumentException("Host IP Address/Name cannot be empty");
        final int port = Integer.parseInt(((EditText) findViewById(R.id.portText)).getText().toString());
        final String userName = ((EditText) findViewById(R.id.userNameText)).getText().toString();
        if(userName.length() == 0) throw new IllegalArgumentException("Username cannot be empty");
        final String locPath = ((EditText) findViewById(R.id.localPathText)).getText().toString();
        if(locPath.length() == 0 || !new File(locPath).exists()) throw new IllegalArgumentException("Local path cannot be empty or non-existent");
        final String remPath = ((EditText) findViewById(R.id.remotePathText)).getText().toString();
        if(remPath.length() == 0) throw new IllegalArgumentException("Remote path cannot be empty");
        boolean usePasswd = ((CheckBox)findViewById(R.id.usePasswordCheckBox)).isChecked();
        final String idPath = ((EditText) findViewById(R.id.privateKeyFileText)).getText().toString();
        if(!usePasswd && (idPath.length() == 0 || !new File(idPath).exists())) throw new IllegalArgumentException("Key file path cannot be empty or non-existent");
        final String rsyncOptions = ((EditText) findViewById(R.id.optionsText)).getText().toString();
        Profile prof = new Profile(name, hostName, port > 0 ? port : 22, userName, idPath, locPath, remPath,
                                        rsyncOptions.length() != 0 ? rsyncOptions : "-avh");
        final ProfileRepo repo = new ProfileRepo(getApplicationContext().getFilesDir());
        repo.saveProfile(prof, isNew);
    }
}
