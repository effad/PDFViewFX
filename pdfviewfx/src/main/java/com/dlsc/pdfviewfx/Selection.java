package com.dlsc.pdfviewfx;

import java.util.List;

import javafx.geometry.Rectangle2D;

/** Selection represents a textual selection within the PDF file. */
public class Selection {
    private int pageNumber;
    private final List<Rectangle2D> marker;

    /**
     * Constructs a new selection.
     *
     * @param pageNumber  The number of the page the selection lives in
     * @param marker The list of rectangles to be highlighted (in PDF coordinates)
     */
    public Selection(int pageNumber, List<Rectangle2D> marker) {
        this.pageNumber = pageNumber;
        this.marker = marker;
    }

    public List<Rectangle2D> getMarker() {
        return marker;
    }

    public List<Rectangle2D> getScaledMarker(double scale) {
        return marker.stream()
            .map(m -> new Rectangle2D(m.getMinX() * scale, m.getMinY() * scale, m.getWidth() * scale, m.getHeight() * scale))
            .toList();
    }

    public int getPageNumber() {
        return pageNumber;
    }
    
    @Override
    public String toString() {
        return "[selection page: " + pageNumber + ", rects: " + marker.size() + "]";
    }
}
