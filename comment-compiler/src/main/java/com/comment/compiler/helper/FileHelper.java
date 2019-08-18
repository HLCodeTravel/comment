package com.comment.compiler.helper;

import com.comment.compiler.CommentModel;

import java.util.List;
import java.util.Map;

import javax.annotation.processing.Filer;

/**
 * 生成文档的接口
 *
 * @author wangjiang
 */
public interface FileHelper {

    /**
     * 将类或方法，注释写成 Java 文件
     *
     * @param filer    操作文件 {@link Filer}
     * @param data     数据
     * @param fileName 生成的 Java 文件的类名
     */
    void writeToFile(Filer filer, Map<String, List<List<CommentModel>>> data, String fileName) throws Exception;
}
