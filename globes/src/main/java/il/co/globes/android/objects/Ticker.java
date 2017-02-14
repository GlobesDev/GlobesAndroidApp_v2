package il.co.globes.android.objects;

import il.co.globes.android.UtilsFiles;
import il.co.globes.android.UtilsWebServices;
import il.co.globes.android.objects.ShareObjects.ShareData;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

import org.json.JSONArray;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class Ticker
{

	private static final String STATIC_RESPONSE = "0,4186,,20130614000000,3.6010,-0.387,%d7%93%d7%95%d7%9c%d7%a8+%d7%90%d7%a8%d7%94%22%d7%91+%d7%99%d7%a6%d7%99%d7%92 0,10463,,20130615010000,3.5955,-0.2082,%d7%93%d7%95%d7%9c%d7%a8+%d7%90%d7%a8%d7%94%22%d7%91%2f%d7%a9%d7%a7%d7%9c+%d7%99%d7%a9%d7%a8%d7%90%d7%9c%d7%99 0,10426,,20130615010000,4.8042,-0.4166,%d7%90%d7%99%d7%a8%d7%95%2f%d7%a9%d7%a7%d7%9c+%d7%99%d7%a9%d7%a8%d7%90%d7%9c%d7%99 0,10432,,20130615010000,1.3345,-0.2049,%d7%90%d7%99%d7%a8%d7%95%2f%d7%93%d7%95%d7%9c%d7%a8+%d7%90%d7%a8%d7%94%22%d7%91 0,979,,20130616134400,1226.23,0.34,%d7%aa%22%d7%90-+25 0,982,,20130616134400,786.89,0.46,%d7%aa%22%d7%90-+75 1,66772,,20130614230000,3423.56,-0.633,%d7%9b%d7%9c%d7%9c%d7%99+NASDAQ 1,66707,,20130614233400,15070.18,-0.6978,%d7%93%d7%90%d7%95+%d7%92'%d7%95%d7%a0%d7%a1 1,373853,,20130614230500,1626.73,-0.5885,S%26P+500+Index";
	private static final String TAG = "Ticker";
	public static final String FILE_LOOPER_ITEMS = "looper";
	public static final int MIN_LOOPER_ITEMS = 3;
	private static final int MIN_RESPONSE_LENGTH = 100;
	private static Ticker instance = null;
	private Context appContext;
	private ArrayList<TickerItem> items;

	private Ticker(Context context)
	{
		this.appContext = context.getApplicationContext();
		this.items = new ArrayList<TickerItem>();

		// read saved Ticker items from internal storage and populate the list
		readLooperItemsFromInternalStorageAsyncTask();

	}

	/**
	 * Singelton
	 * 
	 * @param context
	 *            - used later in class
	 * @return the instance
	 */
	public static synchronized Ticker getInstance(Context appContext)
	{
		if (instance == null)
		{
			instance = new Ticker(appContext.getApplicationContext());
		}
		return instance;
	}

	/**
	 * Wraps the reading function inside of an async task for performance
	 */
	private void readLooperItemsFromInternalStorageAsyncTask()
	{
		new AsyncTask<Void, Void, Void>()
		{
			@Override
			protected Void doInBackground(Void... params)
			{
				readLooperItemsFromInternalStorage();
				return null;
			}

			protected void onPostExecute(Void result)
			{

				// case there are no items saved in the internal storage we take
				// them
				// from the URL
				if (items.size() < 1)
				{
					readLooperItemsFromURLAsyncTask();
				}
				else
				{
					refreshLooperItemsFromURLAsyncTask();
				}

			};
		}.execute();
	}

	/**
	 * Wraps the reading from URL function inside of an async task for
	 * performance and Network operations on UI thread
	 */
	private void readLooperItemsFromURLAsyncTask()
	{
		new AsyncTask<Void, Void, Void>()
		{

			@Override
			protected Void doInBackground(Void... params)
			{
				String response = null;
				boolean emptyResponse = true;
				for (int i = 0; i < 4 && emptyResponse; i++)
				{
					try
					{
						response = UtilsWebServices.getHTTPText(GlobesURL.LOOPER_ITEMS_URL);
					}
					catch (Exception e)
					{
						e.printStackTrace();
						response = null;
					}
					if (response != null && response.length() > MIN_RESPONSE_LENGTH) emptyResponse = false;

				}
				if (emptyResponse)
				{
					response = STATIC_RESPONSE;
				}
				String[] splitByRows = response.split("\n");
				if (splitByRows != null)
				{
					for (int i = 0; i < splitByRows.length; i++)
					{
						String[] splitByComa = splitByRows[i].split(",");

						if (splitByComa != null && splitByComa.length > 5)
						{
							String nameHE = "";
							try
							{
								if (splitByComa.length >= 7){
									nameHE = URLDecoder.decode(splitByComa[6], "UTF-8");
								} else {
									for (int j = 0; j < splitByComa.length ; j++) {
										Log.e("DEBUG", splitByComa[j]);
									}
								}

								if (nameHE != null)
								{

								}
								TickerItem item = new TickerItem(splitByComa[1], nameHE, splitByComa[5] + "%", splitByComa[4],
										splitByComa[0]);
								items.add(item);
							}
							catch (UnsupportedEncodingException e)
							{
								e.printStackTrace();
								Log.e(TAG, "URLDecoder.decode(spiltByComa[6], \"UTF-8\") :" + e.toString());
							}

						}

					}
				}
				return null;
			}

			// TODO TEST refreshing looper items after they were D/L from server
			protected void onPostExecute(Void result)
			{

			};

		}.execute();
	}
	/**
	 * Refresh all items in looper from URL
	 */
	public synchronized void  refreshLooperItemsFromURL()
	{
		if (items != null && items.size() > 0)
		{
			TickerItem item;
			for (int i = 0; i < items.size(); i++)
			{
				item = items.get(i);
				Log.i(TAG, "Refreshing Started :"+ item.getNameHE());
				String url = GlobesURL.LOOPER_ITEM_REFRESH_URL.replace("<feeder>", item.getFeeder()).replace("<id>", item.getInsturmentID());
				String response = null;
				try
				{
					response = UtilsWebServices.getHTTPText(url);
				}
				catch (Exception e1)
				{
					Log.e("eli", "error", e1);
					response = null;
				}
				if (response != null && !response.contains("Exception") && response.length() > 0)
				{
					String[] splitResponse = response.split(",");
					if (splitResponse.length < 7) // make sure the response returned is adequate 
						continue;
					item.setFeeder(splitResponse[0]);
					item.setInsturmentID(splitResponse[1]);
					item.setLast(splitResponse[4]);
					String nameHE = "";
					try
					{
						nameHE = URLDecoder.decode(splitResponse[6], "UTF-8");
					}
					catch (Exception e)
					{
						Log.e(TAG, "refreshLooperItemsFromURL", e);
						nameHE = "";
					}
					item.setNameHE(nameHE);
					item.setPercentage_change(splitResponse[5] + "%");
					
					Log.i(TAG, "Refreshing Finished success"+ item.getNameHE());
				}else{
					Log.i(TAG, "Refreshing Response failure on item: "+item.getNameHE()+" the response is: " + response);
				}
			}
		}
	}

	/**
	 * Refresh all items in looper from URL Async Task
	 */
	public void refreshLooperItemsFromURLAsyncTask()
	{
		new AsyncTask<Void, Void, Void>()
		{

			@Override
			protected Void doInBackground(Void... params)
			{
				refreshLooperItemsFromURL();
				return null;
			}

			protected void onPostExecute(Void result)
			{
				logMsg("Done refreshing items");
			};

		}.execute();
	}

	/**
	 * Adds a TickerItem to the arrayList
	 * 
	 * @param item
	 *            the TickerItem to be added. to avoid aliasing must receive a
	 *            New ItemLooper
	 */
	public void addLooperItem(TickerItem item)
	{
		this.items.add(item);
		logMsg("addLooperItem(TickerItem item): " + item.getNameHE());
	}

	public void addLooperItem(Stock stock)
	{
		TickerItem item = new TickerItem(stock);
		this.items.add(item);
		logMsg("addLooperItem(Stock stock): " + stock.toString());
	}

	public void addLooperItem(Instrument insturment)
	{
		TickerItem item = new TickerItem(insturment);
		this.items.add(item);
		logMsg("addLooperItem(Instrument insturment): " + insturment.toString());
	}

	public void addLooperItem(ShareData shareData)
	{
		TickerItem item = new TickerItem(shareData);
		this.items.add(item);
		logMsg("addLooperItem(ShareData shareData): " + item.getNameHE());
	}

	/**
	 * 
	 * @param item
	 *            to be removed from the looper list
	 */
	public void removeLooperItem(TickerItem item)
	{
		this.items.remove(item);
		logMsg("removeLooperItem(TickerItem item): " + item.getNameHE());
	}

	public void removeLooperItem(Stock stock)
	{
		TickerItem item = new TickerItem(stock);
		this.items.remove(item);
		logMsg("removeLooperItem(Stock stock): " + item.getNameHE());
	}

	public void removeLooperItem(Instrument insturment)
	{
		TickerItem item = new TickerItem(insturment);
		this.items.remove(item);
		logMsg("removeLooperItem(Instrument insturment): " + item.getNameHE());
	}

	public void removeLooperItem(ShareData shareData)
	{
		TickerItem item = new TickerItem(shareData);
		this.items.remove(item);
		logMsg("removeLooperItem(ShareData shareData): " + item.getNameHE());
	}

	/**
	 * 
	 * @return the TickerItem Count size
	 */
	public int getLooperItemsCount()
	{
		return this.items.size();
	}

	/**
	 * 
	 * @param item
	 *            the item to check within the list
	 * @return true if the item is in the looper list
	 */
	public boolean isInLooper(TickerItem item)
	{
		if (item != null)
		{
			return this.items.contains(item);
		}
		else
		{
			return true;
		}
	}

	/**
	 * 
	 * @param Stock
	 *            the item to check within the list
	 * @return true if the item is in the looper list
	 */
	public boolean isInLooper(Stock stock)
	{
		if (stock != null)
		{
			TickerItem item = new TickerItem(stock);
			return this.items.contains(item);
		}
		else
		{
			return true;
		}
	}

	/**
	 * 
	 * @param insturment
	 *            the item to check within the list
	 * @return true if the item is in the looper list
	 */
	public boolean isInLooper(Instrument insturment)
	{
		if (insturment != null)
		{
			TickerItem item = new TickerItem(insturment);
			return this.items.contains(item);
		}
		else
		{
			return true;
		}
	}

	/**
	 * 
	 * @param shareData
	 *            the item to check within the list
	 * @return true if the item is in the looper list
	 */
	public boolean isInLooper(ShareData shareData)
	{
		if (shareData != null)
		{
			TickerItem item = new TickerItem(shareData);
			return this.items.contains(item);
		}
		else
		{
			return true;
		}
	}

	/**
	 * Cache LooperItems to internal Storage
	 * 
	 * @return success = true
	 */
	private void writeLooperItemsToInternalStorage()
	{
		try
		{
			String data = "";
			JSONArray array = new JSONArray();

			for (TickerItem item : this.items)
			{
				array.put(item.toJson());
			}
			data = array.toString();
			UtilsFiles.writeTextFile(appContext, Ticker.FILE_LOOPER_ITEMS, data);
		}
		catch (Exception e)
		{
			Log.e(TAG, "writeLooperItemsToInternalStorage() error: " + e.toString());
			e.printStackTrace();
		}
	}

	/**
	 * Wraps the writeLooperItemsToInternalStorage via AsyncTask for performance
	 */
	public void writeLooperItemsToInternalStorageAsyncTask()
	{
		new AsyncTask<Void, Void, Void>()
		{

			@Override
			protected Void doInBackground(Void... params)
			{
				writeLooperItemsToInternalStorage();
				return null;
			}

			@Override
			protected void onPostExecute(Void result)
			{
				logMsg("writeLooperItemsToInternalStorageAsyncTask: Done");
			}
		}.execute();
	}

	/**
	 * Read & populate LooperItems from internal Storage
	 * 
	 * @return
	 */
	public boolean readLooperItemsFromInternalStorage()
	{
		try
		{
			String data = UtilsFiles.readTextFile(appContext, Ticker.FILE_LOOPER_ITEMS);
			if (data != null)
			{
				JSONArray array = new JSONArray(data);
				for (int i = 0; i < array.length(); i++)
				{
					this.items.add(new TickerItem(array.getJSONObject(i)));
				}
				return true;

			}
			return false;
		}
		catch (Exception e)
		{
			Log.e(TAG, "readLooperItemsFromInternalStorage() error: " + e.toString());
			e.printStackTrace();
			return false;
		}
	}

	// /GETTERS/SETTERS////
	public ArrayList<TickerItem> getItems()
	{
		return items;
	}

	public void setItems(ArrayList<TickerItem> items)
	{
		this.items = items;
	}
	// /GETTERS/SETTERS////

	/**
	 * 
	 * @param msg
	 *            the msg to log to debug TAG
	 */
	private void logMsg(String msg)
	{
		Log.d(TAG, msg);
	}

}
