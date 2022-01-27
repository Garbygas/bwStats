package com.garby.hypixelstats;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.KeyEvent;

public class ui extends JPanel {

    public static void main(String[] args) {

        //Creating the Frame
        JFrame frame = new JFrame("Hypixel Stats");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 150);
        frame.setResizable(false);


        //Creating the panel at bottom and adding components
        JPanel panel = new JPanel(); // the panel is not visible in output
        JLabel label = new JLabel("Enter Text");
        JTextField tf = new JTextField(16); // accepts upto 16 characters
        JButton send = new JButton("Lookup");
        JButton reset = new JButton("Exit");
        panel.add(label); // Components Added using Flow Layout
        panel.add(tf);
        panel.add(send);
        panel.add(reset);

        // Text Area at the Center
        //JTable table = new JTable(new DefaultTableModel(new Object[]{"Column1", "Column2"}));

        DefaultTableModel model = new DefaultTableModel();
        JTable table = new JTable(model);

        // Create a couple of columns


        model.addColumn("Username");

        model.addColumn("Level");

        model.addColumn("Beds Broken");
        model.addColumn("Games");
        model.addColumn("Wins");
        model.addColumn("Losses");
        model.addColumn("W/L R");
        model.addColumn("F Kills");
        model.addColumn("F Deaths");
        model.addColumn("FK/D R");
        model.addColumn("Kills");
        model.addColumn("Deaths");
        model.addColumn("K/D R");
        setJTableColumnsWidth(table, 1000, 13, 3, 7, 5, 3, 5, 5, 5, 5, 3, 5, 4, 3);


        // Append a row
        table.setPreferredScrollableViewportSize(new Dimension(1000, 90));
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        JScrollPane scrollPane = new JScrollPane(table);

        scrollPane.setPreferredSize(new Dimension(1000, 90));


        //Adding Components to the frame.
        frame.getContentPane().add(BorderLayout.SOUTH, panel);
        frame.getContentPane().add(BorderLayout.EAST, scrollPane);
        frame.pack();
        frame.setVisible(true);

        send.addActionListener(e -> {

            System.out.println("Button Clicked");
            model.addRow(stats.main(tf.getText(), false));
            tf.setText("");


        });

        reset.addActionListener(e -> {
            System.out.println("Exit Clicked");
            System.exit(0);
        });
        tf.registerKeyboardAction(e -> {
            System.out.println("Button Clicked");
            send.doClick();
            tf.setText("");
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public static void setJTableColumnsWidth(JTable table, int tablePreferredWidth,
                                             double... percentages) {
        double total = 0;
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            total += percentages[i];
        }

        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            column.setMinWidth((int)
                    (tablePreferredWidth * (percentages[i] / total)));
        }
    }

}
