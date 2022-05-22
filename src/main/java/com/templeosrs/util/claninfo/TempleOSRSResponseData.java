package com.templeosrs.util.claninfo;

import com.google.gson.annotations.SerializedName;

public class TempleOSRSResponseData
{
	@SerializedName("added_names")
	public Integer addedNames;

	@SerializedName("removed_names")
	public Integer removedNames;

	@SerializedName("old_member_count")
	public Integer oldMemberCount;

	@SerializedName("new_member_count")
	public Integer newMemberCount;
}
