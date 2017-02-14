package il.co.globes.android.adapters;

import il.co.globes.android.R;
import il.co.globes.android.objects.CustomMenuItem;

import java.util.List;

import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @author Eviatar<br>
 * <br>
 *         adapter for Right menu Items
 * 
 */
public class AdapterRightMenuItems extends BaseAdapter
{
	// item types
	private static final int TYPE_SIMPLE_ITEM = 0;
	private static final int TYPE_IMAGE_ITEM = 1;
	private static final int TYPE_SEPERATOR = 2;

	// max count
	private static final int TYPE_MAX_COUNT = 3;

	// data
	private List<CustomMenuItem> items;
	// inflater
	private LayoutInflater inflater;
	private Context context;

	// ctor
	public AdapterRightMenuItems(Activity activity, List<CustomMenuItem> menuItems, Context context)
	{
		this.inflater = activity.getLayoutInflater();
		this.items = menuItems;
		this.context = context;
	}

	@Override
	public int getCount()
	{
		return (items == null) ? 0 : items.size();
	}

	@Override
	public Object getItem(int position)
	{
		return (items == null) ? new String() : items.get(position);
	}

	

	@Override
	public int getViewTypeCount()
	{
		return TYPE_MAX_COUNT;
	}

	@Override
	public int getItemViewType(int position)
	{

		if ((items.get(position).name).equals("separator"))
		{
			return TYPE_SEPERATOR;
		}
		
		else if (!TextUtils.isEmpty((items.get(position).ImgUrl)))
		{

			return TYPE_IMAGE_ITEM;
		}

		else
		{
			return TYPE_SIMPLE_ITEM;
		}

	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		// get the item
		CustomMenuItem item = items.get(position);

		// get the item type
		int itemViewType = getItemViewType(position);

		// view holder
		final ViewHolderMenuItems holder;

		// new item to inflate
		if (convertView == null)
		{
			holder = new ViewHolderMenuItems();
			if (itemViewType == TYPE_SIMPLE_ITEM)
			{

				// simple textView
				convertView = inflater.inflate(R.layout.row_layout_right_menu_item_text, null);
				holder.textViewItemName = (TextView) convertView.findViewById(R.id.textView_right_menu_item_text);

			}
			else if (itemViewType == TYPE_SEPERATOR)
			{
				convertView = inflater.inflate(R.layout.row_layout_right_menu_item_seperator, null);
				holder.imageViewSeperator = (ImageView) convertView.findViewById(R.id.imageView_menu_item_seperator);
			}
			else
			{
				convertView = inflater.inflate(R.layout.row_layout_right_menu_item_image, null);
				holder.imageViewItemImage = (ImageView) convertView.findViewById(R.id.imageView_right_menu_item_image);
			}

			convertView.setTag(holder);

		}
		else
		{
			// Reuse
			holder = (ViewHolderMenuItems) convertView.getTag();
		}

		// put values into views
		if (itemViewType == TYPE_SIMPLE_ITEM)
		{
			// set the text name from item's name
			holder.textViewItemName.setText(item.name);

			// set the text color from the item's color
			holder.textViewItemName.setTextColor(item.textColor);
		}
		else if (itemViewType == TYPE_SEPERATOR)
		{
			// not used
		}
		else
		{

			Picasso.with(context).load(item.ImgUrl.replaceAll("null", "").trim()).into(holder.imageViewItemImage);

			// holder.imageViewItemImage.setImageResource(item.imageResId);
		}

		return convertView;
	}

	// view holder
	public static class ViewHolderMenuItems
	{
		public TextView textViewItemName;
		public ImageView imageViewItemImage;
		public ImageView imageViewSeperator;
		public EditText editTextSearchView;
		public ImageView imageViewSearchButton;
	}

	@Override
	public long getItemId(int position)
	{
		// TODO Auto-generated method stub
		return 0;
	}

}
