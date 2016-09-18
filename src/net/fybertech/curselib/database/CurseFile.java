package net.fybertech.curselib.database;

public class CurseFile 
{
	/** JSON Field */
	public int Id;
	
	/** JSON Field */
	public String FileName;
	
	/** JSON Field */
	public String FileNameOnDisk;
	
	/** JSON Field */
	public String FileDate;
	
	/** JSON Field */
	public int ReleaseType;
	
	/** JSON Field */
	public int FileStatus;
	
	/** JSON Field */
	public String DownloadURL;
	
	/** JSON Field */
	public boolean IsAlternate;
	
	/** JSON Field */
	public int AlternateFileId;
	
	/** JSON Field */
	// TODO - Dependencies[]
	
	/** JSON Field */
	public boolean IsAvailable;
	
	/** JSON Field */
	// TODO - Modules[]
	
	/** JSON Field */
	public long PackageFingerprint;
	
	/** JSON Field */
	public String[] GameVersion;
	
	
	
	@Override
	public String toString()
	{
		return "File [Id=" + Id + ", FileName=" + FileName + ", DownloadURL=" + DownloadURL + "]";
	}
	
}
