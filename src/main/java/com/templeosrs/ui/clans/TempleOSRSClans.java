package com.templeosrs.ui.clans;

import com.google.common.base.Strings;
import com.templeosrs.TempleOSRSConfig;
import com.templeosrs.TempleOSRSPlugin;
import com.templeosrs.util.TempleOSRSClan;
import static com.templeosrs.util.TempleOSRSService.CLAN_PAGE;
import static com.templeosrs.util.TempleOSRSService.HOST;
import static com.templeosrs.util.TempleOSRSService.fetchClanAsync;
import static com.templeosrs.util.TempleOSRSService.postClanMembersAsync;
import com.templeosrs.util.TempleOSRSSync;
import com.templeosrs.util.claninfo.TempleOSRSClanAchievementSkill;
import com.templeosrs.util.claninfo.TempleOSRSClanInfo;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import javax.inject.Inject;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import net.runelite.api.Client;
import net.runelite.api.clan.ClanMember;
import net.runelite.api.clan.ClanSettings;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.IconTextField;
import net.runelite.client.ui.components.PluginErrorPanel;
import net.runelite.client.util.LinkBrowser;

public class TempleOSRSClans extends PluginPanel
{
	private static final Pattern isNumeric = Pattern.compile("-?\\d+(\\.\\d+)?");

	public final IconTextField clanLookup;

	private final Client client;

	private final TempleOSRSPlugin plugin;

	private final TempleOSRSConfig config;

	private final JButton verifyButton;

	private final PluginErrorPanel errorPanel = new PluginErrorPanel();

	private final JPanel fetchLayout;

	public TempleOSRSClanMembers clanMembers;

	public TempleOSRSClanAchievements clanAchievements;

	private TempleOSRSClanMembers clanLeaders;

	private JButton searchButton;

	private JButton clanButton;

	@Inject
	public TempleOSRSClans(TempleOSRSConfig config, TempleOSRSPlugin plugin, Client client)
	{
		this.plugin = plugin;
		this.client = client;
		this.config = config;

		setBackground(ColorScheme.DARKER_GRAY_COLOR);

		fetchLayout = new JPanel();
		fetchLayout.setLayout(new BoxLayout(fetchLayout, BoxLayout.Y_AXIS));
		fetchLayout.setBorder(new EmptyBorder(5, 5, 0, 5));
		fetchLayout.setBackground(ColorScheme.DARK_GRAY_HOVER_COLOR);

		clanLookup = buildTextField();
		fetchLayout.add(clanLookup);

		JPanel buttons = buildFetchButtons();
		fetchLayout.add(buttons);

		JPanel verifyLayout = new JPanel();
		verifyLayout.setLayout(new BorderLayout());
		verifyLayout.setBorder(new EmptyBorder(0, 0, 5, 0));
		verifyLayout.setOpaque(false);

		verifyButton = createNewButton("Sync Members", "Sync all members of the clan you are currently in (Requires key)");
		verifyButton.addActionListener(e -> verify());
		verifyLayout.add(verifyButton);

		fetchLayout.add(verifyLayout);

		errorPanel.setContent("Clans", "You have not fetched clan information yet.");

		add(fetchLayout);
		add(errorPanel);
	}

	private IconTextField buildTextField()
	{
		IconTextField lookup = new IconTextField();

		lookup.setIcon(IconTextField.Icon.SEARCH);
		lookup.setEditable(true);
		lookup.setPreferredSize(new Dimension(PANEL_WIDTH, 25));
		lookup.setBackground(ColorScheme.SCROLL_TRACK_COLOR);
		lookup.addActionListener(e -> fetchClan());
		lookup.addClearListener(() -> {
			completed();
			reset();
		});

		return lookup;
	}

	private JPanel buildFetchButtons()
	{
		JPanel buttonsLayout = new JPanel();
		buttonsLayout.setLayout(new FlowLayout());
		buttonsLayout.setBackground(ColorScheme.DARK_GRAY_HOVER_COLOR);

		searchButton = createNewButton("Search", "Search for clan by ID (Found in TempleOSRS clan-page url)");
		searchButton.addActionListener(e -> fetchClan());

		clanButton = createNewButton("Open Page", "Opens TempleOSRS clan page");
		clanButton.addActionListener(e -> open());

		buttonsLayout.add(searchButton);
		buttonsLayout.add(clanButton);

		return buttonsLayout;
	}

	private JButton createNewButton(String text, String tooltip)
	{
		JButton newButton = new JButton();

		newButton.setFont(FontManager.getRunescapeFont());
		newButton.setText(text);
		newButton.setToolTipText(tooltip);
		newButton.setForeground(ColorScheme.GRAND_EXCHANGE_LIMIT);

		newButton.addMouseListener(new MouseAdapter()
		{
			public void mouseEntered(MouseEvent evt)
			{
				newButton.setForeground(ColorScheme.PROGRESS_COMPLETE_COLOR);
			}

			public void mouseExited(MouseEvent evt)
			{
				newButton.setForeground(ColorScheme.GRAND_EXCHANGE_LIMIT);
			}
		});

		return newButton;
	}

