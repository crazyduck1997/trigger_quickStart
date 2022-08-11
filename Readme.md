## Trigger quick start
#### 通过canal实现监听数据库指定表或列,当监听数据有变更时来触发事件
### 1.将canal连接信息配置到demo下application.yml
canal quick start https://github.com/alibaba/canal/wiki/QuickStart
### 2.在InsertTriggerTest和UpdateTriggerTest类中的注解上配置要监听的表名和字段
### 3.启动项目，更新数据库监听字段
## 目前只实现了insert和update的基本功能
![img.png](img.png)
