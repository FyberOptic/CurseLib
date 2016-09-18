package net.fybertech.curselib.database;


public class CurseFileStub 
{
	/** JSON Field */
	public String GameVesion;
	
	/** JSON Field */
	public int ProjectFileID;
	
	/** JSON Field */
	public String ProjectFileName;
	
	/** JSON Field */
	public int FileType;
	
	
	
	@Override
	public String toString()
	{
		return "CurseFileStub [GameVersion=" + GameVesion + ", ProjectFileID=" + ProjectFileID + ", ProjectFileName=" + ProjectFileName + ", FileType=" + FileType + "]";
	}
}
