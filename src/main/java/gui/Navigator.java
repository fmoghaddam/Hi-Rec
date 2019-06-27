package gui;

import javafx.scene.Parent;

import java.util.ArrayList;
import java.util.List;

public class Navigator {
    private List<Parent> pages;
    private int currentPageIndex;

    public Navigator() {
        pages = new ArrayList<>();
        currentPageIndex = 0;
    }

    public void addPage(Parent page) {
        pages.add(page);
    }

    public Parent getCurrentPage() {
        return pages.get(currentPageIndex);
    }

    public Parent getNextPage() {
        if (currentPageIndex + 1 < pages.size()) {
            currentPageIndex = currentPageIndex + 1;
        }
        return pages.get(currentPageIndex);
    }

    public Parent getPreviousPage() {
        if (currentPageIndex - 1 >= 0) {
            currentPageIndex = currentPageIndex - 1;
        }
        return pages.get(currentPageIndex);
    }

}
