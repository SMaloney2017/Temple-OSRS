package com.templeosrs.ui.activities;

import com.google.common.collect.ImmutableList;
import com.templeosrs.util.TempleHiscoreSkill;
import static com.templeosrs.util.TempleHiscoreSkill.*;
import com.templeosrs.util.player.TemplePlayer;
import com.templeosrs.util.player.TemplePlayerData;
import com.templeosrs.util.player.TemplePlayerSkill;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.inject.Inject;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import net.runelite.client.hiscore.HiscoreSkillType;
import net.runelite.client.ui.ColorScheme;

public class TempleActivity extends JPanel
{
	public static final List<TempleHiscoreSkill> SKILLS = ImmutableList.of(
		ATTACK, DEFENCE, STRENGTH,
		HITPOINTS, RANGED, PRAYER,
		MAGIC, COOKING, WOODCUTTING,
		FLETCHING, FISHING, FIREMAKING,
		CRAFTING, SMITHING, MINING,
		HERBLORE, AGILITY, THIEVING,
		SLAYER, FARMING, RUNECRAFT,
		HUNTER, CONSTRUCTION
	);

	public static final List<TempleHiscoreSkill> BOSSES = ImmutableList.of(
		ABYSSAL_SIRE, ALCHEMICAL_HYDRA, BARROWS_CHESTS,
		BRYOPHYTA, CALLISTO, CERBERUS,
		CHAMBERS_OF_XERIC, CHAMBERS_OF_XERIC_CHALLENGE_MODE, CHAOS_ELEMENTAL,
		CHAOS_FANATIC, COMMANDER_ZILYANA, CORPOREAL_BEAST,
		DAGANNOTH_PRIME, DAGANNOTH_REX, DAGANNOTH_SUPREME,
		CRAZY_ARCHAEOLOGIST, DERANGED_ARCHAEOLOGIST, GENERAL_GRAARDOR,
		GIANT_MOLE, GROTESQUE_GUARDIANS, HESPORI,
		KALPHITE_QUEEN, KING_BLACK_DRAGON, KRAKEN,
		KREEARRA, KRIL_TSUTSAROTH, MIMIC,
		NEX, THE_NIGHTMARE, PHOSANIS_NIGHTMARE,
		OBOR, SARACHNIS, SCORPIA,
		SKOTIZO, TEMPOROSS, THE_GAUNTLET,
		THE_CORRUPTED_GAUNTLET, THEATRE_OF_BLOOD, THEATRE_OF_BLOOD_CHALLENGE_MODE,
		THERMONUCLEAR_SMOKE_DEVIL, TZKAL_ZUK, TZTOK_JAD,
		VENENATIS, VETION, VORKATH,
		WINTERTODT, ZALCANO, ZULRAH
	);

	private static final Color[] COLORS = {ColorScheme.DARKER_GRAY_COLOR, ColorScheme.DARK_GRAY_HOVER_COLOR};

	final Map<String, TempleActivityTableRow> map = new HashMap<>();

	final TempleActivitySortHeader sortPanel;

	final TempleActivityTableRow overall;

	ArrayList<TempleActivityTableRow> rows = new ArrayList<>();

	HiscoreSkillType hiscoreSkillType;

	long total;

	@Inject
	public TempleActivity(HiscoreSkillType type)
	{
		hiscoreSkillType = type;

		setLayout(new GridLayout(0, 1));
		setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, ColorScheme.DARK_GRAY_COLOR, ColorScheme.SCROLL_TRACK_COLOR), new EmptyBorder(5, 5, 5, 5)));
		setBackground(ColorScheme.DARKER_GRAY_COLOR);

		sortPanel = new TempleActivitySortHeader(this, hiscoreSkillType);

		overall = new TempleActivityTableRow("overall", "Overall", COLORS[1], HiscoreSkillType.OVERALL);

		initialize();
	}

	private void initialize()
	{
		/* reset rows */
		rows.clear();

		/* add default panels */
		add(sortPanel);
		add(overall);

		/* get correct list of HiscoreSkills */
		List<TempleHiscoreSkill> list = (hiscoreSkillType.equals(HiscoreSkillType.SKILL) ? SKILLS : BOSSES);

		/*
		* for each skill in the activity-list ->
		* { create a new ActivityRow,
		*	add entry <key, row> to map,
		* 	add row to rows-list,
		* 	add row to instance }
		*/
		for (int i = 0; i < list.size(); i++)
		{
			TempleHiscoreSkill skill = list.get(i);
			String formattedKey = skill.getName().replaceAll("[^A-Za-z0-9]", "").toLowerCase();

			TempleActivityTableRow row = new TempleActivityTableRow(formattedKey, skill.getName(), COLORS[i % 2], hiscoreSkillType);
			map.put(formattedKey, row);
			rows.add(row);
			add(row);
		}
	}

	public void update(TemplePlayer result)
	{
		/* determine and get type of json */
		TemplePlayerData playerData = hiscoreSkillType.equals(HiscoreSkillType.SKILL) ? result.playerSkillsOverview.data : result.playerBossesOverview.data;

		/*
		 * for each entry in playerData ->
		 * { get skill by index,
		 * 	 create key,
		 * 	 validate key is in map,
		 * 	 get row by key,
		 * 	 update row with values }
		 */
		for (Map.Entry<String, TemplePlayerSkill> entry : playerData.table.entrySet())
		{
			TempleHiscoreSkill skill = TempleHiscoreSkill.values()[entry.getValue().index];
			String formattedKey = skill.getName().replaceAll("[^A-Za-z0-9]", "").toLowerCase();

			if (map.containsKey(formattedKey))
			{
				TempleActivityTableRow row = map.get(formattedKey);
				TemplePlayerSkill skillData = playerData.table.get(skill.getName());

				long total = Objects.nonNull(skillData.xp) ? skillData.xp.longValue() : 0;
				long levels = Objects.nonNull(skillData.level) ? skillData.level.longValue() : 0;
				long rank = Objects.nonNull(skillData.rank) ? skillData.rank.longValue() : 0;
				double ehp = hiscoreSkillType.equals(HiscoreSkillType.SKILL) ? (Objects.nonNull(skillData.ehp) ? skillData.ehp : 0) : (Objects.nonNull(skillData.ehb) ? skillData.ehb : 0);

				this.total += total;

				row.update(total, levels, rank, ehp);
			}
		}
	}

	/* update overall row */
	public void update(long rank, double ehp)
	{
		overall.update(total, 0, rank, ehp);
	}

	/* reset activity panel to defaults */
	public void reset()
	{
		total = 0;

		overall.reset();
		sortPanel.reset();

		removeAll();

		initialize();
	}

	/* re-build list of skills by sorted row-list */
	private void rebuild()
	{
		int i = 0;
		for (TempleActivityTableRow row : rows)
		{
			String skill = row.name;
			TempleActivityTableRow entry = map.get(skill);
			entry.setBackground(COLORS[i++ % 2]);
			add(entry);
		}

		repaint();
		revalidate();
	}

	/* sort row-list with comparator passed in through sort-filter mouse event */
	void sort(Comparator<TempleActivityTableRow> comparator)
	{
		rows.sort(comparator);
		rebuild();
	}
}
