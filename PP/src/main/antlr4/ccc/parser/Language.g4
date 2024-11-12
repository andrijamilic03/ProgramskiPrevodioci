grammar Language;

// Program
start : statement* EOF;

// Naredba
statement
    : printStatement
    | inputStatement
    | declaration
    | expressionStatement
    | ifStatement
    | loopStatement
    | functionDeclaration
    ;

// Deklaracija promenljive
declaration : type ID ASSIGN expression SEMICOLON | arrayDeclaration;

// Deklaracija niza
arrayDeclaration : type ID '['ID']' ASSIGN expression SEMICOLON;

// Iskaz sa izrazom (npr. print ili samo izraz)
expressionStatement : expression SEMICOLON;

// Naredba grananja (if-else)
ifStatement : IF LPAREN expression RPAREN statement ELSE statement;

// Petlja (for, while)
loopStatement : whileLoop | forLoop;

// For petlja
forLoop : FOR LPAREN declaration expression SEMICOLON expression RPAREN statement;

// While petlja
whileLoop : WHILE LPAREN expression RPAREN statement;

// Funkcija (deklaracija sa tipom povratne vrednosti i parametrima)
functionDeclaration : type ID LPAREN parameterList? RPAREN LBRACE statement+ RBRACE;

// Parametri funkcije
parameterList : parameter (COMMA parameter)*;
parameter : type ID;

// Tipovi podataka
type : 'int' | 'bool';

// Izrazi
expression
    : ID
    | INT_LIT
    | BOOL_LIT
    | arrayAccess
    | expression ASSIGN expression
    | expression PLUS expression
    | expression MINUS expression
    | expression MUL expression
    | expression DIV expression
    | expression INCREMENT
    | expression DECREMENT
    | expression LT expression
    | expression GT expression
    | expression EQ expression
    | expression NEQ expression
    | expression AND expression
    | expression OR expression
    | NOT expression
    | LPAREN expression RPAREN
    ;

// Pristup elementima niza
arrayAccess : ID '[' expression ']';

// Naredba za unos sa komandne linije
inputStatement : INPUT LPAREN ID RPAREN SEMICOLON;

// Naredba za ispis na standardni izlaz
printStatement : PRINT LPAREN expression RPAREN SEMICOLON;


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
ID : [a-zA-Z_][a-zA-Z_0-9]*;
INT_LIT : [0-9]+;
BOOL_LIT : 'true' | 'false';
LPAREN : '(';
RPAREN : ')';
LBRACE : '{';
RBRACE : '}';
SEMICOLON : ';';
COMMA : ',';
PRINT: 'print';
INPUT: 'input';
WS : [ \t\r\n]+ -> skip;
