# Overview

## Dedenpencies

~~~bash
# Spring
spring-boot-starter-parent:2.7 (parent)
spring-boot-starter-web
spring-boot-starter-tomcat
spring-context

# MySQL, ORM(Hibernate), and so on
spring-boot-starter-jdbc
spring-boot-starter-data-jpa
spring-boot-starter-data-jdbc
mysql-connector-java
hibernate-core
hibernate-c3p0

# NoSQL (Redis)
spring-boot-starter-data-redis

# Unit test
spring-boot-starter-test

# Log
log4j-core

# Utils
gson:2.9.1
guava:31.1-jre
~~~

> For a more accurate and specific dependency list, check out `pom.xml`.

## Directory Structure

~~~php
<root>
├─docs				        # documentations
│  ├─overview.md			# overview of the porject structure
│  ├─exception.md			# (scaffold doc) exception module
│  └─...
│
├─src					# source code
│  ├─main				# development code
│  │  ├─java				# java development code
│  │  │  └─com.couree.backend           # current artifact development code
│  │  │  
│  │  └─resource		        # resource root directory
│  │     ├─templates	                # template root directory
│  │     ├─application.properties	# application properties
│  │     └─env.properties               # (ignored) environment properties
│  │
│  └─test			        # test code
│     └─java				# java test code
│        └─com.couree.backend		# current artifact test code
│
├─target			        # java bytecodes generation target directory
├─.gitignore				# files and directories in this file are not staged by git
├─mvnw					# (mvnw)
├─mvnw.cmd				# (mvnw)
├─pom.xml				# maven project object model file
└─README.md				# project README file
~~~

`docs` directory stores documentation files (only markdown files are allowed).

`src` stores development and test code files, while `target` stores java bytecodes after compilation. `target` directory is often marked as excluded (should tell the IDE). The `main` directory includes two folders. The `java` directory stores all java development code, and the `resource` stores all resource file, such as templates and settings files. Note that in the future there may be code written in other programming language, such as Python and Go, included, by then just create directories `python` or `go` in the same level directory. 

