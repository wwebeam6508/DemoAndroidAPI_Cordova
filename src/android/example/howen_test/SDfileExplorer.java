package example.howen_test;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;




import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.yho_test.R;

public class SDfileExplorer extends Activity {
    private ListView listView;
    private TextView  contentsTextView;

    private File currentParent;
    private File[] currentFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_showmain);

        listView = (ListView) findViewById(R.id.list);
        contentsTextView = (TextView) findViewById(R.id.ContentsTextView);
        contentsTextView.setText("file save at ： "+CameraActivity.extsd_path.toString());

        File file = new File(CameraActivity.extsd_path);
        currentParent = file;
        currentFiles = file.listFiles();
        inflateListView(currentFiles);

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                File f = new File(currentFiles[arg2].getAbsolutePath());

                if (f.toString().endsWith(".mp4")) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(f), "video/*");
                    startActivity(intent);
                } else if (f.toString().endsWith(".jpg")) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(f), "image/*");
                    startActivity(intent);
                }
            }
        });
    }

    private void inflateListView(File[] files)
    {
        List<Map<String, Object>> listItems =
                new ArrayList<Map<String, Object>>();
        for (int i = 0; i < files.length; i++)
        {
            Map<String, Object> listItem =
                    new HashMap<String, Object>();

            listItem.put("fileName", files[i].getName());
            listItems.add(listItem);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(this
                , listItems, R.layout.camera_showline
                , new String[] {
                        "fileName"
                }
                , new int[] {
                        R.id.file_name
                });
        listView.setAdapter(simpleAdapter);
    }
}

