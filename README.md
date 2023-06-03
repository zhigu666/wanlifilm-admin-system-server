# wanlifilm-admin-system-server
万里影院前后端分离项目（后端）

主要涉及技术：
- Springboot:基于spring框架，以约定大于配置的思想为基础，核心功能在于起步依赖以及自动配置
- Mybatis-plus:mybatis的增加工具，在mybatis的基础上只做增强不做修改，本项目中主要用于与数据库的交互
- Springsecurity:底层原理为filter过滤链的安全框架,本项目中用于生成以及验证验证码
- Redis:缓存数据库，在本项目中用于临时储存权限数据以及个别页面的分页数据
- Jwt:工具类，用于生成token并将token挂到响应头,实现身份的验证,用于在各方面之间安全地将信息作为JSON对象传输
- 阿里云OSS文件储存:与阿里云服务器交互以实现从服务器中获取或向服务器中保存用户头像
