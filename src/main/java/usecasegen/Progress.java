package usecasegen;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;

import com.change_vision.jude.api.inf.ui.IWindow;


public class Progress extends JFrame implements ActionListener,PropertyChangeListener {
	

	private JProgressBar bar = new JProgressBar(0 , 100);
	private JLabel label = new JLabel();
	private UseCaseGenTask task; 
	
	public Progress(IWindow _window) {
		getContentPane().add(bar , BorderLayout.NORTH);
		getContentPane().add(label , BorderLayout.SOUTH);
		
		task = new UseCaseGenTask(_window);
        task.addPropertyChangeListener(this);
        task.execute();
        
	}
	public void actionPerformed(ActionEvent e) {
		
	}
	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress" == evt.getPropertyName()) {
            int progress = (Integer) evt.getNewValue();
            bar.setValue(progress);
            label.setText(task.getClassProgressCount() + " / " + task.getClassSize() + " : " + task.getClassProgressName());
            
            if(progress == 100){
            	label.setText("Done");
            }
        } 
	}
}


