/**
 * Created by chenqian on 2017/12/9.
 */

package GUI;

import GrammaticalParser.GrammerAnalysis;
import GrammaticalParser.TreeNode;
import LexParser.LexAnalysis;
import LexParser.Token;
import SemanticAnalysis.SemanticAnalysis;
import SemanticAnalysis.*;
import ShowResult.GenerateResult;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class MenuItemListener implements ActionListener{
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(MainForm.runItem)){
			Thread runThread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						IMainForm mf = new MainForm();
						String content[] = mf.getEditorText();

						LexAnalysis lexAnalysis = new LexAnalysis(content, mf);
						ArrayList<Token> result = lexAnalysis.analysis();
						mf.setLexText(result);

						MainForm.consoleTextPane.setText("");

						GrammerAnalysis grammerAnalysis = new GrammerAnalysis(result, mf);
						TreeNode root = grammerAnalysis.parse();
						mf.setGramText(root);
						SemanticAnalysis semanticAnalysis = new SemanticAnalysis(root, mf);
						ArrayList<TAC> tacs = semanticAnalysis.analysis();
						mf.setMidText(tacs);
						GenerateResult generateResult = new GenerateResult(mf);
						generateResult.getResult(tacs);
						//semanticAnalysis.output();
					} catch (Exception exception) {
						// TODO Auto-generated catch block
						exception.printStackTrace();
					}
				}
			});
			runThread.start();
		}else if(e.getSource().equals(MainForm.openItem)) {
			JFileChooser jfc = new JFileChooser();
			jfc.setDialogTitle("选择要打开的文件");
			FileNameExtensionFilter filter = new FileNameExtensionFilter("文本文件（*.txt,*.cmm,*.cpp,*.c,*.h）", "txt", "cmm", "cpp", "c", "h");
			jfc.setFileFilter(filter);
			int returnVal = jfc.showOpenDialog(null);
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				File file = jfc.getSelectedFile();
				MainForm.cur_filename = file.getPath();
				MainForm.consoleTextPane.setText("");
				MainForm.editor.setText("");
				MainForm.midTextPane.setText("");
				MainForm.lexTextPane.setText("");
				MainForm.gramTextPane.setVisible(true);
				MainForm.treeView.setViewportView(MainForm.gramTextPane);
				try {
					BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
					String line_data = null;
					while((line_data = br.readLine()) != null) {
						SimpleAttributeSet attrset = new SimpleAttributeSet();
						StyleConstants.setFontSize(attrset,13);
						line_data += "\r\n";

						Document doc = MainForm.editor.getDocument();
						doc.insertString(doc.getLength(), line_data, attrset);
					}
				}catch (Exception exception) {
					exception.printStackTrace();
				}
			}
		}else if(e.getSource().equals(MainForm.saveItem)) {
			if(MainForm.cur_filename != "") {
				File file = new File(MainForm.cur_filename);
				try {
					String data = MainForm.editor.getText();
					BufferedWriter writer = new BufferedWriter (new OutputStreamWriter(new FileOutputStream (file,true),"UTF-8"));
					//FileWriter fw = new FileWriter(file);
					writer.write(data);
					writer.flush();
					writer.close();
				}catch (IOException exception) {
					exception.printStackTrace();
				}
			}else {
				JFileChooser jfc = new JFileChooser();
				jfc.setDialogTitle("选择存放文件夹");
				//jfc.setFileSelectionMode(JFileChooser.);
				int returnVal = jfc.showSaveDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = jfc.getSelectedFile();
					try {
						//file.createNewFile();
						MainForm.cur_filename = file.getPath();

						String data = MainForm.editor.getText();
						FileWriter fw = new FileWriter(file);
						fw.write(data);
						fw.flush();
						fw.close();
					}catch (IOException exception) {
						exception.printStackTrace();
					}

				}
			}
		}else if(e.getSource().equals(MainForm.newItem)) {
			JFileChooser jfc = new JFileChooser();
			jfc.setDialogTitle("选择存放文件夹");
			//jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnVal = jfc.showSaveDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = jfc.getSelectedFile();
				try {
					file.createNewFile();
					MainForm.consoleTextPane.setText("");
					MainForm.cur_filename = file.getPath();
					MainForm.editor.setText("");
					MainForm.midTextPane.setText("");
					MainForm.lexTextPane.setText("");
					MainForm.gramTextPane.setVisible(true);
					MainForm.treeView.setViewportView(MainForm.gramTextPane);
					//System.out.println(MainForm.editor.getDocument().getLength());
				}catch (Exception exception) {
					exception.printStackTrace();
				}
			}
		}else if(e.getSource().equals(MainForm.undoItem)&&MainForm.um.canUndo()) {
			MainForm.um.undo();
			if(MainForm.um.canUndo()) {
				MainForm.um.undo();
				if(MainForm.um.canUndo()) {
					MainForm.um.undo();
				}
			}
		}else if(e.getSource().equals(MainForm.redoItem)&&MainForm.um.canRedo()) {
			MainForm.um.redo();
		}
	}
}
