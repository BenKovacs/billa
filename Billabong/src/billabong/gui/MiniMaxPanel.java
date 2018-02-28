package billabong.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import billabong.ai.MiniMax;
import billabong.ai.model.MiniMaxNode;
import billabong.model.TurnState;

public class MiniMaxPanel extends JPanel {

	private JButton btnCalcMove;
	private JButton btnMove;
	private DefaultMutableTreeNode rootNode;
	private JTree tree;
	private JPanel panel;
	private JLabel lblTimeTaken;
	private int nodeCount;
	private JToolBar toolBar_1;
	private MainApp mainApp;
	private JButton btnTurn;

	/**
	 * Create the panel.
	 */
	public MiniMaxPanel(MainApp app) {
		ActionListener listener = app;
		this.mainApp = app;
		setPreferredSize(new Dimension(363, 500));
		setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane);

		tree = new JTree();
		scrollPane.setViewportView(tree);

		rootNode = new DefaultMutableTreeNode("Root");
		tree.setModel(new DefaultTreeModel(rootNode));
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addTreeSelectionListener(new SelectionListener());

		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		add(toolBar, BorderLayout.NORTH);

		btnCalcMove = new JButton("Step 1 depth");
		btnCalcMove.setActionCommand("calculate");
		btnCalcMove.addActionListener(listener);
		toolBar.add(btnCalcMove);

		btnMove = new JButton("Move");
		btnMove.setActionCommand("move");
		btnMove.addActionListener(listener);
		toolBar.add(btnMove);
		
		btnTurn = new JButton("Turn");
		btnTurn.setActionCommand("turn");
		btnTurn.addActionListener(listener);
		toolBar.add(btnTurn);

		panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel.setPreferredSize(new Dimension(100, 30));
		add(panel, BorderLayout.SOUTH);

		toolBar_1 = new JToolBar();
		add(toolBar_1, BorderLayout.SOUTH);

		lblTimeTaken = new JLabel("Time taken: ");
		toolBar_1.add(lblTimeTaken);
		toolBar_1.add(ProgressBarPanel.getInstance());

		setState(TurnState.Idle, 0);
	}

	public void setState(TurnState state, long time) {
		btnCalcMove.setEnabled(false);
		btnMove.setEnabled(false);

		if (time != 0) {
			lblTimeTaken.setText("Time taken: " + time + " ms. Node count: " + nodeCount);
		} else {
			lblTimeTaken.setText("Time taken: ");
		}

		switch (state) {
		case Idle:
			btnCalcMove.setEnabled(true);
			break;
		case MovesFound:
			btnCalcMove.setEnabled(true);
			break;
		case DepthComplete:
			btnMove.setEnabled(true);;
			break;
		default:
			break;

		}

		updateTree();

	}

	private void updateTree() {
		DefaultTreeModel tm = (DefaultTreeModel) tree.getModel();

		rootNode.removeAllChildren();
		tm.reload();
		nodeCount = 0;
		addChildren(rootNode, MiniMax.getInstance().getRoot());
		tm.reload();
		MiniMax.getInstance().setNodeCount(nodeCount);

	}

	private void addChildren(DefaultMutableTreeNode treeNode, MiniMaxNode mmNode) {
		if (mmNode.move == null) {
			treeNode.setUserObject(mmNode);
		} else {
			treeNode.setUserObject(mmNode);
		}
		for (MiniMaxNode child : mmNode.getChildren()) {
			DefaultMutableTreeNode childTreeNode = new DefaultMutableTreeNode(child);
			DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
			model.insertNodeInto(childTreeNode, treeNode, treeNode.getChildCount());
			nodeCount++;
			// treeNode.add(childTreeNode);
			addChildren(childTreeNode, child);
		}

	}

	class SelectionListener implements TreeSelectionListener {

		public void valueChanged(TreeSelectionEvent se) {
			JTree tree = (JTree) se.getSource();
			DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			if (selectedNode != null){

				Object uo = selectedNode.getUserObject();
				if (uo instanceof MiniMaxNode){
					MiniMaxNode node = (MiniMaxNode) uo;
					mainApp.showBoard(node.getBoard());
				}else{
					mainApp.resetBoard();
				}
			}else{
				mainApp.resetBoard();
			}
		}
	}

	public void setDebugMode(boolean debugMode) {
		btnCalcMove.setEnabled(debugMode);
		btnMove.setEnabled(debugMode);
	}
}
