package org.geogebra.web.full.gui.toolbarpanel;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.gui.toolbar.ToolbarItem;
import org.geogebra.common.gui.toolcategorization.ToolCategory;
import org.geogebra.common.gui.toolcategorization.ToolCollection;
import org.geogebra.common.gui.toolcategorization.ToolCollectionFactory;
import org.geogebra.web.full.gui.toolbar.ToolButton;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW.ToolTipLinkType;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author judit Content of tools tab of Toolbar panel.
 */
public class Tools extends FlowPanel implements SetLabels {

	/**
	 * Tool categories
	 */
	private ToolCollection toolCollection;
	/**
	 * application
	 */
	private AppW app;
	/**
	 * see {@link ToolsTab}
	 */
	private ToolsTab parentTab;
	/**
	 * move button
	 */
	private StandardButton moveButton;

	private ArrayList<CategoryPanel> categoryPanelList;

	/**
	 * @param app
	 *            application
	 * @param parentTab
	 *            see {@link ToolsTab}
	 */
	public Tools(AppW app, ToolsTab parentTab) {
		this.app = app;
		this.parentTab = parentTab;

		ToolCollectionFactory toolCollectionFactory = app.createToolCollectionFactory();
		toolCollection = toolCollectionFactory.createToolCollection();

		this.addStyleName("toolsPanel");
		buildGui();
	}

	/**
	 * Selects MODE_MOVE as mode and changes visual settings accordingly of
	 * this.
	 */
	public void setMoveMode() {
		app.setMode(EuclidianConstants.MODE_MOVE);
		clearSelectionStyle();
		if (moveButton != null) {
			moveButton.getElement().setAttribute("selected", "true");
		}
	}

	/**
	 * Changes visual settings of selected mode.
	 * 
	 * @param mode
	 *            the mode will be selected
	 */
	public void setMode(int mode) {
		if (mode == EuclidianConstants.MODE_SELECTION_LISTENER) {
			return;
		}
		for (int i = 0; i < getWidgetCount(); i++) {
			Widget w = getWidget(i);
			if (w instanceof CategoryPanel) {
				FlowPanel panelTools = ((CategoryPanel) w).getToolsPanel();
				for (int j = 0; j < panelTools.getWidgetCount(); j++) {
					if ((mode + "").equals(panelTools.getWidget(j).getElement()
							.getAttribute("mode"))) {
						panelTools.getWidget(j).getElement()
								.setAttribute("selected", "true");
					} else {
						panelTools.getWidget(j).getElement()
								.setAttribute("selected", "false");
					}
				}
			}
		}

	}

	/**
	 * @return application
	 */
	public AppW getApp() {
		return app;
	}

	/**
	 * @param moveButton
	 *            floating action move btn
	 */
	private void setMoveButton(StandardButton moveButton) {
		this.moveButton = moveButton;
	}

	/**
	 * Clears visual selection of all tools.
	 */
	private void clearSelectionStyle() {
		for (int i = 0; i < getWidgetCount(); i++) {
			Widget w = getWidget(i);
			if (w instanceof CategoryPanel) {
				FlowPanel panelTools = ((CategoryPanel) w).getToolsPanel();
				for (int j = 0; j < panelTools.getWidgetCount(); j++) {
					panelTools.getWidget(j).getElement()
							.setAttribute("selected", "false");
				}
			}
		}
	}

	/**
	 * Builds the panel of tools.
	 */
	public void buildGui() {
		// clear panel
		this.clear();
		categoryPanelList = new ArrayList<>();
		// decide if custom toolbar or not
		String def = app.getGuiManager().getCustomToolbarDefinition();
		boolean isCustomToolbar = !ToolBar.isDefaultToolbar(def)
				&& !ToolBar.isDefaultToolbar3D(def);
		parentTab.isCustomToolbar = isCustomToolbar;
		// build tools panel depending on if custom or not
		if (!isCustomToolbar) {
			toolCollection.setLevel(app.getSettings().getToolbarSettings()
					.getToolsetLevel());

			for (ToolCategory category : toolCollection.getCategories()) {
				CategoryPanel catPanel = new CategoryPanel(category);
				categoryPanelList.add(catPanel);
				add(catPanel);
			}
		} else {
			this.addStyleName("customToolbar");
			Vector<ToolbarItem> toolbarItems = getToolbarVec(def);
			for (ToolbarItem toolbarItem : toolbarItems) {
				CategoryPanel catPanel = new CategoryPanel(toolbarItem);
				categoryPanelList.add(catPanel);
				add(catPanel);
			}
		}
		setMoveMode();
	}

