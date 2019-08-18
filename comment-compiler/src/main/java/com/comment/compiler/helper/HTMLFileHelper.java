package com.comment.compiler.helper;

import com.comment.compiler.CommentModel;
import com.sun.org.apache.xerces.internal.dom.DocumentImpl;
import com.sun.org.apache.xerces.internal.dom.DocumentTypeImpl;

import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.lang.model.element.ElementKind;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

/**
 * 生成HTML文档
 *
 * @author wangjiang
 */
public final class HTMLFileHelper implements FileHelper {


    private static final String TITLE = "API";

    private HTMLFileHelper() {

    }

    public static FileHelper create() {
        return new HTMLFileHelper();
    }


    @Override
    public void writeToFile(Filer filer, Map<String, List<List<CommentModel>>> data, String filename) throws Exception {


        for (Map.Entry<String, List<List<CommentModel>>> entry :
                data.entrySet()) {

            Document htmlDoc = makeBasicHTMLDoc(TITLE);
            createCss(htmlDoc);

            Element divElement = htmlDoc.createElement("div");
            Element bodyElement = (Element) htmlDoc.getElementsByTagName("body").item(0);
            bodyElement.appendChild(divElement);


            String pkgName = entry.getKey();

            appendChild(htmlDoc, divElement, "h1", pkgName);

            List<List<CommentModel>> entryValue = entry.getValue();
            for (List<CommentModel> commentModels : entryValue) {

                String path = null;

                Element tableElement = htmlDoc.createElement("table");


                appendChild(htmlDoc, tableElement, "th", "方法名字");
                appendChild(htmlDoc, tableElement, "th", "方法描述");

                for (CommentModel commentModel :
                        commentModels) {

                    if (commentModel.getElementKind() == ElementKind.METHOD) {
                        Element trElement = htmlDoc.createElement("tr");

                        appendChild(htmlDoc, trElement, "td", commentModel.getName());
                        appendChild(htmlDoc, trElement, "td", commentModel.getComment());

                        tableElement.appendChild(trElement);

                    } else if (commentModel.getElementKind() == ElementKind.CLASS) {
                        String canonicalName = commentModel.getCanonicalName();
                        String className = canonicalName.substring(canonicalName.lastIndexOf(".") + 1, canonicalName.length());
                        FileObject fileObject = filer.getResource(StandardLocation.SOURCE_OUTPUT, pkgName, className);
                        String fileObjectName = fileObject.getName();
                        path = fileObjectName.substring(0, fileObjectName.lastIndexOf(File.separator));

                        appendChild(htmlDoc, divElement, "h2", className + " : " + commentModel.getComment());

                    }

                }

                divElement.appendChild(tableElement);

                write(htmlDoc, path, filename);
            }


        }


    }


    /**
     * 写入 HTML 文档
     *
     * @param document 文档对象
     * @param path     文档路径
     * @param filename 文件名字
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     * @throws FileNotFoundException
     */
    private void write(Document document, String path, String filename) throws InstantiationException, IllegalAccessException, ClassNotFoundException, FileNotFoundException {

        DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
        DOMImplementationLS domImplLS = (DOMImplementationLS) registry.getDOMImplementation("LS");

        LSSerializer lsSerializer = domImplLS.createLSSerializer();
        DOMConfiguration domConfig = lsSerializer.getDomConfig();
        domConfig.setParameter("format-pretty-print", true);  //if you want it pretty and indented

        LSOutput lsOutput = domImplLS.createLSOutput();
        lsOutput.setEncoding("UTF-8");

        File file = new File(path, filename + ".html");
        OutputStream os = new FileOutputStream(file);
        lsOutput.setByteStream(os);
        lsSerializer.write(document, lsOutput);

    }


    /**
     * 创建基本HTML文档
     *
     * @param title 文档标题
     * @return 文档对象
     */
    private Document makeBasicHTMLDoc(String title) {
        Document htmlDoc = new DocumentImpl();

        DocumentType docType = new DocumentTypeImpl(null, "html",
                "-//W3C//DTD HTML 4.01//EN",
                "http://www.w3.org/TR/html4/strict.dtd");
        htmlDoc.appendChild(docType);

        Element htmlElement = htmlDoc.createElementNS("http://www.w3.org/1999/xhtml", "html");
        htmlDoc.appendChild(htmlElement);

        Element headElement = htmlDoc.createElement("head");
        htmlElement.appendChild(headElement);


        Element metaElement = htmlDoc.createElement("meta");
        metaElement.setAttribute("content", "txt/html; charset=utf-8");
        metaElement.setAttribute("http-equiv", "content-type");
        headElement.appendChild(metaElement);

        Element titleElement = htmlDoc.createElement("title");
        if (title != null)
            titleElement.setTextContent(title);
        headElement.appendChild(titleElement);

        Element bodyElement = htmlDoc.createElement("body");
        htmlElement.appendChild(bodyElement);

        return htmlDoc;
    }

    /**
     * 创建 CSS
     *
     * @param htmlDoc 文档对象
     */
    private void createCss(Document htmlDoc) {
        Element cssElement = htmlDoc.createElement("style");
        cssElement.setAttribute("type", "text/css");
        StringBuilder sb = new StringBuilder();
        sb.append("div{max-width:100%;width:50%;margin:0 auto}").append("\n")
                .append("table{width:100%;color:#333333;border-width:1px;border-color:#666666;border-collapse:collapse;}").append("\n")
                .append("table th{border-width:1px;padding:10px;border-style:solid;border-color:#666666;background-color:#dedede;}").append("\n")
                .append("table td{border-width:1px;padding:10px;border-style:solid;border-color:#666666;background-color:#ffffff;}").append("\n")
        ;
        cssElement.setTextContent(sb.toString());
        Element headElement = (Element) htmlDoc.getElementsByTagName("head").item(0);
        headElement.appendChild(cssElement);
    }

    /**
     * 添加子元素
     *
     * @param htmlDoc       文档对象
     * @param parentElement 父元素
     * @param name          子元素名字
     * @param textContent   子元素文本值
     */
    private void appendChild(Document htmlDoc, Element parentElement, String name, String textContent) {
        Element element = htmlDoc.createElement(name);
        if (textContent != null)
            element.setTextContent(textContent);
        parentElement.appendChild(element);
    }
}
