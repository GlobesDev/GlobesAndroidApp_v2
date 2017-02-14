package il.co.globes.android.objects;

public class DocumentFragmentExtraData
{
	private String caller;
	private String rowType;
	private String pushNotificationDocID;
	private int position;
	private String articleId;
	private NewsSet parsedNewsSet;
	private String DocType;
	private boolean isFromPUSH;
	
	public DocumentFragmentExtraData(String caller, String rowType, String pushNotificationDocID, int position, String articleId,  boolean isFromPUSH,	NewsSet parsedNewsSet, String DocType)
	{
		super();
		this.caller = caller;
		this.rowType = rowType;
		this.pushNotificationDocID = pushNotificationDocID;
		this.position = position;
		this.articleId = articleId;
		this.parsedNewsSet = parsedNewsSet;
		this.DocType = DocType;
		this.isFromPUSH = isFromPUSH;
	}

	@Override
	public String toString()
	{
		return "DocumentFragmentExtraData [caller=" + caller + ", rowType=" + rowType + ", pushNotificationDocID=" + pushNotificationDocID
				+ ", position=" + position + ", articleId=" + articleId + ", parsedNewsSet=" + parsedNewsSet + ", DocType=" + DocType
				+ ", isFromPUSH=" + isFromPUSH + "]";
	}

	public boolean isFromPUSH()
	{
		return isFromPUSH;
	}

	public void setFromPUSH(boolean isFromPUSH)
	{
		this.isFromPUSH = isFromPUSH;
	}

	public String getDocType()
	{
		return DocType;
	}

	public void setDocType(String docType)
	{
		DocType = docType;
	}

	public String getCaller()
	{
		return caller;
	}

	public void setCaller(String caller)
	{
		this.caller = caller;
	}

	public String getRowType()
	{
		return rowType;
	}

	public void setRowType(String rowType)
	{
		this.rowType = rowType;
	}

	public String getPushNotificationDocID()
	{
		return pushNotificationDocID;
	}

	public void setPushNotificationDocID(String pushNotificationDocID)
	{
		this.pushNotificationDocID = pushNotificationDocID;
	}

	public int getPosition()
	{
		return position;
	}

	public void setPosition(int position)
	{
		this.position = position;
	}

	public String getArticleId()
	{
		return articleId;
	}

	public void setArticleId(String articleId)
	{
		this.articleId = articleId;
	}

	public NewsSet getParsedNewsSet()
	{
		return parsedNewsSet;
	}

	public void setParsedNewsSet(NewsSet parsedNewsSet)
	{
		this.parsedNewsSet = parsedNewsSet;
	}
	
	
	
}
