package org.poo.visitors;

public interface Visitable {
    /**
     * Accepts a visitor
     * @param visitor the visitor to be accepted
     * @return true if the visitor can implement the behaviour, false if not
     */
    boolean accept(Visitor visitor);
}
