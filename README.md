ABot 是一个 AccessibilityEvent 驱动的动作引擎。

本项目基于 ABot 实现了微信抢红包功能插件，功能截图如下：


### 关于 ABot

ABot 的目的是基于 AccessibilityEvent 实现对任意 APP 的自动化操作。

目前，ABot 包含了两个模块:

####  AccessibilityEvent 节点解析

1. 节点使用表达式进行匹配:

    ```
    index:class_name|k1=v1,k2=v2
    ```

    * index: 表示该节点在父节点的位置, `*` 表示任意节点，根节点可用任意数字。

    * class_name: 表示 View 的类名

    * `|` 之后是 `&` 号分隔， `=` 号相连的属性列表，目前支持的属性有:

        `parent`: 回退到父节点层级
        `childCount`: 子元素数目
        `textContains`: text 包含关键字

2. 节点之间使用 `>` 连接，表示一个节点路径，如下：

```
0:android.widget.FrameLayout>1:android.support.v4.view.ViewPager>0:android.widget.ListView>*:android.widget.LinearLayout`
```

3. 节点配置

支持节点配置文件，节点名和节点配置用 `=>` 连接，每行一条配置，如下

```
key1  =>  expression1
key2  =>  expression2
```

示例:

```
open_lucky_money   =>  0:android.widget.FrameLayout>3:android.widget.Button
close_lucky_money  =>  0:android.widget.FrameLayout>*:android.widget.ImageView
```

####  Action

ABot 引入 Action 概念，Event 驱动 Action 完成，通过不同的 Action 的组合，来完成一个复杂的操作。

1. Action 分类

    分为『单一 Action 』和 『复合 Action 』，前者是比如点击，判断有无元素，计时等简单操作，后者是前者的组合。

2. 简单的 Action

   目前支持的有点击，判断元素存在与否，滑动，计时，Toast 等。

3. 组合 Action

    1.  `AndAction`，挨个完成 Action， 直到所有的都完成。

    2.  `OrAction`， 事件将同时派发到每个子 Action，直到所有完成。

    3.  `BranchAction`，按顺序派发事件给各个 Action， 如果事件被某个 Action 完成，则会继续派发到此 Action 直到该 Action 完成。