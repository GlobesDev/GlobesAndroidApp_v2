package il.co.globes.android.native_video_player;



import il.co.globes.android.native_video_player.TrackingVideoView.CompleteCallback;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;

import com.google.ads.interactivemedia.v3.api.player.ContentProgressProvider;
import com.google.ads.interactivemedia.v3.api.player.VideoAdPlayer;
import com.google.ads.interactivemedia.v3.api.player.VideoProgressUpdate;



public class NativePlayer extends RelativeLayout implements VideoAdPlayer
{
	private final List<VideoAdPlayerCallback> adCallbacks = new ArrayList<VideoAdPlayerCallback>(1);

	private TrackingVideoView video;
	private FrameLayout adUiContainer;
	private MediaController mediaController;

	private String savedContentUrl;
	private int savedContentPosition = 0;
	private int savedPosition = 0;
	private boolean contentPlaying;
	
	private Activity activity;
	
	public NativePlayer(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		this.activity = (Activity) context;
		init();
	}

	public NativePlayer(Context context, AttributeSet attrs)
	{
		super(context, attrs);	
		this.activity = (Activity) context;
		init();
	}

	public NativePlayer(Context context)
	{
		super(context);		
		this.activity = (Activity) context;
		init();
	}
	
	

	public MediaController getMediaController()
	{
		return mediaController;
	}

	public void setMediaController(MediaController mediaController)
	{
		this.mediaController = mediaController;
	}

	private void init()
	{
		mediaController = new MediaController(getContext());
		mediaController.setAnchorView(this);

		// Center the video in the parent layout (when video ratio doesn't match the
		// layout size it will by default position to the left).
		//LayoutParams videoLayouyParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		
		LayoutParams videoLayouyParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				
		videoLayouyParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		videoLayouyParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		video = new TrackingVideoView(getContext());
		video.setPlayVideoContext((PlayVideoActivity)activity);
				
		// Adding the touch listener to allow pause/resume during an ad. This is necessary because we
		// remove the MediaController during ad playback to prevent seeking.
		//		video.setOnTouchListener(new OnTouchListener() 
		//		{
		//			@Override
		//			public boolean onTouch(View v, MotionEvent event)
		//			{
		//				stopAd() ;
		//				play() ;
		//				return false;
		//				//				if (!contentPlaying)
		//				//				{
		//				//					// Only applies when ad is playing
		//				//					video.togglePlayback();
		//				//				}
		//				//				return false;
		//			}
		//		});

		addView(video, videoLayouyParams);
		adUiContainer = new FrameLayout(getContext());
		addView(adUiContainer, LayoutParams.MATCH_PARENT);
		
	}

	public ViewGroup getUiContainer()
	{
		return adUiContainer;
	}

	public void setCompletionCallback(CompleteCallback callback)
	{
		video.setCompleteCallback(callback);
	}

	/**
	 * Play whatever is already in the video view.
	 */
	public void play() {
		video.start();
	}

	public void playContent(String contentUrl)
	{
		contentPlaying = true;
		savedContentUrl = contentUrl;
		video.setVideoPath(contentUrl);
		video.setMediaController(mediaController);
//		mediaController.setVisibility(View.VISIBLE);
		play();
	}

	public void pauseContent()
	{
		savedContentPosition = video.getCurrentPosition();
		video.stopPlayback();
		video.setMediaController(null); // Disables seeking during ad playback.
//		mediaController.setVisibility(View.GONE);

	}

	public void resumeContent() 
	{
		contentPlaying = true;
		video.setVideoPath(savedContentUrl);
		video.seekTo(savedContentPosition);
		video.setMediaController(mediaController);
//		mediaController.setVisibility(View.VISIBLE);

		play();
	}

	public boolean isContentPlaying() 
	{
		return contentPlaying;
	}

	public void savePosition()
	{
		savedPosition = video.getCurrentPosition();
	}

	public void restorePosition() 
	{
		video.seekTo(savedPosition);
	}

	// Methods implementing VideoAdPlayer interface.

	@Override
	public void playAd() 
	{
		contentPlaying = false;
		video.start();
	}

	@Override
	public void stopAd() 
	{
		video.stopPlayback();
	}

	@Override
	public void loadAd(String url) 
	{
		video.setVideoPath(url);
	}

	@Override
	public void pauseAd()
	{
		video.pause();
	}

	@Override
	public void resumeAd()
	{
		video.start();
	}

	@Override
	public void addCallback(VideoAdPlayerCallback callback) 
	{
		video.addCallback(callback);
		adCallbacks.add(callback);
	}

	@Override
	public void removeCallback(VideoAdPlayerCallback callback)
	{
		video.removeCallback(callback);
		adCallbacks.remove(callback);
	}


	public TrackingVideoView getVideo()
	{
		return video;
	}

	public OnTouchListener getRegularOnTouchListener()
	{
		return new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				if (!contentPlaying)
				{
					video.togglePlayback();
				}
				return false;
			}
		};
	}

	@Override
	public VideoProgressUpdate getAdProgress()
	{
		// TODO Auto-generated method stub
		int durationMs =  video.getDuration();

		if (durationMs <= 0)
		{
			return VideoProgressUpdate.VIDEO_TIME_NOT_READY;
		}
		VideoProgressUpdate vpu = new VideoProgressUpdate(video.getCurrentPosition(), durationMs);
		Log.i("PLAYER", vpu.toString());
		return vpu;
	}

	

}
