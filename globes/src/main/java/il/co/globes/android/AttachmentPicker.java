package il.co.globes.android;

import java.io.File;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AppEventsLogger;

public class AttachmentPicker extends Activity
{
	protected Button _button;
	protected ImageView _image;
	protected TextView _field;
	protected String _path;
	protected boolean _taken;	
	private Intent intent;	
	private Bitmap[] windows;
	
	Context context;

	private int attachmentType = Definitions.CLIP_ATTACHMENT;  
	
	protected static final String PHOTO_TAKEN = "photo_taken";	
	static final int REQUEST_IMAGE_CAPTURE = 1010;
	
    private int count; 
		
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        
        setContentView(R.layout.attachment_picker);
        
        intent = getIntent(); //attachmentType
        attachmentType = intent.getIntExtra("attachmentType", 0);
		if(intent.getIntExtra("getAttachmentMode", 0) == Definitions.MAKE_ATTACHMENT)
		{   	    		
	    		captureImage();
		}
		else
		{	
			if(attachmentType == Definitions.CLIP_ATTACHMENT)
			{
				//clipsGallery();
				Intent intent = new Intent(Intent.ACTION_PICK);	           
	            intent.setType("video/*");
	            startActivityForResult(intent, 1);
			}				
	    	else
	    	{
	    		//imagesGallery();	    		
	    		Intent intent = new Intent(Intent.ACTION_PICK);	           
	            intent.setType("image/*");
	            startActivityForResult(intent, 1);	    		
	    	}	    					
		}        
    } 
	
	protected void captureImage()
    {	    	  	
		File imagesFolder = new File(Environment.getExternalStorageDirectory(), "GlobesRedEmailImages");
		imagesFolder.mkdirs();
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
		String date = dateFormat.format(new java.util.Date());				
		
		_path = imagesFolder.toString() + "/image_" + date + ".jpg";
		File image = new File(_path);    	
    	
    	Uri outputFileUri = Uri.fromFile( image );
    	
    	Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE );
    	intent.putExtra( MediaStore.EXTRA_OUTPUT, outputFileUri );   	
    	
    	startActivityForResult( intent, REQUEST_IMAGE_CAPTURE );
    }   
	
	 @Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	 {
		  if(resultCode == RESULT_OK)
		  {
			  if(requestCode == REQUEST_IMAGE_CAPTURE)
			  {
				  onPhotoTaken();
			  }
			  else
			  {
				  Uri selectedImageUri = data.getData();				  
				  
				  String[] filePathColumn = {MediaStore.Images.Media.DATA};

		          Cursor cursor = getContentResolver().query(selectedImageUri, filePathColumn, null, null, null);
		          cursor.moveToFirst();

		          int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		          String attachmentPath = cursor.getString(columnIndex);		            
		          cursor.close();				   
				  
				  if(attachmentPath != null)
				  {
					  doUpload(attachmentPath);
				  }				  	
			  }
		  }	
	 }	
	
    protected void onPhotoTaken()
    {	
    	doUpload(_path); 
    }
    
    protected void doUpload(String attachmentPath) 
    {	
    	intent.putExtra("imageUri", attachmentPath);    	   
    	
    	setResult(RESULT_OK, intent); 	
    	
    	finish();
    }
    
    @Override 
    protected void onRestoreInstanceState( Bundle savedInstanceState)
    {	
    	if( savedInstanceState.getBoolean( AttachmentPicker.PHOTO_TAKEN ) )  
    	{
    		onPhotoTaken();
    	}
    }
    
    @Override
    protected void onSaveInstanceState( Bundle outState ) 
    {
    	outState.putBoolean( AttachmentPicker.PHOTO_TAKEN, _taken );    	
    }
	
	private void imagesGallery()
	{
		final String[] columns = { MediaStore.Images.Media._ID };
		final String orderBy = MediaStore.Images.Media._ID;
		Cursor imagecursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);
		
		int image_column_index = imagecursor.getColumnIndex(MediaStore.Images.Media._ID);
		
		this.count = imagecursor.getCount();
		this.windows = new Bitmap[this.count];
		
		for(int i = 0; i < this.count; i++)
		{
			imagecursor.moveToPosition(i);
		    int id = imagecursor.getInt(image_column_index);
		    windows[i] = MediaStore.Images.Thumbnails.getThumbnail(getApplicationContext().getContentResolver(),
		                                                           id, MediaStore.Images.Thumbnails.MICRO_KIND, null);
		 }
		
		 GridView imagegrid = (GridView) findViewById(R.id.sdcard);
		 imagegrid.setAdapter(new ImageAdapter(getApplicationContext()));
		        
		 imagegrid.setOnItemClickListener(new OnItemClickListener() 
		 {
			 public void onItemClick(@SuppressWarnings("rawtypes") AdapterView parent, View v, int position, long id)
		     {
				 String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
		         Cursor actualimagecursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, null);
		         
		         final int dataColumnIndex = actualimagecursor.getColumnIndex(MediaStore.Images.Media.DATA);		         
		         
		         actualimagecursor.moveToPosition(position);
		         final String filename = actualimagecursor.getString(dataColumnIndex);		         
		 	            
		         doUpload(filename);	            
		 	            
		 	     closeGrid(); 
		                
		         actualimagecursor.close();		         
		      }

			  private void closeGrid() 
			  {
				  GridView sdcardImages = (GridView) findViewById(R.id.sdcard);		 			    
		 		  sdcardImages.setAdapter(null);						
			  }
		 });
		 
		 imagecursor.close();
	}	
	
	private void clipsGallery() 
	{		
		final String[] columns = { MediaStore.Video.Media._ID };
		final String orderBy = MediaStore.Video.Media._ID;
		Cursor clipCursor = managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);
		
		int clip_column_index = clipCursor.getColumnIndex(MediaStore.Video.Media._ID);
		
		this.count = clipCursor.getCount();
		this.windows = new Bitmap[this.count];
		
		for(int i = 0; i < this.count; i++)
		{
			clipCursor.moveToPosition(i);
		    int id = clipCursor.getInt(clip_column_index);
		    windows[i] = MediaStore.Video.Thumbnails.getThumbnail(getApplicationContext().getContentResolver(),
		                                                           id, MediaStore.Video.Thumbnails.MICRO_KIND, null);
		 }
		
		 GridView clipsGrid = (GridView) findViewById(R.id.sdcard);
		 clipsGrid.setAdapter(new ImageAdapter(getApplicationContext()));
		        
		 clipsGrid.setOnItemClickListener(new OnItemClickListener() 
		 {
			 public void onItemClick(@SuppressWarnings("rawtypes") AdapterView parent, View v, int position, long id)
		     {
				 String[] columns = { MediaStore.Video.Media.DATA, MediaStore.Video.Media._ID };
		         Cursor actualClipCursor = managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, columns, null, null, null);
		         
		         final int dataColumnIndex = actualClipCursor.getColumnIndex(MediaStore.Video.Media.DATA);		         
		         
		         actualClipCursor.moveToPosition(position);
		         final String filename = actualClipCursor.getString(dataColumnIndex);		         
		 	            
		         doUpload(filename);	            
		 	            
		 	     closeGrid(); 
		                
		         actualClipCursor.close();		         
		      }

			  private void closeGrid() 
			  {
				  GridView sdcardImages = (GridView) findViewById(R.id.sdcard);		 			    
		 		  sdcardImages.setAdapter(null);						
			  }
		 });
		 
		 clipCursor.close();
	}
	
	public class ImageAdapter extends BaseAdapter
	{
		private Context mContext;
		 
		public ImageAdapter(Context c)
		{
			mContext = c;
		}		 
		        
		public int getCount()
		{
			return count;
		}		 
		        
		public Object getItem(int position)
		{
			return position;
		}		 
		        
		public long getItemId(int position)
		{
			return position;
		}		 
		        
		public View getView(int position, View convertView, ViewGroup parent)
		{
			ImageView i = (ImageView)convertView;
		    if(i != null)
		    {
		    	i.setImageBitmap(windows[position]);
		    }
		    else
		    {
		    	i = new ImageView(mContext.getApplicationContext());
		        i.setImageBitmap(windows[position]);
		        i.setLayoutParams(new GridView.LayoutParams(92, 92));
		    }
		    
		    return i;
		}
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		if (UtilsWebServices.checkInternetConnection(context)){
			AppEventsLogger.activateApp(context, Definitions.FACEBOOK_APP_ID);
		}
		
		
	}
	
	@Override
	protected void onStart()
	{
//		EasyTracker.getInstance(this).activityStart(this);
		super.onStart();
	}
	@Override
	protected void onStop()
	{
//		EasyTracker.getInstance(this).activityStop(this);
		super.onStop();
	}
	
	
}
