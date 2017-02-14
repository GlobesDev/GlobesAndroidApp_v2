package il.co.globes.android.interfaces;

import com.actionbarsherlock.app.SherlockFragment;

public abstract class AbstractGlobesFragment extends SherlockFragment
{

	/**
	 * Set arguments as bundle from an object
	 * 
	 * @param object
	 *            {@code Object} not to limit the choices, each fragment will
	 *            know what object to cast to and set data from
	 */
	public abstract void addArguments(Object object);

}
