package gui;

import javafx.fxml.Initializable;
import javafx.scene.Parent;
import util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Navigator {
    private List<Pair<Parent, Initializable>> pages;
    private int currentPageIndex;


    public Navigator() {
        pages = new ArrayList<>();
        currentPageIndex = 0;
    }

    public void addPage(Parent page, Initializable controller) {
        pages.add(new Pair<>(page, controller));
    }

    public Parent getCurrentPage() {
        return pages.get(currentPageIndex).getFirst();
    }

    public Initializable getCurrentPageController() {
        return pages.get(currentPageIndex).getSecond();
    }

    public Parent getNextPage() {
        if (currentPageIndex + 1 < pages.size()) {
            currentPageIndex = currentPageIndex + 1;
        }
        return pages.get(currentPageIndex).getFirst();
    }

    public Parent getPreviousPage() {
        if (currentPageIndex - 1 >= 0) {
            currentPageIndex = currentPageIndex - 1;
        }
        return pages.get(currentPageIndex).getFirst();
    }

    public Parent goToFirstPage() {
        currentPageIndex = 0;
        return pages.get(currentPageIndex).getFirst();
    }

    public boolean isFirstPage() {
        return currentPageIndex == 0;
    }

    public boolean isLastPage() {
        return currentPageIndex + 1 == pages.size();
    }
}