	public void fetchClan()
	{
		final String clanID = clanLookup.getText();

		if (Strings.isNullOrEmpty(clanID))
		{
			return;
		}

		if (!isNumeric.matcher(clanID).matches())
		{
			error();
			return;
		}

		loading();

		reset();

		new Thread(() -> {
			try
			{
				fetchClanAsync(clanID).whenCompleteAsync((result, err) -> rebuild(clanID, result, err));
			}
			catch (Exception ignored)
			{
				error();
			}
		}).start();
	}

	private void reload(String clanID)
	{
		loading();

		reset();

		try
		{
			fetchClanAsync(clanID).whenCompleteAsync((result, err) -> rebuild(clanID, result, err));
		}
		catch (Exception ignored)
		{
			error();
		}
	}

	private void rebuild(String clanID, TempleOSRSClan result, Throwable err)
	{
		remove(errorPanel);

		if (!clanLookup.getText().equals(clanID))
		{
			completed();
			return;
		}

		if (Objects.isNull(result) || Objects.nonNull(err) || result.error)
		{
			error();
			return;
		}
		rebuild(result);
	}

	private void rebuild(TempleOSRSClan result)
	{
		if (client == null)
		{
			return;
		}

		String[] leaders = result.clanOverview.data.leaders;
		String[] members = result.clanOverview.data.members;
		TempleOSRSClanInfo info = result.clanOverview.data.info;
		List<TempleOSRSClanAchievementSkill> clanActivity = result.clanAchievements.data;

		SwingUtilities.invokeLater(() -> {
			add(new TempleOSRSClanOverview(info));

			clanLeaders = new TempleOSRSClanMembers(plugin, "Leaders", leaders);
			add(clanLeaders);

			clanAchievements = new TempleOSRSClanAchievements(clanActivity);
			if (config.clanAchievements())
			{
				add(clanAchievements);
			}

			clanMembers = new TempleOSRSClanMembers(plugin, "Members", members);
			if (config.clanMembers())
			{
				add(clanMembers);
			}
		});

		completed();

		revalidate();
		repaint();
	}

	private void open()
	{
		String clanID = clanLookup.getText();
		if (Strings.isNullOrEmpty(clanID))
		{
			return;
		}

		if (!isNumeric.matcher(clanID).matches())
		{
			error();
			return;
		}

		loading();

		String clanPageURL = HOST + CLAN_PAGE + clanID;
		SwingUtilities.invokeLater(() -> LinkBrowser.browse(clanPageURL));

		completed();
	}

	private void verify()
	{
		if (client == null)
		{
			return;
		}

		if (Strings.isNullOrEmpty(config.clanKey()))
		{
			return;
		}

		ClanSettings localClan = client.getClanSettings();

		if (localClan == null)
		{
			return;
		}

		final int confirmation = JOptionPane.showOptionDialog(verifyButton, "This will sync the fetched clan's TempleOSRS members-list to all members in the current account's clan.",
			"Are you sure?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
			null, new String[]{
				"Yes",
				"No"
			}, "No");

		if (confirmation != JOptionPane.YES_OPTION)
		{
			return;
		}

		List<String> clanList = new ArrayList<>();

		for (ClanMember member : localClan.getMembers())
		{
			clanList.add(format(member.getName()));
		}

		loading();

		String clanID = clanLookup.getText();

		new Thread(() -> {
			try
			{
				postClanMembersAsync(clanID, config.clanKey(), clanList).whenCompleteAsync((result, err) -> response(clanID, result, err));
			}
			catch (Exception ignored)
			{
				error();
			}
		}).start();

	}

	private void response(String clanID, TempleOSRSSync response, Throwable err)
	{
		if (Objects.isNull(response) || Objects.nonNull(err) || response.error)
		{
			syncingError();
			return;
		}
		reload(clanID);
	}

	private void reset()
	{
		clanLeaders = null;
		clanMembers = null;
		clanAchievements = null;

		removeAll();
		add(fetchLayout);
		add(errorPanel);

		repaint();
		revalidate();
	}

	private void error()
	{
		searchButton.setEnabled(true);
		clanButton.setEnabled(true);
		verifyButton.setEnabled(true);
		clanLookup.setIcon(IconTextField.Icon.ERROR);
		clanLookup.setEditable(true);
		reset();
	}

	private void syncingError()
	{
		searchButton.setEnabled(true);
		clanButton.setEnabled(true);
		verifyButton.setEnabled(true);
		clanLookup.setIcon(IconTextField.Icon.ERROR);
		clanLookup.setEditable(true);
	}

	private void loading()
	{
		searchButton.setEnabled(false);
		clanButton.setEnabled(false);
		verifyButton.setEnabled(false);
		clanLookup.setIcon(IconTextField.Icon.LOADING);
		clanLookup.setEditable(false);
	}

	private void completed()
	{
		searchButton.setEnabled(true);
		clanButton.setEnabled(true);
		verifyButton.setEnabled(true);
		clanLookup.setIcon(IconTextField.Icon.SEARCH);
		clanLookup.setEditable(true);
	}

	private String format(String text)
	{
		return text.replace('\u00A0', ' ');
	}
}
