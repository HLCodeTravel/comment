package com.comment.compiler;

import javax.lang.model.element.ElementKind;

public final class CommentModel {

    /**
     * 名字
     */
    private String name;
    /**
     * 注释
     */
    private String comment;

    /**
     * 类的全名（包括报名）
     */
    private String canonicalName;

    /**
     * 类型（类或方法）
     */
    private ElementKind elementKind;

    /**
     * 作者名字
     */
    private String author;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCanonicalName() {
        return canonicalName;
    }

    public void setCanonicalName(String canonicalName) {
        this.canonicalName = canonicalName;
    }

    public ElementKind getElementKind() {
        return elementKind;
    }

    public void setElementKind(ElementKind elementKind) {
        this.elementKind = elementKind;
    }


    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
