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
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 * 注解解析类
 *
 * @author wangjiang
 * @version 0.0.1
 */
public final class CommentProcessor extends AbstractProcessor {


    /**
     * 生成的文件名字
     */
    private static final String FILE_NAME = "JavaCommentDoc";
    /**
     * 编译参数，是否是debug环境
     */
    private static final String OPTION_DEBUGGABLE = "debuggable";
    /**
     * 编译参数，是否要检查注释
     */
    private static final String OPTION_CHECK_COMMENT = "check_comment";
    /**
     * 是否是debug环境
     */
    private boolean mDebuggable = false;
    /**
     * 是否要检查注释
     */
    private boolean mCheckComment = true;

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

        Map<String, String> options = processingEnvironment.getOptions();
        if (options != null) {
            String debuggable = options.get(OPTION_DEBUGGABLE);
            if (debuggable != null) {
                mDebuggable = Boolean.valueOf(debuggable);
            }
            String checkoutComment = options.get(OPTION_CHECK_COMMENT);
            if (checkoutComment != null) {
                mCheckComment = Boolean.valueOf(checkoutComment);
            }
        }
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
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
                        fileHelper.writeToFile(mFiler, data, FILE_NAME);
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
        if (mDebuggable)
            mMessager.printMessage(Diagnostic.Kind.NOTE, "element=" + element.toString());

        CommentModel commentModel = null;
        Set<Modifier> modifiers = element.getModifiers();
        if (modifiers.contains(Modifier.PUBLIC) || modifiers.contains(Modifier.DEFAULT)) {
            commentModel = new CommentModel();
            String docComment = mElementUtil.getDocComment(element);

            checkDocComment(element, canonicalName, docComment);

            StringBuilder author = null;
            if (docComment != null && docComment.length() > 0) {
                StringBuilder sb = new StringBuilder(docComment);//得到类或方法的注释
                if (sb.length() > 0) {
                    int startHtmlTagIndex = sb.indexOf("<");
                    int endHtmlTagIndex = sb.lastIndexOf(">");
                    if (startHtmlTagIndex != -1 && endHtmlTagIndex != -1) {
                        sb.delete(startHtmlTagIndex, endHtmlTagIndex + 1);
                    }
                    String authorStr = "@author";
                    int authorTagIndex = sb.indexOf(authorStr);
                    if (authorTagIndex != -1) {
                        author = new StringBuilder();
                        int startAuthorTagIndex = authorTagIndex;
                        author.append(sb.charAt(startAuthorTagIndex));
                        startAuthorTagIndex += authorStr.length() + 1;
                        while (sb.charAt(startAuthorTagIndex) != '\n') {
                            author.append(sb.charAt(startAuthorTagIndex));
                            startAuthorTagIndex++;
                        }
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
            commentModel.setAuthor(author != null ? author.toString().trim() : null);
        }
        return commentModel;
    }

    /**
     * 检查方法是否有注释
     *
     * @param element       当前元素
     * @param canonicalName 类全名
     * @param docComment    当前类或者构造器或方法注释
     */
    private void checkDocComment(Element element, String canonicalName, String docComment) {
        if (mCheckComment && (docComment == null || "".equals(docComment.trim()) || "null".equals(docComment))) {
            StringBuilder message = new StringBuilder("You should add comment to " + element.getKind().toString().toLowerCase() + " : ");
            if (element.getKind() != ElementKind.CLASS) {
                message.append(canonicalName).append('#');
            }
            message.append(element.toString());
            mMessager.printMessage(Diagnostic.Kind.ERROR, message.toString());
        }
    }
}
