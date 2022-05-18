package com.templeosrs.util.playerinfo;

import com.google.gson.annotations.SerializedName;

public class TempleOSRSSkill
{
	@SerializedName("index")
	public Integer index;

	@SerializedName("xp")
	public Double xp;

	@SerializedName("xp_total")
	public Double xpTotal;

	@SerializedName("rank")
	public Double rank;

	@SerializedName("rank_total")
	public Double rankTotal;

	@SerializedName("level")
	public Double level;

	@SerializedName("level_total")
	public Double levelTotal;

	@SerializedName("ehp")
	public Double ehp;

	@SerializedName("ehb")
	public Double ehb;
}

