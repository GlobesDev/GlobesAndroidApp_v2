package il.co.globes.android.adapters;

import il.co.globes.android.R;
import il.co.globes.android.objects.Article;

import java.util.Vector;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 
 * @author Eviatar<br>
 * <br>
 *         General search Items Adapter
 */
public class AdapterGeneralSearchItems extends BaseAdapter
{

	// data
	private Vector<Object> items;

	// UI
	private LayoutInflater inflater;

	public AdapterGeneralSearchItems(Activity activity, Vector<Object> items)
	{
		// inflater
		inflater = activity.getLayoutInflater();
		// items
		this.items = items;
	}

	@Override
	public int getCount()
	{
		return (items == null) ? 0 : items.size();
	}

	@Override
	public Object getItem(int position)
	{
		return (items == null) ? null : items.elementAt(position);
	}

	@Override
	public long getItemId(int position)
	{
		// not used
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		// Article item
		Article article = (Article) items.elementAt(position);

		final ViewHolder holder;
		if (convertView == null)
		{
			holder = new ViewHolder();

			convertView = inflater.inflate(R.layout.row_layout_search_item_right_menu_search, null);
			holder.textViewTitle = (TextView) convertView.findViewById(R.id.textView_search_result_item_title);
			holder.textViewDate = (TextView) convertView.findViewById(R.id.textView_search_result_item_date);
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}

		// set values
		holder.textViewTitle.setText(article.getTitle());
		holder.textViewDate.setText(article.getCreatedOn());

		return convertView;
	}

	public Vector<Object> getItems()
	{
		return items;
	}

	public void setItems(Vector<Object> items)
	{
		this.items = items;
	}

	// view holder
	public class ViewHolder
	{
		public TextView textViewTitle;
		public TextView textViewDate;
	}

}
