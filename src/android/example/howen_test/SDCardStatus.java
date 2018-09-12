
package example.howen_test;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.example.yho_test.R;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.widget.EditText;
import android.widget.ProgressBar;

public class SDCardStatus extends Activity {
    private EditText out_sd_EditText, out_sd_all_EditText,
            out_sd_free_EditText, in_sd_EditText, in_sd_all_EditText,
            in_sd_free_EditText, out_tf_EditText, out_tf_all_EditText,
            out_tf_free_EditText;
    private ProgressBar out_sd_progressBar, out_tf_progressBar, in_sd_progressBar;
    private boolean out_existSDCard, out_existTFCard, in_existSDCard;
    private long allSize, freeSize;
    private int jindu;
    private String extsd_path, exttf_path, sdcard_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sd_card);

        Configuration configuration = getResources().getConfiguration();
        if (configuration.orientation == configuration.ORIENTATION_PORTRAIT) {
            SDCardStatus.this
                    .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        view_init();

        in_existSDCard = in_ExistSDCard();
        if (in_existSDCard == true) {
            in_sd_EditText.setText("Built-in SD card is normal");
            allSize = getSDAllSize(sdcard_path);
            in_sd_all_EditText.setText(allSize + "  MB");
            freeSize = getSDFreeSize(sdcard_path);
            in_sd_free_EditText.setText(freeSize + "  MB");
            progress("in");
        } else {
            in_sd_EditText.setText("Built-in SD card error");
            allSize = 0;
            in_sd_all_EditText.setText(allSize + "  MB");
            freeSize = 0;
            in_sd_free_EditText.setText(freeSize + "  MB");
        }

        out_existSDCard = out_ExistSDCard();
        if (out_existSDCard == true) {
            out_sd_EditText.setText("External SD card is inserted");
            allSize = getSDAllSize(extsd_path);
            out_sd_all_EditText.setText(allSize + "  MB");
            freeSize = getSDFreeSize(extsd_path);
            out_sd_free_EditText.setText(freeSize + "  MB");
            progress("outsd");
        } else {
            out_sd_EditText.setText("No external SD card");
            allSize = 0;
            out_sd_all_EditText.setText(allSize + "  MB");
            freeSize = 0;
            out_sd_free_EditText.setText(freeSize + "  MB");
        }

        out_existTFCard = out_ExistTFCard();
        if (out_existTFCard == true) {
            out_tf_EditText.setText("External TF card is inserted");
            allSize = getSDAllSize(exttf_path);
            out_tf_all_EditText.setText(allSize + "  MB");
            freeSize = getSDFreeSize(exttf_path);
            out_tf_free_EditText.setText(freeSize + "  MB");
            progress("outtf");
        } else {
            out_tf_EditText.setText("No external TF card");
            allSize = 0;
            out_tf_all_EditText.setText(allSize + "  MB");
            freeSize = 0;
            out_tf_free_EditText.setText(freeSize + "  MB");
        }

    }

    private boolean out_ExistTFCard() {

        exttf_path = getTFPath();

        if (exttf_path.equals("/mnt/extsd")) {
            return true;
        } else
            return false;
    }

    public String getTFPath() {
        String tfcard_path = "";
        String tffcard_path = "";
        String sd_default = Environment.getExternalStorageDirectory()
                .getAbsolutePath();
        if (sd_default.endsWith("/")) {
            sd_default = sd_default.substring(0, sd_default.length() - 1);
        }
        try {
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            String line;
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                if (line.contains("secure"))
                    continue;
                if (line.contains("asec"))
                    continue;
                if (line.contains("fat") && line.contains("/mnt/")) {
                    String columns[] = line.split(" ");
                    if (columns != null && columns.length > 1) {
                        if (sd_default.trim().equals(columns[1].trim())) {
                            tffcard_path = tfcard_path;
                            continue;
                        }
                        tfcard_path = columns[1];
                    }
                } else if (line.contains("fuse") && line.contains("/mnt/")) {
                    String columns[] = line.split(" ");
                    if (columns != null && columns.length > 1) {
                        if (sd_default.trim().equals(columns[1].trim())) {

                            continue;
                        }
                        tfcard_path = columns[1];
                    }
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return tffcard_path;
    }

    private boolean in_ExistSDCard() {
        // TODO Auto-generated method stub
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File sdCardDir = Environment.getExternalStorageDirectory();
            sdcard_path = sdCardDir.toString();
            return true;
        } else {
            return false;
        }
    }

    private void view_init() {
        // TODO Auto-generated method stub
        out_sd_EditText = (EditText) findViewById(R.id.out_sd_editText);
        out_sd_all_EditText = (EditText) findViewById(R.id.out_sd_all_editText);
        out_sd_free_EditText = (EditText) findViewById(R.id.out_sd_free_editText);
        out_sd_progressBar = (ProgressBar) findViewById(R.id.out_sd_progressBar);

        in_sd_EditText = (EditText) findViewById(R.id.in_sd_editText);
        in_sd_all_EditText = (EditText) findViewById(R.id.in_sd_all_editText);
        in_sd_free_EditText = (EditText) findViewById(R.id.in_sd_free_editText);
        in_sd_progressBar = (ProgressBar) findViewById(R.id.in_sd_progressBar);

        out_tf_EditText = (EditText) findViewById(R.id.out_tf_editText);
        out_tf_all_EditText = (EditText) findViewById(R.id.out_tf_all_editText);
        out_tf_free_EditText = (EditText) findViewById(R.id.out_tf_free_editText);
        out_tf_progressBar = (ProgressBar) findViewById(R.id.out_tf_progressBar);

    }

    private void progress(String str) {
        if (allSize == 0) {
            jindu = 0;
        } else {
            jindu = (int) (1000 * (allSize - freeSize) / allSize);
        }
        if (str.equals("in")) {
            in_sd_progressBar.setProgress(jindu);
        } else if (str.equals("outsd")) {
            out_sd_progressBar.setProgress(jindu);
        } else {
            out_tf_progressBar.setProgress(jindu);
        }
    }

    private boolean out_ExistSDCard() {

        extsd_path = getPath2();

        if (extsd_path.equals("/mnt/extsd1")) {
            return true;
        } else
            return false;
    }

    public String getPath2() {
        String sdcard_path = "";
        String sd_default = Environment.getExternalStorageDirectory()
                .getAbsolutePath();
        if (sd_default.endsWith("/")) {
            sd_default = sd_default.substring(0, sd_default.length() - 1);
        }
        try {
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            String line;
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                if (line.contains("secure"))
                    continue;
                if (line.contains("asec"))
                    continue;
                if (line.contains("fat") && line.contains("/mnt/")) {
                    String columns[] = line.split(" ");
                    if (columns != null && columns.length > 1) {
                        if (sd_default.trim().equals(columns[1].trim())) {
                            continue;
                        }
                        sdcard_path = columns[1];
                    }
                } else if (line.contains("fuse") && line.contains("/mnt/")) {
                    String columns[] = line.split(" ");
                    if (columns != null && columns.length > 1) {
                        if (sd_default.trim().equals(columns[1].trim())) {
                            continue;
                        }
                        sdcard_path = columns[1];
                    }
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return sdcard_path;
    }

    public long getSDFreeSize(String path) {
        // File path = Environment.getExternalStorageDirectory();
        // StatFs sf = new StatFs(path.getPath());

        StatFs sf = new StatFs(path);
        long blockSize = sf.getBlockSize();
        long freeBlocks = sf.getAvailableBlocks();
        return (freeBlocks * blockSize) / 1024 / 1024;
    }

    public long getSDAllSize(String path) {
        // File path = Environment.getExternalStorageDirectory();
        // StatFs sf = new StatFs(path.getPath());

        StatFs sf = new StatFs(path);
        long blockSize = sf.getBlockSize();
        long allBlocks = sf.getBlockCount();
        return (allBlocks * blockSize) / 1024 / 1024;
    }

}
