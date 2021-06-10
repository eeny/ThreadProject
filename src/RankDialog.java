import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class RankDialog extends JDialog {

    JTable table;
    DefaultTableModel model;

    public RankDialog(JFrame f, String ran) {
        super(f,ran);
        this.setLayout(null);
        this.setSize(300,400);
        this.setLocation(800, 390);

        String header[] = {"유저명","점수"};
        String contents[][] ={};

        model = new DefaultTableModel(contents, header);
        table = new JTable(model);

        table.setAutoCreateRowSorter(true);
        TableRowSorter tablesorter = new TableRowSorter(table.getModel());
        table.setRowSorter(tablesorter);

        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(10, 10, 260, 320);


        File file = new File("rank.txt");
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            String l = null;

            //  String tmp="<html>";
            String[] str = null;
            while ((l = br.readLine()) != null) {
                System.out.println(l);
                str = l.split("/");
                // tmp+=str[0]+"="+str[1]+"="+str[2]+"<br>";
                model.addRow(str);
            }


        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

            }
        }
        this.add(sp);
        this.setVisible(true);
    }
}
