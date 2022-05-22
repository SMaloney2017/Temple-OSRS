package com.templeosrs.util;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TempleOSRSService
{
	public static final String HOST = "https://templeosrs.com/";

	public static final String PLAYER_PAGE = "player/overview.php?player=";

	public static final String CLAN_PAGE = "groups/overview.php?id=";

	private static final String PLAYER_OVERVIEW = "player/view/overview_skilling_view.php?player=";

	private static final String CLAN_OVERVIEW = "api/group_info.php?id=";

	private static final String CLAN_ACHIEVEMENTS = "api/group_achievements.php?id=";

	private static final String CLAN_EDIT = "api/edit_group.php?";

	private static final String BOSSES = "&tracking=bosses";

	private static final String DURATION = "&duration=";

	private static String getJsonFromURL(String urlString) throws IOException
	{
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder()
			.url(urlString)
			.build();

		Response response = client.newCall(request).execute();

		if (response.body() != null)
		{
			return response.body().string();
		}
		return null;
	}

	public static CompletableFuture<TempleOSRSPlayer> fetchUserGainsAsync(String player, String duration) throws Exception
	{
		String playerSkillsOverviewURL = HOST + PLAYER_OVERVIEW + player + DURATION + duration;
		String playerBossingOverviewURL = HOST + PLAYER_OVERVIEW + player + BOSSES + DURATION + duration;

		String playerSkillsOverviewJSON;
		String playerBossingOverviewJSON;

		playerSkillsOverviewJSON = getJsonFromURL(playerSkillsOverviewURL);
		playerBossingOverviewJSON = getJsonFromURL(playerBossingOverviewURL);

		CompletableFuture<TempleOSRSPlayer> future = new CompletableFuture<>();
		future.complete(new TempleOSRSPlayer(playerSkillsOverviewJSON, playerBossingOverviewJSON));
		return future;
	}

	public static CompletableFuture<TempleOSRSClan> fetchClanAsync(String clanID) throws Exception
	{
		String clanOverviewURL = HOST + CLAN_OVERVIEW + clanID;

		String clanAchievementsURL = HOST + CLAN_ACHIEVEMENTS + clanID;

		String clanOverviewJSON;

		String clanAchievementsJSON;

		clanOverviewJSON = getJsonFromURL(clanOverviewURL);

		clanAchievementsJSON = getJsonFromURL(clanAchievementsURL);

		CompletableFuture<TempleOSRSClan> future = new CompletableFuture<>();
		future.complete(new TempleOSRSClan(clanOverviewJSON, clanAchievementsJSON));
		return future;
	}

	public static CompletableFuture<Response> postClanMembersAsync(String clanID, String verification, List<String> members) throws Exception
	{
		OkHttpClient client = new OkHttpClient();

		RequestBody formBody = new FormBody.Builder()
			.add("id", clanID)
			.add("key", verification)
			.add("memberlist", String.valueOf(members))
			.build();

		Request request = new Request.Builder()
			.url(HOST + CLAN_EDIT)
			.post(formBody)
			.build();

		Call call = client.newCall(request);
		Response response = call.execute();

		CompletableFuture<Response> future = new CompletableFuture<>();
		future.complete(response);
		return future;
	}
}
