package com.comment.compiler;

import com.comment.annotation.Comment;
import com.comment.compiler.helper.FileHelper;
import com.comment.compiler.helper.HTMLFileHelper;
import com.comment.compiler.helper.JavaFileHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

public final class CommentProcessor extends AbstractProcessor {

    /**
     * 操作元素的工具方法
     */
    private Elements mElementUtil;
    /**
     * 用来创建新源、类或辅助文件的 Filer
     */
    private Filer mFiler;
    /**
     * 用来报告错误、警报和其他通知的 Messager
     */
    private Messager mMessager;

    private List<FileHelper> mFileHelpers;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mElementUtil = processingEnvironment.getElementUtils();
        mFiler = processingEnvironment.getFiler();
        mMessager = processingEnvironment.getMessager();
        mFileHelpers = Arrays.asList(JavaFileHelper.create(), HTMLFileHelper.create());
    }

    @Override
    public Set<String> getSupportedOptions() {
        return super.getSupportedOptions();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(Comment.class.getCanonicalName());
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (!set.isEmpty()) {
            for (TypeElement typeElement : set) {
                Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(typeElement);//得到有标记@JavaDoc注解的元素
                Map<String, List<List<CommentModel>>> data = new LinkedHashMap<>();
                for (Element element : elements) {
                    PackageElement pkgElement = (PackageElement) element.getEnclosingElement();//获得标记@JavaDoc注解包元素
                    String pkgName = pkgElement.getQualifiedName().toString();
                    List<CommentModel> commentModels = new ArrayList<>();
                    String canonicalName = element.toString();
                    CommentModel classModel = getCommentModel(element, canonicalName);
                    if (classModel != null) {
                        commentModels.add(classModel);
                        List<? extends Element> enclosedElements = element.getEnclosedElements();//获得标记@JavaDoc注解的类中的所有方法元素
                        for (Element enclosedElement : enclosedElements) {
                            CommentModel methodModel = getCommentModel(enclosedElement, canonicalName);
                            if (methodModel != null)
                                commentModels.add(methodModel);
                        }
                    }
                    List<List<CommentModel>> classCommentModels = data.get(pkgName);
                    if (classCommentModels == null) {
                        classCommentModels = new ArrayList<>();
                    }
                    classCommentModels.add(commentModels);
                    data.put(pkgName, classCommentModels);

                }
                for (FileHelper fileHelper : mFileHelpers) {
                    try {
                        fileHelper.writeToFile(mFiler, data, "JavaCommentDoc");
                    } catch (Exception e) {
                        e.printStackTrace();
                        mMessager.printMessage(Diagnostic.Kind.ERROR, e.toString());
                    }
                }
            }

            return true;
        }
        return false;
    }


    /**
     * 根据元素封装成数据类
     *
     * @param element       相应元素
     * @param canonicalName 类名
     * @return 封装成的数据类
     */
    private CommentModel getCommentModel(Element element, String canonicalName) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, "element=" + element.toString());
        CommentModel commentModel = null;
        if (element.getKind() == ElementKind.CLASS || element.getKind() == ElementKind.METHOD) {
            commentModel = new CommentModel();

            String docComment = mElementUtil.getDocComment(element);
            if (docComment != null && docComment.length() > 0) {
                StringBuilder sb = new StringBuilder(docComment);//得到类或方法的注释
                if (sb.length() > 0) {
                    int startHtmlTagIndex = sb.indexOf("<");
                    int endHtmlTagIndex = sb.lastIndexOf(">");
                    if (startHtmlTagIndex != -1 && endHtmlTagIndex != -1) {
                        sb.delete(startHtmlTagIndex, endHtmlTagIndex + 1);
                    }
                    int docTagIndex = sb.indexOf("@");
                    if (docTagIndex != -1) {
                        sb.delete(docTagIndex, sb.length());
                    }
                    docComment = sb.toString().trim();
                }
            }
            commentModel.setElementKind(element.getKind());
            commentModel.setComment(docComment);
            commentModel.setCanonicalName(canonicalName);
            commentModel.setName(element.toString());
        }
        return commentModel;
    }
}
