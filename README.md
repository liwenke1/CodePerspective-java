# 介绍

该仓库用于从词法特征、布局特征和语法特征上提取代码高维特征

This repository is used to extract high-dimensional code features from lexical features, layout features and syntax features.

## 安装

* Java 19
* [Antlr4](https://github.com/antlr/antlr4)

## 代码介绍

```
├── src                     # 源代码目录
│   ├── main.java.com.hust
│   │   ├── Main.java        # 主程序文件
│   │   ├── antlr           # antlr 层，用于生成编译树
│   │   ├── arff            # 将结果转化为 arff 文件格式
│   │   ├── model           # 中间层定义，如函数、变量结构
│   │   ├── output          # 输出结构定义
│   │   ├── parse           # 文件接口层
│   │   └── tools           # 额外组建，用于读取文件、计算结果
│   └── test                # 测试代码
├── target          
```

## 使用

运行 Main.java 文件即可