<div align="center">
  <img src="./tools/transparent.png" width="300" height="300">
</div>

<div align="center">

[English](README_en.md) | 简体中文

</div>

*这个项目是[simplekb](https://github.com/guchengxi1994/simplekb)的重构*

### 介绍

我一直觉得，非结构化数据的处理，离不开结构化思维。一个文档，总有（用户关心的）重点。于是，我们将非结构化数据的重点提取出来，变成结构化数据，让非结构化数据能够更方便的比较，聚合，并且，能够像结构化数据一样脱敏，分享。
正好RAG是一个数据清洗+检索流程，所以基于以上思维做了这个项目。

### 流程

**1.内容提取**


![image](./readme/data-extract.png)

**2.问答**

![image](./readme/qa.png)

### 不足之处

1. 不适合超长文本，或者说超长文本需要做额外的处理  
2. 不适合类似法律法规这种条目很多的场景，也需要额外处理