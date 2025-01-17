grammar Language;

// Program
start : statement* EOF;

// Naredba
statement
    : declaration SEMICOLON
    | assignment SEMICOLON
    | ifStatement
    | loopStatement
    | functionDeclaration
    | printStatement
    | inputStatement
    | returnStatement
    | block
    ;

// Deklaracija
declaration
    : type=(INT | BOOL) ID (LBRACK arraySize+=INT_LIT? RBRACK)*;
assignment
    : leftHandSide ASSIGN expression;

leftHandSide
    :ID
    |arrayAccess
    |declaration
    ;

// Naredba grananja (if-else)
ifStatement : IF LPAREN expression RPAREN LBRACE (then+=statement)* RBRACE (ELSE LBRACE (else+=statement)* RBRACE)?;

// Petlja (for, while)
loopStatement : whileLoop | forLoop;

// For petlja
//forLoop : FOR LPAREN declaration? expression? SEMICOLON expression? RPAREN LBRACE statement* RBRACE;
forLoop: FOR LPAREN (assignment SEMICOLON)? expression? SEMICOLON assignment RPAREN LBRACE statement* RBRACE;


// While petlja
whileLoop : WHILE LPAREN expression RPAREN LBRACE statement* RBRACE;

// Funkcija (deklaracija sa tipom povratne vrednosti i parametrima)

functionDeclaration
    : type=(INT | BOOL) name=ID LPAREN ( firstType=(INT | BOOL) firstName=ID (COMMA restType+=(INT | BOOL) restName+=ID)* )? RPAREN LBRACE body+=statement* returnStatement SEMICOLON RBRACE;

argumentList
    : (expression (COMMA expression)*)?
    ;

//return
returnStatement: RETURN expression?;


// Naredba za unos sa komandne linije
inputStatement : INPUT LPAREN '&' expression (COMMA '&' expression)* RPAREN SEMICOLON;

// Naredba za ispis na standardni izlaz
printStatement : PRINT LPAREN argumentList RPAREN SEMICOLON;


block: LBRACE statement* RBRACE;

expression
    : logicalExpression
    ;

logicalExpression
    : initial=relationalExpression ( op+=(AND | OR) rest+=relationalExpression )*
    ;

relationalExpression
    : initial=additionalExpression ( op+=(LT | LTE | EQ | NEQ| GT | GTE) rest+=additionalExpression )*
    ;

additionalExpression
    : initial=multiplicationExpression ( op+=(PLUS | MINUS) rest+=multiplicationExpression )*
    ;

multiplicationExpression
    : initial=atom(op+=(MUL | DIV) rest+=atom)*
    ;

atom
    :primary
    |functionCall
    |arrayAccess
    ;

primary
    : ID #VariableReference
    | INT_LIT #NumberLiteral
    | BOOL_LIT #LogicalValue
    | LPAREN expression (COMMA expression)* RPAREN #GroupingOperator
    ;

// Pristup elementima niza
arrayAccess
    :initial=ID(LBRACK rest+=expression* RBRACK)*;

// Poziv funkcije
functionCall : ID LPAREN argumentList RPAREN;

// Operatori i drugi simboli
ASSIGN : '=';
PLUS : '+';
MINUS : '-';
MUL : '*';
DIV : '/';
INCREMENT : '++';
DECREMENT : '--';
AND : '&&';
OR : '||';
NOT : '!';
LT : '<';
LTE : '<=';
GT : '>';
GTE : '>=';
EQ : '==';
NEQ : '!=';
INT : 'int' ;
BOOL : 'bool';
IF : 'if';
ELSE : 'else';
FOR : 'for';
WHILE : 'while';
PRINT : 'print';
INPUT : 'input';
ARR: 'arr';
RETURN : 'return';

LPAREN : '(';
RPAREN : ')';
LBRACE : '{';
RBRACE : '}';
LBRACK : '[';
RBRACK : ']';
SEMICOLON : ';';
COMMA : ',';

BOOL_LIT : 'true' | 'false';
ID : [a-zA-Z_][a-zA-Z_0-9]*;
INT_LIT : [0-9]+;


WS : [ \t\r\n]+ -> skip;
COMMENT: '/*' .*? '*/' -> skip; // Skips block comments
LINE_COMMENT: '//' ~[\r\n]* -> skip; // Skips line comments