package com.templeosrs.ui.clans;

import javax.swing.JPanel;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.materialtabs.MaterialTab;
import net.runelite.client.ui.components.materialtabs.MaterialTabGroup;

public class TempleClanMembers extends PluginPanel
{
	public TempleClanMembers(TempleClanMembersList leaders, TempleClanMembersList members)
	{
		setBackground(ColorScheme.DARKER_GRAY_COLOR);

		JPanel display = new JPanel();
		MaterialTabGroup tabGroup = new MaterialTabGroup(display);

		MaterialTab leadersTab = new MaterialTab("Leaders", tabGroup, leaders);
		MaterialTab membersTab = new MaterialTab("Members", tabGroup, members);

		tabGroup.addTab(leadersTab);
		tabGroup.addTab(membersTab);
		tabGroup.select(leadersTab);

		add(tabGroup);
		add(display);
	}
}
