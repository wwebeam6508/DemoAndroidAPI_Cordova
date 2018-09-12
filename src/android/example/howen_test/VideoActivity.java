package example.howen_test;

import com.example.yho_test.R;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Video.VideoColumns;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoActivity extends Activity {
	private MediaController mediaController;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.video);

		Configuration configuration = getResources().getConfiguration();
		if (configuration.orientation == configuration.ORIENTATION_PORTRAIT) {
			VideoActivity.this
					.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}

		mediaController = new MediaController(this);

		final VideoView vv = (VideoView) findViewById(R.id.videoView);
		vv.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/"
				+ R.raw.yho));
		vv.setMediaController(mediaController);
		mediaController.setMediaPlayer(vv);
		vv.start();
	}
}
