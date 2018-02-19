/**
 * Created by chenqian on 2017/12/9.
 */

package GUI;

import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import GrammaticalParser.GrammerTreeUtil;
import GrammaticalParser.TreeNode;
import SemanticAnalysis.*;
import LexParser.*;

public class MainForm implements IMainForm{
	private static final long serialVersionUID = 1L;

	//记录当前的后台线程
	private static Thread curThread;

	//frame控件
	private static JFrame frame;

	//textpane控件
	public static JTextPane editor;
	public static JTextPane midTextPane;
	public static JTextPane lexTextPane;
	public static JTextPane gramTextPane;
	public static JTextPane lineNumberView;
	public static JTextPane consoleTextPane;

	//scrollpane控件
	public static JScrollPane treeView;
	public static JScrollPane editorView;
	public static JScrollPane midView;
	public static JScrollPane lexView;
	public static JScrollPane consoleView;

	//管理撤销功能
	public static UndoManager um;

	//菜单栏
	private static JMenuBar jmb;

	//菜单
	private static JMenu fileMenu;
	private static JMenu runMenu;

	//子菜单
	public static JMenuItem openItem;
	public static JMenuItem saveItem;
	public static JMenuItem runItem;
	public static JMenuItem newItem;
	public static JMenuItem undoItem;
	public static JMenuItem redoItem;

	//标签
	private static JLabel label0;
	private static JLabel label1;
	private static JLabel label2;
	private static JLabel label3;
	private static JLabel label4;

	//显示行号
	private static LineNumberHeaderView lheader;

	//记录当前文件名
	public static String cur_filename;

	//记录控制台输入的text
	public static String console_text;


	//主体方法
	public void build_start() {
		initialize();
		createUIComponents();
	}

	//初始化控件
	private void initialize() {
		frame = new JFrame("CMMParser");

		editor = new JTextPane();
		midTextPane = new JTextPane();
		lexTextPane = new JTextPane();
		gramTextPane = new JTextPane();
		consoleTextPane = new JTextPane();

		editorView = new JScrollPane();
		treeView = new JScrollPane();
		midView = new JScrollPane();
		lexView = new JScrollPane();
		consoleView = new JScrollPane();

		um = new UndoManager();

		jmb = new JMenuBar();

		fileMenu = new JMenu("文件");
		runMenu = new JMenu("编辑");

		openItem = new JMenuItem("打开");
		saveItem = new JMenuItem("保存");
		runItem = new JMenuItem("运行");
		newItem = new JMenuItem("新建");
		undoItem = new JMenuItem("撤销");
		redoItem = new JMenuItem("恢复");

		lheader = new LineNumberHeaderView();

		cur_filename = "";
		console_text = "";

		label0 = new JLabel("编辑面板");
		label1 = new JLabel("中间代码");
		label2 = new JLabel("词法分析");
		label3 = new JLabel("语法分析");
		label4 = new JLabel("控制台");
	}

	//将控件添加到container中
	private void createUIComponents() {
		Container container = frame.getContentPane();

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(750, 750);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setLayout(null);
		frame.setVisible(true);

		setMenu();
		setLineHeader();
		setTextPane();
		setLabel();
		//setButton();
		setView();

		container.add(jmb);
		//container.add(jbtn);
		//container.add(editor);

		container.add(label0);
		container.add(label1);
		container.add(label2);
		container.add(label3);
		container.add(label4);

		//container.add(lheader);

		container.add(editorView);
		container.add(midView);
		container.add(lexView);
		container.add(treeView);
		container.add(consoleView);
		//subcontainer.add(jtree);
		//jtree.setVisible(false);
	}

	//设置menu的各项属性
	private void setMenu() {

		openItem.addActionListener(new MenuItemListener());
		saveItem.addActionListener(new MenuItemListener());
		newItem.addActionListener(new MenuItemListener());
		undoItem.addActionListener(new MenuItemListener());
		redoItem.addActionListener(new MenuItemListener());
		runItem.addActionListener(new MenuItemListener());


		fileMenu.add(newItem);
		fileMenu.add(openItem);
		fileMenu.add(saveItem);
		runMenu.add(undoItem);
		runMenu.add(redoItem);
		runMenu.add(runItem);

		jmb.add(fileMenu);
		jmb.add(runMenu);
		jmb.setBounds(0, 0, 750, 30);
	}

	//设置scrollpane的各项属性
	private void setView(){
		editorView.setBounds(5, 60, 370, 645);
		midView.setBounds(560, 60, 180, 330);
		lexView.setBounds(380, 420, 175, 285);
		treeView.setBounds(560, 420, 180, 285);
		consoleView.setBounds(380, 60, 175, 330);
	}

