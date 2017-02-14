package il.co.globes.android.adapters;

import com.squareup.picasso.Picasso;

import il.co.globes.android.R;
import il.co.globes.android.objects.ArticleSmallOpinion;
import il.co.globes.android.objects.NewsSet;
import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AdapterSmallopinions extends BaseAdapter
{

	private NewsSet newsSet;

	private LayoutInflater inflater;

	private Context context;

	public AdapterSmallopinions(Activity activity, NewsSet set)
	{
		inflater = activity.getLayoutInflater();
		newsSet = set;
		context = activity.getApplicationContext();
	}

	@Override
	public int getCount()
	{
		if (newsSet != null)
		{
			return newsSet.itemHolder == null ? 0 : newsSet.itemHolder.size();
		}else {
			return 0;
		}
	}

	@Override
	public Object getItem(int position)
	{
		return newsSet.itemHolder == null ? new ArticleSmallOpinion() : newsSet.itemHolder.get(position);
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
		// get article
		ArticleSmallOpinion articleOpinionLeftMenu = (ArticleSmallOpinion) newsSet.itemHolder.get(position);

		final ViewHolder holder;
		if (convertView == null)
		{
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.row_layout_left_menu_small_article_opinion, null);
			holder.textViewAuthorNameF7 = (TextView) convertView.findViewById(R.id.textView_left_menu_author_name_f7);
			holder.textViewTitle = (TextView) convertView.findViewById(R.id.textView_left_menu_article_title);
			holder.textViewCreatedOn = (TextView) convertView.findViewById(R.id.textView_left_menu_article_created_on);
			holder.imageViewAuthorImg = (ImageView) convertView.findViewById(R.id.imageView_left_menu_small_article_opinion);
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}

		// set values
		holder.textViewAuthorNameF7.setText(articleOpinionLeftMenu.getF7());
		holder.textViewCreatedOn.setText(articleOpinionLeftMenu.getCreatedOn());
		holder.textViewTitle.setText(articleOpinionLeftMenu.getTitle());

		// load image

		if (!TextUtils.isEmpty(articleOpinionLeftMenu.getAuthorImgURL()))
		{
			Picasso.with(context).load(articleOpinionLeftMenu.getAuthorImgURL()).into(holder.imageViewAuthorImg);

		}

		return convertView;
	}

	public class ViewHolder
	{
		public TextView textViewAuthorNameF7, textViewTitle, textViewCreatedOn;
		public ImageView imageViewAuthorImg;
	}

	public NewsSet getNewsSet()
	{
		return newsSet;
	}

	public void setNewsSet(NewsSet newsSet)
	{
		this.newsSet = newsSet;
	}

}
