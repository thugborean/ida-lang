# **IDA**

**Summary**:

IDA is a custom made, statically typed, JVM-based, interpreted, high-level programming language. It is currently being developed by Lund University CompSci student Alexander Mårtensson. The language interpreter is meant to be a toy-interpreter and a passion project.

The goal and purpose of the language is to combine two things:
  * A modern JVM-based language that will be straighforward easy to use and pick up.
  * The use of structs and composition for custom datatypes (instead of OOP).

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

**Grammar:**

<pre>
program → statement* EOF ;
 
statement → expressionStatement | ifStatement | whileStatement | variableDeclaration | printStatement | block ;

expressionStatement → expression “;” ;

expression → literal | variableReference | binaryExpression ;

literal → NUMBER | DOUBLE | STRING | “true” | “false” | “null” ;

variableReference → IDENTIFIER ;

binaryExpression → expression operator expression ;

operand → literal | IDENTIFIER ;

operator → “+” | “-“ | “*” | “%” | “==” | “!=” | “<” | “<=” | “>” | “>=” ;

ifStatement → “if” “(“ expression “)” block (“else” (block | ifStatement))? ;

whileStatement → “while” “(“ expression “)” block ;

variableDeclaration → type IDENTIFIER ( “=” expression)? “;” ;

type → “num” | “double” | “string” | “bool” ;

printStatement → “print” “(“ expression “)” “;” ;

block → “{“ statement* “}” ;
</pre>

**Prerequisites**:

  * Maven v3.9.9+
  * JDK/JRE v.21+

**Disclaimer**:

This project is still under development and has a long road ahead with many features planned. Please be mindful that some features may not have been fully implemented/realized yet.
