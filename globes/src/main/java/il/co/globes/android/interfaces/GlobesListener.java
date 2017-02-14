package il.co.globes.android.interfaces;
import il.co.globes.android.objects.CustomMenuItem;
import android.support.v4.app.Fragment;
import android.webkit.WebView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

/**
 * 
 * @author Eviatar<br>
 * <br>
 * 
 *         General Callback interface between {@code Activity} and
 *         {@code Fragment}
 * 
 */
public interface GlobesListener
{
	/**
	 * Callback between main right menu {@code MenuListFragment} and
	 * {@code MainSlidingActivity}
	 * 
	 * @param item
	 *            {@link CustomMenuItem} chosen from list
	 */
	public void onMainRightMenuItemSelected(CustomMenuItem item);

	/**
	 * Callback from any fragment that wants to load other content fragment
	 * 
	 * @param fragment
	 *            the {@code Fragment} to load
	 * @param addToBackStack
	 *            if true , added to backStack
	 * @param isLoadAfterSlidingMenuCloses
	 *            if true commit is done after the sliding menu is closed
	 * @param b 
	 */
	public void onSwitchContentFragment(Fragment fragment, String tag, boolean addToBackStack, boolean isLoadAfterSlidingMenuCloses, boolean b);

	/**
	 * Each fragment with {@code WebView} may set it's {@code WebView} to be the
	 * current {@code WebView} of the host activity. This is done for proper
	 * back press navigation.
	 * 
	 * @param webView
	 *            {@code WebView} to set.
	 */
	public void onSetFragmentWebview(WebView webView);

	/**
	 * Set the Default ActionBar
	 */
	public void onSetDefaultActionBar( ); 

	/**
	 * Set the Default ActionBar
	 */
//	public void onSetActionBarWithTextTitle(String title);
	public void onSetActionBarWithTextTitle(String title, int color);

	/**
	 * RedMail send picture
	 */
	public void onRedMailSendPicture();

	/**
	 * RedMail send video
	 */
	public void onRedMailSendVideo();

	/**
	 * Callback upon general search closed, should replace
	 * {@code GeneralSearchFragment} with {@code MenuListFragment} in the right
	 * menu (secondary menu).
	 */
	public void onCloseGeneralSearch();

	// TODO create onSearchItemSelected if needed

	// get sliding menu from each fragment
	public SlidingMenu getsSlidingMenu();
	
	//get title string 
	public String getActionBarTitle();

	public void setRegularMador(boolean b);

	public boolean getIsRegularMador();

}
