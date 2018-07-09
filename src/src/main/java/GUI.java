import java.net.URISyntaxException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import javax.swing.*;
public class GUI extends JFrame implements ActionListener {
	private JLabel write;
	private JTextField data;
	private JButton btnread;
	private JButton btnwrite;
	private String tmp = null;
	int num = 0;
	
	Pradeep_Application obj = new Pradeep_Application();
	public GUI() {
		setLayout(new FlowLayout(1));
		write = new JLabel("Enter the value to write");
		add(write);
		
		data = new JTextField(0);
		data.setEditable(true);
		add(data);
		
		btnread = new JButton("Read Property");
		btnwrite = new JButton("Write Property");
		add(btnread);
		add(btnwrite);
		
		btnread.addActionListener(this);
		btnwrite.addActionListener(this);
		
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("BACnet IT Device");
		setSize(300, 200);
		setVisible(true);
	
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == btnread) {
			try {
				obj.read();
			} catch (URISyntaxException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		else if (e.getSource() == btnwrite) {
			try {
				tmp = data.getText();
				num = Integer.parseInt(tmp);
				obj.write(num);
			} catch (URISyntaxException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
}
