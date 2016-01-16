package org.sb.simplersyncclient;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by sam on 09-01-2016.
 */
public abstract class BaseExecTask<Params> extends AsyncTask<Params, String, Integer> {

    protected final Progress rsyncOutput;
    private Process proc = null;

    interface Progress
    {
        void update(String progress);
        void finished(String progress);
    }

    protected final File binDir;

    protected BaseExecTask(File baseDir, Progress rsyncOutput) {
        this.binDir = new File(baseDir, "bin");
        this.rsyncOutput = rsyncOutput;
    }

    @Override
    protected Integer doInBackground(Params... params) {
        for(Params param : params)
        {
            List<String> command = makeCommand(param);
            Log.d(BuildConfig.APPLICATION_ID, "Executing command: " + command);
            publishProgress("Executing command: " + command.toString().replace(",", " "));
            ProcessBuilder pb = new ProcessBuilder(command).directory(binDir);
            pb.environment().put("HOME", binDir.getParent());
            Map<String, String> env = makeEnv(param);
            if(!env.isEmpty())
                pb.environment().putAll(env);

            pb.redirectErrorStream(true);
            InputStreamReader br = null;
            try
            {
                proc = pb.start();
                br = new InputStreamReader(proc.getInputStream());
                int read;
                char[] buffer = new char[1024];
                while ((read = br.read(buffer)) > 0) publishProgress(new String(buffer, 0 , read));
                return proc.waitFor();
            }
            catch(IOException ex)
            {
                publishProgress(ex.getMessage());
                ex.printStackTrace();
                return -1;
            }
            catch(InterruptedException ex)
            {
                publishProgress(ex.getMessage());
                ex.printStackTrace();
                return -1;
            }
            finally {
                if(br != null) try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                br = null;
            }
        }
        return 0;

    }

    @Override
    protected void onProgressUpdate(String... values) {
        for(String val : values) {
            rsyncOutput.update(val);
            Log.d(BuildConfig.APPLICATION_ID, "Task log: " + val);
        }
    }

    @Override
    protected void onPostExecute(Integer res) {
        rsyncOutput.finished(res == 0 ? "Task completed " : " Task failed with code " + res);
    }

    protected abstract List<String> makeCommand(Params keyFile);

    protected Map<String, String> makeEnv(Params keyFile)
    {
        return Collections.emptyMap();
    }

    protected void writeToTaskInputStream(String data)
    {
        if(proc != null)
        {
            try {
                byte[] bytes = data.getBytes("US-ASCII");
                StringBuffer sb = new StringBuffer();
                for(byte b : bytes)
                    sb.append(Byte.toString(b)).append('-');

                Log.d(BuildConfig.APPLICATION_ID, "Task log: " + sb);
                proc.getOutputStream().write(bytes);
                proc.getOutputStream().flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

