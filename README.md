# Works HTTP

A simple to use Http library, but still offering low level customization and processing easily by overriding the class. It uses AsyncTask usage paradigm.

## 1. Usage in code

#### 1.1. Adding as dependency

**Manual**
 * [Download JAR](https://github.com/yunarta/works-http/releases/download/v1.0.0/works-http-1.0.jar)
 * Put the AAR in the **libs** subfolder of your Android project
 * If you are using gradle you can use this dependency setting below
``` groovy
compile(name:'works-widget-1.0.1', ext:'aar')
```

or

**Gradle dependency**

``` groovy
compile 'com.mobilesolutionworks:works-http:1.0'
```

**Maven dependency**

``` xml
<dependency>
	<groupId>com.mobilesolutionworks</groupId>
	<artifactId>works-http</artifactId>
	<version>1.0</version>
	<type>pom</type>
</dependency>
```

#### 1.2. Simple request usage
```java
WorksHttpRequest request = new WorksHttpRequest();
request.url = "http://www.google.com/robots.txt";

new WorksHttpAsyncTask<String>(this) {

    @Override
    public void onLoadFinished(WorksHttpRequest request, int statusCode, String data) {
        // receive the processresult here
    }
}.execute(request);

or

new WorksHttpFutureTask<String>(this) {

    @Override
    public void onLoadFinished(WorksHttpRequest request, int statusCode, String data) {
        // receive the processresult here
    }
}.execute(request);
```

Further usage can be checked in [project wiki](https://github.com/yunarta/works-http/wiki)

