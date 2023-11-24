package com.apple.library.foundation;

import org.jetbrains.annotations.Nullable;

public class NSLayoutConstraint {

    protected final Object firstItem;
    protected final Object secondItem;
    protected final NSLayoutAttribute firstAttribute;
    protected final NSLayoutAttribute secondAttribute;
    protected final NSLayoutRelation relation;
    protected final float multiplier;

    protected float constant;
    protected NSLayoutPriority priority = NSLayoutPriority.REQUIRED;
    protected boolean active = true;
    protected String identifier;


    /**
     * Creates a constraint that defines the relationship between the specified attributes of the given views.
     * <p>
     * Constraints represent linear equations of the form view1.attr1 <relation> multiplier x view2.attr2 + constant.
     * If the constraint you wish to express does not have a second view and attribute, use null and NSLayoutAttribute.NONE.
     *
     * @param view1      The view for the left side of the constraint.
     * @param attr1      The attribute of the view for the left side of the constraint.
     * @param relation   The relationship between the left side of the constraint and the right side of the constraint.
     * @param view2      The view for the right side of the constraint.
     * @param attr2      The attribute of the view for the right side of the constraint.
     * @param multiplier The constant multiplied with the attribute on the right side of the constraint as part of getting the modified attribute.
     * @param constant   The constant added to the multiplied attribute value on the right side of the constraint to yield the final modified attribute.
     */
    public NSLayoutConstraint(Object view1, NSLayoutAttribute attr1, NSLayoutRelation relation, @Nullable Object view2, NSLayoutAttribute attr2, float multiplier, float constant) {
        this.firstItem = view1;
        this.firstAttribute = attr1;
        this.relation = relation;
        this.secondItem = view2;
        this.secondAttribute = attr2;
        this.multiplier = multiplier;
        this.constant = constant;
    }

    public Object firstItem() {
        return firstItem;
    }

    public NSLayoutAttribute firstAttribute() {
        return firstAttribute;
    }

    public Object secondItem() {
        return secondItem;
    }

    public NSLayoutAttribute secondAttribute() {
        return secondAttribute;
    }

    public NSLayoutRelation relation() {
        return relation;
    }

    public float multiplier() {
        return multiplier;
    }

    public void setConstant(float constant) {
        this.constant = constant;
    }

    public float constant() {
        return constant;
    }

    public void setPriority(NSLayoutPriority priority) {
        this.priority = priority;
    }

    public NSLayoutPriority priority() {
        return priority;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String identifier() {
        return identifier;
    }
}
