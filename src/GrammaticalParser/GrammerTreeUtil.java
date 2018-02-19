package GrammaticalParser;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.tree.TreePath;

public class GrammerTreeUtil {
    private TreeNode root;

    public GrammerTreeUtil(TreeNode root){
        this.root = root;
    }

    public JTree drawTree(){
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("program");

        ArrayList<TreeNode> nodes = root.getNextLayer();
        for (TreeNode node: nodes){
            rootNode.add(DFS(node));
        }

        final JTree tree = new JTree(rootNode);
        //JFrame f = new JFrame("Grammer Tree");
//        gramTextPane.setSize(600, 600);
//        f.setVisible(true);
        return tree;
    }

    private DefaultMutableTreeNode DFS(TreeNode fatherNode){
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(fatherNode.toString());
        ArrayList<TreeNode> nodes = fatherNode.getNextLayer();
        if (nodes != null) {
            //还需要继续遍历
            for (TreeNode node: nodes) {
                rootNode.add(DFS(node));
            }
            return rootNode;
        }
        else{
            return rootNode;
        }
    }

}
