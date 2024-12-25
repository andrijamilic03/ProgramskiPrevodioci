grammar Language;

// Program
start : statement* EOF;

// Naredba
statement
    : declaration
    | ifStatement
    | loopStatement
    | functionDeclaration
    | expression
    | inputStatement
    | printStatement
    ;

// Deklaracija promenljive
declaration : type ID (ASSIGN expression)? SEMICOLON | arrayDeclaration;

// Deklaracija niza
//arrayDeclaration : type ID '[' INT_LIT ']' (ASSIGN LBRACE expression (COMMA expression)* RBRACE)? SEMICOLON;
arrayDeclaration : type ID '[' INT_LIT ']' (ASSIGN LBRACE argumentList RBRACE)? SEMICOLON;

// Naredba grananja (if-else)
ifStatement : IF LPAREN expression RPAREN LBRACE (then+=statement)* RBRACE (ELSE LBRACE (else+=statement)* RBRACE)?;

// Petlja (for, while)
loopStatement : whileLoop | forLoop;

// For petlja
forLoop : FOR LPAREN declaration? expression? SEMICOLON expression? RPAREN LBRACE statement*  RBRACE;

// While petlja
whileLoop : WHILE LPAREN expression RPAREN LBRACE statement* RBRACE;

// Funkcija (deklaracija sa tipom povratne vrednosti i parametrima)
functionDeclaration : type ID LPAREN parameterList RPAREN LBRACE statement+ 'return' expression SEMICOLON RBRACE;

// Parametri funkcije
parameterList : (parameter (COMMA parameter)*)?;
parameter : type ID;

// Tipovi podataka
type : 'int' | 'bool';

// Izrazi
expression
    :expression AND expression
    | expression OR expression
    | expression EQ expression
    | expression NEQ expression
    | expression LT expression
    | expression GT expression
    | expression MUL expression
    | expression DIV expression
    | expression (PLUS | MINUS) expression
    | expression ASSIGN expression
    | NOT expression
    | LPAREN expression RPAREN
    | ID
    | INT_LIT
    | BOOL_LIT
    | arrayAccess
    | functionCall
    | expression INCREMENT
    | expression DECREMENT
    ;

argumentList
    : (expression (COMMA expression)*)?
    ;

// Pristup elementima niza
arrayAccess : ID LBRACK expression RBRACK;

// Poziv funkcije
functionCall : ID LPAREN argumentList RPAREN;

// Naredba za unos sa komandne linije
inputStatement : INPUT LPAREN '&' expression (COMMA '&' expression)* RPAREN SEMICOLON;

// Naredba za ispis na standardni izlaz
printStatement : PRINT LPAREN argumentList RPAREN SEMICOLON;

atom
    : INT_LIT #NumberConstant
    | ID #VariableReference
    | '(' expression ')' #GroupingOperator
    ;

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
GT : '>';
EQ : '==';
NEQ : '!=';
IF : 'if';
ELSE : 'else';
FOR : 'for';
WHILE : 'while';
PRINT : 'print';
INPUT : 'input';
BOOL_LIT : 'true' | 'false';
ID : [a-zA-Z_][a-zA-Z_0-9]*;
INT_LIT : [0-9]+;
LPAREN : '(';
RPAREN : ')';
LBRACE : '{';
RBRACE : '}';
LBRACK : '[';
RBRACK : ']';
SEMICOLON : ';';
COMMA : ',';

WS : [ \t\r\n]+ -> skip;
COMMENT: '/*' .*? '*/' -> skip; // Skips block comments
LINE_COMMENT: '//' ~[\r\n]* -> skip; // Skips line comments