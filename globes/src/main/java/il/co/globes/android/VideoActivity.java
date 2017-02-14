package il.co.globes.android;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoActivity extends Activity {
    public static final String KEY_VIDEO_URL = "key video";
    VideoView videoView1;
    Dialog progressDialog;
    String theURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        videoView1 = (VideoView) this.findViewById(R.id.videoView_video_activity);

        videoView1.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {

                return false;
            }
        });

        // video finish listener
        videoView1
                .setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        VideoActivity.this.finish();
                    }
                });

        progressDialog = ProgressDialog.show(VideoActivity.this, "",
                getString(R.string.tof_loading));
        videoView1.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                progressDialog.dismiss();
            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            //get the url from intent
            theURL = intent.getStringExtra(KEY_VIDEO_URL);
            theURL = "http://www.cast-tv.biz/play/?movId=ejfjkc&clid=22753&media=yes&AndroidType=html&autoplay=true";
            playVideo();
        } else {
            VideoActivity.this.finish();
        }

    }

    public void playVideo() {
        MediaController mc = new MediaController(this);
        videoView1.setMediaController(mc);
        videoView1.setVideoURI(Uri.parse(theURL));
        videoView1.requestFocus();
        videoView1.start();
    }

    @Override
    protected void onStart() {
//		EasyTracker.getInstance(this).activityStart(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
//		EasyTracker.getInstance(this).activityStop(this);
        super.onStop();
    }

}
