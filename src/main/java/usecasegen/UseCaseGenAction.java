package usecasegen;

import javax.swing.JFrame;

import com.change_vision.jude.api.inf.ui.IPluginActionDelegate;
import com.change_vision.jude.api.inf.ui.IWindow;


public class UseCaseGenAction implements IPluginActionDelegate{

	public Object run(IWindow window) throws UnExpectedException {

		JFrame win = new Progress(window);
		win.setBounds(450 , 450 , 400 , 100);
		win.setVisible(true);
		
	    return null;
	}


    
}
