package geogebra.web.gui.toolbar;

import geogebra.common.awt.Color;
import geogebra.common.main.AbstractApplication;
import geogebra.web.awt.GeneralPath;
import geogebra.web.javax.swing.JPopupMenu;
import geogebra.web.main.Application;

import java.util.ArrayList;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

public class ModeToggleMenu extends MenuBar{

	private static final long serialVersionUID = 1L;
	ModeToggleButtonGroup bg;
	private MyJToggleButton tbutton, mouseOverButton;
	private JPopupMenu popMenu;
	private ArrayList<MenuItem> menuItemList;

//	private ActionListener popupMenuItemListener;
	private Application app;
	int size;

	final static Color bgColor = Color.white;

	public ModeToggleMenu(Application app, ToolBar toolbar,
			ModeToggleButtonGroup bg) {
		super(true);
		this.app = app;
		this.bg = bg;
//		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		tbutton = new MyJToggleButton(this);
//		tbutton.setAlignmentY(BOTTOM_ALIGNMENT);
		addItem(tbutton);

		popMenu = new JPopupMenu();
//		popMenu.setBackground(bgColor);
		menuItemList = new ArrayList<MenuItem>();
		//popupMenuItemListener = new MenuItemListener();
		size = 0;
	}

	public int getToolsCount() {
		return size;
	}

//	public MyJToggleButton getJToggleButton() {
//		return tbutton;
//	}
//
	public boolean selectMode(int mode) {
		String modeText = mode + "";

		for (int i = 0; i < size; i++) {
			MenuItem mi = menuItemList.get(i);
			// found item for mode?
			AbstractApplication.debug(mi.getElement().getAttribute("mode"));
			//if (mi.getActionCommand().equals(modeText)) {
			if (mi.getElement().getAttribute("mode").equals(modeText)) {
				selectMenuItem(mi);
				return true;
			}
		}
		return false;
	}

	public int getFirstMode() {
		if (menuItemList == null || menuItemList.size() == 0) {
			return -1;
		}
		MenuItem mi = menuItemList.get(0);
		return Integer.parseInt(mi.getElement().getAttribute("mode"));
	}

	/*
	 * This method has the same functionality as the 
	 * geogebra.gui.toolbar.ModeToggleMenu.selectItem(JMenuItem mi)
	 */
	private void selectMenuItem(MenuItem mi) {
		// check if the menu item is already selected
		if (tbutton.isSelected()
				&& tbutton.getElement().getAttribute("mode") == mi.getElement().getAttribute("mode")) {
			return;
		}

//		tbutton.setIcon(mi.getIcon());
		tbutton.setText(app.getToolName(Integer.parseInt(mi
				.getElement().getAttribute("mode"))));
		tbutton.getElement().setAttribute("mode",mi.getElement().getAttribute("mode"));
		tbutton.setSelected(true);
		// tbutton.requestFocus();
	}

