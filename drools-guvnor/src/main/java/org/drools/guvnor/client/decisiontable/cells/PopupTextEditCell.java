package org.drools.guvnor.client.decisiontable.cells;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.TextBox;

/**
 * A Popup Text Editor.
 * 
 * @author manstis
 * 
 */
public class PopupTextEditCell extends AbstractPopupEditCell<String, String> {

	private final TextBox textBox;

	public PopupTextEditCell() {
		super();
		this.textBox = new TextBox();

		// Tabbing out of the TextBox commits changes
		textBox.addKeyDownHandler(new KeyDownHandler() {

			public void onKeyDown(KeyDownEvent event) {
				boolean keyTab = event.getNativeKeyCode() == KeyCodes.KEY_TAB;
				boolean keyEnter = event.getNativeKeyCode() == KeyCodes.KEY_ENTER;
				if (keyTab || keyEnter) {
					commit();
				}
			}

		});
		vPanel.add(textBox);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.cell.client.AbstractCell#render(java.lang.Object,
	 * java.lang.Object, com.google.gwt.safehtml.shared.SafeHtmlBuilder)
	 */
	@Override
	public void render(String value, Object key, SafeHtmlBuilder sb) {
		// Get the view data.
		String viewData = getViewData(key);
		if (viewData != null && viewData.equals(value)) {
			clearViewData(key);
			viewData = null;
		}

		String s = null;
		if (viewData != null) {
			s = viewData;
		} else if (value != null) {
			s = value;
		}
		if (s != null) {
			if (s.length() > 8) {
				sb.append(renderer.render(s.substring(0, 8) + "..."));
			} else {
				sb.append(renderer.render(s));
			}
		}
	}

	// Commit the change
	@Override
	protected void commit() {
		// Hide pop-up
		Element cellParent = lastParent;
		String oldValue = lastValue;
		Object key = lastKey;
		panel.hide();

		// Update values
		String text = textBox.getValue();
		if (text.length() == 0) {
			text = null;
		}
		setViewData(key, text);
		setValue(cellParent, oldValue, key);
		if (valueUpdater != null) {
			valueUpdater.update(text);
		}

	}

	// Start editing the cell
	@Override
	protected void startEditing(final Element parent, String value, Object key) {

		String viewData = getViewData(key);
		String text = (viewData == null) ? value : viewData;
		textBox.setValue(text);

		panel.setPopupPositionAndShow(new PositionCallback() {
			public void setPosition(int offsetWidth, int offsetHeight) {
				panel.setPopupPosition(parent.getAbsoluteLeft() + offsetX,
						parent.getAbsoluteTop() + offsetY);

				// Focus the first enabled control
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {

					public void execute() {
						String text = textBox.getValue();
						textBox.setFocus(true);
						textBox.setCursorPos(text.length());
						textBox.setSelectionRange(0, text.length());
					}

				});
			}
		});

	}
}
