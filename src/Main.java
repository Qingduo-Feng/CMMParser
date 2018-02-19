package Main;

import java.util.ArrayList;
import java.util.Scanner;
/**
 * Created by qingduo-feng on 2017/10/9.
 */

import GUI.IMainForm;
import GUI.MainForm;
import LexParser.*;
import GrammaticalParser.*;
import SemanticAnalysis.*;

public class Main {
    public static void main(String[] args) throws Exception{
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                IMainForm mf = new MainForm();
                mf.build_start();
            }
        });
//        try {
//            if (args.length != 1) {
//                throw new Exception("invalid parameter");
//            } else {
//                ReadFile rf = new ReadFile();
//                //简单解析
//                String[] readString = rf.readFromFile(args[0]);
//                LexAnalysis lexAnalysis = new LexAnalysis(readString);
//                ArrayList<Token> result = lexAnalysis.analysis();
////                Util.ConsoleOutput(result);
//                GrammerAnalysis grammerAnalysis = new GrammerAnalysis(result);
//                TreeNode root = grammerAnalysis.parse();
//                GrammerTreeUtil treeOutpot = new GrammerTreeUtil(root);
//                treeOutpot.drawTree();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        try {
//			//读取源文件
//			System.out.println("Please input the path of source file:");
//            ReadFile rf = new ReadFile();
//			Scanner scanner = new Scanner(System.in);
//			String fileName = scanner.nextLine();
//            String[] readString = rf.readFromFile(fileName);
//            LexAnalysis lexAnalysis = new LexAnalysis(readString);
//            ArrayList<Token> result = lexAnalysis.analysis();
//            Util.ConsoleOutput(result);
//            GrammerAnalysis grammerAnalysis = new GrammerAnalysis(result);
//            TreeNode root = grammerAnalysis.parse();
////            GrammerTreeUtil treeOutpot = new GrammerTreeUtil(root);
////            treeOutpot.drawTree();
//            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(root);
//            semanticAnalysis.analysis();
//            semanticAnalysis.output();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

    }
}