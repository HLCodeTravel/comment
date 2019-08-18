# comment
**comment** 的主要作用是将类注释扫描到文档中。通过在类中添加类注解`@Comment`，在项目编译的时候，生成`.java` 和`.html` 文档。`.java` 和`.html` 文档会把一个包下的每个类具有的方法汇聚到一起，帮助开发人员一览项目 API。

## 如何使用？
1.在你需要生成注释文档的类中添加注解`@Comment`，如在工具类`DisplayUtil`中添加：

```
package com.example.comment.util;
/**
 * 页面相关
 *
 * @author wangjiang wangjiang7747@gmail.com
 * @version V1.0
 */
@Comment
public final class DisplayUtil {

    private DisplayUtil() {
        throw new UnsupportedOperationException("不能创建此对象");
    }

    /**
     * 获得StatusBar的高度
     *
     * @param context 上下文对象
     * @return 状态栏的高度
     */
    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen",
                "android");
        int statusBarHeight = resources.getDimensionPixelSize(resourceId);
        return statusBarHeight;
    }
}

```

2. 构建项目，运行`./gradlew build` 或 `./gradlew assembleDebug` 或 `./gradlew assembleRelease`命令。

3. 构建完成后，查看主 Module 下的目录`/build/generated/source/apt/debug/`，再找到你添加注解`@Comment`的类的包下，如上面类包名为`com.example.comment.util`，则你会在该包下看到文件：JavaCommentDoc.java 和 JavaCommentDoc.html。

4.查看生成的文件 JavaCommentDoc.java ：

```
package com.example.comment.util;
class JavaCommentDoc{
	/**
	*	{@link com.example.comment.util.DisplayUtil, 页面相关}
	*		{@link com.example.comment.util.DisplayUtil#getStatusBarHeight(android.content.Context), 获得StatusBar的高度}
	*		{@link com.example.comment.util.DisplayUtil#getNavigationBarHeight(android.content.Context), 获得NavigationBar的高度}
	*		{@link com.example.comment.util.DisplayUtil#getActionBarHeight(android.app.Activity), 获得ActionBar的高度，注：在配置文件中获得ActionBar高度可通过：?attr/actionBarSize}
	*		{@link com.example.comment.util.DisplayUtil#getContentHeight(android.app.Activity), 获得页面高度}
	*		{@link com.example.comment.util.DisplayUtil#px2dip(android.content.Context,float), 将px转换为dp}
	*		{@link com.example.comment.util.DisplayUtil#dp2px(android.content.Context,float), 将dp转换为px}
	*		{@link com.example.comment.util.DisplayUtil#getScreenWidth(android.content.Context), 获得屏幕宽度}
	*		{@link com.example.comment.util.DisplayUtil#getScreenHeight(android.content.Context), 获得屏幕高度}
	*
	*/
}
```
JavaCommentDoc.java 文件会把一个包下的每个类具有的方法汇聚到一起，可以通过AndroidStudio 的快捷键也可直接进入到原类。如果有多个包，则有多个JavaCommentDoc.java 文件。

查看生成的文件 JavaCommentDoc.html，需要在浏览器中打开：

![image](https://github.com/WJRye/comment/blob/master/JavaCommentDoc-html.png)


JavaCommentDoc.html 和 JavaCommentDoc.java 表达的意思一样。
