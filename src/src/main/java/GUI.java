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
		JFrame frame = new JFrame();
		JPanel panel1 = new JPanel();
		JPanel panel2 = new JPanel();
		frame.setLayout(new FlowLayout());
		write = new JLabel("Enter the value to write");
		panel1.add(write);
		
		data = new JTextField(10);
		data.setEditable(true);
		panel1.add(data);
		
		btnread = new JButton("Read Property");
		btnwrite = new JButton("Write Property");
		panel2.add(btnread);
		panel2.add(btnwrite);
		
		btnread.addActionListener(this);
		btnwrite.addActionListener(this);
		
		frame.add(panel1);
		frame.add(panel2);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setTitle("BACnet IT Device");
		frame.setSize(300, 150);
		frame.setVisible(true);
	
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
