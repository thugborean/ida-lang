# **IDA**

**Summary**:

IDA is a custom made, statically typed, JVM-based, interpreted, high-level programming language. It is currently being developed by Lund University CompSci student Alexander Mårtensson. The language interpreter is meant to be a toy-interpreter and a passion project.
The goal and purpose of the language is to combine several things:
  * A modern JVM-based pseudo-scripting language that will be easy to use and pick up.
  * The use of structs and composition for custom datatypes (instead of OOP).
  * A stricter more formal take on a modern scripting language, straying from the likes of Python and Lua.

**Usage**:

Clone the program
<pre>
git clone git@github.com:thugborean/ida-lang.git
cd ida-lang
</pre>
Run interpreter with a source file from the examples directory (optionally your own) as an argument using maven/maven wrapper and being in the root directory
<pre>
mvn exec:java -Dida.file=src/main/java/io/github/thugborean/examples/examplefile.ida
</pre>
or
<pre>
mvnw exec:java -Dida.file=src/main/java/io/github/thugborean/examples/examplefile.ida
</pre>
You can also compile normally and run the JAR-file
<pre>
mvn package
java -jar target/ida-lang-1.0-SNAPSHOT.jar examples/examplefile.ida
</pre>
Note that running without args will launch into a REPL debug mode

**Prerequisites**:

  * Maven v3.9.9+
  * JDK/JRE v.21+

**Disclaimer**:

This project is still under development and has a long road ahead with many features planned. Please be mindful that some features may not have been fully implemented/realized yet.
