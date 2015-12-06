package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.vividsolutions.jts.awt.ShapeWriter;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.Polygon;

import controller.tools.Tool;
import model.Robot;
import model.SimModel;

/**
 * View which builds the gui and access the model to retrieve all the object
 * which need to be drawn on the render component.
 * 
 * @author 150021237
 *
 */
public class RenderView {

	private static final int WINDOW_WIDTH = 1100;
	private static final int WINDOW_HEIGHT = 700;
	private static final int FRAMES_PER_SECOND = 40;

	private List<LineSegment> path = Collections.synchronizedList(new ArrayList<LineSegment>());
	private List<Shape> obstacles = new ArrayList<Shape>();

	private JFrame renderFrame;
	private JPanel framePanel;
	private JPanel toolPanel;
	private JComponent renderComponent;
	private SimModel model;
	private List<Tool> tools;
	private String label1 = "";
	private String label2 = "";

	public RenderView(SimModel m, List<Tool> tools) {
		model = m;
		this.tools = tools;

		buildRenderFrame();
		startRenderThread();
	}

	private void buildShapeList() {
		List<Polygon> obs = model.getObstacles();
		if (obstacles.size() != obs.size()) {
			obstacles = new ArrayList<Shape>();
			ShapeWriter sw = new ShapeWriter();
			for (Geometry o : obs) {
				obstacles.add(sw.toShape(o));
			}
		}
	}

	private void startRenderThread() {
		Thread thread = new Thread() {
			public void run() {
				while (true) {
					long startTime = System.currentTimeMillis();
					renderFrame.getContentPane().repaint();
					long elapsed = System.currentTimeMillis() - startTime;

					try {
						long delta = (long) 1000.0 / FRAMES_PER_SECOND;
						Thread.sleep(delta - elapsed);
					} catch (Exception e) {
					}
				}
			}
		};
		thread.setDaemon(true);
		thread.start();
	}

	private void buildRenderFrame() {
		renderFrame = new JFrame(this.getClass().getSimpleName());
		renderFrame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		renderFrame.setLocationRelativeTo(null);
		renderFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		framePanel = new JPanel();
		framePanel.setLayout(new BorderLayout());
		framePanel.requestFocus();
		renderFrame.add(framePanel);

		renderComponent = new JComponent() {
			private static final long serialVersionUID = 2671666226385477214L;

			public void paint(Graphics graphics) {
				Graphics2D g = (Graphics2D) graphics;
				renderComponents(g, this);
			}
		};

		toolPanel = new JPanel();
		toolPanel.setLayout(new BoxLayout(toolPanel, BoxLayout.Y_AXIS));
		Box combos = new Box(BoxLayout.X_AXIS);
		Box btns = new Box(BoxLayout.X_AXIS);
		
		for (Tool t : tools) {
			JComponent c = t.getComponent();
			if(c instanceof JButton) btns.add(c);
			else combos.add(c);
		}
		toolPanel.add(combos);
		toolPanel.add(btns);
		
		framePanel.add(toolPanel, BorderLayout.NORTH);
		framePanel.add(renderComponent, BorderLayout.CENTER);

		renderFrame.setVisible(true);
	}

	public void addMouseListener(MouseListener ml) {
		renderComponent.addMouseListener(ml);
	}

	public void addKeyListener(KeyListener kl) {
		renderComponent.addKeyListener(kl);
	}

	public void addMouseMotionListener(MouseMotionListener mouseMotionListener) {
		renderComponent.addMouseMotionListener(mouseMotionListener);
	}

	public void removeToolListeners(Tool t) {
		renderComponent.removeMouseListener(t);
		renderComponent.removeKeyListener(t);
		renderComponent.removeMouseMotionListener(t);
	}

	/**
	 * Renders all the abstract object on the screen.
	 * @param g
	 * @param c render component
	 */
	private void renderComponents(Graphics2D g, JComponent c) {
		// Background
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, c.getWidth(), c.getHeight());

		// text
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.BLACK);
		g.drawString(label1, renderComponent.getX() + 10, renderComponent.getY() - 20);
		g.drawString(label2, renderComponent.getX() + 10, renderComponent.getY());

		// obstacles
		buildShapeList();
		g.setColor(Color.DARK_GRAY);
		for (Shape s : obstacles) {
			g.fill(s);
		}
		
		// moving obstacles
		for (Robot r : model.getMovingObstacles()) {
			r.draw(g);
		}

		// path
		g.setColor(Color.DARK_GRAY);
		path.forEach((l) -> g.drawLine((int) l.p0.x, (int) l.p0.y, (int) l.p1.x, (int) l.p1.y));

		// Goal
		if (model.isGoalSet())
			drawGoal(g);

		// Robot
		if (model.isRobotSet())
			model.getRobot().draw(g);

	}

	private void drawGoal(Graphics2D g) {
		Coordinate c = model.getGoal();
		int r = SimModel.GOAL_SIZE;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.ORANGE);
		g.fill(new Ellipse2D.Double(c.x - r, c.y - r, 2 * r, 2 * r));
	}

	public void addToPath(LineSegment l) {
		path.add(l);
	}

	public void clearPath() {
		path.clear();
	}

	public void setLabel1(String str) {
		label1 = str;
	}

	public void setLabel2(String str) {
		label2 = str;
	}

	public List<Tool> getTools() {
		return tools;
	}

	public JComponent getFramePanel() {
		return framePanel;
	}
}
