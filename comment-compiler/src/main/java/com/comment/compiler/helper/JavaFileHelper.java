package com.comment.compiler.helper;

import com.comment.compiler.CommentModel;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.lang.model.element.ElementKind;
import javax.tools.JavaFileObject;

public final class JavaFileHelper implements FileHelper {


    private JavaFileHelper() {
    }

    public static FileHelper create() {
        return new JavaFileHelper();
    }

    @Override
    public void writeToFile(Filer filer, Map<String, List<List<CommentModel>>> data, String className) {


        for (Map.Entry<String, List<List<CommentModel>>> entry :
                data.entrySet()) {
            String pkgName = entry.getKey();

            StringBuilder builder = new StringBuilder()
                    .append("package ").append(pkgName).append(";").append("\n")
                    .append("class ").append(className).append("{").append("\n")
                    .append("\t").append("/**").append("\n");

            List<List<CommentModel>> entryValue = entry.getValue();
            for (List<CommentModel> commentModels : entryValue) {
                for (CommentModel commentModel : commentModels) {
                    builder.append("\t").append("*");
                    builder.append("\t");
                    int insertIndex = builder.length() - 1;
                    builder.append("{@link").append(" ");
                    if (commentModel.getElementKind() == ElementKind.CLASS) {
                        builder.append(commentModel.getName());
                    } else if (commentModel.getElementKind() == ElementKind.METHOD) {
                        builder.insert(insertIndex, "\t");
                        builder.append(commentModel.getCanonicalName()).append("#").append(commentModel.getName());
                    }
                    builder.append(',').append(" ").append(commentModel.getComment());
                    builder.append("}");
                    builder.append("\n");
                }

                builder.append("\t").append("*");
                builder.append("\n");
            }


            builder.append("\t").append("*/").append("\n")
                    .append("}");

            try {
                JavaFileObject fileObject = filer.createSourceFile(pkgName + "." + className);//生成 Java 文件
                Writer writer = fileObject.openWriter();
                writer.append(builder.toString());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }


}
