package org.sb.simplersyncclient;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends Activity {

    ProfileRepo repo = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            copyExecutables(false);

            ListView lv = (ListView) findViewById(R.id.listView);
            registerForContextMenu(lv);
            repo = new ProfileRepo(getApplicationContext().getFilesDir());
            setProfileList();
            lv.setLongClickable(true);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String entryName = (String) parent.getItemAtPosition(position);
                    Log.d(getPackageName(), "execute profile : " + entryName);
                    Intent intent = new Intent(MainActivity.this, RsyncOutputActivity.class);
                    intent.putExtra(EditProfileActivity.PROFILE_NAME_MSG, entryName);
                    startActivity(intent);
                }
            });


            /*((Button) findViewById(R.id.addProfileButton)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(new Intent(MainActivity.this, EditProfileActivity.class), EditProfileActivity.ADD_PROFILE_REQUEST);
                }
            });*/
        } catch (IOException e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
            setResult(-1);
            finish();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (v.getId()==R.id.listView) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle((String)((ListView) v).getItemAtPosition(info.position));
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.profile_menu, menu);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId())
        {
            case R.id.editProfileMenuItem : {
                ListView lv = (ListView) findViewById(R.id.listView);
                String entryName = (String) lv.getItemAtPosition(info.position);
                Log.d(getPackageName(), "edit profile : " + entryName);
                Intent intent = new Intent(MainActivity.this, EditProfileActivity.class);
                intent.putExtra(EditProfileActivity.PROFILE_NAME_MSG, entryName);
                startActivity(intent);
                return true;
            }
            case R.id.deleteProfileMenuItem : {
                ListView lv = (ListView) findViewById(R.id.listView);
                String entryName = (String) lv.getItemAtPosition(info.position);
                Log.d(getPackageName(), "delete profile : " + entryName);
                Toast.makeText(MainActivity.this, "Deleting profile " + entryName + " !", Toast.LENGTH_SHORT).show();
                repo.removeProfile(entryName);
                setProfileList();
                return true;
            }
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId())
        {
            case R.id.action_generate_keys :
                Log.d(getPackageName(), "generate keys ");
                startActivity(new Intent(MainActivity.this, GenerateKeyActivity.class));
                return true;
            case R.id.action_add:
                Log.d(getPackageName(), "add profile ");
                startActivityForResult(new Intent(MainActivity.this, EditProfileActivity.class), EditProfileActivity.ADD_PROFILE_REQUEST);
                return true;
            case R.id.action_help :
                Log.d(getPackageName(), "help ");
                Toast.makeText(MainActivity.this, BuildConfig.APPLICATION_ID + "-" +  BuildConfig.VERSION_NAME
                        +  " on sdk-level=" + Build.VERSION.SDK_INT + ". Help in the works", Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_copy_bins:
                Log.d(getPackageName(), "copy binaries ");
                try {
                    copyExecutables(true);
                    Toast.makeText(MainActivity.this, "Binaries refreshed.", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void copyExecutables(boolean overwrite) throws IOException {
        AssetManager am = this.getAssets();
        final File binDir = new File(getFilesDir(), "bin");
        if (!binDir.exists()) binDir.mkdir();

        final String EXE_TYPE = (Build.VERSION.SDK_INT >= 21 ? "pie" : "fixed");
        final String assetDir = "bin" + File.separator + EXE_TYPE;
        for (String bin : am.list(assetDir)) {
            File toPath = new File(binDir, bin);
            if (overwrite || !toPath.exists()) {
                copyAsset(am.open(assetDir + File.separator + bin), toPath);
                toPath.setExecutable(true);
            }
        }
    }

    private static void copyAsset(InputStream in, File toPath) throws IOException {
        if(!toPath.exists())
            toPath.createNewFile();
        FileOutputStream out = new FileOutputStream(toPath);
        copyFile(in, out);
        in.close();
        out.flush();
        out.close();
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) out.write(buffer, 0, read);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == EditProfileActivity.ADD_PROFILE_REQUEST)
        {
            setProfileList();
        }
    }

    private void setProfileList() {
        ListView lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, repo.getProfiles()));
    }
}