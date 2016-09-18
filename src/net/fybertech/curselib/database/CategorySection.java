package net.fybertech.curselib.database;

public class CategorySection 
{
	/** JSON Field */
	public int ID;
	
	/** JSON Field */
	public int GameID;
	
	/** JSON Field */
	public String Name;
	
	/** JSON Field */
	public int PackageType;
	
	/** JSON Field */
	public String Path;
	
	/** JSON Field */
	public String InitialInclusionPattern;
	
	/** JSON Field */
	public String ExtraIncludePattern;
	
	
	@Override
	public String toString()
	{
		return "CategorySection [ID=" + ID + ", GameID=" + GameID + ", Name=" + Name + "]";
	}
	
}
