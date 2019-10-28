package banrep.poc;

import com.borland.silktest.jtf.BrowserBaseState;
import org.junit.Before;
import org.junit.Test;
import com.borland.silktest.jtf.xbrowser.DomTextField;
import com.borland.silktest.jtf.common.BrowserType;
import com.borland.silktest.jtf.common.types.MouseButton;
import com.borland.silktest.jtf.common.types.Point;
import com.borland.silktest.jtf.xbrowser.DomButton;
import com.borland.silktest.jtf.Desktop;

public class PruebaRecord {

	private Desktop desktop = new Desktop();

	@Before
	public void baseState() {
		// Go to web page 'http://km.choucairtesting.com/login/index.php'
		BrowserBaseState baseState = new BrowserBaseState();
		baseState.setUrl("http://km.choucairtesting.com/login/index.php");	
		baseState.setBrowserType(BrowserType.GoogleChrome);
		baseState.execute(desktop);
	}

	@Test
	public void Inicial() {
		desktop.<DomTextField>find("km_choucairtesting_com.BrowserWindow.username").click(MouseButton.LEFT,
				new Point(67, 5));
		desktop.<DomTextField>find("km_choucairtesting_com.BrowserWindow.username").click(MouseButton.LEFT,
				new Point(67, 5));
		desktop.<DomTextField>find("km_choucairtesting_com.BrowserWindow.username").typeKeys("ocastro<Tab>");
		desktop.<DomTextField>find("km_choucairtesting_com.BrowserWindow.password").click(MouseButton.LEFT,
				new Point(53, 12));
		desktop.<DomTextField>find("km_choucairtesting_com.BrowserWindow.password").typePasswordKeys("9kR4EsCnAg==");
		desktop.<DomButton>find("km_choucairtesting_com.BrowserWindow.submit").click(MouseButton.LEFT,
				new Point(34, 24));
	}

}