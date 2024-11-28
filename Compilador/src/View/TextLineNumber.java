package View;

import java.awt.*;
import java.awt.geom.Point2D;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;

public class TextLineNumber extends JPanel {
    private final JTextComponent textComponent;

    public TextLineNumber(JTextComponent textComponent) {
        this.textComponent = textComponent;
        textComponent.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                repaint();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                repaint();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        FontMetrics fontMetrics = g.getFontMetrics();
        int lineHeight = fontMetrics.getHeight();
        Rectangle clip = g.getClipBounds();

        int startOffset = textComponent.viewToModel2D(new Point(0, clip.y));
        int endOffset = textComponent.viewToModel2D(new Point(0, clip.y + clip.height));

        Element root = textComponent.getDocument().getDefaultRootElement();
        int startLine = root.getElementIndex(startOffset);
        int endLine = root.getElementIndex(endOffset);

        int y = clip.y + fontMetrics.getAscent();
        for (int line = startLine; line <= endLine; line++) {
            String lineNumber = String.valueOf(line + 1);
            g.drawString(lineNumber, 0, y);
            y += lineHeight;
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(30, Integer.MAX_VALUE); // ancho fijo, altura dinámica
    }
}
