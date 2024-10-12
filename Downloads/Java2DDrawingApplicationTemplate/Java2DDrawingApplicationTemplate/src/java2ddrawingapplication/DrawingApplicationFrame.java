/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package java2ddrawingapplication;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.AncestorListener;

/**
 * @author Elliot Wright
 * @purpose This project uses the Swing toolkit to create a drawing application.
 */
public class DrawingApplicationFrame extends JFrame
{

    // Create the panels for the top of the application. One panel for each
    // line and one to contain both of those panels.
    private JPanel topPanel;
    private JPanel line1;
    private JPanel line2;
    
    // create the widgets for the firstLine Panel.
    private JLabel label1;
    private JComboBox shapes;
    private JButton color1;
    private JButton color2;
    private JButton undo;
    private JButton clear;
    
    //create the widgets for the secondLine Panel.
    private JLabel label2;
    private JCheckBox filled;
    private JCheckBox gradient;
    private JCheckBox dashed;
    private JLabel label3;
    private JSpinner width;
    private JLabel label4;
    private JSpinner length;
    
    // Variables for drawPanel.
    private DrawPanel drawPanel;
    private ArrayList<MyShapes> shapesList = new ArrayList<>();
    private String selectedShape;
    private Color colorA, colorB;
    private boolean isFilled, useGradient, useDash;
    private int lineWidth;
    private int dashLength;
    float[] dash = new float[1];
    private BasicStroke stroke;
    private Paint paint;
    private Point startPoint;
    private Point endPoint;
    
    // add status label
  private JLabel statusLabel;
  
    // Constructor for DrawingApplicationFrame
    public DrawingApplicationFrame()
    {
        // add widgets to panels
        topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(2,1));
        line1 = new JPanel();
        line2 = new JPanel();
        line1.setBackground(Color.CYAN);
        line2.setBackground(Color.CYAN);
        
        // firstLine widgets
        label1 = new JLabel("Shape:");
        String boxList[] = {"Line", "Oval", "Rectangle"};
        shapes = new JComboBox(boxList);
        color1 = new JButton("1st Color");
        color2 = new JButton("2nd Color");
        undo = new JButton("Undo");
        clear = new JButton("Clear");
        line1.add(label1);
        line1.add(shapes);
        line1.add(color1);
        line1.add(color2);
        line1.add(undo);
        line1.add(clear);
        
        // secondLine widgets
        label2 = new JLabel("Options:");
        filled = new JCheckBox("Filled");
        gradient = new JCheckBox("Use Gradient");
        dashed = new JCheckBox("Dashed");
        label3 = new JLabel("Line Width:");
        width = new JSpinner(new SpinnerNumberModel(10, 1, 99, 1));
        label4 = new JLabel("Dash Length:");
        length = new JSpinner(new SpinnerNumberModel(10, 1, 99, 1));
        line2.add(label2);
        line2.add(filled);
        line2.add(gradient);
        line2.add(dashed);
        line2.add(label3);
        line2.add(width);
        line2.add(label4);
        line2.add(length);
        
        // add top panel of two panels
        topPanel.add(line1);
        topPanel.add(line2);

        // add topPanel to North, drawPanel to Center, and statusLabel to South
        setTitle("Java 2D Drawings");
        
        add(topPanel, BorderLayout.NORTH);
        
        drawPanel = new DrawPanel();
        add(drawPanel, BorderLayout.CENTER);
        
        statusLabel = new JLabel();
        statusLabel.setBackground(Color.GRAY);
        JPanel bottom = new JPanel();
        bottom.add(statusLabel);
        add(bottom, BorderLayout.SOUTH);
        
        pack();
        
        //add listeners and event handlers

        color1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                colorA = JColorChooser.showDialog(DrawingApplicationFrame.this, "Choose Color", colorA);
            }
        });

        color2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                colorB = JColorChooser.showDialog(DrawingApplicationFrame.this, "Choose Color", colorB);
            }
        });

        undo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!shapesList.isEmpty()) {
                    shapesList.remove(shapesList.size() - 1);
                    drawPanel.repaint();
                }
            }
        });

        clear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                shapesList.clear();
                drawPanel.repaint();
            }
        });

    }

    // Create event handlers, if needed

    // Create a private inner class for the DrawPanel.
    private class DrawPanel extends JPanel
    {

        public DrawPanel()
        {
            setBackground(Color.WHITE);
            MouseHandler mouse = new MouseHandler();
            addMouseListener(mouse);
            addMouseMotionListener(mouse);
        }

        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            //loop through and draw each shape in the shapes arraylist
            for (MyShapes shape : shapesList) {
                shape.draw(g2d);
            }
        }


        private class MouseHandler extends MouseAdapter implements MouseMotionListener
        {

            public void mousePressed(MouseEvent event)
            {
                lineWidth = (Integer)width.getValue();
                dashLength = (Integer)length.getValue();
                float[] dash = new float[1];
                dash[0] = dashLength;
                selectedShape = (String)shapes.getSelectedItem();
                isFilled = filled.isSelected();
                useGradient = gradient.isSelected();
                useDash = dashed.isSelected();
                startPoint = event.getPoint();
                endPoint = event.getPoint();
                
                if (useGradient) {
                    paint = new GradientPaint(0, 0, colorA, 50, 50, colorB, true);
                } else {
                    paint = colorA;
                }
                if (useDash)
                {
                    stroke = new BasicStroke(lineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, dash, 0);
                } else
                {
                    stroke = new BasicStroke(lineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                }
                
                if (selectedShape == "Line") {
                    shapesList.add(new MyLine(startPoint, endPoint, paint, stroke));
                } else if (selectedShape == "Oval"){
                    shapesList.add(new MyOval(startPoint, endPoint, paint, stroke, isFilled));
                } else if (selectedShape == "Rectangle"){
                    shapesList.add(new MyRectangle(startPoint, endPoint, paint, stroke, isFilled));
                }
                
                drawPanel.repaint();
            }
 
            public void mouseReleased(MouseEvent event)
            {
                drawPanel.repaint();
            }

            @Override
            public void mouseDragged(MouseEvent event)
            {
                drawPanel.repaint();
                shapesList.get(shapesList.size()-1).setEndPoint(event.getPoint());
            }

            @Override
            public void mouseMoved(MouseEvent event)
            {
                statusLabel.setText(String.format("(%d,%d)", event.getX(), event.getY()));
            }
        }

    }
}
