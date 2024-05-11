import org.w3c.dom.ls.LSOutput;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.*;

public class TimeTable extends JFrame implements ActionListener {

	private JPanel screen = new JPanel(), tools = new JPanel();
	private JButton tool[];
	private JTextField field[];
	private CourseArray courses;
	private Color CRScolor[] = {Color.RED, Color.GREEN, Color.BLACK};
	private int m = Integer.MAX_VALUE;
	private int steps_all = 0;
//	JLabel patternCounter = new JLabel("Patterns Left: " + m);

	AutoAssociator HopfieldNetwork;

	
	public TimeTable() {
		super("Dynamic Time Table");
		setSize(700, 800);
		setLayout(new FlowLayout());
		
		screen.setPreferredSize(new Dimension(400, 800));
		add(screen);
		
		setTools();
		add(tools);
		
		setVisible(true);
	}
	
	public void setTools() {
		String capField[] = {"Slots:", "Courses:", "Clash File:", "Iters:", "Shift:"};
		field = new JTextField[capField.length];
		
		String capButton[] = {"Load", "Start", "Step", "Continue", "Print", "Exit", "TestVals"};
		tool = new JButton[capButton.length];
		
		tools.setLayout(new GridLayout(2 * capField.length + capButton.length, 1));
		
		for (int i = 0; i < field.length; i++) {
			tools.add(new JLabel(capField[i]));
			field[i] = new JTextField(5);
			tools.add(field[i]);
		}
		
		for (int i = 0; i < tool.length; i++) {
			tool[i] = new JButton(capButton[i]);
			tool[i].addActionListener(this);
			tools.add(tool[i]);
		}


//		tools.add(patternCounter);
		
		field[0].setText("17");
		field[1].setText("381");
		field[2].setText("lse-f-91.stu");
		field[3].setText("100");
		field[4].setText("17");
	}
	
	public void draw() {
		Graphics g = screen.getGraphics();
		int width = Integer.parseInt(field[0].getText()) * 10;
		for (int courseIndex = 1; courseIndex < courses.length(); courseIndex++) {
			g.setColor(CRScolor[courses.status(courseIndex) > 0 ? 0 : 1]);
			g.drawLine(0, courseIndex, width, courseIndex);
			g.setColor(CRScolor[CRScolor.length - 1]);
			g.drawLine(10 * courses.slot(courseIndex), courseIndex, 10 * courses.slot(courseIndex) + 10, courseIndex);
		}

	}
	
	private int getButtonIndex(JButton source) {
		int result = 0;
		while (source != tool[result]) result++;
		return result;
	}
	
	public void actionPerformed(ActionEvent click) {

		int source = getButtonIndex((JButton) click.getSource()), i, min, step, clashes;
		switch (source) {
		case 0:
			int slots = Integer.parseInt(field[0].getText());
			courses = new CourseArray(Integer.parseInt(field[1].getText()) + 1, slots);
			courses.readClashes(field[2].getText());

			steps_all = 0;
			m = (int)(0.139 * Integer.parseInt(field[1].getText()));
			// Initialize Hopfield Network with the number of courses
			HopfieldNetwork = new AutoAssociator(Integer.parseInt(field[1].getText()) + 1);

			draw();
			break;
		case 1:
			min = Integer.MAX_VALUE;
			step = 0;
			for (i = 1; i < courses.length(); i++) courses.setSlot(i, 0);
			
			for (int iteration = 1; iteration <= Integer.parseInt(field[3].getText()); iteration++) {
				courses.iterate(Integer.parseInt(field[4].getText()));
				draw();
				clashes = courses.clashesLeft();
				if (clashes < min) {
					min = clashes;
					step = iteration;
				}
			}
			steps_all += Integer.parseInt(field[3].getText());
			System.out.println("Shift = " + field[4].getText() + "\tMin clashes = " + min + "\tat step " + step);

//			System.out.println(" Slot \tCourses \tClashes");
//			for (int j = 0; j < Integer.parseInt(field[0].getText()); j++) {
//				System.out.println(Integer.toString(j) + " \t\t\t" + Integer.toString(temp[0]) + " \t\t\t" + Integer.toString(temp[1]));
//			}

			for (int j = 0; j < Integer.parseInt(field[0].getText()); j++) {
				int[] temp = courses.slotStatus(j);
				int threshold = Integer.parseInt(field[1].getText()) / Integer.parseInt(field[0].getText());
				// Pattern Gathering
				if (temp[0] >= threshold/2 && temp[1] == 0 && m > 0) {
					m--;
					int[] pattern = courses.getTimeSlot(j);
//					System.out.println(Arrays.toString(pattern) + "\n");
					HopfieldNetwork.train(pattern);
				}
			}

			if (m == 0) {
				System.out.println("Yay, training is done");
			}

			System.out.println("Patterns remain to find is " + m);

			setVisible(true);
			break;
		case 2:
			courses.iterate(Integer.parseInt(field[4].getText()));
			steps_all++;
			draw();
			break;
		case 3:
			min = Integer.MAX_VALUE;
			step = 0;

			for (int iteration = 1; iteration <= Integer.parseInt(field[3].getText()); iteration++) {
				courses.iterate(Integer.parseInt(field[4].getText()));
				draw();
				clashes = courses.clashesLeft();
				if (clashes < min) {
					min = clashes;
					step = iteration + steps_all;
				}
			}
			steps_all += Integer.parseInt(field[3].getText());
			System.out.println("Shift = " + field[4].getText() + "\tMin clashes = " + min + "\tat step " + step);

			for (int j = 0; j < Integer.parseInt(field[0].getText()); j++) {
				int[] temp = courses.slotStatus(j);
				int threshold = Integer.parseInt(field[1].getText()) / Integer.parseInt(field[0].getText());
				// Pattern Gathering
				if (temp[0] >= threshold/2 && temp[1] == 0 && m > 0) {
					m--;
					int[] pattern = courses.getTimeSlot(j);
//					System.out.println(Arrays.toString(pattern) + "\n");
					HopfieldNetwork.train(pattern);
				}
			}

			if (m == 0) {
				System.out.println("Training is done");
				// FIXME : Yay Training is done
			}

			System.out.println("Patterns remain to find is " + m);


			setVisible(true);
			break;
		case 4:
			System.out.println("Exam\tSlot\tClashes");
			for (i = 1; i < courses.length(); i++)
				System.out.println(i + "\t" + courses.slot(i) + "\t" + courses.status(i));
			break;
		case 5:
			System.exit(0);
			break;
		case 6:
			break;
		}
	}

	public static void main(String[] args) {
		new TimeTable();
	}
}
