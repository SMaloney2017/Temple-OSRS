package com.templeosrs.util.playerinfo;

import com.google.gson.annotations.SerializedName;
import com.templeosrs.util.TempleOSRSErrorType;

public class TempleOSRSPlayerError
{
	@SerializedName("error")
	public TempleOSRSErrorType error;
}