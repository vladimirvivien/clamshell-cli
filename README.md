# Clamshell-Cli
Clamshell-Cli is a framework for building console-based command-line applications in Java.  Clamshell uses a simple plugin architecture (based on the [http://download.oracle.com/javase/6/docs/api/java/util/ServiceLoader.html ServiceLoader API]) that let developers deploy components to build components with control over all aspects of console-based applications including splashscreen display, IO, prompt, input control, and command delegation.  Clamshell-Cli comes with a default runtime that implements all of the basic components needed for a fully functioning console-based app.  To customize the runtime with your own command, you simply create and deploy your own plugin jars (see example below).

## News
  * Version 0.5.2 released.  It is a bug fix to update jline 1.0 to support Windows 64-bit.   [http://code.google.com/p/clamshell-cli/downloads/detail?name=clamshellcli-0.5.2-bin.zip#makechanges Download here.]

## Features
  * Easy to get started
  * Small API footprint with low learning curve
  * Ability to build complex CLI tools such as REPL using plugin architecture
  * Simple component model that imposes little constraints on your design
  * The plugin architecture is designed for extensibility and feature-scalability:
    * If you don't like how the default implementation works, you can change it completely
    * Implement the components you want to change and your feature will be included next time the console is restarted 
  * Statically-defined extension points provide control over all facets of console-based apps including:
    * `A Splashscreen`
    * `Console IO`
    * `Prompt`
    * `Input Controller`
    * `Command Handlers`
  * Each extension point is mapped to a Java type for easy implementation
  * Plugins are deployed as simple jar files 
  * Support for input hints (tab-press at the console)
  * Support for input buffer history
  
## Getting Started
It's easy to get started with Clamshell-Cli to build your first console application.
  * First, download the packaged zipped binary distribution from this site.  This is a default runtime implementation that you can use for your own CLI tools.
  * Unzip at a locatin of your choice.  The zip file will create a directory named `clamshellcli-{version}` (will refer to it as {CLI_HOME}).
  * Change directory inside {CLI_HOME} and inspect the files.  You will see the followings:
```
-rw-r--r--@ 1 299   Mar  17 cli.config
-rw-r--r--@ 1 3748  Mar  18 cli.jar
drwxrwxrwx  5 170   Mar  18 clilib
drwxrwxrwx  4 136   Mar  18 lib
drwxrwxrwx  5 170   Mar  18 plugins
```
    * `cli.config` - Clamshell-Cli configuration file 
    * `cli.jar` - the launcher jar file
    * `clilib` - lib files to boot Clamshell-Cli
    * `lib` - place your dependency jars here
    * `plugins` - location for Clamshell-Cli plugin jars

From within {CLI_HOME}, start the Clamshell-Cli launcher by typing:
  
```
> java -jar cli.jar
```

```
 .d8888b.  888                         .d8888b.  888               888 888
d88P  Y88b 888                        d88P  Y88b 888               888 888
888    888 888                        Y88b.      888               888 888
888        888  8888b.  88888b.d88b.   :Y888b.   88888b.   .d88b.  888 888
888        888     :88b 888 :888 :88b     :Y88b. 888 :88b d8P  Y8b 888 888
888    888 888 .d888888 888  888  888       :888 888  888 88888888 888 888
Y88b  d88P 888 888  888 888  888  888 Y88b  d88P 888  888 Y8b.     888 888
 :Y8888P:  888 :Y888888 888  888  888  :Y8888P:  888  888  :Y8888  888 888

                                                  Command-Line Interpreter

Java version: 1.6.0_22
Java Home: /usr/lib/jvm/java-6-openjdk/jre
OS: Linux, Version: 2.6.38-10-generic

prompt> _
```

Rightaway, you see three plugins in action:
  * Splashscreen Plugin - displays the ASCII text art scree
  * The Prompt - displays the current prompt at the command-line
  * The IOConsole - accepts user input at the command-line
  
The default runtime does not do too much.  It does, however, provide a command/controller implementation. An internal `Input Controller` (a plugin) parses input from the command-line, then delegates handling of the input to a registered `Command` plugin (if any).

If you type 'help' at the prompt, for instance, you will be actually invoking the `HelpCmd` plugin which lists help info for all Command objects that are mapped.  Each command is backed by a plugin class installed in the `plugins` directory.

```
prompt> help

Available Commands
------------------
      exit       Exits ClamShell.
      help       Displays help information for available commands.
   sysinfo       Displays current JVM runtime information.
      time       Prints current date/time

prompt> _
```

## Adding A Command
As stated above, Clamshell-Cli uses a plugin architecture.  All extension points of the framework can be customized by providing your own plugins.  This section shows how to add a new Command plugin and deploy it.  It examines the implementation of the `time` command using the `TimeCmd` plugin (deployed with the runtime).

Here are the simple steps for creating your own Command:
  * Create a new Java project (standard or maven) in your favorite IDE with a class that extends interface `org.clamshellcli.api.Command` interface (see below).
  * Package your project as a ServiceLoader SPI jar file (say, `time-cmd-0.1.jar`) and drop it in directory `plugins`
  * Voila! You just added your first command to Clamshell-Cli

### The Code
```java
public class TimeCmd implements Command {
    private static final String NAMESPACE = "syscmd";
    private static final String ACTION_NAME = "time";

    @Override
    public Object execute(Context ctx) {
        IOConsole console = ctx.getIoConsole();
        console.writeOutput(String.format("%n%s%n%n",new Date().toString()));
        return null;
    }

    @Override
    public void plug(Context plug) {
        // no load-time setup needed
    }
    
    @Override
    public Command.Descriptor getDescriptor(){
        return new Command.Descriptor() {
            @Override public String getNamespace() {return NAMESPACE;}
            
            @Override
            public String getName() {
                return ACTION_NAME;
            }

            @Override
            public String getDescription() {
               return "Prints current date/time";
            }

            @Override
            public String getUsage() {
                return "Type 'time'";
            }

            @Override
            public Map<String, String> getArguments() {
                return Collections.emptyMap();
            }
        };
    }
}
```

A quick explanation of the code is in order:
  * Method `execute()` - invoked by the input controller instance when it detects the String `time` from the command-line.  The method retrieves the IOConsole from the context object and use it to print the time.  It returns null to the controller (indicating the command did not generate a result).
  * Method `plug()` - a lifecycle method that is invoked by the framework when the command is first initialized.  For our example, there nothing to do.
  * Method `getDescriptor()` - returns an instance of interface `Command.Descriptor` which is used to describe the features and document the Command.  For our example, the Descriptor interface is implemented anonymously with the following methods:
    * Method `Descriptor.getNamespace()` - returns a string identifying the command's namespace.  This value can be used by input controllers to avoid command name collisions.
    * Method `Descriptor.getName()` - returns the string mapped to this command object.  In our implementation, it returns "time".
    * Method `Descriptor.getUsage()` - intended to provide a descriptive way of using the command.
    * Method `Descriptor.getArguments()` - returns a Map containing the description for each arguments that may be attached to the command.  This example uses none.

### Package Your Plugin
Once your class compiles properly, package the project as a standard Java Service provider (SPI).  To do this, do the followings:
  * In your source tree, create service descriptor text file META-INF/services/org.clamshellcli.api.Plugin
  * On the first line of that file, put `demo.command.TimeCmd` (assuming the class is placed in package `demo.command`).
  * Next, save the text file and package the project as a jar file (*it is important that the descriptor text file gets copied into your jar*)

Find more information on Java's Service Loader API:
  * [http://download.oracle.com/javase/6/docs/api/java/util/ServiceLoader.html Service Loader API]
  * [http://java.sun.com/developer/technicalArticles/javase/extensible/ Article on Java Extensibility]

#### Update File cli.config
Before your command can be made available in the console, you must let Clamshell-cli know about it.  cli.config is JSON-formatted configuration file where you declare and configure your input controller components.  In order for your controller to respond to your command, you must configure the controller properly to respond to the input pattern corresponding to your command.  

The sample code below shows the configuration of the Command Controller (default controller that comes with Clamshell-Cli):
```
    "controllers":{
        "org.clamshellcli.impl.CmdController":{
            "enabled":"true"
        }
        ...
    }
```

### Deploy and Test the Command
  * Drop the jar file in the `plugins/` directory (shown above).
  * Start the Clamshell-Cli runtime.
  * From the command-line type 'help' and you should see your new command listed.

## Developing with the API
When creating your own console-based tool, you can integrate the Clamshell-Cli API by directly pointing to the jar files or use Maven.

### Direct Setup
From within your Java IDE, add the followings to your classpath
  * {CLI_HOME}/clilib/clamshell-api-{version}.jar		
  * {CLI_HOME}/clilib/clamshell-impl-core-{version}.jar
  
### Maven Artifacts
  * Repository:
```xml
<repositories>
	<repository>
		<id>clamshellcli.repo</id>
		<name>Clamshell-Cli Repository</name>
		<url>http://s3.amazonaws.com/repo.clamshellcli.org/release</url>
	</repository>        
</repositories>
```
  * POM dependencies:
```xml
<dependency>
	<groupId>org.clamshellcli</groupId>
	<artifactId>clamshell-api</artifactId>
	<version>0.5.2</version>
</dependency>
<dependency>
	<groupId>org.clamshellcli</groupId>
	<artifactId>clamshell-impl-core</artifactId>
	<version>0.5.2</version>
</dependency>
```

## Clamshell-Cli Examples
The best way to learn how to use the Clamshell-Cli API is to download the source code and look at how the Command plugins are implemented.  You can also check out: 
  * Jmx-Cli, a fully-functional JMX command-line tool implemented using the Clamshell-Cli API [https://github.com/vladimirvivien/jmx-cli]
