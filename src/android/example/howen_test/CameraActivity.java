package example.howen_test;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import com.example.yho_test.R;

public class CameraActivity extends Activity {
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Button okButton, photoButton, videoButton, browseButton;
    private Spinner systemSpinner, channelSpinner;

    private MediaRecorder mediaRecorder;
    private String videoFile = null;
    private boolean isRecord = false;

    private String dateString = null;
    public static String extsd_path = null;
    private int height = 0;
    private int width = 0;

    private Camera camera = null;
    private Parameters param = null;
    private boolean previewRunning = false;
    private boolean isOpen = false;

    private String[] sysList = {
            "P  A  L", "N T S C",
    };
    private String[] channelList = {
            "single ch1", "single ch2", "single ch3", "single ch4", "double chs", "four chs",
    };
    private SharedPreferences preferencesValue;
    private SharedPreferences.Editor editorValue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.camera);

        preferencesValue = getSharedPreferences("feijie", MODE_WORLD_READABLE);
        editorValue = preferencesValue.edit();

        view_init();

        surfaceView();

    }

    private void surfaceView() {
        // TODO Auto-generated method stub
        System.out.println("------surfaceView init------");
        surfaceView = (SurfaceView) findViewById(R.id.surface);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(new MySurfaceViewCallback());
        surfaceHolder.setType(surfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private class MySurfaceViewCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                int height) {
            // TODO Auto-generated method stub
            System.out.println("------surfaceChanged------");
            surfaceHolder = holder;
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // TODO Auto-generated method stub
            System.out.println("------surfaceCreated------");
            surfaceHolder = holder;

            ok_choice();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // TODO Auto-generated method stub
            System.out.println("------surfaceDestroyed------");
            surfaceView = null;
            surfaceHolder = null;
        }
    }

    private void view_init() {
        // TODO Auto-generated method stub
        System.out.println("------view_init------");

        File sdCardDir = Environment.getExternalStorageDirectory();
        extsd_path = sdCardDir.toString() + "/HW_VEDIO/";
        File new_path = new File(extsd_path);
        if (new_path.exists()) {
            File files[] = new_path.listFiles();
            for (int i = 0; i < files.length; i++) {
                files[i].delete();
            }
        } else {
            if (!new_path.mkdirs()) {
                Toast.makeText(CameraActivity.this, "create dir /HW_VEDIO/ failed， check please！",
                        Toast.LENGTH_SHORT).show();
            }
        }

        systemSpinner = (Spinner) findViewById(R.id.system_spinner);
        channelSpinner = (Spinner) findViewById(R.id.channel_spinner);
        okButton = (Button) findViewById(R.id.ok_button);
        okButton.setOnClickListener(new buttonClick());
        photoButton = (Button) findViewById(R.id.photo_button);
        photoButton.setOnClickListener(new buttonClick());
        videoButton = (Button) findViewById(R.id.video_button);
        videoButton.setOnClickListener(new buttonClick());
        browseButton = (Button) findViewById(R.id.browse_button);
        browseButton.setOnClickListener(new buttonClick());

        SpinnerAdapter sysAdapter = new SpinnerAdapter(CameraActivity.this,
                android.R.layout.simple_spinner_item, sysList);
        systemSpinner.setAdapter(sysAdapter);

        SpinnerAdapter channelAdapter = new SpinnerAdapter(CameraActivity.this,
                android.R.layout.simple_spinner_item, channelList);
        channelSpinner.setAdapter(channelAdapter);

        int systemPosition = preferencesValue.getInt("system", 0);
        systemSpinner.setSelection(systemPosition);

        int channelPosition = preferencesValue.getInt("channel", 5);
        channelSpinner.setSelection(channelPosition);

        ok_choice();

    }

    public class buttonClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.ok_button:
                    ok_choice();
                    browseButton.setClickable(true);
                    videoButton.setClickable(true);
                    break;
                case R.id.photo_button:
                    photo(v);
                    break;
                case R.id.video_button:
                    video();
                    browseButton.setClickable(false);
                    videoButton.setClickable(false);
                    break;
                case R.id.browse_button:
                    browse();
                    break;
                default:
                    break;
            }
        }
    }

    private void ok_choice() {
        // TODO Auto-generated method stub
        System.out.println("------ok button down------");

        String system = systemSpinner.getSelectedItem().toString();
        String channel = channelSpinner.getSelectedItem().toString();

        if (system.equals("N T S C")) {
            height = 291;
        } else {
            height = 290;
        }

        if (channel.equals("single ch1")) {
            width = 500;
        } else if (channel.equals("single ch2")) {
            width = 501;
        } else if (channel.equals("single ch3")) {
            width = 502;
        } else if (channel.equals("single ch4")) {
            width = 503;
        } else if (channel.equals("double chs")) {
            width = 505;
        } else if (channel.equals("four chs")) {
            width = 504;
        }

        editorValue.putInt("system", systemSpinner.getSelectedItemPosition());
        editorValue.putInt("channel", channelSpinner.getSelectedItemPosition());
        editorValue.commit();

        CloseVideo();

        CloseCamera();

        InitCamera();
    }

    private void InitCamera() {
        System.out.println("------InitCamera------");

        if (!isOpen && !isRecord) {
            camera = Camera.open();

            param = camera.getParameters();
            param.setPreviewSize(width, height);
            param.setPreviewFpsRange(4, 10);
            param.setPictureFormat(ImageFormat.JPEG);
            param.set("jpeg-quality", 95);
            param.setPictureSize(1600, 900);
            camera.setParameters(param);
            try {
                camera.setPreviewDisplay(surfaceHolder);
            } catch (IOException e) {
            }
            if (!previewRunning) {
                camera.startPreview();
                previewRunning = true;
            }

            isOpen = true;
        }
    }

    private void CloseCamera() {
        if (camera != null) {
            System.out.println("------CloseCamera------");
            if (previewRunning) {
                camera.stopPreview();
                previewRunning = false;
            }
            camera.release();
            camera = null;
            isOpen = false;
        }
    }

    private void CloseVideo() {
        if (isRecord == true) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecord = false;

            Toast.makeText(CameraActivity.this, "record successfully!", Toast.LENGTH_SHORT).show();
        }
    }

    private void browse() {
        // TODO Auto-generated method stub
        System.out.println("------browse button down------");

        Intent intent = new Intent(CameraActivity.this, SDfileExplorer.class);
        startActivity(intent);
    }

    private void video() {
        // TODO Auto-generated method stub
        System.out.println("------video button down------");

        if (isRecord == false) {
            System.out.println("------mediaRecorder_setting------");
            camera.unlock();
            mediaRecorder = new MediaRecorder();
            mediaRecorder.reset();
            mediaRecorder.setCamera(camera);
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.setAudioEncodingBitRate(12200);
            mediaRecorder.setAudioSamplingRate(8000);
            mediaRecorder.setVideoSize(width, height);
            mediaRecorder.setVideoFrameRate(25);
            mediaRecorder.setVideoEncodingBitRate(1500000);

            mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());

            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.CHINA);
            dateString = sDateFormat.format(new java.util.Date());

            videoFile = extsd_path + dateString + ".mp4";
            mediaRecorder.setOutputFile(videoFile);

            try {
                mediaRecorder.prepare();
                mediaRecorder.start();

                Toast.makeText(CameraActivity.this, "start to record", Toast.LENGTH_SHORT).show();

                isRecord = true;

                System.out.println("------mediaRecorder_start------");
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                System.out.println("IllegalStateException.......");
                Toast.makeText(CameraActivity.this, "recording failed", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                System.out.println("IOException.......");
                Toast.makeText(CameraActivity.this, "recording failed", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    private void photo(View v) {
        // TODO Auto-generated method stub
        System.out.println("------photo button down------");

        if (isRecord == false) {
            System.out.println("------shoot the photo at this time------");
            camera.autoFocus(autoFocusCallback);
        } else {
            // jietu(v);
            System.out.println("------film the photo at this time-----");
            camera.autoFocus(autoFocusCallback);
        }
    }

    private void jietu(View v) {
        // TODO Auto-generated method stub
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.CHINA);
        dateString = sDateFormat.format(new java.util.Date());

        String fileName = extsd_path + dateString + ".jpg";
        File file = new File(fileName);

        View view = v.getRootView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();

        Bitmap bitmap2 = view.getDrawingCache();

        System.out.println(bitmap2.getHeight());
        System.out.println(bitmap2.getWidth());

        Bitmap bitmap1 = Bitmap.createBitmap(bitmap2, 10, 10, 300, 300);

        if (bitmap1 != null)
        {
            System.out.println("bitmap    got!");
            try {
                FileOutputStream out = new FileOutputStream(fileName);
                bitmap1.compress(Bitmap.CompressFormat.PNG, 100,
                        out);
                System.out.println("file" + fileName + "outputdone.");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("no");
            }

        } else {
            System.out.println("bitmap  is NULL!");
        }
    }

    AutoFocusCallback autoFocusCallback = new AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            // TODO Auto-generated method stub
            if (success) {
                camera.takePicture(new ShutterCallback() {
                    public void onShutter()
                    {
                    }
                }, new PictureCallback()
                {
                    public void onPictureTaken(byte[] data, Camera c)
                    {
                    }
                }, myJpegCallback);
            }
        }
    };

    PictureCallback myJpegCallback = new PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.CHINA);
            dateString = sDateFormat.format(new java.util.Date());

            String fileName = extsd_path + dateString + ".jpg";
            File file = new File(fileName);

            try {
                BufferedOutputStream bos = new BufferedOutputStream(
                        new FileOutputStream(file));
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                bos.flush();
                bos.close();

                Toast.makeText(CameraActivity.this, "shooting phote successfully！", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(CameraActivity.this, "fail to shoot the photo！", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
        System.out.println("------onDestroy------");

        CloseCamera();

        CloseVideo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
