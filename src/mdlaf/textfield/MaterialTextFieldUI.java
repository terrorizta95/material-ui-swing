package mdlaf.textfield;

import mdlaf.MaterialColors;
import mdlaf.MaterialFonts;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextFieldUI;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class MaterialTextFieldUI extends BasicTextFieldUI implements FocusListener, PropertyChangeListener {

	private Color focusedBackground;
	private Color unfocusedBackground;
	private Color focusedSelectionBackground;
	private Color unfocusedSelectionBackground;

	public static ComponentUI createUI (final JComponent c) {
		return new MaterialTextFieldUI ();
	}

	@Override
	public void installUI (JComponent c) {
		super.installUI (c);

		JTextField textField = (JTextField) c;
		textField.setOpaque (false);
		textField.setBorder (BorderFactory.createEmptyBorder (5, 2, 10, 0));
		textField.setBackground (MaterialColors.LIGHT_BLUE);
		textField.setFont (MaterialFonts.REGULAR);

		this.focusedBackground = textField.getBackground ();
		this.unfocusedBackground = MaterialColors.LIGHT_GRAY;

		this.focusedSelectionBackground = MaterialColors.bleach (focusedBackground, 0.3f);
		this.unfocusedSelectionBackground = unfocusedBackground;
	}

	@Override
	protected void installListeners () {
		getComponent ().addFocusListener (this);
		getComponent ().addPropertyChangeListener (this);
	}

	@Override
	protected void installKeyboardActions () {
		super.installKeyboardActions ();

		Action selectAll = new AbstractAction () {
			@Override
			public void actionPerformed (ActionEvent e) {
				getComponent ().selectAll ();
			}
		};

		Action delete = new AbstractAction () {
			@Override
			public void actionPerformed (ActionEvent e) {
				if (getComponent ().getSelectedText () == null) {
					int pos = getComponent ().getCaretPosition () - 1;

					if (pos >= 0) {
						getComponent ().select (pos, pos + 1);
						getComponent ().replaceSelection ("");
					}
				}
				else {
					getComponent ().replaceSelection ("");
				}
			}
		};

		Action left = new AbstractAction () {
			@Override
			public void actionPerformed (ActionEvent e) {
				getComponent ().setCaretPosition (Math.max (0, getComponent ().getCaretPosition () - 1));
			}
		};

		Action right = new AbstractAction () {
			@Override
			public void actionPerformed (ActionEvent e) {
				getComponent ().setCaretPosition (Math.min (getComponent ().getText ().length (), getComponent ().getCaretPosition () + 1));
			}
		};

		// note getMenuShortcutKeyMask() is deprecated in Java 10 - change to getMenuShortcutKeyMaskEx()
		getComponent ().getInputMap ().put (KeyStroke.getKeyStroke (KeyEvent.VK_A, Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask ()), "selectAll");
		getComponent ().getInputMap ().put (KeyStroke.getKeyStroke (KeyEvent.VK_BACK_SPACE, 0), "delete");
		getComponent ().getInputMap ().put (KeyStroke.getKeyStroke (KeyEvent.VK_LEFT, 0), "left");
		getComponent ().getInputMap ().put (KeyStroke.getKeyStroke (KeyEvent.VK_RIGHT, 0), "right");

		getComponent ().getActionMap ().put ("selectAll", selectAll);
		getComponent ().getActionMap ().put ("delete", delete);
		getComponent ().getActionMap ().put ("left", left);
		getComponent ().getActionMap ().put ("right", right);
	}

	@Override
	public void paintSafely (Graphics g) {
		JTextField c = (JTextField) getComponent ();
		Graphics2D g2 = (Graphics2D) g;

		g2.addRenderingHints (new RenderingHints (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));

		Color lineColor;

		if (getComponent ().hasFocus ()) {
			lineColor = focusedBackground;
			c.setSelectionColor (focusedSelectionBackground);
		}
		else {
			lineColor = unfocusedBackground;
			c.setSelectionColor (unfocusedSelectionBackground);
		}

		int x = getComponent ().getInsets ().left;
		int y = getComponent ().getInsets ().top;
		int w = getComponent ().getWidth () - getComponent ().getInsets ().left - getComponent ().getInsets ().right;

		g.setColor (lineColor);
		getComponent ().setBackground (lineColor);
		g.fillRect (x, c.getHeight () - y, w, 2);

		super.paintSafely (g);
	}

	@Override
	public void paintBackground (final Graphics g) {
		super.paintBackground (g);
	}

	@Override
	public void focusGained (FocusEvent e) {
		getComponent ().setBackground (focusedBackground);
	}

	@Override
	public void focusLost (FocusEvent e) {
		getComponent ().setBackground (unfocusedBackground);
	}

	@Override
	public String getPropertyPrefix () {
		return "TextField";
	}

	@Override
	public void propertyChange (PropertyChangeEvent pce) {
		if (pce.getPropertyName ().equals ("background")) {
			Color newColor = (Color) pce.getNewValue ();

			if (!newColor.equals (focusedBackground) && !newColor.equals (unfocusedBackground)) {
				this.focusedBackground = (Color) pce.getNewValue ();
				this.focusedSelectionBackground = MaterialColors.bleach (this.focusedBackground, 0.3f);
			}
		}
	}
}