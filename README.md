# What is jlite?

Jlite is a personal project in which I develop a simple scripting language called jlite.
I intend on making two version of the interpreter, one in Java and the other in C++.

The reason behind this is a personal interest in the area of Compilers and Interpreters.

As references, I use Crafting Interpreters and the Dragon Book.

The language is basically the same as lox (from Crafting Interpreters). With some modifications made by myself.

## How to build and run
### Build
To build Jlite, go on the root directory for this project and use;
```bash
make
```
### Install
Then, to install jlite, use:
```bash
make install
```
This already adds the path to the `.jar` file to your system's `PATH`. 
### Run
To run, just call jlite, with or without a input file. Using jlite on its own opens up a REPL terminal, which can be exited by pressing `ctrl+D` (a.k.a. typying `EOF`).
```bash
jlite
```
```bash
jlite main.jlite
```
### Uninstall
To uninstall, use:
```bash
make uninstall
```

## Grammar

Current grammar for the language's Parser:
```
program        -> declaration * EOF
declaration    -> varDecl | fnDecl | statement
varDecl        -> "var" IDENTIFIER ("=" exrpession)? ";"
fnDecl         -> "fn" function
function       -> IDENTIFIER "(" parameters? ")" block
parameters     -> IDENTIFIER ("," IDENTIFIER)*
statement      -> exprStmt | printStmt | block | ifStatement | whileStatement | forStatement | breakStatement
exprStmt       -> expression ";"
printStmt      -> "print" expression ";"
block          -> "{" declaration* "}"
ifStatement    -> "if" "(" expression ")" statement ("else" statement)?
whileStatement -> "while" "(" expression ")" statement
forStatement   -> "for" "(" ( varDecl | exprStmt | ";")  expression?  ";" expression? ")" statement
breakStatement -> "break" ";"
expression     -> assignment
assignment     -> equality | IDENTIFIER "=" assignment;
equality       -> logComparison (("!="|"==") logComparison)*
logComparison  -> bitComparison (("and"|"or") bitComparison)*
bitComparison  -> comparison (("|" | "^" | "&") comparison)* 
comparison     -> term (("<"|">"|"<="|">=") term)*
term           -> factor (("+"|"-") factor)*
factor         -> unary (("*"|"/") unary)*
unary          -> ("!"|"-"|"~") unary | call
call           -> primary ( "(" arguments? ")" )*
primary        -> IDENTIFIER | NUMBER | STRING | "true" | "false" | "nil" |"("expression")" | "("expression")?"expression":"expression
arguments      -> expression ( "," expression)*

```

## TODOs:

* 
* 
* 
* 
