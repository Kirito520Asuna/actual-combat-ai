
****************************************************
---
# 极简
#### 介绍
让代码变简单
项目依据 若依 为参考 修改 并提供了高扩展

(springboot2)

github 地址：https://github.com/Kirito520Asuna/minimalism

gitee  地址：https://gitee.com/kirito-asuna/minimalism

(springboot3)

github 地址：https://github.com/Kirito520Asuna/actual-combat-ai

gitee  地址：https://gitee.com/kirito-asuna/actual-combat-ai

#### 软件架构

- 架构:
  - 微服务架构
- 部署: 
  - 提供了一键部署为docker运行的脚本
- 安全框架: 
  - 提供了shiro 和 security 俩种选择,甚至可以不同微服务使用俩种不同框架，
    其中shiro使用的是自定义的注解实现的权限校验，
    security使用的是spring security框架
  - api和jwt校验提供了拦截器和过滤器俩种方案的实现
    

#### 安装教程

1.  xxxx
2.  xxxx
3.  xxxx

#### 使用说明

1. 关于安全模块的使用
- micro-auth 模块
- 包含 auth、security、shiro

- micro-auth/auth 服务模块
- 已经依赖 micro-auth下的auth-security,auth-shiro
- 使用时 只需要引入auth即可 注意安全模块 security,shiro只能多选一
- 不同的服务 可以引入不同的安全模块（已兼容）

- security,shiro 已提供 权限 测试环境 跳过 的配置

2.  gateway网关
- gateway网关使用的是springcloud gateway
- 已经整合各个微服务的swagger文档(配置只生效于dev,test 如需自定义配置 请修改HomePageConfiguration中@Profile配置的值)
- 具体配置请参考gateway下的bootstrap-test.yml
- 地址 ip:port/${server.servlet.context-path}/home
3.  xxxx

#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request


#### 特技

1.  使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2.  Gitee 官方博客 [blog.gitee.com](https://blog.gitee.com)
3.  你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解 Gitee 上的优秀开源项目
4.  [GVP](https://gitee.com/gvp) 全称是 Gitee 最有价值开源项目，是综合评定出的优秀开源项目
5.  Gitee 官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6.  Gitee 封面人物是一档用来展示 Gitee 会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)
