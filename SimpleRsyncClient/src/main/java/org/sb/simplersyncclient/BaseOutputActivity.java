package org.sb.simplersyncclient;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public abstract class BaseOutputActivity<T extends BaseExecTask> extends Activity implements RsyncTask.Progress{

    private TextView rsyncOutput = null;
    private ScrollView rsyncScroll = null;
    protected T task;

    abstract T makeTask();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rsync_output);

        task = makeTask();
        Button button = (Button) findViewById(R.id.cancelRsyncButton);
        button.setText("Cancel");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (task.getStatus() != RsyncTask.Status.FINISHED)
                    task.cancel(true);
                finish();
            }
        });
        rsyncOutput = (TextView)findViewById(R.id.rsyncOutputText);
        rsyncOutput.setText("");
        rsyncScroll = (ScrollView)findViewById(R.id.rsyncOutputScrollView);

    }

    @Override
    public void update(String progress) {
        if(rsyncOutput != null)
        {
            rsyncOutput.append(progress);
            if(rsyncScroll != null) rsyncScroll.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }

    @Override
    public void finished(String progress) {
        update(progress);
        ((Button)findViewById(R.id.cancelRsyncButton)).setText("Close");
    }
}