	/**
	 * @param toolbarString
	 *            string definition of toolbar
	 * @return the vector of groups of tools
	 */
	private Vector<ToolbarItem> getToolbarVec(String toolbarString) {
		Vector<ToolbarItem> toolbarVec;
		try {
			toolbarVec = ToolBar.parseToolbarString(toolbarString);
		} catch (Exception e) {
			toolbarVec = ToolBar.parseToolbarString(ToolBar.getAllTools(app));
		}
		return toolbarVec;
	}

	@Override
	public void setLabels() {
		if (categoryPanelList != null && !categoryPanelList.isEmpty()) {
			for (CategoryPanel categoryPanel : categoryPanelList) {
				categoryPanel.setLabels();
			}
		}
	}

	public ToolCollection getToolCollection() {
		return toolCollection;
	}

	private class CategoryPanel extends FlowPanel implements SetLabels {
		private ToolCategory category;
		private FlowPanel toolsPanel;
		private Label categoryLabel;
		private ArrayList<ToolButton> toolButtonList;

		CategoryPanel(ToolCategory cat) {
			super();
			category = cat;
			initGui();
		}

		CategoryPanel(ToolbarItem toolbarItem) {
			toolsPanel = new FlowPanel();
			toolsPanel.addStyleName("categoryPanel");
			toolButtonList = new ArrayList<>();
			Vector<Integer> tools = toolbarItem.getMenu();
			for (Integer mode : tools) {
				if (app.isModeValid(mode)) {
					addToolButton(mode);
				}
			}
			add(toolsPanel);
		}

		private void addToolButton(Integer mode) {
			ToolButton btn = getToolButton(mode);
			toolButtonList.add(btn);
			toolsPanel.add(btn);
			if (mode == EuclidianConstants.MODE_MOVE) {
				setMoveButton(btn);
			}
		}

		private void initGui() {
			categoryLabel = new Label(category.getLocalizedHeader(app.getLocalization()));
			categoryLabel.setStyleName("catLabel");
			add(categoryLabel);
			AriaHelper.hide(categoryLabel);

			toolsPanel = new FlowPanel();
			toolsPanel.addStyleName("categoryPanel");
			List<Integer> tools = toolCollection.getTools(
					toolCollection.getCategories().indexOf(category));
			toolButtonList = new ArrayList<>();
			ToolBar.parseToolbarString(
					app.getGuiManager().getToolbarDefinition());
			for (Integer tool : tools) {
				addToolButton(tool);
			}
			add(toolsPanel);
		}

		FlowPanel getToolsPanel() {
			return toolsPanel;
		}

		private ToolButton getToolButton(final int mode) {
			final ToolButton btn = new ToolButton(mode, getApp());
			AriaHelper.hide(btn);
			btn.setIgnoreTab();
			btn.addFastClickHandler(new FastClickHandler() {

				@Override
				public void onClick(Widget source) {
					getApp().setMode(mode);
					showTooltip(mode);
					getApp().updateDynamicStyleBars();
				}
			});
			return btn;
		}

		@Override
		public void setLabels() {
			// update label of category header
			categoryLabel.setText(category.getLocalizedHeader(app.getLocalization()));
			// update tooltips of tools
			for (ToolButton toolButton : toolButtonList) {
				toolButton.setLabel();
			}
		}
	}

	private boolean allowTooltips() {
		// allow tooltips for iPad
		boolean isIpad = Window.Navigator.getUserAgent().toLowerCase()
				.contains("ipad");
		return (!Browser.isMobile() || isIpad) && app.showToolBarHelp();
	}

	/**
	 * @param mode
	 *            mode number
	 */
	public void showTooltip(int mode) {
		if (allowTooltips()) {
			ToolTipManagerW.sharedInstance().setBlockToolTip(false);
			ToolTipManagerW.sharedInstance()
					.showBottomInfoToolTip(app.getToolTooltipHTML(mode),
							app.getGuiManager().getTooltipURL(mode),
							ToolTipLinkType.Help, app,
							app.getAppletFrame().isKeyboardShowing());
			ToolTipManagerW.sharedInstance().setBlockToolTip(true);
		}
	}
}
