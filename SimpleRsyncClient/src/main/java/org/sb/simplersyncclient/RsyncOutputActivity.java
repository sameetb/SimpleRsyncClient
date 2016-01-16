package org.sb.simplersyncclient;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class RsyncOutputActivity extends BaseOutputActivity<RsyncTask> implements BaseExecTask.Progress{
    private Profile entry = null;

    @Override
    RsyncTask makeTask() {
        return new RsyncTask(getApplicationContext().getFilesDir(), this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            ProfileRepo repo = new ProfileRepo(getApplicationContext().getFilesDir());
            String profileName = getIntent().getStringExtra(EditProfileActivity.PROFILE_NAME_MSG);
            entry = repo.getProfile(profileName);
            task.execute(entry);
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void update(String progress) {
        super.update(progress);
        if(progress.contains(entry.getUserName() + "@" + entry.getHostName() + "'s password:"))
        {
            Log.d(getPackageName(), "Detected password prompt in rsync output.");
            capturePassword();
        }
    }

    private void capturePassword()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Password for '" + entry.getUserName() + "@" + entry.getHostName() + "'");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(getPackageName(), "Writing password to rsync input.");
                task.writeToTaskInputStream(input.getText().toString() + "\n");
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(RsyncOutputActivity.this, "Cannot continue without password.", Toast.LENGTH_SHORT).show();
                task.cancel(true);
                dialog.cancel();
            }
        });

        builder.show();
        return ;
    }
}