`mvnw`, `mvnw.cmd`, and the hidden directory `.mvn` are for [Maven Wrapper](https://github.com/takari/maven-wrapper), which allows machines to run the Maven project without having Maven installed and present on the path. (See [What is the purpose of mvnw and mvnw.cmd files?](https://stackoverflow.com/questions/38723833/what-is-the-purpose-of-mvnw-and-mvnw-cmd-files) )

`pom.xml` is the maven project object model file. (See [POM Reference](https://maven.apache.org/pom.html))

### Java Development code

~~~php
com.couree.backend
├─annotation					# annotations
├─aspect					# AOP(aspect oriented programming) related
│  ├─packer					# packer related
│  ├─GeneralControllerAdvice.java       	# core advice targeting at all controller methods
│  ├─RequestInterceptor.java			# request interceptor
│  └─ResponseInterceptor.java			# response interceptor
│
├─common				        # common classes
│  ├─GeneralExceptionResolver.java	        # implementation of HandlerExceptionResolver
│  └─Hibernate.java				# hibernate common class
│
├─config					# configuration classes
│  ├─Config.java				# Project global config
│  ├─RequestExceptionConfig.java		# Request Exception config
│  └─WebConfig.java				# implementation of WebMvcConfigurer
│
├─constant					# constants
│  ├─EnvironmentType.java	        	# environment type enum
│  ├─JSendStatus.java				# JSend status enum
│  ├─PermissionType.java			# * permission type
│  └─...
│
├─controller					# controller classes
│  ├─DefaultController.java			# defautl controller
│  ├─UserController.java			# * user controller
│  └─...
│
├─dto						# DTO(Data transfer object)
│  ├─Dto.java					# basic DTO
│  ├─JSendDto.java				# * JSend DTO
│  └─...
│
├─exception					# exception classes
│  ├─bootstrap					# bootstrap exceptions
│  ├─business					# business logic exceptions
│  ├─ControllerException.java	                # controller exception
│  ├─RequestException.java			# basic class of request exceptions
│  └─...
│
├─model						# model (or entity) classes
│  ├─Model					# basic class of model
│  ├─User.java					# * user model
│  └─...
│
├─service					# service (service layer, or business logics) classes; 
│  ├─UserService.java	        		# * user services
│  └─...
│
├─util						# utility classes
│  ├─AnnotationUtil.java		        # annotation related helper functions class
│  └─...
│
└─Application.java			        # (SpringBoot) application launch class
~~~

> Files that marked with "*" in the corresponding comment are not core classes. They are just examples of custom classes.

#### Annotation

Custom annotations are put in the `annotation` folder. These annotations should be commented properly for other developers to understand and apply.

#### Aspect

AOP related classes are put in the `aspect` folder. These classes are based on [Spring AOP](https://docs.spring.io/spring-framework/docs/2.5.5/reference/aop.html).

`GeneralControllerAdvice` is a core component. It implements a `controllerAdvice` method which arounds all controller methods. Any actions done before or after the matched controller method can be written in this method. However, the main function of this method is to convert the result yielded by the controller method into a property string, which serves as the HTTP body, by a specified packer.

> To get more information about packer, see [packer](./request-processing.md#packer).

The `RequestInterceptor` and `ResponseInterceptor` are interceptors that control actions before and after running controller methods respectivelly. The difference between these interceptors and `GeneralControllerAdvice` is that these two interceptors do not intervene the running of controller methods. They are responsible for tasks like dealing with CORS and logging.

#### Common

Common classes are generally complete sub-modules. They do not belong to any other directory so are put in `common`.

#### Config

Config classes are put in the  `config` folder.

The `Config` class is the global configuration of the whole project, including registry, interceptor classes and the environment config. It have no component to inject, so any class can depend on this class without concerning the nested initializing error.

#### Constant

Enum classes and any other classes that serve as constant containers are put in the `constant` folder. For example, the `EnvironmentType` lists all environment types in the package.

#### Controller

Controller classes are put in the `controller` folder.

#### DTO

Data transfer object classes are put in the `dto` folder. All classes should extend `DTO` class, which is the basic DTO.

#### Exception

Exception classes that extend `Throwable` are put in the `exception` folder. The exceptions that are thrown during the bootstrap stage are put in `exception/bootstrap` folder; the exceptions related to business logic are put in the `exception/business` folder, for example, `UserServiceException`.

> For more information about the exception module, see [exception](./exception.md).

#### Model

All models (or entities, DAO) are put in the `model` folder. All classes should extend `Model` abstract class, which is the basic model. Note that names of model classes do not have to suffixed with `Entity` or `Model`, etc. All model classes are annotated by `@javax.persistence.Entity`;

#### Service

All business logic classes, which are called service classes, are put in the `service` folder. Service classes are suffixed with `Service`, and they are all annotated by `@org.springframework.stereotype.Service`.

#### Util

Util classes provide useful helper functions, which are **independent** and **static**. Take `AnnotationUtil` as example.

~~~java
public class AnnotationUtil {
    /**
     * Returns the first annotation object bound to the specified method.
     * @param method          method to find
     * @param annotationClass a specified annotation class
     * @return the first annotation object bound to the specified method.
     */
    public static <E extends Annotation> E get(Method method, Class<E> annotationClass) {
        Annotation[] annotations = method.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().equals(annotationClass)) {
                @SuppressWarnings("unchecked")
                E result = (E) annotation;
                return result;
            }
        }

        return null;
    }
}
~~~

The `get` function returns the first annotation object bound to the specified method. So this helper function can be applied as follows.

~~~java
SuccessMessage successMessage = AnnotationUtil.get(method, SuccessMessage.class);
~~~

The `successMessage` is an annotation object if found.