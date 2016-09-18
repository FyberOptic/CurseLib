package net.fybertech.curselib.database.manifest;

public class ManifestFile 
{
	/** JSON Field */
	public int projectID;
	
	/** JSON Field */
	public int fileID;
	
	/** JSON Field */
	public boolean required;
	
	
	@Override
	public String toString() {
		return "ManifestFile [projectID=" + projectID + ", fileID=" + fileID + ", required=" + required + "]";
	}
}
