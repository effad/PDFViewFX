package com.dlsc.pdfviewfx;

import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

/** SelectionExtractor allows to get selections for a given page of the pdf file. */
class SelectionExtractor  extends PDFTextStripper {

    private int pageNumber = -1;
    private List<TextLine> lines = new ArrayList<TextLine>(64);
    private TextLine currentLine;

    public SelectionExtractor(int pageNumber) {
        setStartPage(pageNumber+1);
        setEndPage(pageNumber+1);
        setSortByPosition(true);
        this.pageNumber = pageNumber;
    }

    @Override
    protected void writeString(String text, List<TextPosition> positions) {
        for (TextPosition textPosition : positions) {
            if (currentLine == null) {
                currentLine = new TextLine(textPosition);
                lines.add(currentLine);
            } else {
                TextLine oldLine = currentLine;
                currentLine = currentLine.add(textPosition);
                if (currentLine != oldLine) {
                    lines.add(currentLine);
                }
            }
        }
    }
    
    public int getPageNumber() {
        return pageNumber;
    }
    
    public List<Rectangle2D> getSelectionRectangles(Point2D start, Point2D end) {
        List<Rectangle2D> selectionRectangles = new ArrayList<>();
        TextLine startLine = getFirstLineAt(start.getY());
        TextLine endLine = getLastLineAt(end.getY());
        if (startLine == endLine) {
            startLine.getRectangle(start.getX(), end.getX()).ifPresent(selectionRectangles::add);
        } else {
            startLine.getRectangle(start.getX(), Double.MAX_VALUE).ifPresent(selectionRectangles::add);
            int startIdx = lines.indexOf(startLine) + 1;
            int endIdx = lines.indexOf(endLine);
            for (int idx = startIdx; idx < endIdx; idx++) {
                TextLine line = lines.get(idx);
                line.getRectangle(Double.MIN_VALUE, Double.MAX_VALUE).ifPresent(selectionRectangles::add);
            }
            endLine.getRectangle(Double.MIN_VALUE, end.getX()).ifPresent(selectionRectangles::add);
        }

        return selectionRectangles;
    }

    private TextLine getFirstLineAt(double y) {
        return lines.stream()
            .filter(line -> line.containsHeight(y))
            .findFirst()
            .orElse(lines.getFirst());            
    }
    
    private TextLine getLastLineAt(double y) {
        return lines.reversed().stream()
            .filter(line -> line.containsHeight(y))
            .findFirst()
            .orElse(lines.getLast());            
    }
    
}