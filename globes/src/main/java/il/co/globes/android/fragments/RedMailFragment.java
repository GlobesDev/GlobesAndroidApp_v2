package il.co.globes.android.fragments;

import il.co.globes.android.Definitions;
import il.co.globes.android.MainSlidingActivity;
import il.co.globes.android.R;
import il.co.globes.android.interfaces.GlobesListener;
import il.co.globes.android.objects.ArticleSmallOpinion;
import il.co.globes.android.objects.CustomMenuItem;
import il.co.globes.android.objects.GlobesURL;

import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.squareup.picasso.Picasso;

/**
 * 
 * @author Eviatar<br>
 * <br>
 *         RedMail Fragment
 */
public class RedMailFragment extends SherlockFragment
{
	// UI
	private Button btnWriteUs, btnSendPicture, btnSendVideo = null;

	// callback
	private GlobesListener mCallback;

	// sliding menu/actionbar
	private ActionBar actionBar;
	private SlidingMenu sm;

	private Typeface almoni_aaa_regular;

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try
		{
			mCallback = (GlobesListener) activity;
		}
		catch (ClassCastException e)
		{
			throw new ClassCastException(activity.toString() + " must implement " + GlobesListener.class.getSimpleName());
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		almoni_aaa_regular = Typeface.createFromAsset(getActivity().getAssets(), "almoni-dl-aaa-regular.otf");

		// sliding menu / ActionBar
		sm = ((MainSlidingActivity) getActivity()).getSlidingMenu();
		actionBar = ((MainSlidingActivity) getActivity()).getSupportActionBar();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.red_mail_fragment_layout, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		// ActionBar
		//		createActionBarMenu();

		// UI
		initUI();
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();

		// return activity's default actionBar
		mCallback.onSetDefaultActionBar();
	}

	private void initUI()
	{
		View view = getView();
		btnWriteUs = (Button) view.findViewById(R.id.btnWriteUs);
		btnSendPicture = (Button) view.findViewById(R.id.btnSendPicture);
		btnSendVideo = (Button) view.findViewById(R.id.btnSendVideo);

		btnSendVideo.setTypeface(almoni_aaa_regular);
		btnSendPicture.setTypeface(almoni_aaa_regular);
		btnWriteUs.setTypeface(almoni_aaa_regular);


		btnWriteUs.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				startRedEmail("");
			}
		});

		btnSendPicture.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				mCallback.onRedMailSendPicture();
			}
		});

		btnSendVideo.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				mCallback.onRedMailSendVideo();
			}
		});

	}

	/**
	 * Starts Email Intent
	 * 
	 * @param attachmentUri
	 *            Unknown
	 */
	private void startRedEmail(String attachmentUri)
	{
		String mailBody = "×�× ×� ×ž×œ×� ×�×ª ×”×¤×¨×˜×™×� ×”×‘×�×™×�:\n×©×�:\n×˜×œ×¤×•×Ÿ:\n×“×•×�×¨ ×�×œ×§×˜×¨×•× ×™:\n×ª×•×›×Ÿ:\n\n\n\n";
		Intent emailSend = new Intent();
		emailSend.setAction(Intent.ACTION_SEND);
		String mimeType = "message/rfc822";

		emailSend.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]
				{Definitions.RED_ALERT_MAIL_TARGET});

		// emailSend.putExtra(Intent.EXTRA_CC, R.string.redemail_globes_co_il);
		emailSend.putExtra(Intent.EXTRA_SUBJECT, R.string.red_email);

		emailSend.putExtra(Intent.EXTRA_TEXT, mailBody);
		emailSend.putExtra(Intent.EXTRA_TITLE, R.string.red_email);
		if (attachmentUri.contains("."))
		{
			emailSend.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + attachmentUri));
			mimeType = "image/jpeg";
		}
		emailSend.setType(mimeType);
		startActivity(Intent.createChooser(emailSend, getString(R.string.do_you_have_news)));
	}

	//	private void loadMainHome()
	//	{
	//		String name = getString(R.string.news);
	//		CustomMenuItem menuItem = new CustomMenuItem(name , -1, -1, -1, GlobesURL.URLMainScreen,
	//				Definitions.MAINSCREEN, Definitions.MAINTABACTVITY, null, false, null, false, false);
	//		mCallback.onMainRightMenuItemSelected(menuItem);
	//	}
	/**
	 * Creates a Custom ActionBar view
	 */
	//	private int[] imagesOfAutors = {R.drawable.m1,R.drawable.m2,R.drawable.m3,R.drawable.m4};
	//	private void createActionBarMenu()
	//	{
	//		View view = getActivity().getLayoutInflater().inflate(R.layout.actionbar_custom_view_text_title, null);
	//		ImageView btn_home = (ImageView) view.findViewById(R.id.btn_home);
	//		btn_home.setOnClickListener(new OnClickListener()
	//		{
	//			@Override
	//			public void onClick(View v)
	//			{
	//				loadMainHome();		
	//
	//			}
	//		});
	////		ImageView imagViewLeftIcon = (ImageView) view.findViewById(R.id.imageView_actionBar_left_icon);
	//		ImageView imagViewRightIcon = (ImageView) view.findViewById(R.id.imageView_actionBar_right_icon);
	//		TextView textViewTitle = (TextView) view.findViewById(R.id.textView_actionBar_title);
	//
	//		Random ran = new Random();
	//		int i = ran.nextInt(imagesOfAutors.length);
	////		Picasso.with(getActivity()).load(imagesOfAutors[i]).fit().into(imagViewLeftIcon);
	//
	//		// title
	//		textViewTitle.setText(R.string.red_email);
	//
	////		imagViewLeftIcon.setOnClickListener(new View.OnClickListener()
	////		{
	////			@Override
	////			public void onClick(View arg0)
	////			{
	////				sm.toggle();
	////			}
	////		});
	//
	//		imagViewRightIcon.setOnClickListener(new View.OnClickListener()
	//		{
	//			@Override
	//			public void onClick(View arg0)
	//			{
	//				if (sm.isSecondaryMenuShowing())
	//					sm.showContent(true);
	//				else sm.showSecondaryMenu(true);
	//			}
	//		});
	//
	//		actionBar.setCustomView(view);
	//	}

	@Override
	public void onStart()
	{
		super.onStart();
//		EasyTracker tracker = EasyTracker.getInstance(getActivity());
//		tracker.set(Fields.SCREEN_NAME, getActivity().getResources().getString(R.string.il_co_globes_android_fragments_RedMailFragment));
//		tracker.send(MapBuilder.createAppView().build());
	}

}
