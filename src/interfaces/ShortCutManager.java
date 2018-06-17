package interfaces;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

import utilities.Controller;

public class ShortCutManager implements KeyEventDispatcher {

	private Controller controler;

	private boolean busy = false;
	private boolean enableCsvSaving = false;

	public ShortCutManager(Controller controler) {
		this.controler = controler;
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {

		if (!busy) {
			busy = true;

			switch (e.getKeyCode()) {
			case KeyEvent.VK_F1:
				controler.loadPer();
				break;
			case KeyEvent.VK_F2:
				controler.savePer();
				break;
			case KeyEvent.VK_F3:
				controler.loadCsv();
				break;
			case KeyEvent.VK_F4:
				if (enableCsvSaving) {
					controler.saveCsv();
				}
				break;
			default:
				break;
			}

			busy = false;
		}

		return false;
	}

	public void enableCsvSaving(boolean b) {
		enableCsvSaving = b;
	}

}
