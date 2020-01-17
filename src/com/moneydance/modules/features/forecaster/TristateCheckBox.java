package com.moneydance.modules.features.forecaster;

import java.awt.event.ActionListener;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.event.ChangeListener;

public class TristateCheckBox extends JCheckBox {
	static final long serialVersionUID = 0;

	/** This is a type-safe enumerated type */
	public static class State {
		private State() {
		}
	}

	public final State NOT_SELECTED = new State();
	public final State SELECTED = new State();
	public final static State DONT_CARE = new State();

	private final TristateDecorator model;

	public TristateCheckBox(String text, Icon icon, State initial) {
		super(text, icon);
		// set the model to the adapted model
		model = new TristateDecorator(getModel());
		setModel(model);
		setState(initial);
	}

	// Constructor types:
	public TristateCheckBox(String text, State initial) {
		this(text, null, initial);
	}

	public TristateCheckBox(String text) {
		this(text, DONT_CARE);
	}

	public TristateCheckBox() {
		this(null);
	}


	/**
	 * Set the new state to either SELECTED, NOT_SELECTED or DONT_CARE. If state
	 * == null, it is treated as DONT_CARE.
	 */
	public void setState(State state) {
		model.setState(state);
	}

	/**
	 * Return the current state, which is determined by the selection status of
	 * the model.
	 */
	public State getState() {
		//System.err.println("setState");
		return model.getState();
	}


	/**
	 * Exactly which Design Pattern is this? Is it an Adapter, a Proxy or a
	 * Decorator? In this case, my vote lies with the Decorator, because we are
	 * extending functionality and "decorating" the original model with a more
	 * powerful model.
	 */
	private class TristateDecorator implements ButtonModel {
		private final ButtonModel other;

		private TristateDecorator(ButtonModel other) {
			this.other = other;
		}

		private void setState(State state) {
		   	//System.err.println("decorator setState");
			if (state == NOT_SELECTED) {
				other.setArmed(false);
				setPressed(false);
				setSelected(false);
			} else if (state == SELECTED) {
				other.setArmed(false);
				setPressed(false);
				setSelected(true);
			} else { // either "null" or DONT_CARE
				other.setArmed(true);
				setPressed(true);
				setSelected(false);
			}
		}

		/**
		 * The current state is embedded in the selection / armed state of the
		 * model.
		 *
		 * We return the SELECTED state when the checkbox is selected but not
		 * armed, DONT_CARE state when the checkbox is selected and armed (grey)
		 * and NOT_SELECTED when the checkbox is deselected.
		 */
		private State getState() {
		   	//System.err.println("decorator setState");
			if (isSelected() && !isArmed()) {
				// normal black tick
				return SELECTED;
			} else if (isSelected() && isArmed()) {
				// don't care grey tick
				return DONT_CARE;
			} else {
				// normal deselected
				return NOT_SELECTED;
			}
		}

		/** Filter: No one may change the armed status except us. */
		@Override
		public void setArmed(boolean b) {
		}

		/**
		 * We disable focusing on the component when it is not enabled.
		 */
		@Override
		public void setEnabled(boolean b) {
			setFocusable(b);
			other.setEnabled(b);
		}

		/**
		 * All these methods simply delegate to the "other" model that is being
		 * decorated.
		 */
		@Override
		public boolean isArmed() {
			return other.isArmed();
		}

		@Override
		public boolean isSelected() {
			return other.isSelected();
		}

		@Override
		public boolean isEnabled() {
			return other.isEnabled();
		}

		@Override
		public boolean isPressed() {
			return other.isPressed();
		}

		@Override
		public boolean isRollover() {
			return other.isRollover();
		}

		@Override
		public int getMnemonic() {
			return other.getMnemonic();
		}

		@Override
		public String getActionCommand() {
			return other.getActionCommand();
		}

		@Override
		public Object[] getSelectedObjects() {
			return other.getSelectedObjects();
		}

		@Override
		public void setSelected(boolean b) {
			other.setSelected(b);
		}

		@Override
		public void setPressed(boolean b) {
			other.setPressed(b);
		}

		@Override
		public void setRollover(boolean b) {
			other.setRollover(b);
		}

		@Override
		public void setMnemonic(int key) {
			other.setMnemonic(key);
		}

		@Override
		public void setActionCommand(String s) {
			other.setActionCommand(s);
		}

		@Override
		public void setGroup(ButtonGroup group) {
			other.setGroup(group);
		}

		@Override
		public void addActionListener(ActionListener l) {
			other.addActionListener(l);
		}

		@Override
		public void removeActionListener(ActionListener l) {
			other.removeActionListener(l);
		}

		@Override
		public void addItemListener(ItemListener l) {
			other.addItemListener(l);
		}

		@Override
		public void removeItemListener(ItemListener l) {
			other.removeItemListener(l);
		}

		@Override
		public void addChangeListener(ChangeListener l) {
			other.addChangeListener(l);
		}

		@Override
		public void removeChangeListener(ChangeListener l) {
			other.removeChangeListener(l);
		}

	}
}
