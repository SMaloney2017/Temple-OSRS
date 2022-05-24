package com.templeosrs.ui.competitions;

import com.templeosrs.TempleOSRSPlugin;
import com.templeosrs.util.compinfo.TempleOSRSCompParticipant;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.List;
import java.util.Objects;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import static net.runelite.client.ui.PluginPanel.PANEL_WIDTH;

public class TempleOSRSRankings extends JPanel
{

	private static final Color[] COLORS = {ColorScheme.SCROLL_TRACK_COLOR, ColorScheme.DARK_GRAY_HOVER_COLOR};

	public int i = 0;

	TempleOSRSRankings(TempleOSRSPlugin plugin, List<TempleOSRSCompParticipant> participantList)
	{
		setLayout(new GridLayout(0, 1));
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setBackground(ColorScheme.DARK_GRAY_HOVER_COLOR);

		JPanel layoutPanel = new JPanel();
		layoutPanel.setLayout(new BorderLayout());
		layoutPanel.setBackground(ColorScheme.DARK_GRAY_HOVER_COLOR);

		JPanel compRankings = new JPanel();
		compRankings.setLayout(new GridLayout(0, 1));
		compRankings.add(new TempleOSRSRankingsHeader());

		for (TempleOSRSCompParticipant player : participantList)
		{
			if (Objects.nonNull(player.xpGained) && player.xpGained > 0)
			{
				TempleOSRSCompetitionRow row = new TempleOSRSCompetitionRow(plugin, i, player, COLORS[i % 2]);
				compRankings.add(row);
				i++;
			}
		}

		layoutPanel.add(compRankings, BorderLayout.SOUTH);

		TitledBorder custom = BorderFactory.createTitledBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, ColorScheme.DARK_GRAY_COLOR, ColorScheme.SCROLL_TRACK_COLOR), new EmptyBorder(5, 5, 5, 5)), "Competition Rankings");
		custom.setTitleColor(ColorScheme.PROGRESS_COMPLETE_COLOR);
		custom.setTitleFont(FontManager.getRunescapeFont());

		if (i > 10)
		{
			setPreferredSize(new Dimension(PANEL_WIDTH, 250));

			final JScrollPane scroll = new JScrollPane(layoutPanel);
			scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scroll.setBackground(ColorScheme.DARK_GRAY_HOVER_COLOR);
			scroll.setBorder(custom);

			add(scroll, BorderLayout.SOUTH);
		}
		else
		{
			layoutPanel.setBorder(custom);
			add(layoutPanel, BorderLayout.SOUTH);
		}
	}
}
