package il.co.globes.android.ima.v3.player;

import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.*;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;
import il.co.globes.android.AppRegisterForPushApps;
import il.co.globes.android.R;
import net.tensera.sdk.api.TenseraApi;
import net.tensera.sdk.utils.Logger;

/**
 * Main Activity.
 */
public class PlayerImaActivity extends FragmentActivity {

    // The video player.
    private static VideoView mVideoPlayer;
    // The container for the ad's UI.
    private static ViewGroup mAdUIContainer;
    // The play button to trigger the ad request.
    private static View mPlayButton;

    private static String videoURL = "";

    private static ProgressBar prb_loading_video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ima_video);
        orientVideoDescriptionFragment(getResources().getConfiguration().orientation);

        //RelativeLayout rlLoading = (RelativeLayout) findViewById(R.id.rl_loading);
        // rlLoading.setVisibility(View.VISIBLE);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            videoURL = extras.getString("videoURL");
            Log.e("alex", "MMMMMMMMMMMMMMMMMMMMMMMM: " + videoURL);
            if (AppRegisterForPushApps.enableTensera) {
                TenseraApi.report(videoURL);
            }

            //videoURL = "http://download.cast-tv.com/22753_flv/2015_7/22753_150707032538_bezeqhome.0707151.07072015_S.mp4";

            //videoURL = "http://download.cast-tv.com/22753_flv/2015_7/22753_150707032538_bezeqhome.0707151.07072015_M.mp4";

            //videoURL = "http://rmcdn.2mdn.net/MotifFiles/html/1248596/android_1330378998288.mp4";
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        orientVideoDescriptionFragment(configuration.orientation);
    }

    private void orientVideoDescriptionFragment(int orientation) {
        // Hide the extra content when in landscape so the video is as large as possible.
        //  FragmentManager fragmentManager = getSupportFragmentManager();
        
        /*
        Fragment extraContentFragment = fragmentManager.findFragmentById(R.id.videoDescription);

        if (extraContentFragment != null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                fragmentTransaction.hide(extraContentFragment);
            } else {
                fragmentTransaction.show(extraContentFragment);
            }
            fragmentTransaction.commit();
        }
        */
    }

    /**
     * The main fragment for displaying video content.
     */
    public static class VideoFragment extends Fragment {

        VideoView videoView;
        int length;
        MediaController mediaController = null;

        @Override
        public void onActivityCreated(Bundle bundle) {

            super.onActivityCreated(bundle);

            mediaController = new MediaController(this.getActivity());
            final VideoView videoView = (VideoView) mVideoPlayer;
            this.videoView = mVideoPlayer;
            mediaController.setAnchorView(videoView);
            videoView.setMediaController(mediaController);
            if (AppRegisterForPushApps.enableTensera) {
                String tenserifyUrl = TenseraApi.tenserifyUrl(videoURL);
                videoView.setVideoPath(tenserifyUrl);
            } else {
                videoView.setVideoPath(videoURL);
            }
            videoView.requestFocus();
            prb_loading_video.setVisibility(View.VISIBLE);

            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override public void onPrepared(MediaPlayer mp) {
                    Logger.d("MediaPlayer: Prepare is done");
                    prb_loading_video.setVisibility(View.GONE);
                    videoView.start();
                }
            });
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_video_1, container, false);

            mVideoPlayer = (VideoView) rootView.findViewById(R.id.sampleVideoPlayer);
            mAdUIContainer = (ViewGroup) rootView.findViewById(R.id.videoPlayerWithAdPlayback);
            mPlayButton = rootView.findViewById(R.id.playButton);
            prb_loading_video = (ProgressBar) rootView.findViewById(R.id.prb_loading_video);
            return rootView;
        }

         @Override
        public void onResume() {
            if (mediaController != null) {
                videoView.seekTo(length);
                videoView.start();
            }
            super.onResume();

            Log.e("alex", "VideoFragment onResume()");

            //Log.e("alex", "WWW: SampleVideoPlayer setVisibility !!!!!!!!!!!!!!!!!!!!!!!!!");
            //rlLoading.setVisibility(View.GONE);
        }

        @Override
        public void onPause() {
            if (mediaController != null) {
                videoView.pause();
                length = videoView.getCurrentPosition();
            }

            super.onPause();
            Log.e("alex", "VideoFragment onPause()");
        }

        @Override
        public void onStop() {
            super.onStop();
            Log.e("alex", "VideoFragment onStop()");
        }
    }

    /**
     * The fragment for displaying any video title or other non-video content.
     */
    public static class VideoDescriptionFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_video_description, container, false);
        }
    }
}