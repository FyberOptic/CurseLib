package net.fybertech.curselib.database;


public class CurseProject 
{
	/** JSON Field */
	public int Id;
	
	/** JSON Field */
	public String Name;
	
	/** JSON Field */
	public CurseAuthor[] Authors;
	
	/** JSON Field */
	public CurseAttachment[] Attachments;
	
	/** JSON Field */
	public String WebSiteURL;
	
	/** JSON Field */
	public int GameId;
	
	/** JSON Field */
	public String Summary;
	
	/** JSON Field */
	public int DefaultFileId;
	
	/** JSON Field */
	public int CommentCount;
	
	/** JSON Field */
	public double DownloadCount;
	
	/** JSON Field */
	public int Rating;
	
	/** JSON Field */
	public int InstallCount;
	
	/** JSON Field */
	public int IconId;
	
	/** JSON Field */
	public CurseFile[] LatestFiles;
	
	/** JSON Field */
	public CurseCategory[] Categories;
	
	/** JSON Field */
	public String PrimaryAuthorName;
	
	/** JSON Field */
	public String ExternalUrl;
	
	/** JSON Field */
	public int Status;
	
	/** JSON Field */
	public int Stage;
	
	/** JSON Field */
	public String DonationUrl;
	
	/** JSON Field */
	public int PrimaryCategoryId;
	
	/** JSON Field */
	public String PrimaryCategoryName;
	
	/** JSON Field */
	public String PrimaryCategoryAvatarUrl;
	
	/** JSON Field */
	public int Likes;
	
	/** JSON Field */
	public CategorySection CategorySection;
	
	/** JSON Field */
	public int PackageType;
	
	/** JSON Field */
	public String AvatarUrl;
	
	/** JSON Field */
	public CurseFileStub[] GameVersionLatestFiles;
	
	/** JSON Field */
	public int IsFeatured;
	
	/** JSON Field */
	public double PopularityScore;
	
	
	
	/**
	 * Returns true if this project is a modpack.
	 */
	public boolean isModpack() {
		return CategorySection.Name.equals("Modpacks");
	}


	/**
	 * Returns the alphanumeric name for the project that's used in most 
	 * CurseForge URLs.
	 */
	public String getProjectSlug() 
	{
		String url = this.WebSiteURL;
		
		// Remove trailing slashes
		while (url.charAt(url.length() - 1) == '/') url = url.substring(0,  url.length() - 1);		
		
		// Get just the last portion of project url
		int pos = url.lastIndexOf('/');
		if (pos >= 0) url = url.substring(pos + 1, url.length());		
		
		return url;
	}
	
	
	@Override
	public String toString()
	{
		return "Item [Id=" + Id + ", Name=" + Name + ", Summary=" + Summary + ", CategorySection=" + CategorySection.Name + "]";
	}
}
