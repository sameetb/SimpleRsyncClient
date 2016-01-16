package org.sb.simplersyncclient;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.Toast;

/**
 * Created by sam on 09-01-2016.
 */
public class FileChooserListener implements View.OnClickListener, View.OnLongClickListener {

    public enum TYPE{
        FILE("file/*"),
        FOLDER("resource/folder"),
        ALL("*/*");

        private final String name;

        private TYPE(String s) {
            name = s;
        }

        public boolean equalsName(String otherName) {
            return (otherName == null) ? false : name.equals(otherName);
        }

        public String toString() {
            return this.name;
        }
    };

    private final TYPE type;
    private final int requestCode;
    private final Activity callerActivity;

    public FileChooserListener(TYPE type, Activity callerActivity, int requestCode) {
        this.type = type;
        this.requestCode = requestCode;
        this.callerActivity = callerActivity;
    }
    public FileChooserListener(Activity callerActivity, int requestCode) {
        this(TYPE.FILE, callerActivity, requestCode);
    }

    @Override
    public void onClick(View v) {
        onLongClick(v);
    }

    @Override
    public boolean onLongClick(View v) {
        try {
            Intent intent = makeIntent();
            callerActivity.startActivityForResult(intent, requestCode);
            return true;
        } catch (ActivityNotFoundException ae) {
            Toast.makeText(callerActivity, "Please install a File Manager or type the path.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private Intent makeIntent()
    {
        if(type.equals(TYPE.FOLDER)) {
            //try es explorer
            Intent intent = new Intent("com.estrongs.action.PICK_DIRECTORY");
            intent.putExtra("com.estrongs.intent.extra.TITLE", "Select Folder");
            if (intentAvailable(intent)) return intent;
        }
        else if(type.equals(TYPE.FILE)) {
            //try es explorer
            Intent intent = new Intent("com.estrongs.action.PICK_FILE");
            intent.putExtra("com.estrongs.intent.extra.TITLE", "Select File");
            if (intentAvailable(intent)) return intent;
        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(type.name);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        return intent;
    }

    private boolean intentAvailable(Intent intent)
    {
        PackageManager packageManager = callerActivity.getPackageManager();
        return packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
    }
}
