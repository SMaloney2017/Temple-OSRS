package com.templeosrs.ui.clans;

import com.google.common.base.Strings;
import com.templeosrs.util.TempleOSRSClan;
import static com.templeosrs.util.TempleOSRSService.CLAN_PAGE;
import static com.templeosrs.util.TempleOSRSService.HOST;
import static com.templeosrs.util.TempleOSRSService.fetchClanAsync;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;
import java.util.regex.Pattern;
import javax.inject.Inject;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.IconTextField;
import net.runelite.client.util.LinkBrowser;
import static org.pushingpixels.substance.internal.utils.LazyResettableHashMap.reset;

public class TempleOSRSClans extends PluginPanel
{
	private final Pattern isNumeric = Pattern.compile("-?\\d+(\\.\\d+)?");

	public final IconTextField clanLookup;

	private  JButton searchButton;

	private JButton clanButton;
	@Inject
	public TempleOSRSClans()
	{

		setBackground(ColorScheme.DARKER_GRAY_COLOR);

		JPanel layoutPanel = new JPanel();
		layoutPanel.setLayout(new BoxLayout(layoutPanel, BoxLayout.Y_AXIS));
		layoutPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		JPanel fetchPlayer = new JPanel();
		fetchPlayer.setLayout(new BoxLayout(fetchPlayer, BoxLayout.Y_AXIS));
		fetchPlayer.setBorder(new EmptyBorder(5, 5, 0, 5));
		fetchPlayer.setBackground(ColorScheme.DARK_GRAY_HOVER_COLOR);

		clanLookup = buildTextField();
		fetchPlayer.add(clanLookup);

		JPanel buttons = buildFetchButtons();
		fetchPlayer.add(buttons);

		layoutPanel.add(fetchPlayer);

		add(layoutPanel);
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

		searchButton = createNewButton("Search", "Search for clan by ID (found in TempleOSRS Clan-page url)");
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

	private void rebuild(String ClanID, TempleOSRSClan result, Throwable err)
	{
		if (!clanLookup.getText().equals(ClanID))
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
		//update info
		//update leaders
		//update members
		completed();
	}

	private void open()
	{
		String clanID = clanLookup.getText();
		if (Strings.isNullOrEmpty(clanID))
		{
			return;
		}
		else if (clanID.length() > 12)
		{
			error();
			return;
		}

		loading();

		String clanPageURL = HOST + CLAN_PAGE + clanID;
		SwingUtilities.invokeLater(() -> LinkBrowser.browse(clanPageURL));

		completed();
	}

	private void completed()
	{
		searchButton.setEnabled(true);
		clanButton.setEnabled(true);
		clanLookup.setIcon(IconTextField.Icon.SEARCH);
		clanLookup.setEditable(true);
	}

	private void loading()
	{
		searchButton.setEnabled(false);
		clanButton.setEnabled(false);
		clanLookup.setIcon(IconTextField.Icon.LOADING);
		clanLookup.setEditable(false);
	}

	private void error()
	{
		searchButton.setEnabled(false);
		clanButton.setEnabled(false);
		clanLookup.setIcon(IconTextField.Icon.ERROR);
		clanLookup.setEditable(false);
	}
}