	//设置label的各项属性
	private void setLabel() {
		label0.setBounds(5, 30, 370, 30);

		label1.setBounds(565, 30, 360, 30);

		label2.setBounds(380, 390, 180, 30);

		label3.setBounds(565, 390, 180, 30);

		label4.setBounds(380, 30, 100, 30);
	}

	//设置显示行号的各项属性
	private void setLineHeader() {
		editorView.setRowHeaderView(lheader);
	}

	//设置textpane的各项属性
	private void setTextPane() {
		//editor.setBackground(new Color(39,40,34));
		editor.getDocument().addUndoableEditListener(new UndoableEditListener() {
			@Override
			public void undoableEditHappened(UndoableEditEvent e) {
				um.addEdit(e.getEdit());
			}
		});

		editor.setCaretColor(Color.BLACK);
		editor.setFont(new Font(editor.getFont().getName(),editor.getFont().getStyle(),20) );
		editor.getDocument().addDocumentListener(new SyntaxHighLighter(editor));
		editorView.setViewportView(editor);
		//editor.setBounds(40, 60, 330, 645);

		//midTextPane.setBounds(380, 60, 360, 330);
		midTextPane.setEditable(false);
		midView.setViewportView(midTextPane);
		//midTextPane.setBackground(new Color(220,220,220));

		//lexTextPane.setBounds(380, 420, 175, 285);
		lexTextPane.setEditable(false);
		lexView.setViewportView(lexTextPane);
		//lexTextPane.setBackground(new Color(39,40,34));

		consoleTextPane.setEditable(false);
		consoleTextPane.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == 10){
					String[] tmp = consoleTextPane.getText().split("\r\n");
					console_text = tmp[tmp.length - 1];
					curThread.resume();
					//System.out.println(console_text);
				}else {
					super.keyPressed(e);
				}
			}
		});
		consoleView.setViewportView(consoleTextPane);

		gramTextPane.setBounds(560, 420, 180, 285);
		gramTextPane.setEditable(false);
		treeView.setViewportView(gramTextPane);
		//gramTextPane.setBackground(new Color(39,40,34));
	}

	//获取编辑面板中的text
	public String[] getEditorText() {
		String data[] = editor.getText().split("\r\n");
		return data;
	}

	//设置编辑面板中的text
	public void setEditorText(String data) {
		editor.setText(data);
	}

	//设置中间代码面板中的text
	public void setMidText(ArrayList<TAC> data) {
		String midText = "";
		for (TAC tac: data) {
			midText += tac.toString();
			midText += "\n";
		}
		midTextPane.setText(midText);
	}

	//设置词法分析面板中的text
	public void setLexText(ArrayList<Token> data) {
		String lexText = "";
		for (Token token: data) {
			lexText += "{" + token.getRow() + " " + token.getCol() + " " + token.getType().toString() + " " + token.getValue() + "}";
			lexText += "\n";
		}
		lexTextPane.setText(lexText);
	}

	//设置语法分析面板中的内容
	public void setGramText(TreeNode tree) {
		//gramTextPane.setVisible(false);
		treeView.setVisible(true);
		treeView.setViewportView(null);
		GrammerTreeUtil treeOut = new GrammerTreeUtil(tree);
		JTree jtree = treeOut.drawTree();
		treeView.setViewportView(jtree);
		//frame.getContentPane().add(treeView);
	}

	//readFunc调用的子函数，实现具体功能
	private String getConVal(){
	curThread = Thread.currentThread();
		try {
			curThread.suspend();
		}catch (Exception e){
			e.printStackTrace();
		}
		consoleTextPane.setEditable(false);
		return console_text;
	}

	//遇到read调用，作为外界的接口
	public String readFuc(){
		consoleTextPane.setEditable(true);
		String value = getConVal();
		value += "\r\n";
		consoleTextPane.setEditable(false);
		console_text = "";
		return value;
	}

	//遇到write调用
	public void writeFunc(String data){
		String line_data = null;
		SimpleAttributeSet attrset = new SimpleAttributeSet();
		line_data = data;
		line_data += "\r\n";

		Document doc = consoleTextPane.getDocument();
		try {
			doc.insertString(doc.getLength(), line_data, attrset);
		}catch (Exception e){
			e.printStackTrace();
		}
		consoleTextPane.setCaretPosition(doc.getLength());
	}

	//输出错误到控制台时调用
	public void reportErr(String data){
		String line_data = null;
		SimpleAttributeSet attrset = new SimpleAttributeSet();
		line_data = data;
		line_data += "\r\n";
		StyleConstants.setForeground(attrset, Color.RED);

		Document doc = consoleTextPane.getDocument();
		try {
			doc.insertString(doc.getLength(), line_data, attrset);
		}catch (Exception e){
			e.printStackTrace();
		}
		consoleTextPane.setCaretPosition(doc.getLength());
	}
}