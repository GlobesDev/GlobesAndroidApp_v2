package il.co.globes.android.fragments;

import il.co.globes.android.Definitions;
import il.co.globes.android.R;
import il.co.globes.android.interfaces.GlobesListener;
import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

/**
 * 
 * @author Eviatar<br>
 * <br>
 * 
 *         About Fragment
 * 
 */
public class AboutFragment extends SherlockFragment
{

	// UI
	private TextView aboutText;

	// callback
	private GlobesListener mCallback;

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
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_about, null);

		// find views
		aboutText = (TextView) view.findViewById(R.id.textView_About_fragment);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		
		//actionBar
		mCallback.onSetActionBarWithTextTitle(getResources().getString(R.string.about), R.color.red);

		setContent();

	}

	private void setContent()
	{
		appendWithNewLine2(aboutText, "<u>" + getString(R.string.aboutActivityText) + "</u>");
		appendWithNewLine2(aboutText, "<u>" + getString(R.string.headerApplication) + "</u>");

		appendWithNewLine2(aboutText, getString(R.string.about_2));
		appendWithNewLine2(aboutText, getString(R.string.about_3));
		appendWithNewLine2(aboutText, "<u>" + getString(R.string.headerCapabilities) + "</u>");

		appendWithNewLine2(aboutText, getString(R.string.about_5));
		appendWithNewLine2(aboutText, getString(R.string.about_6));
		appendWithNewLine2(aboutText, getString(R.string.about_7));
		appendWithNewLine2(aboutText, getString(R.string.about_8));
		appendWithNewLine2(aboutText, getString(R.string.about_9));
		appendWithNewLine2(aboutText, getString(R.string.about_10));
		appendWithNewLine2(aboutText, getString(R.string.about_11));
		appendWithNewLine2(aboutText, getString(R.string.about_12));

		appendWithNewLine2(aboutText, getString(R.string.version));

		if (Definitions.flipAlignment)
		{
			aboutText.setGravity(android.view.Gravity.RIGHT);
		}
	}

	// private void appendWithNewLine(TextView textView, String text)
	// {
	// textView.append(Html.fromHtml(text));
	// textView.append("\n");
	// }

	private void appendWithNewLine2(TextView textView, String text)
	{
		textView.append(Html.fromHtml(text));
		textView.append("\n\n");
	}
	
	@Override
    public void onStart()
    {
        super.onStart();
//        Log.i("eli", getActivity().getResources().getString(R.string.il_co_globes_android_fragments_AboutFragment));
//        EasyTracker tracker = EasyTracker.getInstance(getActivity());
//        tracker.set(Fields.SCREEN_NAME, getActivity().getResources().getString(R.string.il_co_globes_android_fragments_AboutFragment));
//        tracker.send(MapBuilder.createAppView().build());
    }
}
