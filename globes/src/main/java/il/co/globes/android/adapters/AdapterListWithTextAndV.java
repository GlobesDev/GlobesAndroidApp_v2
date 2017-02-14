package il.co.globes.android.adapters;

import il.co.globes.android.R;
import il.co.globes.android.objects.Tikim;

import java.util.List;

import android.R.color;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AdapterListWithTextAndV extends BaseAdapter
{
	private Context c;
	private List<Tikim> tikim;
	private Activity activity;
	private LayoutInflater inflater;
	
	public AdapterListWithTextAndV(Activity a, List<Tikim> tikim)
	{
		super();
		inflater = a.getLayoutInflater();
		this.c = a.getApplicationContext();
		this.activity = a;
		this.tikim = tikim;
	}

	@Override
	public int getCount()
	{
		if (tikim != null)
		{
			return tikim.size();
		}else {
			return 0;
		}
	}

	@Override
	public Tikim getItem(int position)
	{
		return tikim.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		Tikim tik = tikim.get(position);
		final ViewHolder holder;
		if (convertView == null)
		{
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.row_for_tikim_adapter, null);
			holder.txtView = (TextView) convertView.findViewById(R.id.textView_tik_name);
			holder.imgView = (ImageView) convertView.findViewById(R.id.imageView_vi);
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.txtView.setText(tik.getTik());
		holder.txtView.setTextColor(Color.BLACK);
		if (tik.isSelected()) holder.imgView.setVisibility(View.VISIBLE);
		else holder.imgView.setVisibility(View.INVISIBLE);
		return convertView;
	
	}
	
	public class ViewHolder
	{
		public ImageView imgView;
		public TextView txtView;
	}

}