	public void addMode(int mode) {
		// add menu item to popup menu
		Command tempCommand = new Command() {
		      public void execute() {
		          Window.alert("You selected a menu item.");
		        }
		      };
		MenuItem mi = new MenuItem(app.getToolName(mode), tempCommand);
		//mi.setFont(app.getPlainFont());
		//mi.setBackground(bgColor);

		// tool name as text
		//mi.setText(app.getToolName(mode));

		//Icon icon = app.getModeIcon(mode);
		String actionText = Integer.toString(mode);
		//mi.setIcon(icon);
		mi.getElement().setAttribute("mode", actionText);	
		//mi.addActionListener(popupMenuItemListener);

		//popMenu.add(mi);
		menuItemList.add(mi);
		size++;

		if (size == 1) {
			// init tbutton
			//tbutton.setIcon(icon);
			tbutton.getElement().setAttribute("mode", actionText);

			// tooltip: tool name and tool help
			//tbutton.setToolTipText(app.getToolTooltipHTML(mode));
			tbutton.setText(app.getToolName(mode));
			
			// add button to button group
			bg.add(tbutton);
		}
	}
	
	
	public MyJToggleButton getButton() {
		return tbutton;
	}

//	/**
//	 * Removes all modes from the toggle menu. Used for the temporary
//	 * perspective.
//	 * 
//	 * @author Florian Sonner
//	 * @version 2008-10-22
//	 */
//	public void clearModes() {
//		popMenu.removeAll();
//		menuItemList.clear();
//		size = 0;
//	}
//
//	public void addSeparator() {
//		popMenu.addSeparator();
//	}
//
//	// sets new mode when item in popup menu is selected
//	private class MenuItemListener implements ActionListener {
//
//		public void actionPerformed(ActionEvent e) {
//			JMenuItem item = (JMenuItem) e.getSource();
//			selectItem(item);
//			tbutton.doClick();
//		}
//	}
//
//	public void setMouseOverButton(MyJToggleButton bt) {
//		mouseOverButton = bt;
//		repaint();
//	}
//
//	public JToggleButton getMouseOverButton() {
//		return mouseOverButton;
//	}
//
//	public void mouseOver() {
//		// popup menu is showing
//		JPopupMenu activeMenu = geogebra.javax.swing.JPopupMenu.getImpl(bg.getActivePopupMenu());
//		if (activeMenu != null && activeMenu.isShowing()) {
//			setPopupVisible(true);
//		}
//	}
//
//	// shows popup menu
//	public void setPopupVisible(boolean flag) {
//		if (flag) {
//			bg.setActivePopupMenu(new geogebra.javax.swing.JPopupMenu(popMenu));
//			if (popMenu.isShowing())
//				return;
//			Point locButton = tbutton.getLocationOnScreen();
//			Component component = SwingUtilities.getRootPane(tbutton);
//			if (component == null)
//				component = app.getMainComponent(); // if geogebrapanel is
//													// inside an awt window
//			Point locApp = component.getLocationOnScreen();
//			popMenu.show(component, locButton.x - locApp.x, locButton.y
//					- locApp.y + tbutton.getHeight());
//		} else {
//			popMenu.setVisible(false);
//		}
//
//		tbutton.repaint();
//	}
//
//	public boolean isPopupShowing() {
//		return popMenu.isShowing();
//	}
//
//	public void setMode(int mode) {
//		app.setMode(mode);
//	}
//
//}
//
class MyJToggleButton extends MenuItem /*implements  MouseListener,
		MouseMotionListener, ActionListener*/ {

	private static final long serialVersionUID = 1L;

	private int BORDER = 6;
	private int iconWidth, iconHeight;
	private GeneralPath gp;
	private boolean showToolTipText = true;
	boolean popupTriangleHighlighting = false;
	boolean popupTriangleClicked = false;
	private ModeToggleMenu menu;

	private boolean isSelected = false;

//	private static final Color arrowColor = AwtFactory.prototype.newColor(0, 0, 0, 130);
	// private static final Color selColor = new Color(166, 11, 30,150);
	// private static final Color selColor = new Color(17, 26, 100, 200);
//	private static final Color selColor = AwtFactory.prototype.newColor(0, 0, 153, 200);
//	private static final BasicStroke selStroke = new BasicStroke(3f);

	//private Timer showMenuTimer;

	MyJToggleButton(ModeToggleMenu menu) {
		super("?",new Command() {
			public void execute() {
				Window.alert("You selected a menu item!");
				}
			}
		);
		this.menu = menu;
		
		

		// add own listeners
//		addMouseListener(this);
//		addMouseMotionListener(this);
//		addActionListener(this);
	}
//
//	@Override
//	public String getToolTipText() {
//		if (showToolTipText) {
//			return super.getToolTipText();
//		}
//		return null;
//	}
//
//	// set mode
//	public void actionPerformed(ActionEvent e) {
//
//		// don't select mode if popup triangle clicked
//		if (!popupTriangleClicked || !menu.isPopupShowing()) {
//			menu.setMode(Integer.parseInt(e.getActionCommand()));
//		}
//	}
//
//	@Override
//	public void setIcon(Icon icon) {
//		super.setIcon(icon);
//		iconWidth = icon.getIconWidth();
//		iconHeight = icon.getIconHeight();
//		BORDER = (int) Math.round(icon.getIconWidth() * 0.1875); // 6 pixel
//																	// border
//																	// for 32
//																	// pixel
//																	// icon
//		Dimension dim = new Dimension(iconWidth + 2 * BORDER, iconHeight + 2
//				* BORDER);
//		setPreferredSize(dim);
//		setMinimumSize(dim);
//		setMaximumSize(dim);
//	}
//
//	@Override
//	public void paint(Graphics g) {
//		Graphics2D g2 = (Graphics2D) g;
//		Stroke oldStroke = g2.getStroke();
//
//		super.paint(g2);
//
//		if (isSelected()) {
//			g2.setColor(selColor);
//			g2.setStroke(selStroke);
//			g2.drawRect(BORDER - 1, BORDER - 1, iconWidth + 1, iconHeight + 1);
//
//			g2.setStroke(oldStroke);
//		}
//
//		// draw little arrow (for popup menu)
//		if (menu.size > 1) {
//			if (gp == null)
//				initPath();
//			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//					RenderingHints.VALUE_ANTIALIAS_ON);
//
//			if (menu.getMouseOverButton() == this
//					&& (popupTriangleHighlighting || menu.isPopupShowing())) {
//				g2.setColor(Color.red);
//				g2.fill(gp);
//				g2.setColor(Color.black);
//				g2.draw(gp);
//			} else {
//				g2.setColor(Color.white);
//				g2.fill(gp);
//				g2.setColor(arrowColor);
//				g2.draw(gp);
//			}
//		}
//	}
//
//	private void initPath() {
//		gp = new GeneralPath();
//		int x = BORDER + iconWidth + 2;
//		int y = BORDER + iconHeight + 1;
//
//		if (iconWidth > 20) {
//			gp.moveTo(x - 6, y - 5);
//			gp.lineTo(x, y - 5);
//			gp.lineTo(x - 3, y);
//		} else {
//			gp.moveTo(x - 4, y - 3);
//			gp.lineTo(x, y - 3);
//			gp.lineTo(x - 2, y);
//		}
//		gp.closePath();
//	}
//
//	private boolean popupTriangleClicked(int x, int y) {
//		popupTriangleClicked = (menu.size > 1 && y > iconHeight - 4);
//		return popupTriangleClicked;
//	}
//
//	public void mouseClicked(MouseEvent e) {
//		if (!menu.isPopupShowing() && e.getClickCount()==2) {
//			menu.setPopupVisible(true);
//		}
//	}
//
//	public void mouseEntered(MouseEvent arg0) {
//		menu.setMouseOverButton(this);
//	}
//
//	public void mousePressed(MouseEvent e) {
//		if (!menu.isPopupShowing() && popupTriangleClicked(e.getX(), e.getY())) {
//			menu.setPopupVisible(true);
//			this.getModel().setArmed(false);
//		} else {
//			// Display the menu after a specific amount of time as well, start
//			// a timer for this. The mouseReleased method stops this timer,
//			// which ensures that the menu is not displayed if the user stopped
//			// to press this button.
//			if (showMenuTimer == null) {
//				showMenuTimer = new Timer(1000, new ActionListener() {
//					public void actionPerformed(ActionEvent e) {
//						menu.setPopupVisible(true);
//					}
//				});
//				showMenuTimer.setRepeats(false);
//			}
//
//			showMenuTimer.start();
//		}
//	}
//
//	public void mouseExited(MouseEvent arg0) {
//		menu.setMouseOverButton(null);
//		if (popupTriangleHighlighting) {
//			popupTriangleHighlighting = false;
//			repaint();
//		}
//
//		// Stop the timer to show the menu if the user is no longer
//		// hovering over this button.
//		if (showMenuTimer != null && showMenuTimer.isRunning()) {
//			showMenuTimer.stop();
//		}
//	}
//
//	public void mouseReleased(MouseEvent e) {
//		if (menu.isPopupShowing() && !popupTriangleClicked(e.getX(), e.getY())) {
//			menu.setPopupVisible(false);
//		}
//
//		// Stop the timer to show the menu if the user released his mouse. In
//		// case
//		// the timer already executed his action this has no effect anyway.
//		if (showMenuTimer != null && showMenuTimer.isRunning()) {
//			showMenuTimer.stop();
//		}
//	}
//
//	@Override
//	public void doClick() {
//		super.doClick();
//		if (!hasFocus())
//			requestFocusInWindow();
//	}
//
//	public void mouseDragged(MouseEvent e) {
//		if (popupTriangleClicked(e.getX(), e.getY()))
//			menu.setPopupVisible(true);
//	}
//
//	public void mouseMoved(MouseEvent e) {
//		menu.mouseOver();
//		showToolTipText = !menu.isPopupShowing();
//
//		// highlight popup menu triangle
//		if (menu.size > 1
//				&& popupTriangleHighlighting != popupTriangleClicked(e.getX(),
//						e.getY())) {
//			popupTriangleHighlighting = !popupTriangleHighlighting;
//			repaint();
//		}

	public boolean isSelected() {
	    return isSelected;
    }
	
	public void setSelected(boolean flag){
		isSelected = flag;
	}
}


//	class MyMenuItem extends MenuItem{
//
//		/**
//		 * 
//		 */
//		String actionCommand = "0";
//
//		/**
//		 * @param text
//		 * @param com
//		 */
//		public MyMenuItem(String text, Command com) {    
//	        super(text, com);
//			
//        }
//		
//		public void setActionCommand(String text){
//			actionCommand = text;
//		}
//		
//		public String getActionCommand(){
//			return actionCommand;
//		}
//		
//	}

}
