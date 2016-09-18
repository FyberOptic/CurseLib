package net.fybertech.curselib.database.manifest;

public class CurseManifest 
{
	/** JSON Field */
	public ManifestMinecraft minecraft;
	
	/** JSON Field */
	public String manifestType;
	
	/** JSON Field */
	public int manifestVersion;
	
	/** JSON Field */
	public String name;
	
	/** JSON Field */
	public String version;
	
	/** JSON Field */
	public String author;
	
	/** JSON Field */
	public int projectID;
	
	/** JSON Field */
	public ManifestFile[] files;
	
	/** JSON Field */
	public String overrides;

	
	
	@Override
	public String toString() {
		return "CurseManifest [name=" + name + ", version=" + version + ", author=" + author + "]";
	}
	
}
