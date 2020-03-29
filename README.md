# AEM-Remote-ClientLib-Proxy

![Build Status](https://github.com/GuillaumeCleme/AEM-Remote-ClientLib-Proxy/workflows/Maven%20Build/badge.svg)

An Adobe Experience Manager (AEM) remote [client library](https://docs.adobe.com/content/help/en/experience-manager-65/developing/introduction/clientlibs.html) proxy service to manage remote non-AEM client libraries server-side.

This OSGI bundle contains a custom remote script compiler service which, when deployed, will bind itself to the `.href` file extension. The contents of these files can be used to specify the URL of one or multiple client libraries (JS/CSS) from remote locations that should be "proxied" to the current AEM instance in order to be managed as AEM client libraries without needing to manage static assets locally.

## Sample Usage

By deploying the `core` bundle provided and by creating a JCR structure similar to the following:

```
/
└── apps
    └── guillaumecleme
        └── clientlibs
            └── clientlibs-remote
                ├── css.txt
                ├── css
                │   └── bootstrap.min.css.href
                ├── js.txt
                └── js
                    ├── jquery.min.js.href
                    ├── popper.min.js.href
                    └── bootstrap.min.js.href
```

You will be able to create `.href` files with content similar to the following:

**Single Resource** (e.g. `bootstrap.min.css.href`):
```
#Pull Bootstrap from CDN
https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css
```
**Multiple Resources** (e.g. `vue.js.href`):
```
#Pull Vue.js
https://cdn.jsdelivr.net/npm/vue/dist/vue.js

#Also Pull Vue Router
https://unpkg.com/vue-router@3.1.6/dist/vue-router.js
```

By importing these files into the `js.txt` and `css.txt` files of your AEM clientlibs, you will be able to dynamically import these libraries and to make reference to these clientlibs within your components without using static `<link rel="stylesheet" href="" />` or `<script src="" />` tags.

**Clientlib Resource File** (e.g. `css.txt`):
```
#base=css
bootstrap.min.css.href
```

Once complete, you will be able to access your clientlib via it's standard URL: `http://<server>:<port>/apps/guillaumecleme/clientlibs/clientlib-remote.<js|css>`

Or through the standard AEM clientlib proxy: `http://<server>:<port>/etc.clientlibs/guillaumecleme/clientlibs/clientlib-base.<js|css>`

### A note on caching
Note that once the dependencies have been fetched for the first time, they will remain in the AEM clientlib cache and will not need to be resolved externally until the cache expires (system restarts, etc.) or until any of the source files are modified, therefore invalidating the cache.

## Adding as a Dependency
This project used [GitHub Packages](https://github.com/features/packages) to publish it's artifacts publicly via GitHub. To add this bundle as a dependency to your own Maven project, see the [Authenticating to GitHub Packages](https://help.github.com/en/packages/using-github-packages-with-your-projects-ecosystem/configuring-apache-maven-for-use-with-github-packages#authenticating-to-github-packages) documentation from GitHub and add the bundle dependency to your project:

```
<dependency>
  <groupId>me.guillaumecle.aem</groupId>
  <artifactId>aem-remote-clientlib-proxy-core</artifactId>
  <version>1.0.3</version>
</dependency>
```

## Modules

The main parts of the template are:

* core: Java bundle containing all core OSGI functionality.
* ui-apps: contains sample clientlibs (JS/CSS) under the `/apps` part of the project

## Building From Source

To build all the modules run in the project root directory the following command with Maven 3:

    mvn clean install

If you have a running AEM instance you can build and package the core module and deploy into AEM with:

    mvn clean install -PautoInstallBundle    

Or you can build and package the whole project and deploy into AEM with:

    mvn clean install -PautoInstallPackage -Padobe-public


## Testing

Unit testing of the code contained in the bundle. To test, execute:

    mvn clean test

## Maven Settings

The project comes with the `adobe-public` repository configured. To setup the repository in your Maven settings, refer to:

    http://helpx.adobe.com/experience-manager/kb/SetUpTheAdobeMavenRepository.html

## Contributors

* [Guillaume Clement](https://guillaumecle.me)

## License
Apache License 2.0


