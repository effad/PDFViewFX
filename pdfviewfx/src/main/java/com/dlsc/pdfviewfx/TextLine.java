package com.dlsc.pdfviewfx;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDFontDescriptor;
import org.apache.pdfbox.text.TextPosition;

import javafx.geometry.Rectangle2D;

/** TextLine represents one line of text in a pdf file. */
class TextLine {
    private List<TextPosition> textPositions = new ArrayList<TextPosition>(64);
    private double top = Double.MAX_VALUE;
    private double bottom = 0;
    
    TextLine(TextPosition textPosition) {
        addPosition(textPosition);
    }

    /** Add textPosition or create new line.
     * @param textPosition The text position to add to this line
     * @return this line, if the given text position fit into this line or a new TextLine object
     */
    TextLine add(TextPosition textPosition) {
        TextLine result = this;
        if (isOnThisLine(textPosition)) {
            addPosition(textPosition);
        } else {
            result = new TextLine(textPosition);
        }
        return result;
    }
    
    boolean containsHeight(double y) {
        return top <= y && y <= bottom;
    }

    double getBottom() {
        return bottom;
    }

    double getTop() {
        return top;
    }

    Optional<Rectangle2D> getRectangle(double startx, double endx) {
        if (startx > endx) {
            double tmp = endx;
            endx = startx;
            startx = tmp;
        }
        TextPosition start = null;
        TextPosition end = null;
        for (TextPosition textPosition : textPositions) {
            double middle = textPosition.getX() + textPosition.getWidth() / 2;
            if (start == null && startx <= middle) {
                start = textPosition;
            }
            if (middle <= endx) {
                end = textPosition;
            }
        }
        Rectangle2D rectangle = null;
        if (start != null && end != null) {
            rectangle = new Rectangle2D(start.getX(), top, end.getEndX() - start.getX(), bottom - top);
        }
        return Optional.ofNullable(rectangle);
    }

    private void addPosition(TextPosition textPosition) {
        PDFont font = textPosition.getFont();
        float fontSize = textPosition.getFontSizeInPt();
        PDFontDescriptor fontDescriptor = font.getFontDescriptor();
        float descenderHeight = Math.abs((fontDescriptor.getDescent() / 1000.0f) * fontSize);
        float ascenderHeight = Math.abs((fontDescriptor.getAscent() / 1000.0f) * fontSize);

        top = Math.min(top, textPosition.getYDirAdj() - ascenderHeight + descenderHeight);
        bottom = Math.max(bottom, textPosition.getYDirAdj() + descenderHeight);
        textPositions.add(textPosition);
    }
    
    private boolean isOnThisLine(TextPosition textPosition) {
        TextPosition lastTextPosition = textPositions.getLast();
        float tolerance = lastTextPosition.getHeight() / 2; 
        return Math.abs(lastTextPosition.getYDirAdj() - textPosition.getYDirAdj()) < tolerance;  
    }

    @Override
    public String toString() {
        return "TextLine [top: " + top + ", bottom: " + bottom + ", text: " +
            textPositions.stream().map(TextPosition::getUnicode).collect(Collectors.joining());
    }
}
