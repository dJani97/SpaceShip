/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.AbstractListModel;

/**
 * A pillangós projektben használt modell másolata, projekthez igazítva
 *
 * @param <T>
 */
public class SortedListModel<T extends Comparable<T>> extends AbstractListModel {

    private List<T> model = new ArrayList<>();

    private boolean sorted = false;

    @Override
    public T getElementAt(int index) {
        return model.get(index);
    }

    @Override
    public int getSize() {
        return model.size();
    }

    public void sort() {
        if (!sorted) {
            Collections.sort(model);
            this.fireContentsChanged(this, 0, model.size() - 1);
        }
    }

    /**
     * insert an element to a specific place
     *
     * @param element
     * @param index
     */
    public void addElement(T element, int index) {
        model.add(index, element);
        this.fireIntervalAdded(this, index, index);
    }

    /**
     * insert an element to the end of the model
     *
     * @param element
     */
    public void addElement(T element) {
        this.addElement(element, model.size());
        this.fireIntervalAdded(element, model.size() - 1, model.size() - 1);
    }

    /**
     * insert an element into it's place in the sorted list
     *
     * @param element
     * @param sort
     */
    public void addElement(T element, boolean sortedInsert) {
        if (!sortedInsert) {
            this.addElement(element);
            this.sorted = false;
        } else {
            if (!this.sorted) {
                sort();
            }
            int index = Collections.binarySearch(model, element);
            if (index < 0) {
                addElement(element, -index - 1);
            } else {
                addElement(element, index);
            }
            this.sorted = true;
        }
    }

    /**
     * @return true if the model is sorted
     */
    public boolean isSorted() {
        return sorted;
    }

    /**
     * @param sorted
     */
    public void setRendezett(boolean sorted) {
        this.sorted = sorted;
    }

    public boolean contains(T element) {
        return model.contains(element);
    }

    public void removeElement(T element) {
        int index = model.indexOf(element);
        model.remove(element);
        this.fireIntervalRemoved(element, index, index);
    }
    
    public void clear() {
        int maxIndex = model.size();
        this.model.clear();
        this.fireIntervalRemoved(this, 0, maxIndex);
    }

}
