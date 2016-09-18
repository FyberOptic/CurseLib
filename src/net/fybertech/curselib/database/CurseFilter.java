package net.fybertech.curselib.database;

public class CurseFilter 
{

	public static enum FilterType {
		CATEGORY,
		SECTION,
		VERSION,
		NAME,
		AUTHOR
	}	
	
	public FilterType filterType;
	public String filterValue;
	
	
	
	public CurseFilter(FilterType type, String value) {
		filterType = type;
		filterValue = value;
	}
	
	public static CurseFilter Version(String value) {
		return new CurseFilter(FilterType.VERSION, value);
	}
	
	public static CurseFilter Category(String value) {
		return new CurseFilter(FilterType.CATEGORY, value);
	}
	
	public static CurseFilter Section(String value) {
		return new CurseFilter(FilterType.SECTION, value);
	}
	
	public static CurseFilter Name(String value) {
		return new CurseFilter(FilterType.NAME, value);
	}
	
	public static CurseFilter Author(String value) {
		return new CurseFilter(FilterType.AUTHOR, value);
	}
	
	@Override
	public String toString()
	{
		return "Type: " + filterType + " Value: " + filterValue;
	}

	
	
}
